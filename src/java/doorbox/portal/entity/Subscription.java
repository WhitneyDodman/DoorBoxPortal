/**
* <h1>Subscription</h1>
* This entity bean backs the SUBSCRIPTION database table, its columns and
* all associated constants.
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
@Entity(name = "Subscription")
@Table(name = "subscription")
@NamedQueries({
    @NamedQuery(name = "Subscription.numSubscriptions", query = "SELECT COUNT(a) FROM Subscription a"),
    @NamedQuery(name = "Subscription.findBySubscriptionId", query = "SELECT a FROM Subscription a WHERE a.subscriptionId = :subscriptionId"),
    @NamedQuery(name = "Subscription.findBySubscriptionState", query = "SELECT a FROM Subscription a WHERE a.subscriptionState = :subscriptionState"),
    @NamedQuery(name = "Subscription.findByAccountId", query = "SELECT a FROM Subscription a WHERE a.accountId = :accountId")
})
public class Subscription implements Serializable {
    private static final long serialVersionUID = 1L;
    
    // Subscription State
    // New account, but no subscription, users or doorboxes added. Effectively useless.
    public static final int STATE_NEW = 0;
    
    // Account has paid (and verified subscription, active users, and one or more doorboxes.
    public static final int ACTIVE = 1;
    
    // Account locked for administrative reasons - non payment, abuse, etc.
    public static final int SUSPENDED = 2;
    
    // Account is actually not deleted, but in a state of pending deletion meaning at some point
    // we will purge or archive the account details.
    public static final int DELETED = 3;
        
    @Id
    @GeneratedValue(generator="subscription_id_gen")
    @TableGenerator(name="subscription_id_gen", pkColumnValue="subscription_id_seq", initialValue=1000, table="sequence", pkColumnName="seq_name", valueColumnName="seq_value")
    @Column(name = "subscription_id", nullable = false)
    private Integer subscriptionId;

    @Column(name = "account_id", nullable = false)
    private Integer accountId;
    
    @Column(name = "subscription_state")
    private Integer subscriptionState;
    
    @Column(name = "start_date")
    @Temporal(TemporalType.TIMESTAMP)
    private Date startDate;
    
    @Column(name = "end_date")
    @Temporal(TemporalType.TIMESTAMP)
    private Date endDate;

    @Column(name = "subscription_type")
    private Integer subscriptionType;

    @Column(name = "payment_period")
    private Integer paymentPeriod;
    
    @Column(name = "payment_type")
    private Integer paymentType;
    
    public Integer getSubscriptionId() {
        return getAccountId();
    }

    public void setSubscriptionId(Integer accountId) {
        this.setAccountId(accountId);
    }

   

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (subscriptionId != null ? subscriptionId.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the accountId fields are not set
        if (!(object instanceof Subscription)) {
            return false;
        }
        Subscription other = (Subscription) object;
        if ((this.subscriptionId == null && other.subscriptionId != null) || (this.subscriptionId != null && !this.subscriptionId.equals(other.subscriptionId))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "entity.account.Subscription[subscriptionId=" + subscriptionId + "]";
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
     * @return the accountState
     */
    public Integer getSubscriptionState() {
        return subscriptionState;
    }

    /**
     * @param subscriptionState the accountState to set
     */
    public void setSubscriptionState(Integer subscriptionState) {
        this.subscriptionState = subscriptionState;
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

    /**
     * @return the endDate
     */
    public Date getEndDate() {
        return endDate;
    }

    /**
     * @param endDate the endDate to set
     */
    public void setEndDate(Date endDate) {
        this.endDate = endDate;
    }

    /**
     * @return the subscriptionType
     */
    public Integer getSubscriptionType() {
        return subscriptionType;
    }

    /**
     * @param subscriptionType the subscriptionType to set
     */
    public void setSubscriptionType(Integer subscriptionType) {
        this.subscriptionType = subscriptionType;
    }

    /**
     * @return the paymentPeriod
     */
    public Integer getPaymentPeriod() {
        return paymentPeriod;
    }

    /**
     * @param paymentPeriod the paymentPeriod to set
     */
    public void setPaymentPeriod(Integer paymentPeriod) {
        this.paymentPeriod = paymentPeriod;
    }

    /**
     * @return the paymentType
     */
    public Integer getPaymentType() {
        return paymentType;
    }

    /**
     * @param paymentType the paymentType to set
     */
    public void setPaymentType(Integer paymentType) {
        this.paymentType = paymentType;
    }
      
}
