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

import doorbox.portal.entity.Account;
import doorbox.portal.entity.Log;
import java.util.Date;
import java.util.List;
import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import javax.transaction.NotSupportedException;

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
public class LogBean extends BaseBean {
    private List<Log> logs;
    
    public LogBean() {
    }
        
    /**
     * Reads entity values into current bean for display in view
    */
    public List getLogs() {
        Query q = em.createNamedQuery("Log.findByAccountId");
        int accountId = getSessionBean().getAccount().getAccountId();
        q.setParameter("accountId", accountId);
        
        List<Log> list = (List<Log>) q.getResultList();        
        logs = list;

        return logs;        
    }
    
    public String clearLogs() {
        try {
            utx.begin();
        
            Query q = em.createNamedQuery("Log.findByAccountId");
            int accountId = getSessionBean().getAccount().getAccountId();
            q.setParameter("accountId", accountId);

            Query query = em.createQuery("DELETE FROM Log l WHERE l.accountId = :accountId");
            query.setParameter("accountId", accountId).executeUpdate();

            utx.commit();
        } catch (Exception e) {
            System.out.println("Error clearing logs: " + e.getMessage());
        }
        return "myActivityLogs.xhtml";
    }
    
    /**
     * Reads entity values into current bean for display in view
    */
    public List lastLogs(int limit) {
        Query q = em.createNamedQuery("Log.findByAccountId_sortByTimestampDesc");
        int accountId = getSessionBean().getAccount().getAccountId();
        q.setParameter("accountId", accountId);
        q.setMaxResults(limit);
        
        List<Log> list = (List<Log>) q.getResultList();        
        logs = list;

        return logs;
        
    }
    
    /**
     * @param logs the logs to set
     */
    public void setLogs(List<Log> logs) {
        this.logs = logs;
    }
    
    /**
     * A convenience method for writing database transaction logs from any managed bean extending BaseBean
     * @param event A doorbox.portal.entity.Log.Event enumerated value describing the transaction
     * @param params A comma-separated list of colon-separated name/value parameter pairs (e.g. 'firstname:John, lastname:Doe')
     */
    public void dblog(Log.Event event, String params) {
        dblog(event, 0, params);
    }
    
    /**
     * A convenience method for writing database transaction logs from any managed bean extending BaseBean
     * @param event A doorbox.portal.entity.Log.Event enumerated value describing the transaction
     * @param params A comma-separated list of colon-separated name/value parameter pairs (e.g. 'firstname:John, lastname:Doe')
     */
    public void dblog(Log.Event event, String... params) {
        dblog(event, 0, params);
    }
    
    /**
     * A convenience method for writing database transaction logs from any managed bean extending BaseBean
     * @param event A doorbox.portal.entity.Log.Event enumerated value describing the transaction
     * @param accountId The accountId owner of the transaction
     * @param params A comma-separated list of colon-separated name/value parameter pairs (e.g. 'firstname:John, lastname:Doe')
     */
    public void dblog(Log.Event event, int accountId, String params) {
        try {
          
            boolean inTransaction = false;
            try {
              utx.begin();
            } catch (NotSupportedException e) {
              inTransaction = true;
            }
            em.joinTransaction();
            Log log = new Log(accountId, new Date(), event, params);
            System.out.println("event: " + event + ", accountId: " + accountId + ", " + params );
            em.persist(log);
            if (!inTransaction) {
              utx.commit();
            }
        } catch (Exception e) {
            System.err.println("FATAL! Cannot log transactions in database: " + e.getMessage());
        }
    }
    
    /**
     * A convenience method for writing database transaction logs from any managed bean extending BaseBean.
     * Use this method to capture important transaction (audit) events in the database for the benefit of
     * administrator and user alike. These records should include the Event, an accountId if applicable and
     * a list of name/value pairs of parameters germane to the transaction. For example, a login event:
     * 
     *   LOGIN_SUCCEED, accountId:1001, email:yourname@domain.com
     * 
     * Every interaction with the DoorBox system must tell a story from the time a user first interacts, to the
     * time he stops using it. If the user logs in, deletes a doorbox, creates another, updates a third, then
     * logs out, the activity log should show this story.
     * 
     * Note: This method ignores the NotSupportedException when a transaction is already in progress and will not commit
     * afterward. Useful if you want to tie the commit of the log with the success of whatever you're writing to the database,
     * but note that the commit is also bypassed in which case you must commit afterward.
     * 
     * (Use {@link #dblog(Event e, String params) dblog(Event e, String params)} for non-user events) should 
     * @param event A doorbox.portal.entity.Log.Event enumerated value describing the transaction
     * @param accountId The accountId owner of the transaction
     * @param params A comma-separated list of colon-separated name/value parameter pairs (e.g. 'firstname:John, lastname:Doe')
     */
    public void dblog(Log.Event event, int accountId, String... params) {
        try {
            boolean inTransaction = false;
            try {
              utx.begin();
            } catch (NotSupportedException e) {
              inTransaction = true;
            }
            em.joinTransaction();
            Log log = new Log(accountId, new Date(), event, params);
            System.out.println("event: " + event + ", accountId: " + accountId + ", " + params );
            em.persist(log);
            if (!inTransaction) {
              utx.commit();
            }
        } catch (Exception e) {
            System.err.println("FATAL! Cannot log transactions in database: " + e.getMessage());
        } 
    } 
}
