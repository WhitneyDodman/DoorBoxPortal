/*
 * SessionBean1.java
 *
 * Created on Sep 23, 2008, 11:04:34 AM
 */
 
package doorbox.portal.beans;

import doorbox.portal.entity.Account;
import java.io.Serializable;
import java.util.Date;
import java.util.HashMap;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.faces.context.FacesContext;

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
@SessionScoped
public class SessionBean implements Serializable {
    private Account account;
    private Account newAccount;
    private HashMap serviceMap;
    private String autoLoginSessionKey;
    private String role;
    
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
    public SessionBean() {
        serviceMap = new HashMap();        
    }   
   
    public Account getAccount() {
        return account;
    }

    public void setAccount(Account account) {
        this.account = account;
    }

    public HashMap getServiceMap() {
        return serviceMap;
    }

    public void setServiceMap(HashMap serviceMap) {
        this.serviceMap = serviceMap;
    }

    /**
     * @return the autoLoginSessionKey
     */
    public String getAutoLoginSessionKey() {
        return autoLoginSessionKey;
    }

    /**
     * @param autoLoginSessionKey the autoLoginSessionKey to set
     */
    public void setAutoLoginSessionKey(String autoLoginSessionKey) {
        this.autoLoginSessionKey = autoLoginSessionKey;
    }    
    
    public boolean getUserInAnyRole() {
        System.out.println("FacesContext, isUserInRole: " + FacesContext.getCurrentInstance().getExternalContext().isUserInRole("admin"));
        System.out.println("FacesContext, remoteUser: " + FacesContext.getCurrentInstance().getExternalContext().getRemoteUser());
        System.out.println("FacesContext, authType: " + FacesContext.getCurrentInstance().getExternalContext().getAuthType());
        System.out.println("FacesContext, getPrincipal: " + FacesContext.getCurrentInstance().getExternalContext().getUserPrincipal());

        return false;
    }

    public boolean getUserInMemberRole() {
        return FacesContext.getCurrentInstance().getExternalContext().isUserInRole("member");
    }

    public boolean getUserInAdminRole() {
        System.out.println("role: " + FacesContext.getCurrentInstance().getExternalContext().getUserPrincipal());
        return FacesContext.getCurrentInstance().getExternalContext().isUserInRole("admin");
    }

    public boolean getLoggedIn() {
        return account != null;
    }        

    /**
     * @return the current role
     */
    public String getRole() {
        return role;
    }
    
    /**
     * @param role the role to set
     */
    public void setRole(String role) {
        this.role = role;
    }

    /**
     * @return the newAccount
     */
    public Account getNewAccount() {
        return newAccount;
    }

    /**
     * @param newAccount the newAccount to set
     */
    public void setNewAccount(Account newAccount) {
        this.newAccount = newAccount;
    }

}
