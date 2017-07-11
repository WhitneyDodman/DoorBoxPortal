/*
 * Copyright © Torin Walker 2008, 2009. All Rights Reserved.
 * No part of this document may be reproduced without
 * written consent from the author.
 */

package doorbox.portal.beans;

import doorbox.portal.entity.Log.Event;
import doorbox.portal.entity.Log;
import java.io.Serializable;
import java.util.Date;
import javax.annotation.Resource;
import javax.faces.context.FacesContext;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.transaction.NotSupportedException;

/**
 * Bean base class that extends certain staple methods.
 * 
 * @author torinw
 */
public class BaseBean implements Serializable {
    public static String APPLICATION_BEAN = "#{applicationBean}";
    public static String SESSION_BEAN = "#{sessionBean}";
    public static String REQUEST_BEAN = "#{requestBean}";
    public static String AUTH_BEAN = "#{authBean}";
    public static String LOG_BEAN = "#{logBean}";
    
    @PersistenceContext(name = "persistence/LogicalName", unitName = "DoorBoxPU")
    public EntityManager em;

    @Resource
    public javax.transaction.UserTransaction utx;
    
    /**
     * Get managed bean by name
     * @param beanELReference The EL-encapsulated bean name (e.g. '#{sessionBean}')
     * @return The @ManagedBean object
     */
    public Object getBean(String beanELReference) {
        FacesContext beanContext = FacesContext.getCurrentInstance();
        return beanContext.getApplication().evaluateExpressionGet(beanContext, beanELReference, Object.class);
    }
    
    /**
     * Convenience method for getting the Application bean
     * @return This context's @ManagedBean ApplicationBean
     */
    public ApplicationBean getApplicationBean() {
        return (ApplicationBean)getBean(APPLICATION_BEAN);
    }

    /**
     * Convenience method for getting the Session bean
     * @return This context's @ManagedBean SessionBean
     */
    public SessionBean getSessionBean() {
        return (SessionBean)getBean(SESSION_BEAN);
    }

    /**
     * Convenience method for getting the Request bean
     * @return This context's @ManagedBean RequestBean
     */
    public RequestBean getRequestBean() {
        return (RequestBean)getBean(REQUEST_BEAN);
    }

    /**
     * Convenience method for getting the Auth bean
     * @return This context's @ManagedBean AuthBean
     */
    public AuthBean getAuthBean() {
        return (AuthBean)getBean(AUTH_BEAN);
    }
    
    /**
     * Convenience method for getting the Auth bean
     * @return This context's @ManagedBean AuthBean
     */
    public LogBean getLogBean() {
        return (LogBean)getBean(LOG_BEAN);
    }
    
    /**
     * A convenience method for writing database transaction logs from any managed bean extending BaseBean
     * @param event A doorbox.portal.entity.Log.Event enumerated value describing the transaction
     * @param params A comma-separated list of colon-separated name/value parameter pairs (e.g. 'firstname:John, lastname:Doe')
     */
    public void dblog(Event event, String params) {
        dblog(event, 0, params);
    }
    
    /**
     * A convenience method for writing database transaction logs from any managed bean extending BaseBean
     * @param event A doorbox.portal.entity.Log.Event enumerated value describing the transaction
     * @param params A comma-separated list of colon-separated name/value parameter pairs (e.g. 'firstname:John, lastname:Doe')
     */
    public void dblog(Event event, String... params) {
        dblog(event, 0, params);
    }
    
    /**
     * A convenience method for writing database transaction logs from any managed bean extending BaseBean
     * @param event A doorbox.portal.entity.Log.Event enumerated value describing the transaction
     * @param accountId The accountId owner of the transaction
     * @param params A comma-separated list of colon-separated name/value parameter pairs (e.g. 'firstname:John, lastname:Doe')
     */
    public void dblog(Event event, int accountId, String params) {
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
    public void dblog(Event event, int accountId, String... params) {
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
