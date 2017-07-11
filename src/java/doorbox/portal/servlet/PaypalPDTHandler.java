/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package doorbox.portal.servlet;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.reflect.TypeToken;
import doorbox.portal.beans.BaseBean;
import doorbox.portal.beans.EphemeralCodeBean;
import doorbox.portal.entity.Account;
import doorbox.portal.entity.Doorbox;
import doorbox.portal.entity.EphemeralCode;
import doorbox.portal.utils.Crypto;
import doorbox.portal.utils.NetCodeAPI;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import javax.annotation.Resource;
import javax.faces.FactoryFinder;
import javax.faces.component.UIViewRoot;
import javax.faces.context.FacesContext;
import javax.faces.context.FacesContextFactory;
import javax.faces.lifecycle.Lifecycle;
import javax.faces.lifecycle.LifecycleFactory;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

/**
 *
 * @author torinw
 */
public class PaypalPDTHandler extends HttpServlet {
    @PersistenceContext(name = "persistence/LogicalName", unitName = "DoorBoxPU")
    public EntityManager em;

    @Resource
    public javax.transaction.UserTransaction utx;
    
        
    // <editor-fold defaultstate="collapsed" desc="HttpServlet methods. Click on the + sign on the left to edit the code.">
    /**
     * Handles the HTTP <code>GET</code> method.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        processRequest(request, response);
    }

    /**
     * Handles the HTTP <code>POST</code> method.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        processRequest(request, response);
    }

    /**
     * Returns a short description of the servlet.
     *
     * @return a String containing servlet description
     */
    @Override
    public String getServletInfo() {
        return "Short description";
    }// </editor-fold>
    
    /**
     * Processes requests for both HTTP <code>GET</code> and <code>POST</code>
     * methods.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    protected void processRequest(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        
        System.out.println("Processing Paypal Callback");                
        
        try {
            HttpSession session = request.getSession(true);

            String txnType = request.getParameter("tx");
            if (txnType == null) {
                // TODO: This could be a chargeback if case_type.equals("chargeback")
                System.out.println("No transaction type supplied. Invalid request");
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                return;
            }
            System.out.println("tx=" + txnType);
            
            System.out.println("parameter set:");
            Map params = request.getParameterMap();
            Iterator i = params.keySet().iterator();
            while (i.hasNext()) {
                String key = (String)i.next();
                String value = ((String[]) params.get(key))[0];
            }
            
            switch (txnType) {
                case "adjustment": break;
                case "cart": break;
                case "express_checkout": break;
                case "masspay": break;
                case "merch_pmt": break;
                case "mp_cancel": break;
                case "mp_signup": break;
                case "new_case": break;
                case "payout": break;
                case "pro_hosted": break;
                case "recurring_payment": break;
                case "recurrding_payment_expired": break;
                case "recurring_payment_failed": break;
                case "recurring_payment_profile_cancel": break;
                case "recurring_payment_profile_created": break;
                case "recurring_payment_skipped": break;
                case "recurring_payment_suspended": break;
                case "recurring_payment_suspended_due_to_max_failed_payment": break;
                case "send_money": break;
                case "subscr_cancel": break;
                case "subscr_eot": break;
                case "subscr_failed": break;
                case "subscr_modify": break;
                case "subscr_payment": break;
                case "subscr_signup":
                    break;
                case "virtual_terminal": break;
                case "web_accept": break;
                default:
                    throw new Exception("Unknown txn_type " + txnType + "received via IPN.");
            }
                        
        } catch (Exception e) {
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            PrintWriter out = response.getWriter();
            out.print("Internal error. Please note time and date and report this to info@thedoorbox.com");
            out.flush();
        }
    }
    
 
    /*****************************
     ***    API Methods below  ***
     *****************************/

