/**
* <h1>MyAccountBean</h1>
* This bean backs the myAccount.xhtml view under the My Account
* subsection of the website. myAccount is used to view and alter
* the current account settings such as billing address, phone numbers,
* etc.
*
* @author  Torin Walker
* @version 1.0
* @since   2016-08-03
*/
package doorbox.portal.beans;

import doorbox.portal.constants.Security;
import doorbox.portal.entity.Account;
import doorbox.portal.entity.Log;
import doorbox.portal.utils.Crypto;
import doorbox.portal.utils.Mail;
import doorbox.portal.utils.SessionUtil;
import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.net.URL;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.faces.component.UIComponent;
import javax.faces.component.UIInput;
import javax.faces.context.FacesContext;
import javax.faces.event.ComponentSystemEvent;
import javax.faces.validator.ValidatorException;
import javax.json.Json;
import javax.json.JsonObject;
import javax.json.JsonReader;
import javax.net.ssl.HttpsURLConnection;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import javax.servlet.http.HttpSession;
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
@SessionScoped
@ManagedBean
public class MyAccountBean extends BaseBean {
    public MyAccountBean() {
    }
    
    @NotNull(message = "Required")
    private int accountId;

    // See doorbox.portal.constants.AccountState
    @NotNull(message = "Required")
    private int accountState;

    @NotNull(message = "Required")
    @Pattern(regexp = "^[_A-Za-z0-9-\\+]+(\\.[_A-Za-z0-9-]+)*@[A-Za-z0-9-]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$", message = "Format: yourname@domain.com")
    private String email;

    @NotNull(message = "Required")
    @Size(min = 8, max = 20, message = "Between 8 and 20 characters")
    private String password1;

    @NotNull(message = "Required")
    @Size(min = 8, max = 20, message = "Between 8 and 20 characters")
    private String password2;

    @NotNull(message = "Required")
    @Size(min = 2, max = 20, message = "Between 2 and 20 characters")
    private String firstname;

    @Size(min = 0, max = 20, message = "Between 2 and 20 characters")
    private String middlename;

    @NotNull(message = "Required")
    @Size(min = 2, max = 20, message = "Between 2 and 20 characters")
    private String lastname;

    @Size(min = 0, max = 20, message = "Between 2 and 20 characters")
    private String company;

    @NotNull(message = "Required")
    @Size(min = 4, max = 30, message = "Between 4 and 30 characters")
    private String address1;

    @Size(min = 0, max = 30, message = "30 characters or less")
    private String address2;

    @NotNull(message = "Required")
    @Size(min = 2, max = 30, message = "Between 2 and 20 characters")
    private String city;

    @NotNull(message = "Required")
    @Size(min = 2, max = 30, message = "2 characters")
    private String stateprov;

    @NotNull(message = "Required")
    @Size(min = 2, max = 30, message = "2 character ISO country code")
    private String country;

    @NotNull(message = "Required")
    @Pattern(regexp = "(([a-zA-Z][0-9][a-zA-Z] *[0-9][a-zA-Z][0-9])|([0-9]{5}(-[0-9]{4,5})?))", message = "Canada: A1A 2B2 or US: 12345 or 12345-67890")
    private String postalcode;

    @NotNull(message = "Required")
    //@Pattern(regexp = "^(?:(?:\\+?1\\s*(?:[.-]\\s*)?)?(?:\\(\\s*([2-9]1[02-9]|[2-9][02-8]1|[2-9][02-8][02-9])\\s*\\)|([2-9]1[02-9]|[2-9][02-8]1|[2-9][02-8][02-9]))\\s*(?:[.-]\\s*)?)?([2-9]1[02-9]|[2-9][02-9]1|[2-9][02-9]{2})\\s*(?:[.-]\\s*)?([0-9]{4})(?:\\s*(?:#|x\\.?|ext\\.?|extension)\\s*(\\d+))?$", message = "Format: +1 (nnn) nnn-nnnn")
    private String phone1;

    //@Pattern(regexp = "^(?:(?:\\+?1\\s*(?:[.-]\\s*)?)?(?:\\(\\s*([2-9]1[02-9]|[2-9][02-8]1|[2-9][02-8][02-9])\\s*\\)|([2-9]1[02-9]|[2-9][02-8]1|[2-9][02-8][02-9]))\\s*(?:[.-]\\s*)?)?([2-9]1[02-9]|[2-9][02-9]1|[2-9][02-9]{2})\\s*(?:[.-]\\s*)?([0-9]{4})(?:\\s*(?:#|x\\.?|ext\\.?|extension)\\s*(\\d+))?$", message = "Format: +1 (nnn) nnn-nnnn")
    private String phone2;

