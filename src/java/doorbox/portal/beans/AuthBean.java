/**
 * <h1>AuthBean</h1>
 * This bean is a session-level object used for logging in, logging out,
 * maintaining session state, and other session-related metadata.
 * <p>
 * Other uses include testing to see if a user is logged in, getting his/her
 * userid, and possibly caching important database keys for quick access.
 *
 * Keep the object light, please.
 *
 * @author Torin Walker
 * @version 1.0
 * @since 2016-08-03
 */
package doorbox.portal.beans;

import doorbox.portal.entity.Log.Event;
import doorbox.portal.entity.Account;
import doorbox.portal.entity.SessionKey;
import doorbox.portal.utils.Crypto;
import doorbox.portal.utils.SessionUtil;
import java.security.Principal;
import java.util.logging.Logger;
import javax.annotation.Resource;
import javax.faces.application.ConfigurableNavigationHandler;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.faces.event.ComponentSystemEvent;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import javax.security.auth.Subject;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.validation.constraints.NotNull;
import org.primefaces.context.RequestContext;

@SessionScoped
@ManagedBean
public class AuthBean extends BaseBean {
    private final static Logger logger = Logger.getLogger(AuthBean.class.getName());    

    @NotNull
    private String email;

    @NotNull
    private String password;

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String registerRedirect_action() {
        System.out.println("New Account button clicked. Directing to NewAccount page.");
        return "accountRegistration";
    }

    public void checkAuthorization(ComponentSystemEvent event) {
        FacesContext fc = FacesContext.getCurrentInstance();
        HttpSession httpSession = (HttpSession) (fc.getExternalContext().getSession(false));
        Boolean loggedIn = (Boolean) httpSession.getAttribute("loggedIn");
        if (loggedIn == null || !loggedIn) {
            ConfigurableNavigationHandler handler = (ConfigurableNavigationHandler) fc.getApplication().getNavigationHandler();
            handler.performNavigation("login.xhtml");
        }
    }
    
