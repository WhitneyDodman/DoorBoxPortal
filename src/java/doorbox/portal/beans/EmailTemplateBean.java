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

import doorbox.portal.utils.Mail;
import doorbox.portal.utils.ResponseCatcher;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.logging.Logger;
import javax.faces.application.FacesMessage;
import javax.faces.application.ViewHandler;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.faces.component.UIViewRoot;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Multipart;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;
import javax.servlet.ServletRequest;
import javax.servlet.http.HttpServletResponse;


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
public class EmailTemplateBean extends BaseBean {
    private final static Logger logger = Logger.getLogger(EmailTemplateBean.class.getName());

    public static String DEFAULT_MAIL_HOST = "smtp.portal.thedoorbox.com";
    public static String DO_NOT_REPLY_ADDR = "do-not-reply@thedoorbox.com";
    public static String TEMPLATE_URL_PREFIX = "./WEB-INF/templates/";

    public void test() {
        Map<String,String>map = new HashMap<String,String>();
        map.put("recipientName", "Torin Walker");
        map.put("uniqueId", "doorbox1");
        map.put("description", "Don't just stand there. Go pick it up!");
        map.put("startDate", "now");
        map.put("endDate", "later");
        
        try {
            String htmlBody = capture("./WEB-INF/templates/emailTemplate_sendCode.xhtml", map);
        
        if (htmlBody == null)
            htmlBody = "<b>capture failed</b>";
        
        Mail.sendEmail("torinwalker@gmail.com", "do-not-reply@thedoorbox.com", "Test Email Template", htmlBody);
        
        } catch (Exception e) {
            System.out.println("error sending email template: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    public static void sendTemplateEmail(String recipient, String subject, String template, HashMap<String,String> params) {
        
        try {
            String htmlBody = capture(TEMPLATE_URL_PREFIX + template, params);
        
            if (htmlBody == null) {
                System.err.println("Error sending template email using template " + template);
                return;
            }

            //Mail.sendEmail(recipient, "do-not-reply@portal.thedoorbox.com", subject, htmlBody);
            //Mail.sendEmail(recipient, "do-not-reply@thedoorbox.com", subject, htmlBody);
            Mail.sendEmail(recipient, "sales@thedoorbox.com", subject, htmlBody);
        
        } catch (Exception e) {
            System.out.println("error sending email template: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    public static void sendEmail(String recipient, String sender, String subject, String htmlBody) {
        String replyTo = sender != null ? sender : DO_NOT_REPLY_ADDR;
        Properties properties = System.getProperties();
        properties.setProperty("mail.transport.protocol.rfc822", "smtp");
        properties.setProperty("mail.smtp.auth", "true");
        properties.setProperty("mail.auth", "true");
        properties.setProperty("mail.debug", "true");
        properties.setProperty("mail.debug.auth", "true");
        properties.setProperty("mail.protocol.host", "localhost");
        properties.setProperty("mail.protocol.port", "25");

        // Get the default Session object.
        Session session = Session.getInstance(properties,
            new javax.mail.Authenticator() {
                protected PasswordAuthentication getPasswordAuthentication() {
                    return new PasswordAuthentication("sales@portal.thedoorbox.com", "Bruins2011");
                }
            });

        try {
            Multipart mp = new MimeMultipart();
            MimeBodyPart htmlPart = new MimeBodyPart();
            htmlPart.setContent(htmlBody, "text/html");
            mp.addBodyPart(htmlPart);
            
            MimeMessage message = new MimeMessage(session);
            message.setFrom(new InternetAddress(replyTo));
            message.addRecipient(Message.RecipientType.TO, new InternetAddress(recipient));
            message.setSubject(subject);

            message.setContent(mp);

            Transport.send(message);
            System.out.println("Sent email successfully to " + recipient);

        } catch (MessagingException e) {
            System.out.println("Error sending email: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    /**
     * Render a template in memory and return the content as a string. The
     * request parameter 'emailClient' is set to true during rendering. This
     * method relies on a FacesContext for Facelets templating so it only
     * works when the app is deployed.
     */
    public static String capture(String template, Map params) {
        
        // setup a response catcher
        FacesContext faces = FacesContext.getCurrentInstance();
        ExternalContext context = faces.getExternalContext();
        ServletRequest request = (ServletRequest) faces.getExternalContext().getRequest();
        HttpServletResponse response = (HttpServletResponse)context.getResponse();
        ResponseCatcher catcher = new ResponseCatcher(response);
        
        // hack the request state
        UIViewRoot oldView = faces.getViewRoot();
        Map oldAttributes = null;
        if (params != null) {
            oldAttributes = new HashMap(params.size() * 2); // with buffer
            for (String key : (Set<String>) params.keySet()) {
                oldAttributes.put(key, request.getAttribute(key));
                request.setAttribute(key, params.get(key));
            }
        }
        request.setAttribute("emailClient", true);
        context.setResponse(catcher);
        
        try {
            // build a JSF view for the template and render it
            ViewHandler views = faces.getApplication().getViewHandler();
            UIViewRoot view = views.createView(faces, template);
            faces.setViewRoot(view);
            //views.getViewDeclarationLanguage(faces, template).buildView(faces, view);
            views.renderView(faces, view);
        } catch (IOException ioe) {
            String msg = "Failed to render " + template;
            faces.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, msg, msg));
            return null;
        } finally {
        
            // restore the request state
            if (oldAttributes != null) {
                for (String key : (Set<String>) oldAttributes.keySet()) {
                    request.setAttribute(key, oldAttributes.get(key));
                }
            }
            request.setAttribute("emailClient", null);
            context.setResponse(response);
            faces.setViewRoot(oldView);
        }
        return catcher.toString();
    }        
}
