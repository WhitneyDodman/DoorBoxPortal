/**
* <h1>NewAccountBean</h1>
* This bean backs the accountRegistration.xhtml page for all new accounts
* and is specifically used to add new accounts to the database.
* 
* @author  Torin Walker
* @version 1.0
* @since   2016-08-03
*/
package doorbox.portal.beans;

import doorbox.portal.entity.Account;
import doorbox.portal.utils.Crypto;
import doorbox.portal.utils.DateTimeUtil;
import doorbox.portal.utils.Mail;
import doorbox.portal.utils.VerifyRecaptcha;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.component.UIComponent;
import javax.faces.component.UIInput;
import javax.faces.context.FacesContext;
import javax.faces.event.ComponentSystemEvent;
import javax.faces.validator.ValidatorException;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import javax.validation.constraints.*;

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
@ManagedBean
public class CancellationFormBean extends BaseBean {

    @NotNull(message = "Required")
    @Size(min = 2, max = 32, message = "Between 2 and 32 characters")
    private String name;
    
    @NotNull(message = "Required")
    @Pattern(regexp = "^[_A-Za-z0-9-\\+]+(\\.[_A-Za-z0-9-]+)*@[A-Za-z0-9-]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$", message = "Format: yourname@domain.com")
    private String email;

    @Pattern(regexp = "^(?:(?:\\+?1\\s*(?:[.-]\\s*)?)?(?:\\(\\s*([2-9]1[02-9]|[2-9][02-8]1|[2-9][02-8][02-9])\\s*\\)|([2-9]1[02-9]|[2-9][02-8]1|[2-9][02-8][02-9]))\\s*(?:[.-]\\s*)?)?([2-9]1[02-9]|[2-9][02-9]1|[2-9][02-9]{2})\\s*(?:[.-]\\s*)?([0-9]{4})(?:\\s*(?:#|x\\.?|ext\\.?|extension)\\s*(\\d+))?$", message = "Format: +1 (nnn) nnn-nnnn")
    private String phone;
    
    @NotNull(message = "Required")
    @Size(min = 2, max = 32, message = "Between 2 and 32 characters")
    private String reason;    

    @NotNull(message = "Required")
    @Size(min = 2, max = 1000, message = "Between 2 and 1000 characters")
    private String message;

    public String sendMessage() {
        System.out.println("submit");
        
        FacesContext context = FacesContext.getCurrentInstance();
        String gRecaptchaResponse = context.getExternalContext().getRequestParameterMap().get("g-recaptcha-response");
        System.out.println("gRecaptchaResponse: " + gRecaptchaResponse);
        
        try {
            if (!VerifyRecaptcha.verify(gRecaptchaResponse)) {
                System.out.println("Recaptcha not completed");
                FacesMessage msg = new FacesMessage("Verify you are not a robot");
                msg.setSeverity(FacesMessage.SEVERITY_ERROR);
                context.addMessage(null, msg);
                context.renderResponse();
                return "subscription_cancelled.xhtml";
            }
            
            HashMap<String,String> map = new HashMap<String,String>();
            map.put("name", getName());
            map.put("phone", getName());
            map.put("email", getEmail());
            map.put("message", getMessage());

            EmailTemplateBean.sendTemplateEmail("sales@thedoorbox.com", "Cancellation", "emailTemplate_cancellation.xhtml", map);
            
            return "cancelReasonSubmitted.xhtml";
        } catch (Exception e) {
            System.out.println("Error sending customer inquiry: " + e.getMessage());
            
            e.printStackTrace();
            
            FacesMessage msg = new FacesMessage("We encountered a problem submitting the form. Please report this to info@thedoorbox.com");
            msg.setSeverity(FacesMessage.SEVERITY_ERROR);
            context.addMessage(null, msg);
            context.renderResponse();
        }
        
        return "contactUs.xhtml";
    }

    /**
     * @return the name
     */
    public String getName() {
        return name;
    }

    /**
     * @param name the name to set
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * @return the email
     */
    public String getEmail() {
        return email;
    }

    /**
     * @param email the email to set
     */
    public void setEmail(String email) {
        this.email = email;
    }

    /**
     * @return the phone
     */
    public String getPhone() {
        return phone;
    }

    /**
     * @param phone the phone to set
     */
    public void setPhone(String phone) {
        this.phone = phone;
    }
    
    /**
     * @return the reason
     */
    public String getReason() {
        return reason;
    }

    /**
     * @param reason the reason to set
     */
    public void setReason(String reason) {
        this.reason = reason;
    }

    /**
     * @return the message
     */
    public String getMessage() {
        return message;
    }

    /**
     * @param message the message to set
     */
    public void setMessage(String message) {
        this.message = message;
    }

    
}
