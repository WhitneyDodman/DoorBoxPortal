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
import doorbox.portal.entity.Doorbox;
import doorbox.portal.entity.EphemeralCode;
import doorbox.portal.entity.Log;
import doorbox.portal.utils.DateTimeUtil;
import doorbox.portal.utils.NetCodeAPI;
import doorbox.portal.utils.SMSGateWayAPI;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.logging.Logger;
import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.component.UIComponent;
import javax.faces.component.UIInput;
import javax.faces.context.FacesContext;
import javax.faces.event.ComponentSystemEvent;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import javax.validation.ConstraintViolation;
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
@ViewScoped
@ManagedBean
public class EphemeralCodeBean extends BaseBean {
    private final static Logger logger = Logger.getLogger(EphemeralCodeBean.class.getName());

    private int accountId;
    private int doorboxId;
    private Date initDate;
    
    //@NotNull(message = "Recipient Name is Required")
    private String recipientName;
    
    //@Pattern(regexp = "^[_A-Za-z0-9-\\+]+(\\.[_A-Za-z0-9-]+)*@[A-Za-z0-9-]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$", message = "Format: yourname@domain.com")
    private String recipientEmail;
    
    //@Pattern(regexp = "^(?:(?:\\+?1\\s*(?:[.-]\\s*)?)?(?:\\(\\s*([2-9]1[02-9]|[2-9][02-8]1|[2-9][02-8][02-9])\\s*\\)|([2-9]1[02-9]|[2-9][02-8]1|[2-9][02-8][02-9]))\\s*(?:[.-]\\s*)?)?([2-9]1[02-9]|[2-9][02-9]1|[2-9][02-9]{2})\\s*(?:[.-]\\s*)?([0-9]{4})(?:\\s*(?:#|x\\.?|ext\\.?|extension)\\s*(\\d+))?$", message = "Format: +1 (nnn) nnn-nnnn")
    private String recipientPhone;
    
    private String message;
    
    //@NotNull(message = "Start Date is Required")
    private Date startDate;
    
    @NotNull(message = "Duration is required")
    private int durationId;
    
    private Map<String,Integer> durations;
    
    @PostConstruct
    public void init() {
        // Load current account's doorbox details into this bean
        Map<String, String> params = FacesContext.getCurrentInstance().getExternalContext().getRequestParameterMap();
        String value = params.get("doorboxId");
        if (value != null && !value.equals("null")) {
            setDoorboxId(Integer.parseInt(value));
            System.out.println("Got param doorboxId=" + getDoorboxId());
        }        
    }
    
    public void sendCodeActionListener() {
        sendCode();
    }
    
