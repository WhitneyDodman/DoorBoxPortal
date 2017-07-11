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

import doorbox.portal.entity.Account;
import doorbox.portal.utils.Crypto;
import doorbox.portal.utils.DateTimeUtil;
import doorbox.portal.utils.Mail;
import doorbox.portal.utils.RequestUtil;
import java.text.SimpleDateFormat;
import java.util.logging.Logger;
import javax.annotation.Resource;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.faces.component.UIComponent;
import javax.faces.component.UIInput;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.faces.event.ComponentSystemEvent;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.servlet.http.HttpServletRequest;
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
public class MyPasswordBean extends BaseBean {
    private final static Logger logger = Logger.getLogger(MyPasswordBean.class.getName());

    @NotNull(message = "Required")
    @Size(min = 7, max = 20, message = "Between 8 and 20 characters")
    private String currentPassword;
    
    @NotNull(message = "Required")
    @Size(min = 7, max = 20, message = "Between 8 and 20 characters")
    private String newPassword1;
    
    @NotNull(message = "Required")
    @Size(min = 7, max = 20, message = "Between 8 and 20 characters")
    private String newPassword2;
    
    private boolean passwordUpdated = false;
    
    public void validatePassword(ComponentSystemEvent event) {
        FacesContext fc = FacesContext.getCurrentInstance();
        UIComponent components = event.getComponent();
        
        // get currentPassword
        UIInput uiInputCurrentPassword = (UIInput) components.findComponent("currentPassword");
        String currentPassword = uiInputCurrentPassword.getLocalValue() == null ? ""
                : uiInputCurrentPassword.getLocalValue().toString();
        String currentPasswordMsgId = uiInputCurrentPassword.getClientId();

        // Shouldn't need this if required="true" does its job.
        if (currentPassword.isEmpty()) {
            FacesMessage msg = new FacesMessage("Password cannot be empty.");
            msg.setSeverity(FacesMessage.SEVERITY_ERROR);
            fc.addMessage(currentPasswordMsgId, msg);
            fc.renderResponse();
        }
        
        UIComponent uiUpdateButton = (UIComponent) components.findComponent("updateButton");
        String updateButtonMsgId = uiUpdateButton.getClientId();
                
        String encryptedCurrentPassword;
        try {
            encryptedCurrentPassword = Crypto.encrypt_SHA1(currentPassword);
            if (!getSessionBean().getAccount().getPassword().equals(encryptedCurrentPassword)) {
                FacesMessage msg = new FacesMessage("Invalid password");
                msg.setSeverity(FacesMessage.SEVERITY_ERROR);
                fc.addMessage(currentPasswordMsgId, msg);
                fc.renderResponse();
                return;
            }
        } catch (Exception e) {
            // Problem with crypto
            FacesMessage msg = new FacesMessage("E301 Error validating current password. Please contact support.");
            msg.setSeverity(FacesMessage.SEVERITY_FATAL);
            fc.addMessage(updateButtonMsgId, msg);
            fc.renderResponse();
            return;
        }
        
        // get password
        UIInput uiInputNewPassword1 = (UIInput) components.findComponent("newPassword1");
        String newPassword1 = uiInputNewPassword1.getLocalValue() == null ? ""
                : uiInputNewPassword1.getLocalValue().toString();
        String newPassword1MsgId = uiInputNewPassword1.getClientId();
        
        // get confirm password
        UIInput uiInputNewPassword2 = (UIInput) components.findComponent("newPassword2");
        String newPassword2 = uiInputNewPassword2.getLocalValue() == null ? ""
                : uiInputNewPassword2.getLocalValue().toString();
        String newPassword2MsgId = uiInputNewPassword2.getClientId();

        // Shouldn't need this if required="true" does its job.
        if (newPassword1.isEmpty()) {
            FacesMessage msg = new FacesMessage("Password cannot be empty.");
            msg.setSeverity(FacesMessage.SEVERITY_ERROR);
            fc.addMessage(newPassword1MsgId, msg);
            fc.renderResponse();
        }

        // Shouldn't need this if required="true" does its job.
        if (newPassword2.isEmpty()) {
            FacesMessage msg = new FacesMessage("Password cannot be empty.");
            msg.setSeverity(FacesMessage.SEVERITY_ERROR);
            fc.addMessage(newPassword2MsgId, msg);
            fc.renderResponse();
        }

        if (!newPassword1.equals(newPassword2)) {
            FacesMessage msg = new FacesMessage("Passwords must match");
            msg.setSeverity(FacesMessage.SEVERITY_ERROR);
            fc.addMessage(newPassword2MsgId, msg);
            fc.renderResponse();
        }
    }
    
    public String update() {
        logger.finer("update");
        RequestContext context = RequestContext.getCurrentInstance();

        String ipAddress = RequestUtil.getClientIPAddress();
        
        try {
            utx.begin();
            em.joinTransaction();

            Account account = getSessionBean().getAccount();
            account.setPassword(Crypto.encrypt_SHA1(getNewPassword1()));
            System.out.println("New password is " + account.getPassword());
            em.merge(account);
            em.persist(context);
            
            System.out.println("Password for account " + account.getAccountId() + " udpated");
            //TODO: move this dialog to .xhtml view
            context.showMessageInDialog(new FacesMessage(FacesMessage.SEVERITY_INFO, "Success", "Password updated successfully"));
            //context.execute("messageDialog.show();");
            //context.execute("PF('messageDialog').show();");
            
            sendPasswordUpdatedNotificationEmail(account, ipAddress);
            
            return "myAccountOverview.xhtml";
            //return null;

        } catch (Exception e) {
            System.out.println("Error updating account password: " + e.getMessage());
            context.showMessageInDialog(new FacesMessage("E304 Error updating account password. Please contact support."));
        }
        return null;
    }

    public void sendPasswordUpdatedNotificationEmail(Account account, String ipAddress) {
        try {
            
            String htmlBody =
                "You are receiving this notification because your DoorBox account password\n" +
                "was updated at " + DateTimeUtil.getDateTime() + " from IP address " + ipAddress + "\n" +
                "<p>If you received this message in error, you are advised to log into your account and\n" +
                "verify your settings, and we advise updating your password as a precaution.";

            // Send mail and log activity atomically
            Mail.sendEmail(account.getEmail(), "do-not-reply@thedoorbox.com", "Password Updated", htmlBody);
            // TODO: Write log of password update request
            
        } catch (Exception e) {
            System.out.println("Error sending email: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * @return the currentPassword
     */
    public String getCurrentPassword() {
        return currentPassword;
    }

    /**
     * @param currentPassword the currentPassword to set
     */
    public void setCurrentPassword(String currentPassword) {
        this.currentPassword = currentPassword;
    }

    /**
     * @return the newPassword1
     */
    public String getNewPassword1() {
        return newPassword1;
    }

    /**
     * @param newPassword1 the newPassword1 to set
     */
    public void setNewPassword1(String newPassword1) {
        this.newPassword1 = newPassword1;
    }

    /**
     * @return the newPassword2
     */
    public String getNewPassword2() {
        return newPassword2;
    }

    /**
     * @param newPassword2 the newPassword2 to set
     */
    public void setNewPassword2(String newPassword2) {
        this.newPassword2 = newPassword2;
    }

    /**
     * @return the passwordUpdated
     */
    public boolean isPasswordUpdated() {
        return passwordUpdated;
    }

    /**
     * @param passwordUpdated the passwordUpdated to set
     */
    public void setPasswordUpdated(boolean passwordUpdated) {
        this.passwordUpdated = passwordUpdated;
    }

}
