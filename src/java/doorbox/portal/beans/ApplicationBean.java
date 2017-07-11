/*
 * SessionBean1.java
 *
 * Created on Sep 23, 2008, 11:04:34 AM
 */
 
package doorbox.portal.beans;

import doorbox.portal.listeners.SessionWatchdog;
import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;
import javax.faces.bean.ApplicationScoped;
import javax.faces.bean.ManagedBean;

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
@ApplicationScoped
public class ApplicationBean implements Serializable {
    private HashMap serviceMap;

    //private Map<String,Map<String,String>> data = new HashMap<String, Map<String,String>>();
    private Map<String,String> countries;
    private Map<String,String> stateprovs;
    
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
    public ApplicationBean() {
        serviceMap = new HashMap();
        
        countries  = new HashMap<String, String>();
        countries.put("CA - Canada", "CA");
        
        stateprovs = new HashMap<String, String>();
        stateprovs.put("ON - Ontario", "ON");
        
        /*Map<String,String> map = new HashMap<String, String>();
        map.put("ON", "Ontario");
        data.put("CA", map);
         
        map = new HashMap<String, String>();
        map.put("NY", "New York");
        data.put("US", map);
        */
    }
    
//    public void onCountryChange() {
//        if (country !=null && !country.equals("")) {
//            stateprovs = data.get(country);
//        } else {
//            stateprovs = new HashMap<String, String>();
//        }
//        displayLocation();
//    }

    /**
     * @return the totalActiveSessions
     */
    public int getTotalActiveSessions() {
        return SessionWatchdog.getTotalActiveSession();
    }

    public Map<String, String> getCountries() {
        return countries;
    }
 
    public Map<String, String> getStateprovs() {
        return stateprovs;
    }
}