    /***************
     * Login action - takes email/password as credentials and returns a valid HTTP Servlet session token (JSESSIONID cookie)
     * Subsequent API requests must submit a current (i.e. not expired) session token for each API request thereafter.
     * If the session expires, usually determined by trapping a 401 request to any non-login API call, the client should perform
     * the LOGIN action again to obtain a new, valid session token.
     * @param request An HttpServletRequest object supplied by the incoming servlet handler
     * @param response An HttpServletReponse object supplied by the incoming servlet handler
     * @param email The email credential from the calling client
     * @param password The password credential from the calling client
     * @return HTTP 200 Response if successful, 401 unauthorized if credentials are missing or invalid
     */    
    public boolean MOBILEAPI_login_action(HttpServletRequest request, HttpServletResponse response, final String email, String password) {
        System.out.println("MOBILEAPI_login_action: email=" + email + ", password:" + password);
        
        if (email != null && email.length() > 0 && password != null && password.length() > 0) {
            System.out.println("Authenticating user: " + email);

            try {
                System.out.println("Looking up user by email: " + email);
                System.out.println("em is " + em);
                Query query = em.createNamedQuery("Account.findByEmail");
                query.setParameter("email", email);
                System.out.println("getSingleResult");
                Account account = (Account) query.getSingleResult();
                System.out.println("account is " + account);
                
                if (account != null) {
                    System.out.println("Found user: " + email);
                    String encryptedPassword = Crypto.encrypt_SHA1(password);
                    if (account.getPassword().equals(encryptedPassword)) {                                               
                        HttpSession session = request.getSession();
                        session.setAttribute("email", email);
                        session.setAttribute("account", account);
                        //session.setAttribute("accountId", account.getAccountId());

                        switch (account.getAccountRole()) {
                            case Account.ROLE_ADMIN:
                                session.setAttribute("role", "admin");
                                break;
                            case Account.ROLE_SUBSCRIBER:
                                session.setAttribute("role", "subscriber");
                                break;
                            default:
                                session.setAttribute("role", "unknown");
                                break;
                        }

                        System.out.println("MOBILEAPI Login succeeded. Retrieving account for email " + email + ", role" + account.getAccountRole());

                        //session.setMaxInactiveInterval(3600);
                        session.setMaxInactiveInterval(3600);
                        return true;
                    } else {
                        System.out.println("MOBILEAPI Login failed for email " + email);
                    }
                } else {
                    System.out.println("account is null");
                }
            } catch (Exception e) {
                System.out.println("Authentication failure: " + e.getMessage());
                e.printStackTrace();
            }
        } else {
            System.out.println("Something wrong with email or password?");
        }
        
        return false;
    }
    
    public void MOBILEAPI_listDoorboxes_action(HttpServletRequest request, HttpServletResponse response) {
        System.out.println("MOBILEAPI_listDoorboxes_action");
        
        HttpSession session = request.getSession(false);
        Account account = (Account)session.getAttribute("account");
        Integer accountId = account.getAccountId();
        
        try {
            System.out.println("Retrieving doorbox list by accountId: " + accountId);
            System.out.println("em is " + em);
            Query query = em.createNamedQuery("Doorbox.findSimpleListByAccountId");
            query.setParameter("accountId", accountId);                
            List<Object[]> list = (List<Object[]>) query.getResultList();
            
            JsonArray jsonArray = convertListToJsonArray(list);
            
            response.setStatus(HttpServletResponse.SC_OK);
            PrintWriter out = response.getWriter();
            out.print(jsonArray);
            out.flush();
        } catch (Exception e) {
            System.out.println("listDoorboxes_action exception: " + e.getMessage());
            e.printStackTrace();
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        }
    }
    
    public void MOBILEAPI_sendCode_action(HttpServletRequest request, HttpServletResponse response) {
        System.out.println("MOBILEAPI_sendCode_action");

        int doorboxId;
        long startDate;
        int durationId;
        String email;
        String sms;

        HttpSession session = request.getSession(false);
        Account account = (Account)session.getAttribute("account");
        Integer accountId = account.getAccountId();
        
        try {
            doorboxId = Integer.parseInt(request.getParameter("doorboxId"));
            startDate = Long.parseLong(request.getParameter("startDate"));
            durationId = Integer.parseInt(request.getParameter("duration"));
            email = request.getParameter("email");
            sms = request.getParameter("sms");
            if ((email == null || email.length() == 0) && (sms == null || sms.length() == 0)) {
                throw new Exception("Missing both recipient parameterss.");
            }
            if (email != null && email.length() > 0 && !email.matches("^[_A-Za-z0-9-\\+]+(\\.[_A-Za-z0-9-]+)*@[A-Za-z0-9-]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$")) {
                throw new Exception("Invalid email format.");
            }
            if (sms != null && sms.length() > 0 && !sms.matches("^(?:(?:\\+?1\\s*(?:[.-]\\s*)?)?(?:\\(\\s*([2-9]1[02-9]|[2-9][02-8]1|[2-9][02-8][02-9])\\s*\\)|([2-9]1[02-9]|[2-9][02-8]1|[2-9][02-8][02-9]))\\s*(?:[.-]\\s*)?)?([2-9]1[02-9]|[2-9][02-9]1|[2-9][02-9]{2})\\s*(?:[.-]\\s*)?([0-9]{4})(?:\\s*(?:#|x\\.?|ext\\.?|extension)\\s*(\\d+))?$")) {
                throw new Exception("Invalid sms format.");
            }
        } catch (Exception e) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            System.out.println("MOBILEAPI_sendCode_action Exception: " + e.getMessage());
            //e.printStackTrace();
            return;
        }
        