    public String sendCode() {
        System.out.println("Sending ephemeral code");

        try {
            utx.begin();
            em.joinTransaction();
            System.out.println("H");
            Account account = getSessionBean().getAccount();
            
            Query q = em.createNamedQuery("Doorbox.findByDoorboxId");
            q.setParameter("doorboxId", getDoorboxId());
            Doorbox doorbox = (Doorbox)q.getSingleResult();
            
            System.out.println("Generating NetCode");
            //int hours = (int)(endDate.getTime() - startDate.getTime()) / 60000;
            String netCodeResponse = NetCodeAPI.getNetCode(doorbox.getInitDate(), startDate, durationId, doorbox.getSeed());
            
            if (netCodeResponse == null) {
                System.out.println("Error generating NetCode");
                return null;
            }
            // parse netcode
            String netCode = NetCodeAPI.getActualNetCode(netCodeResponse);
                    
            EphemeralCode ephemeralCode = new EphemeralCode();
            ephemeralCode.setAccountId(account.getAccountId());
            ephemeralCode.setDoorboxId(doorboxId);
            ephemeralCode.setEphemeralCode(netCode);
            ephemeralCode.setMessage(message);
            ephemeralCode.setRecipientName(getRecipientName());
            ephemeralCode.setRecipientEmail(getRecipientEmail());
            ephemeralCode.setRecipientPhone(getRecipientPhone());
            ephemeralCode.setStartDate(startDate);
            ephemeralCode.setDurationId(durationId);
            
            em.merge(ephemeralCode);
            utx.commit();
            
            dblog(Log.Event.CREATE_EPHEMERAL_CODE, account.getAccountId(), "email:" + account.getEmail() + ", doorboxId: " + doorbox.getDoorboxId());

            if (getRecipientEmail() != null) {
                sendCodeByEmail(account, doorbox, ephemeralCode);
                dblog(Log.Event.SEND_EMAIL, account.getAccountId(), "email:" + account.getEmail() + ", doorboxId: " + doorbox.getDoorboxId() + ", recipientEmail:" + recipientEmail);
            }
            
            if (getRecipientPhone() != null) {
                sendCodeBySMS(account, doorbox, ephemeralCode);
                dblog(Log.Event.SEND_SMS, account.getAccountId(), "email:" + account.getEmail() + ", doorboxId: " + doorbox.getDoorboxId() + ", recipientPhone:" + recipientPhone);                
            }
            
            System.out.println("Returning to myDoorboxes.xhtml");
            return "myDoorboxes.xhtml";
        } catch (javax.validation.ConstraintViolationException e) {
            for (Iterator<ConstraintViolation<?>> it = e.getConstraintViolations().iterator(); it.hasNext();) {
                ConstraintViolation<? extends Object> v = it.next();
                System.err.println(v);
                System.err.println("==>>"+v.getMessage());
            }
        } catch (Exception e) {            
            System.out.println("Exception sending code: " + e.getMessage());
            e.printStackTrace();
        }
        
        return null;
    }

    public String sendTempCode() {
        System.out.println("Sending ephemeral code 2");
        FacesContext fc = FacesContext.getCurrentInstance();

        try {
            utx.begin();
            em.joinTransaction();
                        
            Account account = getSessionBean().getAccount();
            
            Query q = em.createNamedQuery("Doorbox.findByDoorboxId");
            q.setParameter("doorboxId", getDoorboxId());
            Doorbox doorbox = (Doorbox)q.getSingleResult();
            
            System.out.println("Generating NetCode");
            //int hours = (int)(endDate.getTime() - startDate.getTime()) / 60000;
            String netCodeResponse = NetCodeAPI.getNetCode(doorbox.getInitDate(), startDate, durationId, doorbox.getSeed());
            
            if (netCodeResponse == null) {
                System.out.println("Error generating NetCode");
                return null;
            }
            // parse netcode
            String netCode = NetCodeAPI.getActualNetCode(netCodeResponse);
                    
            EphemeralCode ephemeralCode = new EphemeralCode();
            ephemeralCode.setAccountId(account.getAccountId());
            ephemeralCode.setDoorboxId(doorboxId);
            ephemeralCode.setEphemeralCode(netCode);
            ephemeralCode.setMessage(message);
            ephemeralCode.setRecipientName(getRecipientName());
            ephemeralCode.setRecipientEmail(getRecipientEmail());
            ephemeralCode.setRecipientPhone(getRecipientPhone());
            ephemeralCode.setStartDate(startDate);
            ephemeralCode.setDurationId(durationId);
            
            em.merge(ephemeralCode);
            utx.commit();
            
            dblog(Log.Event.CREATE_EPHEMERAL_CODE, account.getAccountId(), "email:" + account.getEmail() + ", doorboxId: " + doorbox.getDoorboxId());

            if (getRecipientEmail() != null) {
                sendCodeByEmail(account, doorbox, ephemeralCode);
                dblog(Log.Event.SEND_EMAIL, account.getAccountId(), "email:" + account.getEmail() + ", doorboxId: " + doorbox.getDoorboxId() + ", recipientEmail:" + recipientEmail);
            }
            
            if (getRecipientPhone() != null) {
                sendCodeBySMS(account, doorbox, ephemeralCode);
                dblog(Log.Event.SEND_SMS, account.getAccountId(), "email:" + account.getEmail() + ", doorboxId: " + doorbox.getDoorboxId() + ", recipientPhone:" + recipientPhone);                
            }
            
            System.out.println("Returning to myDoorboxes.xhtml");
            return "myDoorboxes.xhtml";
        } catch (javax.validation.ConstraintViolationException e) {
            for (Iterator<ConstraintViolation<?>> it = e.getConstraintViolations().iterator(); it.hasNext();) {
                ConstraintViolation<? extends Object> v = it.next();
                System.err.println(v);
                System.err.println("==>>"+v.getMessage());
            }
        } catch (Exception e) {            
            System.out.println("Exception sending code: " + e.getMessage());
            e.printStackTrace();
        }
        
        return null;
    }
    
