/**
 * <h1>NewAccountBean</h1>
 * This bean backs the accountRegistration.xhtml page for all new accounts and
 * is specifically used to add new accounts to the database.
 *
 * @author Torin Walker
 * @version 1.0
 * @since 2016-08-03
 */
package doorbox.portal.beans;

import doorbox.portal.entity.Account;
import doorbox.portal.entity.PPSubscription;
import doorbox.portal.utils.Crypto;
import java.net.URLDecoder;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import javax.annotation.PostConstruct;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.validator.ValidatorException;
import javax.persistence.NoResultException;
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
@SessionScoped
@ManagedBean
public class NewSubscriberBean extends BaseBean {

    //private Map<String,Map<String,String>> data = new HashMap<String, Map<String,String>>();
    private Map<String, String> countries;
    private Map<String, String> stateprovs;

    private String hostedButtonId;
    private String buttonImage;
    private String installFee;
    private String monthlyFee;
    
    private String transactionId;
    private String transactionState;
    private Double transactionAmt;
    private String transactionItemNumber;
    private String transactionCM;
    private String transactionCurrency;
    private Date transactionDate;

    /**
     * @return the transactionId
     */
    public String getTransactionId() {
        return transactionId;
    }

    /**
     * @param transactionId the transactionId to set
     */
    public void setTransactionId(String transactionId) {
        this.transactionId = transactionId;
    }

    /**
     * @return the transactionState
     */
    public String getTransactionState() {
        return transactionState;
    }

    /**
     * @param transactionState the transactionState to set
     */
    public void setTransactionState(String transactionState) {
        this.transactionState = transactionState;
    }

    /**
     * @return the buttonImage
     */
    public String getButtonImage() {
        return buttonImage;
    }

    /**
     * @param buttonImage the buttonImage to set
     */
    public void setButtonImage(String buttonImage) {
        this.buttonImage = buttonImage;
    }

    /**
     * @return the transactionAmt
     */
    public Double getTransactionAmt() {
        return transactionAmt;
    }

    /**
     * @param transactionAmt the transactionAmt to set
     */
    public void setTransactionAmt(Double transactionAmt) {
        this.transactionAmt = transactionAmt;
    }

    /**
     * @return the transactionItemNumber
     */
    public String getTransactionItemNumber() {
        return transactionItemNumber;
    }

    /**
     * @param transactionItemNumber the transactionItemNumber to set
     */
    public void setTransactionItemNumber(String transactionItemNumber) {
        this.transactionItemNumber = transactionItemNumber;
    }

    /**
     * @return the transactionDate
     */
    public Date getTransactionDate() {
        return transactionDate;
    }

    /**
     * @param transactionDate the transactionDate to set
     */
    public void setTransactionDate(Date transactionDate) {
        this.transactionDate = transactionDate;
    }

    /**
     * @return the transactionCM
     */
    public String getTransactionCM() {
        return transactionCM;
    }

    /**
     * @param transactionCM the transactionCM to set
     */
    public void setTransactionCM(String transactionCM) {
        this.transactionCM = transactionCM;
    }

    /**
     * @return the transactionCurrency
     */
    public String getTransactionCurrency() {
        return transactionCurrency;
    }

    /**
     * @param transactionCurrency the transactionCurrency to set
     */
    public void setTransactionCurrency(String transactionCurrency) {
        this.transactionCurrency = transactionCurrency;
    }

    /**
     * @return the monthlyFee
     */
    public String getMonthlyFee() {
        return monthlyFee;
    }

    /**
     * @param monthlyFee the monthlyFee to set
     */
    public void setMonthlyFee(String monthlyFee) {
        this.monthlyFee = monthlyFee;
    }

    /**
     * @return the installFee
     */
    public String getInstallFee() {
        return installFee;
    }

    /**
     * @param installFee the installFee to set
     */
    public void setInstallFee(String installFee) {
        this.installFee = installFee;
    }

    public enum Action {
        CREATE_ACCOUNT
    }

