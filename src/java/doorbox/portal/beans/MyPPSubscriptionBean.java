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

import doorbox.portal.entity.Log;
import doorbox.portal.entity.PPSubscription;
import java.util.Date;
import java.util.List;
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
public class MyPPSubscriptionBean extends BaseBean {
    private List<PPSubscription> transactions;
    
    @NotNull(message = "Required")
    private Integer accountId;

    @NotNull(message = "Required")
    private String txId;

    @NotNull(message = "Required")
    private String txState;
    
    @NotNull(message = "Required")
    private Date txDate;

    @NotNull(message = "Required")
    private String txItemNumber;

    @NotNull(message = "Required")
    private Double txAmt;

    @NotNull(message = "Required")
    private String txCurrency;

    @NotNull(message = "Required")
    private String txCM;

    @PostConstruct
    public void init() {
        // Load current account's accountView details into this bean
        //read();
    }
    /**
     * Reads entity values into current bean for display in view
    */
    public void read() {
        Query q = em.createNamedQuery("PPSubscription.findByAccountId");
        q.setParameter("accountId", getSessionBean().getAccount().getAccountId());
        
        try {
            PPSubscription s = (PPSubscription) q.getSingleResult();
            System.out.println("Found account " + s.getAccountId());
            this.setTxId(s.getTxId());
            this.setTxState(s.getTxState());
            this.setAccountId(s.getAccountId());
            this.setTxDate(s.getTxDate());
            this.setTxItemNumber(s.getTxItemNumber());
            this.setTxAmt(s.getTxAmt());
            this.setTxCurrency(s.getTxCurrency());
            this.setTxCM(s.getTxCM());
        } catch (NoResultException e) {
            System.out.println("No subscription found for account id: " + getSessionBean().getAccount().getAccountId());
        } catch (Exception e) {
            System.out.println("Other error while retrieving subscription details for account id: " + getSessionBean().getAccount().getAccountId());
        }
    }
    
    /**
     * Reads entity values into current bean for display in view
    */
    public List getTransactions() {
        Query q = em.createNamedQuery("PPSubscription.findByAccountId_sortByDateDesc");
        int accountId = getSessionBean().getAccount().getAccountId();
        q.setParameter("accountId", accountId);
        
        transactions = (List<PPSubscription>) q.getResultList();
        return transactions;        
    }
    
    public String clearLogs() {
        try {
            utx.begin();
        
            Query q = em.createNamedQuery("PPSubscription.findByAccountId");
            int accountId = getSessionBean().getAccount().getAccountId();
            q.setParameter("accountId", accountId);

            Query query = em.createQuery("DELETE FROM PPSubscription l WHERE l.accountId = :accountId");
            query.setParameter("accountId", accountId).executeUpdate();

            utx.commit();
        } catch (Exception e) {
            System.out.println("Error clearing transactions: " + e.getMessage());
        }
        return "mySubscription.xhtml";
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
     * @return the txId
     */
    public String getTxId() {
        return txId;
    }

    /**
     * @param txId the txId to set
     */
    public void setTxId(String txId) {
        this.txId = txId;
    }

    /**
     * @return the txState
     */
    public String getTxState() {
        return txState;
    }

    /**
     * @param txState the txState to set
     */
    public void setTxState(String txState) {
        this.txState = txState;
    }

    /**
     * @return the txDate
     */
    public Date getTxDate() {
        return txDate;
    }

    /**
     * @param txDate the txDate to set
     */
    public void setTxDate(Date txDate) {
        this.txDate = txDate;
    }

    /**
     * @return the txItemNumber
     */
    public String getTxItemNumber() {
        return txItemNumber;
    }

    /**
     * @param txItemNumber the txItemNumber to set
     */
    public void setTxItemNumber(String txItemNumber) {
        this.txItemNumber = txItemNumber;
    }

    /**
     * @return the txAmt
     */
    public Double getTxAmt() {
        return txAmt;
    }

    /**
     * @param txAmt the txAmt to set
     */
    public void setTxAmt(Double txAmt) {
        this.txAmt = txAmt;
    }

    /**
     * @return the txCurrency
     */
    public String getTxCurrency() {
        return txCurrency;
    }

    /**
     * @param txCurrency the txCurrency to set
     */
    public void setTxCurrency(String txCurrency) {
        this.txCurrency = txCurrency;
    }

    /**
     * @return the txCM
     */
    public String getTxCM() {
        return txCM;
    }

    /**
     * @param txCM the txCM to set
     */
    public void setTxCM(String txCM) {
        this.txCM = txCM;
    }

}