    public void sendCodeByEmail(Account account, Doorbox doorbox, EphemeralCode code) {
        String formattedStartDate = DateTimeUtil.getFormattedDateTime(code.getStartDate());                
        
        HashMap<String,String> map = new HashMap<String,String>();
        map.put("recipientName", code.getRecipientName());
        map.put("uniqueId", doorbox.getUniqueId());
        map.put("ephemeralCode", code.getEphemeralCode());
        map.put("message", code.getMessage());
        map.put("startDate", formattedStartDate);
        map.put("duration", getDurationDescriptionById(code.getDurationId()));
        
        EmailTemplateBean.sendTemplateEmail(code.getRecipientEmail(), "You've received a DoorBox Code", "emailTemplate_sendCode.xhtml", map);
    }
    
    public void sendCodeBySMS(Account account, Doorbox doorbox, EphemeralCode code) {
        // Strip all but numerals from number
        String smsNumber = code.getRecipientPhone().replaceAll("[^0-9]", "");
        
        String smsMessage = 
            "Your DoorBox Code!\n\n" +
//            "Hello " + code.getRecipientName() + "\n\n" +
            "Code: " + code.getEphemeralCode() + "\n\n" +
            "Activation Date:\n" +
            new SimpleDateFormat("yyyy-MM-dd HH:mm").format(code.getStartDate()) + "\n\n" +
            "Duration:" + durationToHumanReadableString(code.getDurationId());
        /*String result =*/ SMSGateWayAPI.sendSMS(smsNumber, smsMessage);        
    }
    
    public String durationToHumanReadableString(int durationId) {
        return NetCodeAPI.DURATION_VALUES[durationId];
    }
    
