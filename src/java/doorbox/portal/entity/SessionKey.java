/**
* <h1>SessionKey</h1>
* This bean is legacy code brought from COTH and is not used.
* This entity will be deleted soon, but I thought I'd document
* it anyway.
*
* @author  Torin Walker
* @version 1.0
* @since   2016-08-03
*/
package doorbox.portal.entity;

import java.io.Serializable;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;

/**
 *
 * @author torinw
 */
@Entity(name="SessionKey")
@Table(name="coth_session_key")
@NamedQueries({
    @NamedQuery(name = "SessionKey.findSessionKeyBySessionId", query = "SELECT s FROM SessionKey s WHERE s.sessionId = :sessionId"),
    @NamedQuery(name = "SessionKey.deleteSessionKeyBySessionId", query = "DELETE FROM SessionKey s WHERE s.sessionId = :sessionId")
})
public class SessionKey implements Serializable {
    private static final long serialVersionUID = 1L;
    @Id
    @Column(name = "session_id", nullable = false, length = 40)
    private String sessionId;
    
    @Column(name = "account_id", nullable = false)
    private Integer accountId;

    @Column(name = "ip_address", nullable = false)
    private String ipAddress;
    
//    @Column(name = "EXPIRY_DATE", nullable = false)
//    @Temporal(TemporalType.TIMESTAMP)
//    private Date expiryDate;
//    
    public SessionKey() {
    }
    
    //public SessionKey(String sessionId, Long accountId, String ipAddress, Date expiryDate) {
    public SessionKey(String sessionId, Integer accountId, String ipAddress) {        
        this.sessionId = sessionId;
        this.accountId = accountId;
        this.ipAddress = ipAddress;
        //this.expiryDate = expiryDate;
    }
    
    public String getSessionId() {
        return sessionId;
    }

    public void setSessionId(String sessionId) {
        this.sessionId = sessionId;
    }

    public Integer getAccountId() {
        return accountId;
    }

    public void setAccountId(Integer accountId) {
        this.accountId = accountId;
    }

    public String getIpAddress() {
        return ipAddress;
    }

    public void setIpAddress(String ipAddress) {
        this.ipAddress = ipAddress;
    }
//
//    public Date getExpiryDate() {
//        return expiryDate;
//    }
//
//    public void setExpiryDate(Date expiryDate) {
//        this.expiryDate = expiryDate;
//    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (sessionId != null ? sessionId.hashCode() : 0);
        hash += (accountId != null ? accountId.hashCode() : 0);
        hash += (ipAddress != null ? ipAddress.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof SessionKey)) {
            return false;
        }
        SessionKey other = (SessionKey) object;
        if ((this.sessionId == null && other.sessionId != null) || (this.sessionId != null && !this.sessionId.equals(other.sessionId))) {
            return false;
        }
        if ((this.accountId == null && other.accountId != null) || (this.accountId != null && !this.accountId.equals(other.accountId))) {
            return false;
        }
        if ((this.ipAddress == null && other.ipAddress != null) || (this.ipAddress != null && !this.ipAddress.equals(other.ipAddress))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "entity.account.Session[sessionId=" + sessionId + ", accountId=" + accountId + ", ipAddress=" + ipAddress + "]";
    }
    
}
