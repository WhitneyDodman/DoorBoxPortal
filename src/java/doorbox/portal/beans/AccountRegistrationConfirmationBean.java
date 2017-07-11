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
import doorbox.portal.entity.Log;
import doorbox.portal.utils.SessionUtil;
import java.util.Date;
import javax.annotation.Resource;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.servlet.http.HttpSession;


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
public class AccountRegistrationConfirmationBean extends BaseBean {   
    private final static Logger logger = Logger.getLogger(AccountRegistrationConfirmationBean.class.getName());

    private Integer accountId;
    private Long timestamp;
    private String hmac;
    private boolean registrationRequestValid;
        
    @PostConstruct
    public void init() {
        accountId = Integer.valueOf(FacesContext.getCurrentInstance().getExternalContext().getRequestParameterMap().get("accountId"));
        timestamp = Long.valueOf(FacesContext.getCurrentInstance().getExternalContext().getRequestParameterMap().get("timestamp"));
        hmac = FacesContext.getCurrentInstance().getExternalContext().getRequestParameterMap().get("hmac");
        registrationRequestValid = validateRegistrationRequest();
    }        
    
    public boolean validateRegistrationRequest() {    
        StringBuffer buf = new StringBuffer();
        buf.append("http://portal.thedoorbox.com/accountRegistrationConfirmation.xhtml?");
        buf.append("accountId=").append(accountId).append("&");
        buf.append("timestamp=").append(timestamp).append("&");
        try {
            System.out.println("cacluated reset url: " + buf.toString());
            String calculatedSignature = Crypto.calculateRFC2104HMAC(buf.toString(), Security.REGISTRATION_CONFIRMATION_HMAC_SECRET);
            System.out.println("calculated signature: " + calculatedSignature);
            System.out.println("actual signature: " + hmac);
            if (calculatedSignature.equals(hmac)) {
                if (timestamp > System.currentTimeMillis() - Security.EMAIL_LINK_VALIDITY_PERIOD) {

                    // Update account and move state from new to registered
                    utx.begin();
                    em.joinTransaction();
                    Query query = em.createNamedQuery("Account.findByAccountId");
                    query.setParameter("accountId", accountId);
                    Account account = (Account) query.getSingleResult();
                    account.setAccountState(Account.STATE_ACTIVE);            
                    em.merge(account);
                    dblog(Log.Event.CONFIRM_REGISTRATION, account.getAccountId(), "email:" + account.getEmail());
                    utx.commit();
                    return true;
                }
            }
        } catch (Exception e) {
            System.out.println("Error during account registration confirmation: " + e.getMessage());
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
     * @return the registrationRequestValid
     */
    public boolean isRegistrationRequestValid() {
        return registrationRequestValid;
    }

    /**
     * @param registrationRequestValid the registrationRequestValid to set
     */
    public void setRegistrationRequestValid(boolean registrationRequestValid) {
        this.registrationRequestValid = registrationRequestValid;
    }

}
