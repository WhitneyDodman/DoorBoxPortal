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

import doorbox.portal.entity.Doorbox;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Iterator;
import java.util.Random;
import java.util.TimeZone;
import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.validation.ConstraintViolation;
import javax.validation.constraints.*;

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
public class AdminInitDoorboxBean extends BaseBean {
    public AdminInitDoorboxBean() {
    }
    
    @NotNull(message = "Required")
    private Date initDate;

    @NotNull(message = "Required")
    private String seed;
    
    @NotNull(message = "Required")
    private String masterCode;

    @NotNull(message = "Required")
    private String subMasterCode;

    private String userCode;
    
    @PostConstruct
    public void init() {
        randomize();
    }
    
    public void randomize() {
        Random r = new Random();
        r.setSeed(System.currentTimeMillis());

        String code = String.valueOf(r.nextInt(999999));
        while (code.length() < 6) code = 0 + code;
        setSeed(code);
        
        code = String.valueOf(r.nextInt(99999999));
        while (code.length() < 8) code = 0 + code;
        setMasterCode(code);
        
        code = String.valueOf(r.nextInt(99999999));
        while (code.length() < 8) code = 0 + code;
        setSubMasterCode(code);
        
        setUserCode(getSubMasterCode().substring(4));
        
        try {
            SimpleDateFormat dtf = new SimpleDateFormat("yyMMddHHmm");
            dtf.setTimeZone(TimeZone.getTimeZone("GMT"));
            Date d = dtf.parse(dtf.format(new Date()));
            setInitDate(d);
        } catch (Exception e) {
            System.out.println("Error parsing date: " + e.getMessage());
        }
    }
    
    public String getInitCodeButtonSequence(boolean factory) {
        SimpleDateFormat dtf = new SimpleDateFormat("yyMMddHHmm");
        dtf.setTimeZone(TimeZone.getTimeZone("GMT"));
        SimpleDateFormat sf = new SimpleDateFormat("ss");
        int remaining = 60 - Integer.valueOf(sf.format(new Date()));
        return "# " + (factory ? "11335577" : masterCode) + " • 00 • " + dtf.format(initDate) + " • " + seed + " • •   (Seconds remaining: " + remaining + ")";
    }

    public String getSetTimeButtonSequence(boolean factory) {
        Calendar cal = Calendar.getInstance();
        cal.setTimeInMillis(System.currentTimeMillis());
        cal.add(Calendar.YEAR, -3);
        Date timestamp = cal.getTime();
        SimpleDateFormat df = new SimpleDateFormat("yyMMdd");
        SimpleDateFormat tf = new SimpleDateFormat("HHmm");
        SimpleDateFormat sf = new SimpleDateFormat("ss");
        int remaining = 60 - Integer.valueOf(sf.format(timestamp));
        StringBuffer buf = new StringBuffer();
        return "# " + (factory ? "11335577" : masterCode) + " • 12 • " + df.format(timestamp) + " • " + tf.format(timestamp) + " • • (Seconds remaining: " + remaining + ")";
    }
    
    public Date getCurrentTime() {
        //SimpleDateFormat tf = new SimpleDateFormat("yy-MM-dd hh:MM:ss");
        //tf.setTimeZone(TimeZone.getDefault());
        return new Date();
    }
        
    public String getUserCodeButtonSequence(boolean factory) {
        return "# " + (factory ? "11335577" : masterCode) + " • 02 • " + userCode + " • •";
    }
    
    public String getSubMasterCodeButtonSequence(boolean factory) {
        return "# " + (factory ? "11335577" : masterCode) + " • 04 • " + subMasterCode + " • " + subMasterCode + " • •";
    }
    
    public String getMasterCodeButtonSequence(boolean factory) {
        return "# " + (factory ? "11335577" : masterCode) + " • 01 • " + masterCode + " • " + masterCode + " • •";
    }
    
    public String reserve() {
        System.out.println("Reserving doorbox lock init details");
        try {
            utx.begin();
            em.joinTransaction();

            Doorbox doorbox = new Doorbox();
            doorbox.setAccountId(0);
            
            Date now = new Date();
            doorbox.setDateCreated(now);
            doorbox.setDateUpdated(now);
            
            doorbox.setUniqueId("reserved");            
            doorbox.setModelId(0);

            doorbox.setInitDate(initDate);
            System.out.println("init date: " + new SimpleDateFormat("yyMMddHHmm").format(initDate));
            doorbox.setSeed(seed);
            doorbox.setMasterCode(masterCode);
            doorbox.setSubMasterCode(subMasterCode);
                        
            doorbox = em.merge(doorbox);
            utx.commit();
            System.out.println("init date: " + new SimpleDateFormat("yyMMddHHmm").format(doorbox.getInitDate()));
                                    
            System.out.println("Doorbox successfully reserved");
            
            randomize();
            
            return null;
        } catch (javax.validation.ConstraintViolationException e) {
            for (Iterator<ConstraintViolation<?>> it = e.getConstraintViolations().iterator(); it.hasNext();) {
                ConstraintViolation<? extends Object> v = it.next();
                System.err.println(v);
                System.err.println("==>>"+v.getMessage());
            }
        } catch (Exception e) {
            System.out.println("Error reserving doorbox: " + e.getMessage());
            e.printStackTrace();
        }
        
        return null;
    }
    

    /**
     * @return the initDate
     */
    public Date getInitDate() {
        return initDate;
    }

    /**
     * @param initDate the initCode to set
     */
    public void setInitDate(Date initDate) {
        this.initDate = initDate;
    }    

    /**
     * @return the masterCode
     */
    public String getMasterCode() {
        return masterCode;
    }

    /**
     * @param masterCode the masterCode to set
     */
    public void setMasterCode(String masterCode) {
        this.masterCode = masterCode;
    }

    /**
     * @return the subMasterCode
     */
    public String getSubMasterCode() {
        return subMasterCode;
    }

    /**
     * @param subMasterCode the subMasterCode to set
     */
    public void setSubMasterCode(String subMasterCode) {
        this.subMasterCode = subMasterCode;
    }

    /**
     * @return the userCode
     */
    public String getUserCode() {
        return userCode;
    }

    /**
     * @param userCode the userCode to set
     */
    public void setUserCode(String userCode) {
        this.userCode = userCode;
    }

    /**
     * @return the seed
     */
    public String getSeed() {
        return seed;
    }

    /**
     * @param seed the seed to set
     */
    public void setSeed(String seed) {
        this.seed = seed;
    }
    
}
