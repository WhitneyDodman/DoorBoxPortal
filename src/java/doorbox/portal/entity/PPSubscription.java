/**
* <h1>PPSubscription</h1>
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
@Entity(name = "PPSubscription")
@Table(name = "pp_subscription")
@NamedQueries({
    @NamedQuery(name = "PPSubscription.numPPSubscriptions", query = "SELECT COUNT(a) FROM PPSubscription a"),
    @NamedQuery(name = "PPSubscription.findByPPSubscriptionId", query = "SELECT a FROM PPSubscription a WHERE a.txId = :txId"),
    @NamedQuery(name = "PPSubscription.findByPPSubscriptionState", query = "SELECT a FROM PPSubscription a WHERE a.txState = :txState"),
    @NamedQuery(name = "PPSubscription.findByAccountId_sortByDateDesc", query = "SELECT a FROM PPSubscription a WHERE a.accountId = :accountId ORDER BY a.txDate DESC "),
    @NamedQuery(name = "PPSubscription.findByAccountId", query = "SELECT a FROM PPSubscription a WHERE a.accountId = :accountId")
})
public class PPSubscription implements Serializable {
    private static final long serialVersionUID = 1L;        
        
    @Column(name = "ACCOUNT_ID", nullable = false)
    private Integer accountId;
    
    @Id
    @Column(name = "TX_ID", nullable = false, length = 32)
    private String txId;

    @Column(name = "TX_STATE", length = 32)
    private String txState;
    
    @Column(name = "TX_DATE")
    @Temporal(TemporalType.DATE)
    private Date txDate;
    
    @Column(name = "TX_AMT")
    private Double txAmt;

    @Column(name = "TX_ITEM_NUMBER", length = 32)
    private String txItemNumber;
    
    @Column(name = "TX_CC", length = 32)
    private String txCurrency;

    @Column(name = "TX_CM", length = 32)
    private String txCM;
    
    @Override
    public int hashCode() {
        int hash = 0;
        hash += (getTxId() != null ? getTxId().hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the accountId fields are not set
        if (!(object instanceof PPSubscription)) {
            return false;
        }
        PPSubscription other = (PPSubscription) object;
        if ((this.getTxId() == null && other.getTxId() != null) || (this.getTxId() != null && !this.txId.equals(other.txId))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "entity.PPSubscription[txId=" + getTxId() + "]";
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