    @PostConstruct
    public void init() {
        // Initialize pull-down menus for state/province, and country
        initDropDowns();
        
        // Load current account's accountView details into this bean
        if (getSessionBean().getLoggedIn()) {
            read();
        }
    }
    
    // The difference between createAccount() and update() is that createAccount
    // does not yet have an accountId associated with it.
    public String createAccount() {
        System.out.println("Creating account object");
        try {
            utx.begin();
            em.joinTransaction();
            
            FacesContext fc = FacesContext.getCurrentInstance();
            Map<String, String> params = fc.getExternalContext().getRequestParameterMap();
            String captchaResponse = params.get("g-recaptcha-response");
            if (!verifyRecaptcha(captchaResponse)) {
              FacesMessage msg = new FacesMessage("Prove you are not a robot. Complete the Captcha");
              msg.setSeverity(FacesMessage.SEVERITY_ERROR);
              fc.addMessage(null, msg);
              return null;
            }
            
            // Encrypt password before storage
            String encryptedPassword = Crypto.encrypt_SHA1(password1);

            System.out.println("email: " + email);
            System.out.println("password: " + password1);
            System.out.println("password (encrypted): " + encryptedPassword);

            Account account = new Account();
            account.setEmail(email);
            account.setPassword(encryptedPassword);

            Date now = new Date();
            account.setDateCreated(now);
            account.setDateUpdated(now);
            account.setAccountState(Account.STATE_NEW);
            account.setAccountRole(Account.ROLE_SUBSCRIBER);
            account.setSessionDuration(Long.valueOf(3600));
            account.setRememberMeFlag(false);

            em.persist(account);

            account.setFirstName(firstname);
            account.setMiddleName(middlename);
            account.setLastName(lastname);
            
            /////////////////////////////
            // Get and set address details
            account.setCompany(company);
            account.setAddress1(address1);
            account.setAddress2(address2);
            account.setCity(city);
            account.setStateprov(stateprov);
            account.setCountry(country);
            account.setPostalCode(postalcode);

            account.setPhone1(phone1);
            account.setPhone2(phone2);

            // Finally, store account in database
            System.out.println("Saving account to database and sending confirmation email");
                        
            em.merge(account);

            utx.commit();
            
            sendRegistrationConfirmationEmail(account);
            
            dblog(Log.Event.CREATE_ACCOUNT, account.getAccountId(), "email:" + account.getEmail());
            
        } catch (Exception e) {
            System.out.println("Error creating new account: " + e.getMessage());
            e.printStackTrace();
        }
        
        return "accountRegistrationCheckEmail.xhtml";
    }
    
    public static final String url = "https://www.google.com/recaptcha/api/siteverify";
	public static final String secret = "6LdTgyYTAAAAAKE3ySA5VunsuWNcu0hx5_647Mkr";
	private final static String USER_AGENT = "Mozilla/5.0";

	public static boolean verifyRecaptcha(String gRecaptchaResponse) throws IOException {
		if (gRecaptchaResponse == null || gRecaptchaResponse.isEmpty()) {
			return false;
		}
		
		try {
          URL obj = new URL(url);
          HttpsURLConnection con = (HttpsURLConnection) obj.openConnection();

          // add reuqest header
          con.setRequestMethod("POST");
          con.setRequestProperty("User-Agent", USER_AGENT);
          con.setRequestProperty("Accept-Language", "en-US,en;q=0.5");

          String postParams = "secret=" + secret + "&response=" + gRecaptchaResponse;

          // Send post request
          con.setDoOutput(true);
          DataOutputStream wr = new DataOutputStream(con.getOutputStream());
          wr.writeBytes(postParams);
          wr.flush();
          wr.close();

          int responseCode = con.getResponseCode();
          System.out.println("\nSending 'POST' request to URL : " + url);
          System.out.println("Post parameters : " + postParams);
          System.out.println("Response Code : " + responseCode);

          BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
          String inputLine;
          StringBuffer response = new StringBuffer();

          while ((inputLine = in.readLine()) != null) {
              response.append(inputLine);
          }
          in.close();

          // print result
          System.out.println(response.toString());

          //parse JSON response and return 'success' value
          JsonReader jsonReader = Json.createReader(new StringReader(response.toString()));
          JsonObject jsonObject = jsonReader.readObject();
          jsonReader.close();

          return jsonObject.getBoolean("success");
		}catch(Exception e){
			e.printStackTrace();
			return false;
		}
	}
    
