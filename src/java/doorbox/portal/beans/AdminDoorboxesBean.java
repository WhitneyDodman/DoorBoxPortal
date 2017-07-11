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

import doorbox.portal.entity.Doorbox;
import java.util.List;
import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import javax.faces.application.ConfigurableNavigationHandler;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import javax.faces.event.ActionEvent;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import org.primefaces.event.SelectEvent;
import org.primefaces.event.UnselectEvent;

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
@ViewScoped
@ManagedBean
public class AdminDoorboxesBean extends BaseBean {

    // List-related members
    private List<Doorbox> doorboxList;
    private Doorbox selectedDoorbox;

    // Selected Doorbox members
    
    @PostConstruct
    public void init() {
        // Load current account's accountView details into this bean
        read();
    }
    
    public List<Doorbox> getDoorboxes() {
        return doorboxList;
    }
    
    public Doorbox getSelectedDoorbox() {
        return selectedDoorbox;
    }
    
    public void setSelectedDoorbox(Doorbox selectedDoorbox) {
        this.selectedDoorbox = selectedDoorbox;
    }
    
    public void onRowSelect(SelectEvent event) {
        FacesMessage msg = new FacesMessage("Doorbox Selected", ((Doorbox) event.getObject()).getDoorboxId().toString());
        FacesContext.getCurrentInstance().addMessage(null, msg);
    }
 
    public void onRowUnselect(UnselectEvent event) {
        FacesMessage msg = new FacesMessage("Doorbox Unselected", ((Doorbox) event.getObject()).getDoorboxId().toString());
        FacesContext.getCurrentInstance().addMessage(null, msg);
    }
    
    /*public void openSendCodePage(ActionEvent event) {
        System.out.println("openSendCodePage");
        ConfigurableNavigationHandler nh = (ConfigurableNavigationHandler)FacesContext.getCurrentInstance().getApplication().getNavigationHandler();
        nh.performNavigation("myDoorbox_sendCode.xhtml");
    }
    
    public String openSendCodePageWithReturnValue() {
        System.out.println("openSendCodePage");
        ConfigurableNavigationHandler nh = (ConfigurableNavigationHandler)FacesContext.getCurrentInstance().getApplication().getNavigationHandler();
        nh.performNavigation("myDoorbox_sendCode.xhtml");
        return "myDoorbox_sendCode.xhtml";
    }
    
    public void openSendCodePage() {
        System.out.println("openSendCodePage");
        ConfigurableNavigationHandler nh = (ConfigurableNavigationHandler)FacesContext.getCurrentInstance().getApplication().getNavigationHandler();
        nh.performNavigation("myDoorbox_sendCode.xhtml");
    }*/
    
    /**
     * Reads entity values into current bean for display in view
    */
    public void read() {
        Query q = em.createNamedQuery("Doorbox.findAll");
        
        List<Doorbox> list = (List<Doorbox>) q.getResultList();        
        doorboxList = list;
        /*if (!list.isEmpty()) {
            selectedDoorbox = list.get(0);
        }*/
    }
            
}
