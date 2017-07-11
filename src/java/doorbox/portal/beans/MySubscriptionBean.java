/**
* <h1>MySubscriptionBean</h1>
* This bean backs the mySubscription.xhtml view under the My Account
* subsection of the website. mySubscription is used to view read-only
* aspects of the user's subscription theoretically obtainable from
* Shopify. In future phases when we take on billing directly, this
* view will become editable.
*
* @author  Torin Walker
* @version 1.0
* @since   2016-08-03
*/
package doorbox.portal.beans;

import doorbox.portal.entity.Subscription;
import java.util.Date;
import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
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
@ManagedBean
public class MySubscriptionBean extends BaseBean {
    @NotNull(message = "Required")
    private Integer subscriptionId;

    @NotNull(message = "Required")
    private Integer subscriptionType;

    @NotNull(message = "Required")
    private Integer subscriptionState;
    
    @NotNull(message = "Required")
    private Integer accountId;

    @NotNull(message = "Required")
    private Date startDate;

    @NotNull(message = "Required")
    private Date endDate;
    
    @NotNull(message = "Required")
    private Integer paymentPeriod;

    @NotNull(message = "Required")
    private Integer paymentType;

    @PostConstruct
    public void init() {
        // Load current account's accountView details into this bean
        read();
    }
    /**
     * Reads entity values into current bean for display in view
    */
    public void read() {
        Query q = em.createNamedQuery("Subscription.findByAccountId");
        q.setParameter("accountId", getSessionBean().getAccount().getAccountId());
        
        try {
            Subscription subscription = (Subscription) q.getSingleResult();
            System.out.println("Found account " + subscription.getAccountId());
            this.subscriptionId = subscription.getSubscriptionId();
            this.subscriptionType = subscription.getSubscriptionType();
            this.setSubscriptionState(subscription.getSubscriptionState());
            this.accountId = subscription.getAccountId();
            this.startDate = subscription.getStartDate();
            this.endDate = subscription.getEndDate();
            this.paymentPeriod = subscription.getPaymentPeriod();
            this.paymentType = subscription.getPaymentType();
        } catch (NoResultException e) {
            System.out.println("No subscription found for account id: " + getSessionBean().getAccount().getAccountId());
        } catch (Exception e) {
            System.out.println("Other error while retrieving subscription details for account id: " + getSessionBean().getAccount().getAccountId());
        }
    }
    
    /**
     * @return the subscriptionId
     */
    public Integer getSubscriptionId() {
        return subscriptionId;
    }

    /**
     * @param subscriptionId the subscriptionId to set
     */
    public void setSubscriptionId(Integer subscriptionId) {
        this.subscriptionId = subscriptionId;
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
        this.setAccountId(accountId);
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
     * @return the paymentPeriod
     */
    public int getPaymentPeriod() {
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

    /**
     * @return the subscriptionState
     */
    public Integer getSubscriptionState() {
        return subscriptionState;
    }

    /**
     * @param subscriptionState the subscriptionState to set
     */
    public void setSubscriptionState(Integer subscriptionState) {
        this.subscriptionState = subscriptionState;
    }

}
