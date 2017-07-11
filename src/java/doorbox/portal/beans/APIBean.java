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

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.reflect.TypeToken;
import java.util.ArrayList;
import java.util.List;
import javax.annotation.Resource;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.RequestScoped;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import javax.persistence.TypedQuery;
import javax.transaction.UserTransaction;


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
public class APIBean extends BaseBean {
    
    public static void main(String[] args) {
        List<Object[]> list = new ArrayList<Object[]>();
        list.add(new String[] {"key1", "value1"});
        list.add(new String[] {"key2", "value2"});
        JsonArray jsonArray = convertListToJsonArray(list);
        System.out.println("jsonArray:\n");
        System.out.println(jsonArray);
    }
    
    public List<Object[]> getSimpleListDoorboxes(int accountId) {
        Query q = em.createNamedQuery("Doorbox.findSimpleListByAccountId");
        q.setParameter("accountId", accountId);
        
        List<Object[]> list = (List<Object[]>) q.getResultList();
        return list;
    }
    
    public static JsonArray convertListToJsonArray(List<Object[]> list) {
        return (JsonArray) new Gson().toJsonTree(list, new TypeToken<List<String[]>>(){}.getType());
    }
    
    public EntityManager getEntityManager() {
        return em;
    }
    
    public UserTransaction getUserTransaction() {
        return utx;
    }
    
    public Query createNamedQuery(String query) {
        return em.createNamedQuery(query);
    }
    
    public TypedQuery createNamedQuery(String query, Class resultClass) {        
        return em.createNamedQuery(query, resultClass);
    }
}