    @PostConstruct
    public void init() {
        hostedButtonId = FacesContext.getCurrentInstance().getExternalContext().getRequestParameterMap().get("hosted_button_id");
        setButtonImage(FacesContext.getCurrentInstance().getExternalContext().getRequestParameterMap().get("button_image"));
        System.out.println("Hosted button ID: " + hostedButtonId);
        System.out.println("Button image: " + getButtonImage());

        countries = new HashMap<String, String>();
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

    @Pattern(regexp = "(([a-zA-Z][0-9][a-zA-Z] *[0-9][a-zA-Z][0-9])|([0-9]{5}(-[0-9]{4,5})?))", message = "Canada: A1A 2B2 OR US: 12345 or 12345-67890")
    private String postalcode;

    @Pattern(regexp = "^(?:(?:\\+?1\\s*(?:[.-]\\s*)?)?(?:\\(\\s*([2-9]1[02-9]|[2-9][02-8]1|[2-9][02-8][02-9])\\s*\\)|([2-9]1[02-9]|[2-9][02-8]1|[2-9][02-8][02-9]))\\s*(?:[.-]\\s*)?)?([2-9]1[02-9]|[2-9][02-9]1|[2-9][02-9]{2})\\s*(?:[.-]\\s*)?([0-9]{4})(?:\\s*(?:#|x\\.?|ext\\.?|extension)\\s*(\\d+))?$", message = "Format: +1 (nnn) nnn-nnnn")
    private String phone1;

    @Pattern(regexp = "^(?:(?:\\+?1\\s*(?:[.-]\\s*)?)?(?:\\(\\s*([2-9]1[02-9]|[2-9][02-8]1|[2-9][02-8][02-9])\\s*\\)|([2-9]1[02-9]|[2-9][02-8]1|[2-9][02-8][02-9]))\\s*(?:[.-]\\s*)?)?([2-9]1[02-9]|[2-9][02-9]1|[2-9][02-9]{2})\\s*(?:[.-]\\s*)?([0-9]{4})(?:\\s*(?:#|x\\.?|ext\\.?|extension)\\s*(\\d+))?$", message = "Format: +1 (nnn) nnn-nnnn")
    private String phone2;

    public String saveAndContinue() {
        System.out.println("save and continue");
        try {
            System.out.println("Creating temporary newSubscriber object");

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

            account.setFirstName(firstname);
            account.setMiddleName(middlename);
            account.setLastName(lastname);

            account.setCompany(company);
            account.setAddress1(address1);
            account.setAddress2(address2);
            account.setCity(city);
            account.setStateprov(stateprov);
            account.setCountry(country);
            account.setPostalCode(postalcode);

            account.setPhone1(phone1);
            account.setPhone2(phone2);

            utx.begin();
            em.joinTransaction();
            em.persist(account);            
            getSessionBean().setNewAccount(account);
            utx.commit();

            return "subscription_registration_step2";

        } catch (Exception e) {
            System.out.println("Error creating new account: " + e.getMessage());
            e.printStackTrace();
        }
        return null;
    }

    public String activateAccount() {
        try {
            System.out.println("paypal return values: tx=" + FacesContext.getCurrentInstance().getExternalContext().getRequestParameterMap().get("tx") +
                    ", st=" + FacesContext.getCurrentInstance().getExternalContext().getRequestParameterMap().get("st") +
                    ", amt=" + FacesContext.getCurrentInstance().getExternalContext().getRequestParameterMap().get("amt") +
                    ", amt (decoded)=" + Double.valueOf(URLDecoder.decode(FacesContext.getCurrentInstance().getExternalContext().getRequestParameterMap().get("amt"),"UTF-8")) +
                    ", cc=" + FacesContext.getCurrentInstance().getExternalContext().getRequestParameterMap().get("cc") +
                    ", cm=" + FacesContext.getCurrentInstance().getExternalContext().getRequestParameterMap().get("cm") +
                    ", cm (decoded)=" + URLDecoder.decode(FacesContext.getCurrentInstance().getExternalContext().getRequestParameterMap().get("cm"), "UTF-8"));
            setTransactionId(FacesContext.getCurrentInstance().getExternalContext().getRequestParameterMap().get("tx"));
            setTransactionState(FacesContext.getCurrentInstance().getExternalContext().getRequestParameterMap().get("st"));
            setTransactionAmt(Double.valueOf(URLDecoder.decode(FacesContext.getCurrentInstance().getExternalContext().getRequestParameterMap().get("amt"),"UTF-8")));
            setTransactionCurrency(FacesContext.getCurrentInstance().getExternalContext().getRequestParameterMap().get("cc"));
            setTransactionCM(URLDecoder.decode(FacesContext.getCurrentInstance().getExternalContext().getRequestParameterMap().get("cm"), "UTF-8"));

            utx.begin();
            em.joinTransaction();

            Account account = getSessionBean().getNewAccount();
            account.setAccountState(Account.STATE_ACTIVE);
            account = em.merge(account);
            
            PPSubscription tx = new PPSubscription();
            tx.setAccountId(account.getAccountId());
            tx.setTxId(transactionId);
            tx.setTxState(transactionState);
            tx.setTxDate(new Date());
            tx.setTxAmt(transactionAmt);
            tx.setTxCurrency(transactionCurrency);
            tx.setTxCM(transactionCM);
            tx.setTxItemNumber(transactionItemNumber);
            
            em.persist(tx);
            utx.commit();

            return "subscription_nextStep.xhtml";

        } catch (Exception e) {
            System.out.println("Failed to activate account");
        }

        return "subscription_accountCreationProblem.xhtml";
    }

    public void validateEmail(final FacesContext context, final UIComponent comp, final Object value) throws ValidatorException {
        boolean isValid = false;
        if (value == null) {
            return;
        }

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

    public void validatePasswords(final FacesContext context, final UIComponent comp, final Object value) throws ValidatorException {
        String password2 = (String)value;
        System.out.println("password validation");
        System.out.println("password1: " + password1);
        System.out.println("password2: " + password2);        
        
        // Let required="true" do its job.
        if (password1.isEmpty() || password2.isEmpty()) {
            System.out.println("One, the other, or possibly both are empty");
            return;
        }

        if (!password1.equals(password2)) {
            System.out.println("Passwords are not equal");
            FacesMessage msg = new FacesMessage("Passwords must match");
            msg.setSeverity(FacesMessage.SEVERITY_ERROR);
            context.addMessage("password2", msg);
            context.renderResponse();
        }
    }
    
    /**
     * @return the hostedButtonId
     */
    public String getHostedButtonId() {
        return hostedButtonId;
    }

    /**
     * @param hostedButtonId the hostedButtonId to set
     */
    public void setHostedButtonId(String hostedButtonId) {
        this.hostedButtonId = hostedButtonId;
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
        if (stateprov != null && country != null) {
            msg = new FacesMessage("Selected", stateprov + " of " + country);
        } else {
            msg = new FacesMessage(FacesMessage.SEVERITY_ERROR, "Invalid", "Province/state not selected.");
        }

        FacesContext.getCurrentInstance().addMessage(null, msg);
    }

    /**
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
