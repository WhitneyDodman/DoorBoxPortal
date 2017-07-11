/**
 * <h1>MyAccountBean</h1>
 * This bean backs the myAccount.xhtml view under the My Account subsection of
 * the website. myAccount is used to view and alter the current account settings
 * such as billing address, phone numbers, etc.
 *
 * @author Torin Walker
 * @version 1.0
 * @since 2016-08-03
 */
package doorbox.portal.beans;

import doorbox.portal.constants.Security;
import doorbox.portal.entity.Account;
import doorbox.portal.utils.Crypto;
import java.util.logging.Logger;
import javax.annotation.PostConstruct;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;
import javax.faces.component.UIComponent;
import javax.faces.component.UIInput;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.faces.event.ComponentSystemEvent;
import javax.persistence.Query;
import javax.validation.constraints.*;
import org.primefaces.context.RequestContext;
import static doorbox.portal.constants.Security.EMAIL_LINK_VALIDITY_PERIOD;
import javax.annotation.Resource;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;


/**
 * <p>
 * Session scope data bean for your application. Create properties here to
 * represent cached data that should be made available across multiple HTTP
 * requests for an individual user.</p>
 *
 * <p>
 * An instance of this class will be created for you automatically, the first
 * time your application evaluates a value binding expression or method binding
 * expression that references a managed bean using this class.</p>
 *
 * @author torinw
 */
@ViewScoped
@ManagedBean
public class ResetPasswordBean extends BaseBean {   
    private final static Logger logger = Logger.getLogger(ResetPasswordBean.class.getName());

    //@ManagedProperty(value="#{param.accountId}")
    private Integer accountId;
    
    //@ManagedProperty(value="#{param.timestamp}")
    private Long timestamp;
    
    //@ManagedProperty(value="#{param.hmac}")
    private String hmac;

    private boolean resetRequestValid;
    
    @NotNull(message = "Required")
    @Size(min = 8, max = 20, message = "Between 8 and 20 characters")
    private String password1;

    @NotNull(message = "Required")
    @Size(min = 8, max = 20, message = "Between 8 and 20 characters")
    private String password2;
    
    
    @PostConstruct
    public void init() {
        accountId = Integer.valueOf(FacesContext.getCurrentInstance().getExternalContext().getRequestParameterMap().get("accountId"));
        timestamp = Long.valueOf(FacesContext.getCurrentInstance().getExternalContext().getRequestParameterMap().get("timestamp"));
        hmac = FacesContext.getCurrentInstance().getExternalContext().getRequestParameterMap().get("hmac");
        setResetRequestValid(validateResetRequest());
    }
    
    public String update() {
        ExternalContext externalContext = FacesContext.getCurrentInstance().getExternalContext();
        RequestContext context = RequestContext.getCurrentInstance();
        
        if (!validateResetRequest()) {
            return "resetPasswordExpired.xhtml";
        }
        
        System.out.println("password reset request is valid");
        
        if (validateResetRequest() && getPassword1().equals(getPassword2())) {
            // Create new account object. Subordinate objects are
            // created automatically
            try {
                utx.begin();

                em.joinTransaction();

                Query query = em.createNamedQuery("Account.findByAccountId");
                query.setParameter("accountId", accountId);
                Account account = (Account) query.getSingleResult();

                account.setPassword(Crypto.encrypt_SHA1(getPassword1()));

                em.merge(account);
                System.out.println("Updated password for account: " + getAccountId());                
                System.out.println("Redirecting to passwordUpdated.xhtml");

                return "passwordUpdated.xhtml";
                
            } catch (Exception e) {
                System.out.println("Unable to update password for account " + getAccountId() + ": " + e.getMessage());
            }
        }
        
        System.out.println("Remaining at resetPassword.xhtml");
        return "resetPassword.xhtml";
    }
    
    public void validatePassword(ComponentSystemEvent event) {
        FacesContext fc = FacesContext.getCurrentInstance();
        UIComponent components = event.getComponent();

        // get password
        UIInput uiInputPassword = (UIInput) components.findComponent("password1");
        String password = uiInputPassword.getLocalValue() == null ? ""
                : uiInputPassword.getLocalValue().toString();
        String passwordId = uiInputPassword.getClientId();

        // get confirm password
        UIInput uiInputConfirmPassword = (UIInput) components.findComponent("password2");
        String confirmPassword = uiInputConfirmPassword.getLocalValue() == null ? ""
                : uiInputConfirmPassword.getLocalValue().toString();

        // Let required="true" do its job.
        if (password.isEmpty() || confirmPassword.isEmpty()) {
            return;
        }

        if (!password.equals(confirmPassword)) {
            FacesMessage msg = new FacesMessage("Passwords must match");
            msg.setSeverity(FacesMessage.SEVERITY_ERROR);
            fc.addMessage(passwordId, msg);
            fc.renderResponse();
        }
    }

    
    public boolean validateResetRequest() {    
        StringBuffer buf = new StringBuffer();
        buf.append("http://portal.thedoorbox.com/resetPassword.xhtml?");
        buf.append("accountId=").append(accountId).append("&");
        buf.append("timestamp=").append(timestamp).append("&");
        try {
            System.out.println("cacluated reset url: " + buf.toString());
            String calculatedSignature = Crypto.calculateRFC2104HMAC(buf.toString(), Security.PASSWORD_RESET_HMAC_SECRET);
            System.out.println("calculated signature: " + calculatedSignature);
            System.out.println("actual signature: " + hmac);
            if (calculatedSignature.equals(getHmac())) {
                if (timestamp > System.currentTimeMillis() - EMAIL_LINK_VALIDITY_PERIOD) {
                    return true;
                }
            }
        } catch (Exception e) {
            System.out.println("Error verifying the validity of password reset request: " + e.getMessage());
            e.printStackTrace();
        }
        
        return false;            
    }

    /**
     * @return the accountId
     */
    public Integer getAccountId() {
        return accountId;
    }

    /**
     * @param accountId the accountId to set
     */
    public void setAccountId(Integer accountId) {
        this.accountId = accountId;
    }

    /**
     * @return the timestamp
     */
    public Long getTimestamp() {
        return timestamp;
    }

    /**
     * @param timestamp the timestamp to set
     */
    public void setTimestamp(Long timestamp) {
        this.timestamp = timestamp;
    }

    /**
     * @return the hmac
     */
    public String getHmac() {
        return hmac;
    }

    /**
     * @param hmac the hmac to set
     */
    public void setHmac(String hmac) {
        this.hmac = hmac;
    }

    /**
     * @return the password1
     */
    public String getPassword1() {
        return password1;
    }

    /**
     * @param password1 the password1 to set
     */
    public void setPassword1(String password1) {
        this.password1 = password1;
    }

    /**
     * @return the password2
     */
    public String getPassword2() {
        return password2;
    }

    /**
     * @param password2 the password2 to set
     */
    public void setPassword2(String password2) {
        this.password2 = password2;
    }

    /**
     * @return the resetRequestValid
     */
    public boolean isResetRequestValid() {
        return resetRequestValid;
    }

    /**
     * @param resetRequestValid the resetRequestValid to set
     */
    public void setResetRequestValid(boolean resetRequestValid) {
        this.resetRequestValid = resetRequestValid;
    }

}