    public void validateEmail(final FacesContext context, final UIComponent comp, final Object value) throws ValidatorException {
      boolean isValid = false;
      if (value == null) return;
      
      try {
        String email = value.toString();
        // if email !matches regex, throw invalid format message
        Query q = em.createNamedQuery("Account.findByEmail");
        q.setParameter("email", email);
        q.getSingleResult();
      } catch (NoResultException e) {
          isValid = true; // no result means validation proved unique
      }

      if (!isValid) {
          FacesMessage msg = new FacesMessage("Email already exists. Choose another.");
          msg.setSeverity(FacesMessage.SEVERITY_ERROR);
          context.addMessage(comp.getClientId(context), msg);
          throw new ValidatorException(msg);
      }
    }

    public void sendRegistrationConfirmationEmail(Account account) {
        StringBuffer buf = new StringBuffer();
        buf.append("http://portal.thedoorbox.com/accountRegistrationConfirmation.xhtml?");
        buf.append("accountId=").append(account.getAccountId()).append("&");
        buf.append("timestamp=").append(System.currentTimeMillis()).append("&");
        try {
            System.out.println("generated reset url: " + buf.toString());
            String signature = Crypto.calculateRFC2104HMAC(buf.toString(), Security.REGISTRATION_CONFIRMATION_HMAC_SECRET);
            System.out.println("generated signature: " + signature);
            buf.append("hmac=").append(signature);
            String url = buf.toString();
            String htmlBody =
                "Thank you for registering on the DoorBox Administration Portal. YFou are receiving this notification because someone " +
                "registered a new account with your email. If you did not request this account, you can safely ignore this email." +
                "<br/>" +
                "To complete the registration process, please click on the link below" +
                "<br/>" +
                "<br/>" +
                "<a href=\"" + url + "\">Yes, please confirm my account registration.</a>" +
                "<br/>" +
                "<i>Please note: This link will expire in <ul>one hour</ul>.</i>";

            // Send mail and log activity atomically
            Mail.sendEmail(account.getEmail(), "do-not-reply@thedoorbox.com", "Welcome to The Doorbox Portal", htmlBody);
            // TODO: Write log of password reset request
            
        } catch (Exception e) {
            System.out.println("Error sending email: " + e.getMessage());
            e.printStackTrace();
        }
    }        
    
    /**
     * Reads entity values into current bean for display in view
    */
    public void read() {
        Query q = em.createNamedQuery("Account.findByAccountId");
        q.setParameter("accountId", getSessionBean().getAccount().getAccountId());
        
        Account account = (Account) q.getSingleResult();
        
        this.accountId = account.getAccountId();
        this.email = account.getEmail();
        this.password1 = account.getPassword();
        this.password2 = account.getPassword();
        this.firstname = account.getFirstName();
        this.middlename = account.getMiddleName();
        this.lastname = account.getLastName();
        this.company = account.getCompany();
        this.address1 = account.getAddress1();
        this.address2 = account.getAddress2();
        this.city = account.getCity();
        this.stateprov = account.getStateprov();
        this.country = account.getCountry();
        this.postalcode = account.getPostalCode();
        this.phone1 = account.getPhone1();
        this.phone2 = account.getPhone2();
    }
    