    public void validateRecipientFields(ComponentSystemEvent event) {
        System.out.println("K");
        FacesContext fc = FacesContext.getCurrentInstance();
        UIComponent components = event.getComponent();
        
        UIInput uiInputRecipientEmail = (UIInput) components.findComponent("recipientEmail");
        String recipientEmail = uiInputRecipientEmail.getLocalValue() == null ? "" : uiInputRecipientEmail.getLocalValue().toString();
        String recipientEmailId = uiInputRecipientEmail.getClientId();

        UIInput uiInputRecipientPhone = (UIInput) components.findComponent("recipientPhone");
        String recipientPhoneId = uiInputRecipientPhone.getLocalValue() == null ? "" : uiInputRecipientPhone.getLocalValue().toString();

        if ((recipientEmail == null || recipientEmail.isEmpty()) && (recipientPhone == null || recipientPhone.isEmpty())) {
            fc.addMessage(recipientEmailId, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Email and/or Phone Required", "Email and/or Phone Required"));
            fc.addMessage(recipientPhoneId, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Email and/or Phone Required", "Email and/or Phone Required"));
            fc.renderResponse();
        }

        System.out.println("L");
    }

    public void validateFields(ComponentSystemEvent event) {
        FacesContext fc = FacesContext.getCurrentInstance();
        UIComponent components = event.getComponent();
        
        UIInput recipientNameField = (UIInput)components.findComponent("recipientName");
        if (recipientNameField.getValue() == null) {
            fc.addMessage("recipientName", new FacesMessage(FacesMessage.SEVERITY_ERROR, "Recipient name required", "Recipient name required"));
            recipientNameField.setValid(false);
        }            
        UIInput recipientEmailField = (UIInput)components.findComponent("recipientEmail");
        UIInput recipientPhoneField = (UIInput)components.findComponent("recipientPhone");
        if (recipientEmailField.getValue() == null && recipientPhoneField.getValue() == null) {
            fc.addMessage("recipientEmail", new FacesMessage(FacesMessage.SEVERITY_ERROR, "Email and/or Phone Required", "Email and/or Phone Required"));
            //((UIInput)components.findComponent("recipientEmail")).setValid(false);
            fc.addMessage("recipientPhone", new FacesMessage(FacesMessage.SEVERITY_ERROR, "Email and/or Phone Required", "Email and/or Phone Required"));
            //((UIInput)components.findComponent("recipientPhone")).setValid(false);
        }
        
        if (recipientEmailField.getValue() != null) {
            String recipientEmail = recipientEmailField.getValue().toString();
            if (!recipientEmail.matches("^[_A-Za-z0-9-\\+]+(\\.[_A-Za-z0-9-]+)*@[A-Za-z0-9-]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$")) {                    
                fc.addMessage("recipientEmail", new FacesMessage(FacesMessage.SEVERITY_ERROR, "Email must be properly formatted: username@domain.com", "Email must be properly formatted: username@domain.com"));
                ((UIInput)components.findComponent("recipientEmail")).setValid(false);
                ((UIInput)components.findComponent("recipientEmail")).setValidatorMessage("Email must be properly formatted: username@domain.com");
                ((UIInput)components.findComponent("recipientEmail")).setRequiredMessage("Email must be properly formatted: username@domain.com");
                ((UIInput)components.findComponent("recipientEmail")).setConverterMessage("Email must be properly formatted: username@domain.com");                
            }
        } else {
            System.out.println("email is null. Not checking format");
        }
        
        if (recipientPhoneField.getValue() != null) {
            String recipientPhone = recipientPhoneField.getValue().toString();
            if (!recipientPhone.matches("^(?:(?:\\+?1\\s*(?:[.-]\\s*)?)?(?:\\(\\s*([2-9]1[02-9]|[2-9][02-8]1|[2-9][02-8][02-9])\\s*\\)|([2-9]1[02-9]|[2-9][02-8]1|[2-9][02-8][02-9]))\\s*(?:[.-]\\s*)?)?([2-9]1[02-9]|[2-9][02-9]1|[2-9][02-9]{2})\\s*(?:[.-]\\s*)?([0-9]{4})(?:\\s*(?:#|x\\.?|ext\\.?|extension)\\s*(\\d+))?$")) {
                fc.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Invalid phone format. Format as (nnn) nnn-nnnn", "Invalid phone format. Format as (nnn) nnn-nnnn"));
                ((UIInput)components.findComponent("recipientPhone")).setValid(false);
            }
        } else {
            System.out.println("phone value is null. Not checking format");
        }
        
        UIInput startDateField = (UIInput)components.findComponent("startDatetime");
        if (startDateField.getValue() == null) {
            fc.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Start date is required", "Start date is required"));
            ((UIInput)components.findComponent("startDatetime")).setValid(false);
        } else {
            Date startDate = (Date)startDateField.getValue();
            //if (startDate.getTime() < System.currentTimeMillis()) {
            //    fc.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Hey McFly, start date cannot be in the past", "Hey McFly, start date cannot be in the past"));
            //    ((UIInput)components.findComponent("startDatetime")).setValid(false);
            //}
        }
        /*UIInput endDateField = (UIInput)components.findComponent("endDatetime");
        if (endDateField.getValue() == null) {
            fc.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "End date is required", "End date is required"));
            ((UIInput)components.findComponent("endDatetime")).setValid(false);
        } else {
            if (startDateField.getValue() != null) {
                Date startDate = (Date)startDateField.getValue();
                Date endDate = (Date)endDateField.getValue();        
                if (endDate.before(startDate)) {
                    fc.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "End date must be greater than start date", "End date must be greater than start date"));
                    ((UIInput)components.findComponent("endDatetime")).setValid(false);
                }
            }
        }*/
        if (!fc.getMessageList().isEmpty()) {
            fc.renderResponse();
        }
    }
    /**
     * Reads entity values into current bean for display in view
    */
    public List getEphemeralCodes() {
        Query q = em.createNamedQuery("EphemeralCode.findByAccountId");
        int accountId = getSessionBean().getAccount().getAccountId();
        q.setParameter("accountId", accountId);
        
        List<EphemeralCode> list = (List<EphemeralCode>) q.getResultList();
        
        return list;
    }
    
    /**
     * @return the recipientName
     */
    public String getRecipientName() {
        return recipientName;
    }

    /**
     * @param recipientName the recipientName to set
     */
    public void setRecipientName(String recipientName) {
        this.recipientName = recipientName;
    }

    /**
     * @return the recipientEmail
     */
    public String getRecipientEmail() {
        return recipientEmail;
    }

    /**
     * @param recipientEmail the recipientEmail to set
     */
    public void setRecipientEmail(String recipientEmail) {
        this.recipientEmail = recipientEmail;
    }

    /**
     * @return the recipientPhone
     */
    public String getRecipientPhone() {
        return recipientPhone;
    }

    /**
     * @param recipientPhone the recipientPhone to set
     */
    public void setRecipientPhone(String recipientPhone) {
        this.recipientPhone = recipientPhone;
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

    /**
     * @return the startDate
     */
    public Date getStartDate() {
        return startDate;
    }

    /**
     * @param startDate the startDate to set
     */
    public void setStartDate(Date startDate) {
        this.startDate = startDate;
    }      

    public Map<String,Integer> getDurations() {
        if (durations == null) {
            durations = new HashMap<String, Integer>();        
            durations.put("1 Hour", 0);
            durations.put("2 Hours", 1);
            durations.put("3 Hours", 2);
            durations.put("4 Hours", 3);
            durations.put("5 Hours", 4);
            durations.put("6 Hours", 5);
            durations.put("7 Hours", 6);
            durations.put("8 Hours", 7);
            durations.put("9 Hours", 8);
            durations.put("10 Hours", 9);
            durations.put("11 Hours", 10);
            durations.put("12 Hours", 11);
            durations.put("1 Day", 12);
            durations.put("2 Days", 13);
            durations.put("3 Days", 14);
            durations.put("4 Days", 15);
            durations.put("5 Days", 16);
            durations.put("6 Days", 17);
            durations.put("7 Days", 18);
        }
        
        return durations;
    }
    
    public String getDurationDescriptionById(int i) {
        Map<String,Integer> map = getDurations();
        for (Entry<String, Integer> entry : map.entrySet()) {
            if (entry.getValue().intValue() == i) {
                return entry.getKey();
            }
        }
        return "undefined duration";
    }
    
    /**
     * @return the doorboxId
     */
    public int getDoorboxId() {
        return doorboxId;
    }

    /**
     * @param doorboxId the doorboxId to set
     */
    public void setDoorboxId(int doorboxId) {
        this.doorboxId = doorboxId;
    }
    
    /**
     * Convenience method for JSF EL calls involving Date
     * @return The current date as a Date object
     */
    public Date getCurrentDate() {
        return new Date();
    }
    
    /**
     * Convenience method for JSF EL calls involving Date
     * @param seconds The offset added to the current time
     * @return The current date as a Date object + 1 hour
     */
    public Date getCurrentDatePlusOneHour() {
        return new Date(getCurrentDate().getTime() + 360000);
    }

    /**
     * @return the durationId
     */
    public int getDurationId() {
        return durationId;
    }

    /**
     * @param durationId the durationId to set
     */
    public void setDurationId(int durationId) {
        this.durationId = durationId;
    }

}