        try {
            Query query = em.createNamedQuery("Doorbox.findByDoorboxId");
            query.setParameter("doorboxId", doorboxId);
            Doorbox doorbox = (Doorbox) query.getSingleResult();

            String netCodeResponse = NetCodeAPI.getNetCode(doorbox.getInitDate(), new Date(startDate), durationId, doorbox.getSeed());
            
            if (netCodeResponse == null) {
                throw new Exception("API Error generating NetCode");
            }
            // parse netcode
            String netCode = NetCodeAPI.getActualNetCode(netCodeResponse);
            
            EphemeralCode ephemeralCode = new EphemeralCode();
            ephemeralCode.setAccountId(accountId);
            ephemeralCode.setDoorboxId(doorboxId);
            ephemeralCode.setStartDate(new Date(startDate));
            ephemeralCode.setDurationId(durationId);
            ephemeralCode.setRecipientEmail(email);
            ephemeralCode.setRecipientPhone(sms);
            ephemeralCode.setEphemeralCode(netCode);
            
            EphemeralCodeBean ephemeralCodeBean = getBean("ephemeralCodeBean", EphemeralCodeBean.class);
        
            try {
                initFacesContext(request, response);
            } catch (Exception e) {
                System.out.println("stupid faces context: " + e.getMessage());
                e.printStackTrace();
                response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);                
            }            
            if (email != null) {
                ephemeralCodeBean.sendCodeByEmail(account, doorbox, ephemeralCode);
            }
            if (sms != null) {
                ephemeralCodeBean.sendCodeBySMS(account, doorbox, ephemeralCode);
            }
            removeFacesContext();
            
            response.setStatus(HttpServletResponse.SC_OK);
            return;
        } catch (Exception e) {
            System.out.println("sendCode_action exception: " + e.getMessage());
            e.printStackTrace();
        }
        response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
    }
    
    public static JsonArray convertListToJsonArray(List<Object[]> list) {
        return (JsonArray) new Gson().toJsonTree(list, new TypeToken<List<String[]>>(){}.getType());
    }
    
    public <T extends BaseBean> T getBean(String beanIdentifier, Class<T> type) {
        T bean = type.cast(getServletContext().getAttribute(beanIdentifier));
        if (bean == null) {
            try {
                bean = type.newInstance();
                getServletContext().setAttribute(beanIdentifier, bean);
            } catch (IllegalAccessException e) {
                System.out.println("Error accessing bean class of type " + type.getName());
            } catch (InstantiationException e) {
                System.out.println("Error instantiating bean: " + beanIdentifier + " of type " + type.getName());
            }
        }
        return bean;
    }
    
    protected void initFacesContext(HttpServletRequest request, HttpServletResponse response) {
        FacesContext facesContext = FacesContext.getCurrentInstance();
        if (facesContext == null) {
 
            FacesContextFactory contextFactory  = (FacesContextFactory)FactoryFinder.getFactory(FactoryFinder.FACES_CONTEXT_FACTORY);
            LifecycleFactory lifecycleFactory = (LifecycleFactory)FactoryFinder.getFactory(FactoryFinder.LIFECYCLE_FACTORY);
            Lifecycle lifecycle = lifecycleFactory.getLifecycle(LifecycleFactory.DEFAULT_LIFECYCLE);
 
            facesContext = contextFactory.getFacesContext(request.getSession().getServletContext(), request, response, lifecycle);
 
            // Set using our inner class
            InnerFacesContext.setFacesContextAsCurrentInstance(facesContext);
 
            // set a new viewRoot, otherwise context.getViewRoot returns null
            UIViewRoot view = facesContext.getApplication().getViewHandler().createView(facesContext, "");
            facesContext.setViewRoot(view);               
        }
    }
    
    public void removeFacesContext() {
        InnerFacesContext.setFacesContextAsCurrentInstance(null);
    }
    
    // You need an inner class to be able to call FacesContext.setCurrentInstance
    // since it's a protected method
    private abstract static class InnerFacesContext extends FacesContext {
        protected static void setFacesContextAsCurrentInstance(FacesContext facesContext) {
            FacesContext.setCurrentInstance(facesContext);
        }
    }
}
