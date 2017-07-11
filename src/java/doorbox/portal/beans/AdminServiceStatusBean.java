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

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.RequestScoped;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;

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
@RequestScoped
@ManagedBean
public class AdminServiceStatusBean extends BaseBean {
    public AdminServiceStatusBean() {
    }
    
    @PostConstruct
    public void init() {
        
    }
    
    public long getNumberOfDoorboxesInService() {
        TypedQuery<Long> q = em.createNamedQuery("Doorbox.count", Long.class);
        return q.getSingleResult();
    }
    
}