    public void update() {
        System.out.println("save");
        try {
            System.out.println("Creating account object");

            // Create new account object. Subordinate objects are
            // created automatically
            utx.begin();

            em.joinTransaction();
            
            // Encrypt password before storage
            String encryptedPassword = Crypto.encrypt_SHA1(password1);

            System.out.println("email: " + email);
            System.out.println("password: " + password1);
            System.out.println("password (encrypted): " + encryptedPassword);

            // FIXME
            // this needs to be by ACCOUNT_ID! ACCOUNT_ID is not presently in
            // the session bean. Don't get it from the form - one can change
            // the form data and overwrite someone else's account that way.
            
            // Get existing account id from the session data
            HttpSession session = SessionUtil.getSession();
            int accountId = (Integer)session.getAttribute("accountId");
            Query query = em.createNamedQuery("Account.findByAccountId");
            query.setParameter("accountId", accountId);
            Account account = (Account) query.getSingleResult();
            
            //Once set, email is read-only
            account.setEmail(email);
            account.setPassword(encryptedPassword);

            Date now = new Date();
            account.setDateUpdated(now);
            account.setAccountState(Account.STATE_NEW);
            account.setSessionDuration(Long.valueOf(3600));
            account.setRememberMeFlag(false);

            em.persist(account);

            account.setFirstName(firstname);
            account.setMiddleName(middlename);
            account.setLastName(lastname);
            
            /////////////////////////////
            // Get and set address details
            account.setCompany(company);
            account.setAddress1(address1);
            account.setAddress2(address2);
            account.setCity(stateprov);
            account.setStateprov(stateprov);
            account.setCountry(country);
            account.setPostalCode(postalcode);

            account.setPhone1(phone1);
            account.setPhone2(phone2);

            // Finally, store account in database
            System.out.println("Saving account to database");
            em.merge(account);

            utx.commit();
            
            dblog(Log.Event.UPDATE_ACCOUNT, account.getAccountId(), "email:" + account.getEmail());

        } catch (Exception e) {
            System.out.println("Error creating new account: " + e.getMessage());
            e.printStackTrace();
        }
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

    /**
     * @return the accountId
     */
    public int getAccountId() {
        return accountId;
    }

    /**
     * @param accountId the accountId to set
     */
    public void setAccountId(int accountId) {
        this.accountId = accountId;
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

    public String getFirstname() {
        return firstname;
    }

    public void setFirstname(String firstname) {
        this.firstname = firstname;
    }

    public String getLastname() {
        return lastname;
    }

    public void setLastname(String lastname) {
        this.lastname = lastname;
    }

    /**
     * @return the middlename
     */
    public String getMiddlename() {
        return middlename;
    }

    /**
     * @param middlename the middlename to set
     */
    public void setMiddlename(String middlename) {
        this.middlename = middlename;
    }

    /**
     * @return the company
     */
    public String getCompany() {
        return company;
    }

    /**
     * @param company the company to set
     */
    public void setCompany(String company) {
        this.company = company;
    }

    /**
     * @return the address1
     */
    public String getAddress1() {
        return address1;
    }

    /**
     * @param address1 the address1 to set
     */
    public void setAddress1(String address1) {
        this.address1 = address1;
    }

    /**
     * @return the address2
     */
    public String getAddress2() {
        return address2;
    }

    /**
     * @param address2 the address2 to set
     */
    public void setAddress2(String address2) {
        this.address2 = address2;
    }

    /**
     * @return the city
     */
    public String getCity() {
        return city;
    }

    /**
     * @param city the city to set
     */
    public void setCity(String city) {
        this.city = city;
    }

    /*
     * This next section is support for both province/state and country selection
     */
    //private Map<String,Map<String,String>> data = new HashMap<String, Map<String,String>>();
    private Map<String,String> countries;
    private Map<String,String> stateprovs;
     
    public void initDropDowns() {
        countries  = new HashMap<String, String>();
        countries.put("CA - Canada", "CA");
        
        stateprovs = new HashMap<String, String>();
        stateprovs.put("ON - Ontario", "ON");
        
        /*Map<String,String> map = new HashMap<String, String>();
        map.put("ON", "Ontario");
        data.put("CA", map);
         
        map = new HashMap<String, String>();
        map.put("NY", "New York");
        data.put("US", map);
        */
    }
 
    /*public Map<String, Map<String, String>> getData() {
        return data;
    }*/

        /**
     * @return the stateprov
     */
    public String getStateprov() {
        return stateprov;
    }

    /**
     * @param stateprov the stateprov to set
     */
    public void setStateprov(String stateprov) {
        this.stateprov = stateprov;
    }

    /**
     * @return the country
     */
    public String getCountry() {
        return country;
    }

    /**
     * @param country the country to set
     */
    public void setCountry(String country) {
        this.country = country;
    }

    public Map<String, String> getCountries() {
        return countries;
    }
 
    public Map<String, String> getStateprovs() {
        return stateprovs;
    }
 
    /*public void onCountryChange() {
        if(country !=null && !country.equals(""))
            stateprovs = data.get(country);
        else
            stateprovs = new HashMap<String, String>();
        displayLocation();
    }*/
     
    public void displayLocation() {
        FacesMessage msg;
        if(stateprov != null && country != null)
            msg = new FacesMessage("Selected", stateprov + " of " + country);
        else
            msg = new FacesMessage(FacesMessage.SEVERITY_ERROR, "Invalid", "Province/state not selected."); 
             
        FacesContext.getCurrentInstance().addMessage(null, msg);  
    }

    
    
    /**
     * Postalcode refers to postal code (canada) or zipcode (USA)
     * @return the postalcode
     */
    public String getPostalcode() {
        return postalcode;
    }

    /**
     * @param postalcode the postalcode to set
     */
    public void setPostalcode(String postalcode) {
        this.postalcode = postalcode;
    }

    /**
     * @return the phone1
     */
    public String getPhone1() {
        return phone1;
    }

    /**
     * @param phone1 the phone1 to set
     */
    public void setPhone1(String phone1) {
        this.phone1 = phone1;
    }

    /**
     * @return the phone2
     */
    public String getPhone2() {
        return phone2;
    }

    /**
     * @param phone2 the phone2 to set
     */
    public void setPhone2(String phone2) {
        this.phone2 = phone2;
    }

    /**
     * @return the password
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
     * @return the accountState
     */
    public int getAccountState() {
        return accountState;
    }

    /**
     * @param accountState the accountState to set
     */
    public void setAccountState(int accountState) {
        this.accountState = accountState;
    }

}
