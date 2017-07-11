/**
* <h1>Account</h1>
* This entity bean backs the Account database table and all the associated
* values and their constants.
*
* @author  Torin Walker
* @version 1.0
* @since   2016-08-03
*/
package doorbox.portal.entity;

import java.io.Serializable;
import java.util.Date;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;
import javax.persistence.TableGenerator;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

/**
 *
 * @author torinw
 */
@Entity(name = "Account")
@Table(name = "account")
@NamedQueries({
    @NamedQuery(name = "Account.numAccounts", query = "SELECT COUNT(a) FROM Account a"),
    @NamedQuery(name = "Account.findByAccountId", query = "SELECT a FROM Account a WHERE a.accountId = :accountId"),
    @NamedQuery(name = "Account.findByAccountState", query = "SELECT a FROM Account a WHERE a.accountState = :accountState"),
    @NamedQuery(name = "Account.findByEmail", query = "SELECT a FROM Account a WHERE a.email = :email"),
    @NamedQuery(name = "Account.findByDateCreated", query = "SELECT a FROM Account a WHERE a.dateCreated = :dateCreated"),
    @NamedQuery(name = "Account.findByDateUpdated", query = "SELECT a FROM Account a WHERE a.dateUpdated = :dateUpdated"),
    @NamedQuery(name = "Account.findBySessionDuration", query = "SELECT a FROM Account a WHERE a.sessionDuration = :sessionDuration"),
    @NamedQuery(name = "Account.findBySessionByRememberMeFlag", query = "SELECT a FROM Account a WHERE a.rememberMeFlag = :flagValue"),
    @NamedQuery(name = "Account.findByFirstName", query = "SELECT a FROM Account a WHERE a.firstName = :firstName"),
    @NamedQuery(name = "Account.findByMiddleName", query = "SELECT a FROM Account a WHERE a.middleName = :middleName"),
    @NamedQuery(name = "Account.findByLastName", query = "SELECT a FROM Account a WHERE a.lastName = :lastName"),
    @NamedQuery(name = "Account.findByAddress1", query = "SELECT a FROM Account a WHERE a.address1 = :address1"),
    @NamedQuery(name = "Account.findByAddress2", query = "SELECT a FROM Account a WHERE a.address2 = :address2"),
    @NamedQuery(name = "Account.findByCity", query = "SELECT a FROM Account a WHERE a.city = :city"),
    @NamedQuery(name = "Account.findByStateProv", query = "SELECT a FROM Account a WHERE a.stateprov = :stateprov"),
    @NamedQuery(name = "Account.findByCountry", query = "SELECT a FROM Account a WHERE a.country = :country"),
    @NamedQuery(name = "Account.findByPostalcode", query = "SELECT a FROM Account a WHERE a.postalCode = :postalcode"),
    @NamedQuery(name = "Account.findByPhone1", query = "SELECT a FROM Account a WHERE a.phone1 = :phone1"),
    @NamedQuery(name = "Account.findByPhone2", query = "SELECT a FROM Account a WHERE a.phone2 = :phone2")
})
public class Account implements Serializable {
    private static final long serialVersionUID = 1L;

    /**
     * Account State - New but registration hasn't been confirmed
     */    
    public static final int STATE_NEW = 0;

    /**
    * Account State - Registration confirmed and account is active
     */    
    public static final int STATE_ACTIVE = 1;

    /**
    * Account State - Account suspended for administrative purposes
     */    
    public static final int STATE_SUSPENDED = 2;

    /**
    * Account State - Account moved into DELETED state (requires a final purge to deallocate unique keys)
     */    
    public static final int STATE_DELETED = 3;

    // Session Type
    public static final int SESSION_TRANSIENT = 0;
    public static final int SESSION_PERSISTENT = 1;

    public static final int ROLE_ADMIN = 9;
    public static final int ROLE_SUBSCRIBER = 5;
    public static final int ROLE_USER = 4;
    public static final int ROLE_COURIER = 3;
    public static final int ROLE_GUEST = 0;
    
    @Id
    @GeneratedValue(generator="account_id_gen")
    @TableGenerator(name="account_id_gen", pkColumnValue="account_id_seq", initialValue=1000, table="sequence", pkColumnName="seq_name", valueColumnName="seq_value")
    @Column(name = "account_id", nullable = false)
    private Integer accountId;

    @Column(name = "account_state")
    private Integer accountState;

    @Column(name = "role")
    private Integer accountRole;
    
    @Column(name = "date_created")
    @Temporal(TemporalType.TIMESTAMP)
    private Date dateCreated;

