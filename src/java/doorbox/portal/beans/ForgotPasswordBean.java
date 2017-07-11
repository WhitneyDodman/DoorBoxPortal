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
import doorbox.portal.utils.Mail;
import java.util.logging.Logger;
import javax.annotation.Resource;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import javax.validation.constraints.*;
import org.primefaces.context.RequestContext;


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
@SessionScoped
@ManagedBean
public class ForgotPasswordBean extends BaseBean {
    private final static Logger logger = Logger.getLogger(ForgotPasswordBean.class.getName());

    @NotNull(message = "Required")
    @Pattern(regexp = "^[_A-Za-z0-9-\\+]+(\\.[_A-Za-z0-9-]+)*@[A-Za-z0-9-]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$", message = "Format: yourname@domain.com")
    private String email;

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

    public String reset() {
        logger.finer("Obtaining context details (session, request, response)");
        ExternalContext externalContext = FacesContext.getCurrentInstance().getExternalContext();
        RequestContext context = RequestContext.getCurrentInstance();
        //FacesMessage message = new FacesMessage(FacesMessage.SEVERITY_WARN, "Password Reset Error", "Invalid email address");

        if (email != null && email.length() > 0) {
            System.out.println("Searching for user by email: " + email);

            try {
                Query query = em.createNamedQuery("Account.findByEmail");
                query.setParameter("email", email);

                Account account = (Account) query.getSingleResult();
                
                if (account != null) {
                    System.out.println("Found account: " + account.getAccountId() + " using email: " + account.getEmail());
                    sendPasswordResetEmail(account);
                }
            } catch (Exception e) {
                System.out.println("No result - supress exception: " + e.getMessage());
            }
        }

        //FacesContext.getCurrentInstance().addMessage(null, message);
        // Returns to the "check email page" regardless of outcome because we don't
        // want to leak true/false responses if they guess email addresses correctly.
        return "resetPasswordCheckEmail.xhtml";
    }

    public void verifyPasswordReset(Account account) {
    }
        
    public void sendPasswordResetEmail(Account account) {
        StringBuffer buf = new StringBuffer();
        buf.append("http://portal.thedoorbox.com/resetPassword.xhtml?");
        buf.append("accountId=").append(account.getAccountId()).append("&");
        buf.append("timestamp=").append(System.currentTimeMillis()).append("&");
        try {
            System.out.println("generated reset url: " + buf.toString());
            String signature = Crypto.calculateRFC2104HMAC(buf.toString(), Security.PASSWORD_RESET_HMAC_SECRET);
            System.out.println("generated signature: " + signature);
            buf.append("hmac=").append(signature);
            String resetUrl = buf.toString();
            System.out.println("full reset url " + resetUrl);
            String htmlBody =
                "You are receiving this notification because someone requested a password reset for the account associated with your" +
                "email. If you did not request this reset, you can safely ignore this request. To continue with the password reset," +
                "please click on the link below." +
                "<br/>" +
                "<br/>" +
                "<a href=\"" + resetUrl + "\">Reset my password</a>" +
                "<br/>" +
                "<i>Please note: This link will expire in <ul>one hour</ul>.</i>";

            // Send mail and log activity atomically
            Mail.sendEmail(account.getEmail(), "do-not-reply@thedoorbox.com", "Password Reset Requested", htmlBody);
            // TODO: Write log of password reset request
            
        } catch (Exception e) {
            System.out.println("Error sending email: " + e.getMessage());
            e.printStackTrace();
        }
    }

}