    public String login_action() {
        logger.finer("Obtaining context details (session, request, response)");
        ExternalContext externalContext = FacesContext.getCurrentInstance().getExternalContext();
        RequestContext context = RequestContext.getCurrentInstance();
        FacesMessage message = new FacesMessage(FacesMessage.SEVERITY_WARN, "Login Error", "Invalid credentials");

        if (email != null && email.length() > 0 && password != null && password.length() > 0) {
            System.out.println("Authenticating user: " + email + " with password " + password);

            try {
                System.out.println("A");
                if (em == null) {                                        // try to create em
                    try {
                        EntityManagerFactory factory = Persistence.createEntityManagerFactory("DoorBoxPU");
                        em = factory.createEntityManager();
                    } catch (Exception e) {
                        System.out.println("Tried but failed to create entity manager. Reason: " + e.getMessage());
                    }
                }
                System.out.println("B");
                if (em == null) {
                    System.out.println("EntityManager is null!");                    
                    message = new FacesMessage(FacesMessage.SEVERITY_FATAL, "System Under Maintenance", "Unable to process request at this time");
                    FacesContext.getCurrentInstance().addMessage(null, message);
                    return null;
                }
                System.out.println("C");
                Query query = em.createNamedQuery("Account.findByEmail");
                query.setParameter("email", email);                
                Account account = (Account) query.getSingleResult();

                if (account != null) {
                    System.out.println("Found user: " + email);
                    String encryptedPassword = Crypto.encrypt_SHA1(password);
                    if (account.getPassword().equals(encryptedPassword)) {
                        System.out.println("Password matches");
                        // Get Session Instance and validate
                        System.out.println("Validating session");

                        Subject s = new Subject();
                        s.getPrincipals().add(new Principal() {
                            public String getName() {
                                return email;
                            }
                        });
                        switch (account.getAccountRole()) {
                            case Account.ROLE_ADMIN:
                                s.getPrincipals().add(new Principal() {
                                    public String getName() {
                                        return "admin";
                                    }
                                });
                                break;
                            case Account.ROLE_SUBSCRIBER:
                                s.getPrincipals().add(new Principal() {
                                    public String getName() {
                                        return "subscriber";
                                    }
                                });
                                break;
                            default:
                                s.getPrincipals().add(new Principal() {
                                    public String getName() {
                                        return "guest";
                                    }
                                });
                                break;                                
                        }

                        FacesContext.getCurrentInstance().getExternalContext().getSessionMap().put("JAASSubject", s);
                        
                        HttpSession session = SessionUtil.getSession();
                        session.setAttribute("email", email);
                        session.setAttribute("accountId", account.getAccountId());
                        session.setAttribute("loggedIn", true);
                        switch (account.getAccountRole()) {
                            case Account.ROLE_ADMIN:
                                session.setAttribute("role", "admin");
                                getSessionBean().setRole("admin");
                                break;
                            case Account.ROLE_SUBSCRIBER:
                                session.setAttribute("role", "subscriber");
                                getSessionBean().setRole("subscriber");
                                break;
                            default:
                                session.setAttribute("role", "unknown");
                                getSessionBean().setRole("unknown");
                                break;
                        }

                        System.out.println("Login succeeded. Retrieving account for email " + email + ", role" + account.getAccountRole());
                        logger.fine("Login succeeded. Retrieving account for email " + email + ", role" + account.getAccountRole());
                        logger.finer("Creating session bean");
                        getSessionBean().setAccount(account);

                        if (account.getRememberMeFlag()) {
                            logger.finer("Resetting persistent session cookie TH_SESSION");
                            HttpServletRequest request = (HttpServletRequest) externalContext.getRequest();
                            HttpServletResponse response = (HttpServletResponse) externalContext.getResponse();
                            System.out.println("Creating auto-login cookie for account " + account.getEmail());
                            String autoLoginSessionKey = session.getId();

                            String ipAddress = request.getRemoteAddr();

                            // Create accountId, autoLoginSessionKey, ipAddress tuple from database
                            SessionKey st = new SessionKey(autoLoginSessionKey, account.getAccountId(), ipAddress);
                            utx.begin();
                            em.joinTransaction();
                            em.persist(st);
                            utx.commit();

                            Cookie sessionCookie = new Cookie("DOORBOX_SESSION", autoLoginSessionKey);
                            sessionCookie.setMaxAge(86400 * 30);  // 30 days
                            sessionCookie.setPath("/");
                            response.addCookie(sessionCookie);

                            getSessionBean().setAutoLoginSessionKey(autoLoginSessionKey);
                        }

                        message = new FacesMessage(FacesMessage.SEVERITY_INFO, "Welcome", email);
                        FacesContext.getCurrentInstance().addMessage(null, message);
                        context.addCallbackParam("loggedIn", true);

                        dblog(Event.LOGIN_SUCCESS, account.getAccountId(), "email:" + email);
                        
                        System.out.println("Exiting");
                        logger.exiting("AuthBean", "login");
                        return "myAccountOverview.xhtml";
                    } else {
                        System.out.println("Incorrect credentials");
                        message = new FacesMessage(FacesMessage.SEVERITY_ERROR, "Incorrect credentials", "Incorrect credentials");
                        FacesContext.getCurrentInstance().addMessage(null, message);
                        logger.exiting("AuthBean", "login");
                        return null;
                    }                    
                } else {
                    // No such account
                    message = new FacesMessage(FacesMessage.SEVERITY_ERROR, "Incorrect credentials", "Incorrect credentials");
                    FacesContext.getCurrentInstance().addMessage(null, message);
                    logger.exiting("AuthBean", "login");
                    return null;
                }
            } catch (Exception e) {
                System.out.println("Authentication failure: " + e.getMessage());
                e.printStackTrace();
                
                message = new FacesMessage(FacesMessage.SEVERITY_ERROR, "Incorrect credentials", "Incorrect credentials");
                FacesContext.getCurrentInstance().addMessage(null, message);                
                logger.exiting("AuthBean", "login");
                return null;
            }
        }

        System.out.println("Login failed");
        FacesContext.getCurrentInstance().addMessage(null, message);
        context.addCallbackParam("loggedIn", false);

        logger.exiting("AuthBean", "login");
        return "login.xhtml";
    }

    public String logout_action() {
        System.out.println("Invalidating session");

        Account account = getSessionBean().getAccount();        
        if (account != null) {
            dblog(Event.LOGOUT, account.getAccountId(), "email:" + account.getEmail());
            HttpSession session = SessionUtil.getSession();
            session.invalidate();
        }

        return "login.html";
    }

    /**
     * @return the loggedIn
     */
    public Boolean getLoggedIn() {
        HttpSession session = SessionUtil.getSession();
        if (session != null) {
            //System.out.println("getLoggedIn: email: " + session.getAttribute("email"));
            //System.out.println("getLoggedIn: state: " + session.getAttribute("loggedIn"));
            Boolean sessionState = (Boolean) session.getAttribute("loggedIn");
            return sessionState == null ? false : sessionState;
        }
        return false;
    }

    /**
     * @param loggedIn the loggedIn to set
     */
    public void setLoggedIn(Boolean loggedIn) {
        // do nothing
    }

}