    @Column(name = "date_updated")
    @Temporal(TemporalType.TIMESTAMP)
    private Date dateUpdated;

    @Column(name = "email", unique = true, nullable = false, length = 64)
    private String email;

    @Column(name = "password", nullable = false, length = 32)
    private String password;

    @Column(name = "remember_me", nullable = false)
    private Boolean rememberMeFlag = Boolean.FALSE;

    @Column(name = "session_duration")
    private Long sessionDuration;

    @Column(name = "first_name", unique = false, nullable = false, length = 32)
    private String firstName;

    @Column(name = "last_name", unique = false, nullable = true, length = 32)
    private String lastName;

    @Column(name = "middle_name", unique = false, nullable = false, length = 32)
    private String middleName;

    @Column(name = "company", unique = false, nullable = true, length = 48)
    private String company;

    @Column(name = "address_1", unique = false, nullable = false, length = 64)
    private String address1;

    @Column(name = "address_2", unique = false, nullable = true, length = 64)
    private String address2;

    @Column(name = "city", nullable = true, length = 32)
    private String city;

    @Column(name = "stateprov", nullable = true, length = 2)
    private String stateprov;

    @Column(name = "country", nullable = true, length = 2)
    private String country;

    @Column(name = "postalcode", nullable = true, length = 16)
    private String postalCode;

    @Column(name = "phone_1", nullable = true, length = 32)
    private String phone1;

    @Column(name = "phone_2", nullable = true, length = 32)
    private String phone2;

    public Integer getAccountId() {
        return accountId;
    }

    public void setAccountId(Integer accountId) {
        this.accountId = accountId;
    }

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

    public Date getDateCreated() {
        return dateCreated;
    }

    public void setDateCreated(Date dateCreated) {
        this.dateCreated = dateCreated;
    }

    public Date getDateUpdated() {
        return dateUpdated;
    }

    public void setDateUpdated(Date dateUpdated) {
        this.dateUpdated = dateUpdated;
    }

    public Integer getAccountState() {
        return accountState;
    }

    public void setAccountState(Integer accountState) {
        this.accountState = accountState;
    }

    public Long getSessionDuration() {
        return sessionDuration;
    }

    public void setSessionDuration(Long sessionDuration) {
        this.sessionDuration = sessionDuration;
    }

    public String getAddress1() {
        return address1;
    }

    public void setAddress1(String address1) {
        this.address1 = address1;
    }

    public String getAddress2() {
        return address2;
    }

    public void setAddress2(String address2) {
        this.address2 = address2;
    }


    @Override
    public int hashCode() {
        int hash = 0;
        hash += (accountId != null ? accountId.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the accountId fields are not set
        if (!(object instanceof Account)) {
            return false;
        }
        Account other = (Account) object;
        if ((this.accountId == null && other.accountId != null) || (this.accountId != null && !this.accountId.equals(other.accountId))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "entity.account.Account[accountId=" + accountId + "]";
    }

    public Boolean getRememberMeFlag() {
        if (rememberMeFlag == null) return Boolean.FALSE;
        return rememberMeFlag;
    }

    public void setRememberMeFlag(Boolean rememberMeFlag) {
        this.rememberMeFlag = rememberMeFlag;
    }

    /**
     * @return the firstName
     */
    public String getFirstName() {
        return firstName;
    }

    /**
     * @param firstName the firstName to set
     */
    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    /**
     * @return the lastName
     */
    public String getLastName() {
        return lastName;
    }

    /**
     * @param lastName the lastName to set
     */
    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    /**
     * @return the middleName
     */
    public String getMiddleName() {
        return middleName;
    }

    /**
     * @param middleName the middleName to set
     */
    public void setMiddleName(String middleName) {
        this.middleName = middleName;
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

    /**
     * @return the postalCode
     */
    public String getPostalCode() {
        return postalCode;
    }

    /**
     * @param postalCode the postalCode to set
     */
    public void setPostalCode(String postalCode) {
        this.postalCode = postalCode;
    }

    public String getPhone1() {
        return this.phone1;
    }

    public void setPhone1(String phone1) {
        this.phone1 = phone1;
    }

    public String getPhone2() {
        return this.phone2;
    }

    public void setPhone2(String phone2) {
        this.phone2 = phone2;
    }

    /**
     * @return the accountRole
     */
    public Integer getAccountRole() {
        return accountRole;
    }

    /**
     * @param accountRole the accountRole to set
     */
    public void setAccountRole(Integer accountRole) {
        this.accountRole = accountRole;
    }

}
