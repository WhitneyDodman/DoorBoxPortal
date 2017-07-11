/*
 * SessionBean1.java
 *
 * Created on Sep 23, 2008, 11:04:34 AM
 */
 
package doorbox.portal.beans;

import java.io.Serializable;
import java.util.HashMap;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.RequestScoped;

/**
 * <p>Session scope data bean for your application.  Create properties
 *  here to represent cached data that should be made available across
 *  multiple HTTP requests for an individual user.</p>
 *
 * <p>An instance of this class will be created for you automatically,
 * the first time your application evaluates a value binding expression
 * or method binding expression that references a managed bean using
 * this class.</p>
 *
 * @author torinw
 */
@ManagedBean
@RequestScoped
public class RequestBean implements Serializable {
    private HashMap serviceMap;    

    // <editor-fold defaultstate="collapsed" desc="Managed Component Definition">

    /**
     * <p>Automatically managed component initialization.  <strong>WARNING:</strong>
     * This method is automatically generated, so any user-specified code inserted
     * here is subject to being replaced.</p>
     */
    private void _init() throws Exception {
    }
    
    // </editor-fold>

    /**
     * <p>Construct a new session data bean instance.</p>
     */
    public RequestBean() {
        serviceMap = new HashMap();
    }   

}
