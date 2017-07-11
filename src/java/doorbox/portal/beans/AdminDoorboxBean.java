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
import doorbox.portal.entity.Doorbox;
import doorbox.portal.entity.DoorboxModel;
import doorbox.portal.entity.EphemeralCode;
import doorbox.portal.entity.Log;
import doorbox.portal.utils.DateTimeUtil;
import doorbox.portal.utils.ImageMagick;
import doorbox.portal.utils.Mail;
import doorbox.portal.utils.NetCodeAPI;
import doorbox.portal.utils.UAFSUtil;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;
import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import javax.faces.application.ConfigurableNavigationHandler;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.component.UIComponent;
import javax.faces.component.UIInput;
import javax.faces.context.FacesContext;
import javax.faces.event.ActionEvent;
import javax.faces.event.ComponentSystemEvent;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import javax.validation.ConstraintViolation;
import javax.validation.constraints.*;
import org.primefaces.context.RequestContext;
import org.primefaces.event.FileUploadEvent;
import org.primefaces.event.SelectEvent;

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
public class AdminDoorboxBean extends BaseBean {
    private final static Logger logger = Logger.getLogger(AdminDoorboxBean.class.getName());  
    
    public AdminDoorboxBean() {
    }
    
    @PostConstruct
    public void init() {
        // Initialize pull-down menus for state/province, and country
        initDropDowns();
        // Load current account's doorbox details into this bean
        
        Map<String, String> params = FacesContext.getCurrentInstance().getExternalContext().getRequestParameterMap();
        String value = params.get("doorboxId");
        System.out.println("param doorboxId: " + value);
        if (value != null && !value.equals("null")) {
            doorboxId = Integer.parseInt(value);
            loadDoorboxEntity(doorboxId);
        }        
    }
    
    @NotNull(message = "Required")
    private Integer doorboxId;

    @NotNull(message = "Required")
    @Size(min = 4, max = 30, message = "Between 4 and 30 characters")
    private String address1;

    @Size(min = 0, max = 30, message = "30 characters or less")
    private String address2;

    @NotNull(message = "Required")
    @Size(min = 2, max = 30, message = "Between 2 and 20 characters")
    private String city;

    @NotNull(message = "Required")
    @Size(min = 2, max = 30, message = "2 characters")
    private String stateprov;
    private String selectedStateprov;
    
    @NotNull(message = "Required")
    @Size(min = 2, max = 30, message = "2 character ISO country code")
    private String country;
    private String selectedCountry;

    @NotNull(message = "Required")
    @Pattern(regexp = "(([a-zA-Z][0-9][a-zA-Z] *[0-9][a-zA-Z][0-9])|([0-9]{5}(-[0-9]{4,5})?))", message = "Canada: A1A 2B2 OR US: 12345 or 12345-67890")
    private String postalcode;
    
    private String latitude;
    private String longitude;
    
    @Size(min = 0, max = 256)
    private String locationDescription;

    private Integer modelId;
    private List<DoorboxModel> doorboxModelsList;
    private Integer selectedModelId;

    private Date dateCreated;
    private Date dateUpdated;
    
    @NotNull(message = "Required")
    private Date initDate;
    
    @NotNull(message = "Required")
    private String seed;
    
    @NotNull(message = "Required")
    private String masterCode;
    
    @NotNull(message = "Required")
    private String subMasterCode;
    
    private String uniqueId;
    
    private String tempImageFilename;
    //private String tempThumbFilename;
    private String photoUrl;

    private Integer ephemeralCodeId;
    private Integer ephemeralCodeType;
    private String ephemeralCode;
    
    @NotNull(message = "Required")
    private String recipientName;
    
    @Pattern(regexp = "^[_A-Za-z0-9-\\+]+(\\.[_A-Za-z0-9-]+)*@[A-Za-z0-9-]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$", message = "Format: yourname@domain.com")
    private String recipientEmail;
    
    @Pattern(regexp = "^(?:(?:\\+?1\\s*(?:[.-]\\s*)?)?(?:\\(\\s*([2-9]1[02-9]|[2-9][02-8]1|[2-9][02-8][02-9])\\s*\\)|([2-9]1[02-9]|[2-9][02-8]1|[2-9][02-8][02-9]))\\s*(?:[.-]\\s*)?)?([2-9]1[02-9]|[2-9][02-9]1|[2-9][02-9]{2})\\s*(?:[.-]\\s*)?([0-9]{4})(?:\\s*(?:#|x\\.?|ext\\.?|extension)\\s*(\\d+))?$", message = "Format: +1 (nnn) nnn-nnnn")
    private String recipientPhone;
    
    @NotNull(message = "Required")
    private Date startDate;
    
    @NotNull(message = "Required")
    private Date endDate;
    
    public void loadDoorboxEntity(Integer doorboxId) {
        System.out.println("Loading doorbox: " + doorboxId);
        try {
            Query q = em.createNamedQuery("Doorbox.findByDoorboxId");
            q.setParameter("doorboxId", doorboxId);            
            Doorbox doorbox = (Doorbox) q.getSingleResult();

            doorboxId = doorbox.getDoorboxId();

            dateCreated = doorbox.getDateCreated();
            dateUpdated = doorbox.getDateUpdated();
            
            uniqueId = doorbox.getUniqueId();
            modelId = doorbox.getModelId();
            selectedModelId = getModelId();
            locationDescription = doorbox.getLocationDescription();
            photoUrl = doorbox.getPhotoUrl();
            tempImageFilename = photoUrl;
            
            address1 = doorbox.getAddress1();
            address2 = doorbox.getAddress2();
            city = doorbox.getCity();
            stateprov = doorbox.getStateProv();
            selectedStateprov = stateprov;
            country = doorbox.getCountry();
            selectedCountry = country;            
            postalcode = doorbox.getPostalCode();            
            longitude = doorbox.getLongitude();
            latitude = doorbox.getLatitude();
            
            initDate = doorbox.getInitDate();            
            seed = doorbox.getSeed();            
            masterCode = doorbox.getMasterCode();
            subMasterCode = doorbox.getSubMasterCode();
        
        } catch (Exception e) {
            System.out.println("Error retrieving doorbox id " + doorboxId);
        }
    }
    
    // The difference between createNewAccount() and update() is that createNewAccount
    // does not yet have an accountId associated with it.
    public String add() {
        System.out.println("Adding new doorbox");
        try {
            utx.begin();
            em.joinTransaction();

            Account account = getSessionBean().getAccount();
            Doorbox doorbox = new Doorbox();
            doorbox.setAccountId(account.getAccountId());
            
            Date now = new Date();
            doorbox.setDateCreated(now);
            doorbox.setDateUpdated(now);
            
            doorbox.setUniqueId(getUniqueId());            
            doorbox.setModelId(selectedModelId);
            doorbox.setLocationDescription(locationDescription);            
            doorbox.setPhotoUrl(getPhotoUrl());
                                    
            doorbox.setAddress1(address1);
            doorbox.setAddress2(address2);
            doorbox.setCity(city);
            doorbox.setStateProv(stateprov);
            doorbox.setCountry(country);
            doorbox.setPostalCode(postalcode);

            doorbox.setLatitude(latitude);
            doorbox.setLongitude(longitude);

            doorbox.setInitDate(initDate);
            doorbox.setSeed(seed);
            doorbox.setMasterCode(getMasterCode());
            doorbox.setSubMasterCode(getSubMasterCode());
                        
            System.out.println("Saving doorbox to database");
            System.out.println("accountId: " + doorbox.getAccountId());
            System.out.println("uniqueId: " + doorbox.getUniqueId());
            System.out.println("modelId: " + doorbox.getModelId());

            // Finally, store doorbox in database            
            doorbox = em.merge(doorbox);
            
            System.out.println("DoorboxID after merge: " + doorbox.getDoorboxId());
            System.out.println("tempImageFilename:" + getTempImageFilename());
            
            if (tempImageFilename != null) {
                String uafsRoot = FacesContext.getCurrentInstance().getExternalContext().getInitParameter("UAFS_ROOT");
                if (uafsRoot == null) {
                  throw new Exception("Fatal Error: UAFS_ROOT must be set as context parameter in web.xml");
                }
                System.out.println("tempImageFilename " + tempImageFilename);
                File tempImageFile = new File(uafsRoot + "/" + tempImageFilename);
                System.out.println("tempImageFile absolute path " + tempImageFile.getAbsolutePath());
                System.out.println("tempImageFile canonical path " + tempImageFile.getCanonicalPath());
                System.out.println("tempImageFile plain path " + tempImageFile.getPath());
                //File tempThumbFile = new File(tempThumbFilename);
                System.out.println("Base media directory (UAFS root): " + uafsRoot);
                
                File permanentImageFile = UAFSUtil.createFile(uafsRoot, "myMedia/doorboxImages", doorbox.getDoorboxId().toString(), tempImageFile.getName());
                System.out.println("Moving file " + tempImageFile.getAbsolutePath() + " to permanent destination " + permanentImageFile.getAbsolutePath());
                Path source = Paths.get(tempImageFile.getAbsolutePath());
                Path dest = Paths.get(permanentImageFile.getAbsolutePath());
                Files.move(source, dest, StandardCopyOption.REPLACE_EXISTING);
                
                //File permanentThumbFile = UAFSUtil.createFile(uafsRoot, "myMedia/doorboxThumbs", doorboxId.toString(), "jpg");
                //tempImageFile.renameTo(permanentThumbFile);
                
                // Extract the relative filename (all but the uafsRoot)
                String permanentImageRelativeFilename = permanentImageFile.getAbsolutePath().substring(uafsRoot.length() + 2);
                permanentImageRelativeFilename = permanentImageRelativeFilename.substring(1);
                permanentImageRelativeFilename = permanentImageRelativeFilename.replace('\\', '/');
                System.out.println("Storing relative imageURL as " + permanentImageRelativeFilename);
                doorbox.setPhotoUrl(permanentImageRelativeFilename);
                photoUrl = permanentImageRelativeFilename;
                
                em.merge(doorbox);
            }

            utx.commit();
            
            dblog(Log.Event.CREATE_DOORBOX, account.getAccountId(), "accountId: " + account.getAccountId() + ", email:" + account.getEmail() + ", doorboxId: " + doorbox.getDoorboxId() + ", doorboxUniqueId: " + doorbox.getUniqueId());
            
            return "admin_manageDoorboxes.xhtml";
        } catch (javax.validation.ConstraintViolationException e) {
            for (Iterator<ConstraintViolation<?>> it = e.getConstraintViolations().iterator(); it.hasNext();) {
                ConstraintViolation<? extends Object> v = it.next();
                System.err.println(v);
                System.err.println("==>>"+v.getMessage());
            }
        } catch (Exception e) {
            System.out.println("Error creating new doorbox: " + e.getMessage());
            e.printStackTrace();
        }
        
        return null;
    }
    
    // The difference between createNewAccount() and update() is that createNewAccount
    // does not yet have an accountId associated with it.
    public String copy() {
        System.out.println("Copy doorbox");
        try {
            utx.begin();
            em.joinTransaction();

            Account account = getSessionBean().getAccount();
            Doorbox doorbox = new Doorbox();
            doorbox.setAccountId(account.getAccountId());
            
            Date now = new Date();
            doorbox.setDateCreated(now);
            doorbox.setDateUpdated(now);
            
            doorbox.setUniqueId(getUniqueId());            
            doorbox.setModelId(selectedModelId);
            doorbox.setLocationDescription(locationDescription);            
            doorbox.setPhotoUrl(getPhotoUrl());
                                    
            doorbox.setAddress1(address1);
            doorbox.setAddress2(address2);
            doorbox.setCity(city);
            doorbox.setStateProv(stateprov);
            doorbox.setCountry(country);
            doorbox.setPostalCode(postalcode);

            doorbox.setLatitude(latitude);
            doorbox.setLongitude(longitude);

            doorbox.setInitDate(initDate);
            doorbox.setSeed(seed);
            doorbox.setMasterCode(getMasterCode());
            doorbox.setSubMasterCode(getSubMasterCode());
                        
            System.out.println("Saving doorbox to database");
            System.out.println("accountId: " + doorbox.getAccountId());
            System.out.println("uniqueId: " + doorbox.getUniqueId());
            System.out.println("modelId: " + doorbox.getModelId());

            System.out.println("DoorboxID after merge: " + doorbox.getDoorboxId());
            System.out.println("tempImageFilename:" + getTempImageFilename());
            
            // Update only if the url has changed
            if (tempImageFilename != null && !tempImageFilename.equals(photoUrl)) {
                String uafsRoot = FacesContext.getCurrentInstance().getExternalContext().getInitParameter("UAFS_ROOT");
                if (uafsRoot == null) {
                  throw new Exception("Fatal Error: UAFS_ROOT must be set as context parameter in web.xml");
                }
                System.out.println("tempImageFilename " + tempImageFilename);
                File tempImageFile = new File(uafsRoot + "/" + tempImageFilename);
                System.out.println("tempImageFile absolute path " + tempImageFile.getAbsolutePath());
                System.out.println("tempImageFile canonical path " + tempImageFile.getCanonicalPath());
                System.out.println("tempImageFile plain path " + tempImageFile.getPath());
                //File tempThumbFile = new File(tempThumbFilename);
                System.out.println("Base media directory (UAFS root): " + uafsRoot);
                
                File permanentImageFile = UAFSUtil.createFile(uafsRoot, "myMedia/doorboxImages", doorbox.getDoorboxId().toString(), tempImageFile.getName());
                System.out.println("Moving file " + tempImageFile.getAbsolutePath() + " to permanent destination " + permanentImageFile.getAbsolutePath());
                Path source = Paths.get(tempImageFile.getAbsolutePath());
                Path dest = Paths.get(permanentImageFile.getAbsolutePath());
                Files.move(source, dest, StandardCopyOption.REPLACE_EXISTING);
                
                //File permanentThumbFile = UAFSUtil.createFile(uafsRoot, "myMedia/doorboxThumbs", doorboxId.toString(), "jpg");
                //tempImageFile.renameTo(permanentThumbFile);
                
                // Extract the relative filename (all but the uafsRoot)
                String permanentImageRelativeFilename = permanentImageFile.getAbsolutePath().substring(uafsRoot.length() + 2);
                permanentImageRelativeFilename = permanentImageRelativeFilename.substring(1);
                permanentImageRelativeFilename = permanentImageRelativeFilename.replace('\\', '/');
                System.out.println("Storing relative imageURL as " + permanentImageRelativeFilename);
                doorbox.setPhotoUrl(permanentImageRelativeFilename);
                photoUrl = permanentImageRelativeFilename;
                
                //String permanentThumbRelativeFilename = permanentThumbFile.getAbsolutePath().substring(uafsRoot.length() + 2);
                //permanentThumbRelativeFilename = permanentThumbRelativeFilename.replace('\\', '/');
                //System.out.println("Storing relative imageURL as " + permanentThumbRelativeFilename);
                //doorbox.setPhotoUrl(photoUrl);
                
            }
            
            em.merge(doorbox);
            utx.commit();
            
            dblog(Log.Event.CREATE_DOORBOX, account.getAccountId(), "accountId: " + account.getAccountId() + ", email:" + account.getEmail() + ", doorboxId: " + doorbox.getDoorboxId() + ", doorboxUniqueId: " + doorbox.getUniqueId());
            
            return "admin_manageDoorboxes.xhtml";
        } catch (javax.validation.ConstraintViolationException e) {
            for (Iterator<ConstraintViolation<?>> it = e.getConstraintViolations().iterator(); it.hasNext();) {
                ConstraintViolation<? extends Object> v = it.next();
                System.err.println(v);
                System.err.println("==>>"+v.getMessage());
            }
        } catch (Exception e) {
            System.out.println("Error creating new doorbox: " + e.getMessage());
            e.printStackTrace();
        }
        
        return null;
    }

    public void uploadFile(FileUploadEvent event) {
        
        File uploadedFile;
        File imageFile;
        //File thumbnailFile = null;
                
        // Convert the uploaded file to a normalized image and thumbnailnail and store in bean until save
        try {
            String uafsRoot = FacesContext.getCurrentInstance().getExternalContext().getInitParameter("UAFS_ROOT");
            if (uafsRoot == null) {
                System.err.println("Fatal Error: UAFS_ROOT must be set as context parameter in web.xml");
                return;
            }
            
            // Get a file handle to the physical (uploaded) file and write out file in memory
            uploadedFile = File.createTempFile("upload", ".tmp", new File(uafsRoot + "/myMedia/tmp"));
            writeFile(uploadedFile.getAbsolutePath(), event.getFile().getInputstream());
            System.out.println("Uploaded file size: " + uploadedFile.length());

            Date creationDate = new Date();
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-mm-dd HH:MM:SS");
            String comment = "DoorBox photo " + doorboxId + " " + dateFormat.format(creationDate) + " Copyright www.thedoorbox.com";

            // Create the file using a random filename. Use the filename as the caption.
            imageFile = File.createTempFile("image", ".jpg", new File(uafsRoot + "/myMedia/tmp"));
            System.out.println("Temp image location: " + imageFile.getAbsolutePath());

            // Create a corresponding thumbnail version of the image filename
            //thumbnailFile = File.createTempFile("thumb", ".jpg", new File("/tmp"));
            //System.out.println("Temp thumbnail location: " + thumbnailFile.getAbsolutePath());

            System.out.println("Transforming image...");
            ImageMagick.normalizeImage(uploadedFile, imageFile, comment);
            System.out.println("Created Image, " + imageFile.length() + " bytes");
            setTempImageFilename("myMedia/tmp/" + imageFile.getName());
            
            //System.out.println("Creating thumbnail...");
            //ImageMagick.createThumbnail(uploadedFile, thumbnailFile, comment);
            //if (!thumbnailFile.exists()) {
            //    throw new Exception("Thumbnail conversion failed.");
            //}
            //System.out.println("Created thumbnail, " + thumbnailFile.length() + " bytes");
            //tempThumbFilename = thumbnailFile.getAbsolutePath();
            
        } catch (Exception e) {
            System.out.println("Error processing image: " + e.getMessage());            
        }
    }
    
    public void writeFile(String filename, InputStream in) {
      try {
        // write the inputStream to a FileOutputStream
        OutputStream out = new FileOutputStream(new File(filename));

        int read = 0;
        byte[] bytes = new byte[4096];

        while ((read = in.read(bytes)) != -1) {
          out.write(bytes, 0, read);
        }

        in.close();
        out.flush();
        out.close();

      } catch (IOException e) {
        System.out.println(e.getMessage());
      }
    }
        
    public String update() {
        System.out.println("Updating doorbox: " + doorboxId);
        try {
            Account account = getSessionBean().getAccount();
            
            utx.begin();
            em.joinTransaction();
            Query q = em.createNamedQuery("Doorbox.findByDoorboxId");
            q.setParameter("doorboxId", doorboxId);            
            Doorbox doorbox = (Doorbox) q.getSingleResult();
            
            Date now = new Date();
            doorbox.setDateUpdated(now);
            
            doorbox.setUniqueId(uniqueId);            
            doorbox.setModelId(selectedModelId);            
            doorbox.setLocationDescription(locationDescription);
            
            doorbox.setAddress1(address1);
            doorbox.setAddress2(address2);
            doorbox.setCity(city);
            doorbox.setStateProv(selectedStateprov);
            doorbox.setCountry(selectedCountry);
            doorbox.setPostalCode(postalcode);

            doorbox.setLatitude(latitude);
            doorbox.setLongitude(longitude);
            
            doorbox.setInitDate(initDate);
            doorbox.setSeed(seed);
            doorbox.setMasterCode(masterCode);
            doorbox.setSubMasterCode(subMasterCode);

            // Update only if the url has changed
            if (tempImageFilename != null && !tempImageFilename.equals(photoUrl)) {
                String uafsRoot = FacesContext.getCurrentInstance().getExternalContext().getInitParameter("UAFS_ROOT");
                if (uafsRoot == null) {
                  throw new Exception("Fatal Error: UAFS_ROOT must be set as context parameter in web.xml");
                }
                System.out.println("tempImageFilename " + tempImageFilename);
                File tempImageFile = new File(uafsRoot + "/" + tempImageFilename);
                System.out.println("tempImageFile absolute path " + tempImageFile.getAbsolutePath());
                System.out.println("tempImageFile canonical path " + tempImageFile.getCanonicalPath());
                System.out.println("tempImageFile plain path " + tempImageFile.getPath());
                //File tempThumbFile = new File(tempThumbFilename);
                System.out.println("Base media directory (UAFS root): " + uafsRoot);
                
                File permanentImageFile = UAFSUtil.createFile(uafsRoot, "myMedia/doorboxImages", doorbox.getDoorboxId().toString(), tempImageFile.getName());
                System.out.println("Moving file " + tempImageFile.getAbsolutePath() + " to permanent destination " + permanentImageFile.getAbsolutePath());
                Path source = Paths.get(tempImageFile.getAbsolutePath());
                Path dest = Paths.get(permanentImageFile.getAbsolutePath());
                Files.move(source, dest, StandardCopyOption.REPLACE_EXISTING);
                
                //File permanentThumbFile = UAFSUtil.createFile(uafsRoot, "myMedia/doorboxThumbs", doorboxId.toString(), "jpg");
                //tempImageFile.renameTo(permanentThumbFile);
                
                // Extract the relative filename (all but the uafsRoot)
                String permanentImageRelativeFilename = permanentImageFile.getAbsolutePath().substring(uafsRoot.length() + 2);
                permanentImageRelativeFilename = permanentImageRelativeFilename.substring(1);
                permanentImageRelativeFilename = permanentImageRelativeFilename.replace('\\', '/');
                System.out.println("Storing relative imageURL as " + permanentImageRelativeFilename);
                doorbox.setPhotoUrl(permanentImageRelativeFilename);
                photoUrl = permanentImageRelativeFilename;
                
                //String permanentThumbRelativeFilename = permanentThumbFile.getAbsolutePath().substring(uafsRoot.length() + 2);
                //permanentThumbRelativeFilename = permanentThumbRelativeFilename.replace('\\', '/');
                //System.out.println("Storing relative imageURL as " + permanentThumbRelativeFilename);
                //doorbox.setPhotoUrl(photoUrl);
                
            }
            
            // Finally, update database            
            em.merge(doorbox);
            utx.commit();

            dblog(Log.Event.UPDATE_DOORBOX, account.getAccountId(), "email:" + account.getEmail() + ", doorboxId: " + doorbox.getDoorboxId() + ", doorboxUniqueId: " + doorbox.getUniqueId());
            
            return "admin_manageDoorboxes.xhtml";
            
        } catch (javax.validation.ConstraintViolationException e) {
            for (Iterator<ConstraintViolation<?>> it = e.getConstraintViolations().iterator(); it.hasNext();) {
                ConstraintViolation<? extends Object> v = it.next();
                System.err.println(v);
                System.err.println("==>>"+v.getMessage());
            }
        } catch (Exception e) {
            System.out.println("Error updating doorbox: " + e.getMessage());
            e.printStackTrace();
        }
        
        return null;
    }
    
    public String delete() {
        System.out.println("Deleting doorbox: " + doorboxId);
        try {
            utx.begin();
            em.joinTransaction();
            
            Account account = getSessionBean().getAccount();
            Query query = em.createNamedQuery("Doorbox.findByDoorboxId");
            query.setParameter("doorboxId", doorboxId);
            
            Doorbox doorbox = (Doorbox)query.getSingleResult();
            
            String uafsRoot = FacesContext.getCurrentInstance().getExternalContext().getInitParameter("UAFS_ROOT");
            if (uafsRoot == null) {
                System.err.println("Fatal Error: UAFS_ROOT must be set as context parameter in web.xml");
                return null;
            }
            String photoUrl = doorbox.getPhotoUrl();
            if (photoUrl != null) {
              File permanentImageFile = new File(uafsRoot + "/" + photoUrl);
              if (!permanentImageFile.delete()) {
                System.out.println("Failed to remove doorbox image");
              }
            }
            em.remove(doorbox);            
            utx.commit();
            
            dblog(Log.Event.DELETE_DOORBOX, account.getAccountId(), "email:" + account.getEmail() + ", doorboxId: " + doorboxId+ ", doorboxUniqueId: " + uniqueId);
            
            return "admin_manageDoorboxes.xhtml";
            
        } catch (Exception e) {
            System.out.println("Error deleting doorbox: " + e.getMessage());
            e.printStackTrace();
        }
        
        return null;
    }
    
    public void openSendCodeDialog() {
        System.out.println("openSendCodeDialog");
        Map<String,Object> options = new HashMap<String, Object>();
        options.put("modal", true);
        org.primefaces.context.RequestContext.getCurrentInstance().openDialog("myDoorbox_sendCode", options, null);
    }
    
    public void openSendCodePage() {
        System.out.println("openSendCodePage");
        ConfigurableNavigationHandler nh = (ConfigurableNavigationHandler)FacesContext.getCurrentInstance().getApplication().getNavigationHandler();
        nh.performNavigation("myDoorbox_sendCode.xhtml");
    }
     
    public void onReturnFromSendCodeDialog(SelectEvent event) {        
        RequestContext.getCurrentInstance().closeDialog(event.getObject());
    }
    
    public void sendCodeActionListener(ActionEvent event) {
        System.out.println("sendCodeActionListener");
    }
    
    /*public String sendCode() {
        System.out.println("sendCode");

        try {
            utx.begin();
            em.joinTransaction();
            Account account = getSessionBean().getAccount();
            
            Query q = em.createNamedQuery("Doorbox.findByDoorboxId");
            q.setParameter("doorboxId", doorboxId);
            Doorbox doorbox = (Doorbox)q.getSingleResult();
            
            System.out.println("Generating NetCode");
            int hours = (int)(endDate.getTime() - startDate.getTime()) / 60000;
            ephemeralCode = NetCodeAPI.getNetCode(initDate, startDate, hours, seed);
            
            if (ephemeralCode == null) {
                System.out.println("Error generating NetCode");
                return null;
            }
            
            EphemeralCode code = new EphemeralCode(doorboxId);
            code.setEphemeralCode(ephemeralCode);
            code.setMessage(locationDescription);
            code.setRecipientName(recipientName);
            code.setRecipientEmail(recipientEmail);
            code.setRecipientPhone(recipientPhone);            
            
            em.persist(ephemeralCode);
            utx.commit();
            
            dblog(Log.Event.CREATE_EPHEMERAL_CODE, account.getAccountId(), "email:" + account.getEmail() + ", doorboxId: " + doorboxId);

            if (recipientEmail != null) {
                sendCodeByEmail(account, doorbox, code);
                dblog(Log.Event.SEND_EMAIL, account.getAccountId(), "email:" + account.getEmail() + ", doorboxId: " + doorboxId + ", recipientEmail:" + recipientEmail);
            }
            if (recipientPhone != null) {
                sendCodeBySMS(recipientPhone);
                dblog(Log.Event.SEND_SMS, account.getAccountId(), "email:" + account.getEmail() + ", doorboxId: " + doorboxId + ", recipientPhone:" + recipientPhone);                
            }
            
            return "admin_manageDoorboxes.xhtml";
        } catch (Exception e) {
            System.out.println("Exception sending code: " + e.getMessage());
        }
        
        return null;
    }*/

    /*public void sendCodeByEmail(Account account, Doorbox doorbox, EphemeralCode code) {
        String formattedStartDate = DateTimeUtil.getFormattedDateTime(code.getStartDate());
        
        String htmlBody =
            "Greetings" + code.getRecipientName() +
            "<br/>" +
            account.getEmail() + " has granted you a temporary code to access his/her DoorBox for a pick up or delivery. " +
            "<br/>" +
            "Your code is valid from " + formattedStartDate + " until " + formattedEndDate + " for the doorbox identified as '" + doorbox.getUniqueId() + "'." +
            "<br/>" +
            "If you received this in error, please accept our apologies and disregard this email.";

            // Send mail and log activity atomically
        Mail.sendEmail(code.getRecipientEmail(), "do-not-reply@portal.thedoorbox.com", "You have received a DoorBox Code!", htmlBody);
    }*/
    
    public void sendCodeBySMS(String phoneNumber) {
        
    }
    
    public void validateRecipientFields(ComponentSystemEvent event) {
        FacesContext fc = FacesContext.getCurrentInstance();
        UIComponent components = event.getComponent();
        
        // get password
        UIInput uiInputRecipientEmail = (UIInput) components.findComponent("recipientEmail");
        System.out.println("uiInputRecipientEmail " + uiInputRecipientEmail);
        String recipientEmail = uiInputRecipientEmail.getLocalValue() == null ? "" : uiInputRecipientEmail.getLocalValue().toString();
        String recipientEmailId = uiInputRecipientEmail.getClientId();

        // get confirm password
        UIInput uiInputRecipientPhone = (UIInput) components.findComponent("recipientPhone");
        String recipientPhone = uiInputRecipientPhone.getLocalValue() == null ? "" : uiInputRecipientPhone.getLocalValue().toString();

        System.out.println("Validating fields");
        System.out.println("accountId: " + getSessionBean().getAccount().getAccountId());
        System.out.println("doorboxId: " + doorboxId);
        System.out.println("recipientName: " + recipientName);
        System.out.println("recipientEmail: " + recipientEmail);
        System.out.println("recipientPhone: " + recipientPhone);
        System.out.println("location description: " + locationDescription);
        System.out.println("Start date: " + startDate);
        System.out.println("End date: " + endDate);
                
        if (recipientEmail == null && recipientPhone == null) {
              System.out.println("Email and/or Phone Required");
              FacesMessage msg = new FacesMessage("Email and/or Phone Required");
              msg.setSeverity(FacesMessage.SEVERITY_ERROR);
              fc.addMessage(null, msg);
              fc.renderResponse();
        } else {
            System.out.println("fields are good");
        }
    }

    /**
     * @return the address1
     */
    public String getAddress1() {
        return address1;
    }

    /**
     * @param address1 the address1 to set
     */
    public void setAddress1(String address1) {
        this.address1 = address1;
    }

    /**
     * @return the address2
     */
    public String getAddress2() {
        return address2;
    }

    /**
     * @param address2 the address2 to set
     */
    public void setAddress2(String address2) {
        this.address2 = address2;
    }

    /**
     * @return the city
     */
    public String getCity() {
        return city;
    }

    /**
     * @param city the city to set
     */
    public void setCity(String city) {
        this.city = city;
    }

    /*
     * This next section is support for both province/state and country selection
     */
    //private Map<String,Map<String,String>> data = new HashMap<String, Map<String,String>>();
    private Map<String,String> countries;
    private Map<String,String> stateprovs;
     
    public void initDropDowns() {
        // Populate DoorboxModels
        Query q = em.createNamedQuery("DoorboxModel.findAll");
        
        List<DoorboxModel> list = (List<DoorboxModel>) q.getResultList();        
        setDoorboxModelsList(list);
        
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
 
    /*public Map<String, Map<String, String>> getData() {
        return data;
    }*/

        /**
     * @return the stateprov
     */
    public String getStateprov() {
        return stateprov;
    }

    /**
     * @param stateprov the stateprov to set
     */
    public void setStateprov(String stateprov) {
        this.stateprov = stateprov;
    }

    /**
     * @return the country
     */
    public String getCountry() {
        return country;
    }

    /**
     * @param country the country to set
     */
    public void setCountry(String country) {
        this.country = country;
    }

    public Map<String, String> getCountries() {
        return countries;
    }
 
    public Map<String, String> getStateprovs() {
        return stateprovs;
    }
 
    /*public void onCountryChange() {
        if(country !=null && !country.equals(""))
            stateprovs = data.get(country);
        else
            stateprovs = new HashMap<String, String>();
        displayLocation();
    }*/
     
    public void displayLocation() {
        FacesMessage msg;
        if(stateprov != null && country != null)
            msg = new FacesMessage("Selected", stateprov + " of " + country);
        else
            msg = new FacesMessage(FacesMessage.SEVERITY_ERROR, "Invalid", "Province/state not selected."); 
             
        FacesContext.getCurrentInstance().addMessage(null, msg);  
    }

    
    
    /**
     * Postalcode refers to postal code (canada) or zipcode (USA)
     * @return the postalcode
     */
    public String getPostalcode() {
        return postalcode;
    }

    /**
     * @param postalcode the postalcode to set
     */
    public void setPostalcode(String postalcode) {
        this.postalcode = postalcode;
    }

    /**
     * @return the latitude
     */
    public String getLatitude() {
        return latitude;
    }

    /**
     * @param latitude the latitude to set
     */
    public void setLatitude(String latitude) {
        this.latitude = latitude;
    }

    /**
     * @return the longitude
     */
    public String getLongitude() {
        return longitude;
    }

    /**
     * @param longitude the longitude to set
     */
    public void setLongitude(String longitude) {
        this.longitude = longitude;
    }

    /**
     * @return the locationDescription
     */
    public String getLocationDescription() {
        return locationDescription;
    }

    /**
     * @param locationDescription the locationDescription to set
     */
    public void setLocationDescription(String locationDescription) {
        this.locationDescription = locationDescription;
    }

    /**
     * @return the doorboxModel
     */
    public Integer getSelectedModelId() {
        return selectedModelId;
    }

    /**
     * @param doorboxModel the doorboxModel to set
     */
    public void setSelectedModelId(Integer doorboxModelId) {
        this.selectedModelId = doorboxModelId;
    }

    /**
     * @return the technicianCode
     */
    public String getSeed() {
        return seed;
    }

    /**
     * @param technicianCode the technicianCode to set
     */
    public void setSeed(String seed) {
        this.seed = seed;
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
     * @return the uniqueId
     */
    public String getUniqueId() {
        return uniqueId;
    }

    /**
     * @param uniqueId the uniqueId to set
     */
    public void setUniqueId(String uniqueId) {
        this.uniqueId = uniqueId;
    }

    /**
     * @return the photoUrl
     */
    public String getPhotoUrl() {
        return photoUrl;
    }

    /**
     * @param photoUrl the photoUrl to set
     */
    public void setPhotoUrl(String photoUrl) {
        this.photoUrl = photoUrl;
    }

    /**
     * @return the doorboxModelsList
     */
    public List<DoorboxModel> getDoorboxModelsList() {
        return doorboxModelsList;
    }

    /**
     * @param doorboxModelsList the doorboxModelsList to set
     */
    public void setDoorboxModelsList(List<DoorboxModel> doorboxModelsList) {
        this.doorboxModelsList = doorboxModelsList;
    }

    /**
     * @return the doorboxId
     */
    public Integer getDoorboxId() {
        return doorboxId;
    }

    /**
     * @param doorboxId the doorboxId to set
     */
    public void setDoorboxId(Integer doorboxId) {
        this.doorboxId = doorboxId;
    }

    /**
     * @return the selectedStateprov
     */
    public String getSelectedStateprov() {
        return selectedStateprov;
    }

    /**
     * @param selectedStateprov the selectedStateprov to set
     */
    public void setSelectedStateprov(String selectedStateprov) {
        this.selectedStateprov = selectedStateprov;
    }

    /**
     * @return the selectedCountry
     */
    public String getSelectedCountry() {
        return selectedCountry;
    }

    /**
     * @param selectedCountry the selectedCountry to set
     */
    public void setSelectedCountry(String selectedCountry) {
        this.selectedCountry = selectedCountry;
    }

    /**
     * @return the ephemeralCodeId
     */
    public Integer getEphemeralCodeId() {
        return ephemeralCodeId;
    }

    /**
     * @param ephemeralCodeId the ephemeralCodeId to set
     */
    public void setEphemeralCodeId(Integer ephemeralCodeId) {
        this.ephemeralCodeId = ephemeralCodeId;
    }

    /**
     * @return the ephemeralCodeType
     */
    public Integer getEphemeralCodeType() {
        return ephemeralCodeType;
    }

    /**
     * @param ephemeralCodeType the ephemeralCodeType to set
     */
    public void setEphemeralCodeType(Integer ephemeralCodeType) {
        this.ephemeralCodeType = ephemeralCodeType;
    }

    /**
     * @return the ephemeralCode
     */
    public String getEphemeralCode() {
        return ephemeralCode;
    }

    /**
     * @param ephemeralCode the ephemeralCode to set
     */
    public void setEphemeralCode(String ephemeralCode) {
        this.ephemeralCode = ephemeralCode;
    }

    /**
     * @return the recipientName
     */
    public String getRecipientName() {
        return recipientName;
    }

    /**
     * @param recipientName the recipientName to set
     */
    public void setRecipientName(String recipientName) {
        this.recipientName = recipientName;
    }

    /**
     * @return the recipientEmail
     */
    public String getRecipientEmail() {
        return recipientEmail;
    }

    /**
     * @param recipientEmail the recipientEmail to set
     */
    public void setRecipientEmail(String recipientEmail) {
        this.recipientEmail = recipientEmail;
    }

    /**
     * @return the recipientPhone
     */
    public String getRecipientPhone() {
        return recipientPhone;
    }

    /**
     * @param recipientPhone the recipientPhone to set
     */
    public void setRecipientPhone(String recipientPhone) {
        this.recipientPhone = recipientPhone;
    }
    
    /**
     * @return the startDate
     */
    public Date getStartDate() {
        return startDate;
    }

    /**
     * @param startDate the startDate to set
     */
    public void setStartDate(Date startDate) {
        this.startDate = startDate;
    }

    /**
     * @return the endDate
     */
    public Date getEndDate() {
        return endDate;
    }

    /**
     * @param endDate the endDate to set
     */
    public void setEndDate(Date endDate) {
        this.endDate = endDate;
    }

    /**
     * @return the initDate
     */
    public Date getInitDate() {
        return initDate;
    }

    /**
     * @param initDate the initDate to set
     */
    public void setInitDate(Date initDate) {
        this.initDate = initDate;
    }

    /**
     * @return the dateCreated
     */
    public Date getDateCreated() {
        return dateCreated;
    }

    /**
     * @param dateCreated the dateCreated to set
     */
    public void setDateCreated(Date dateCreated) {
        this.dateCreated = dateCreated;
    }

    /**
     * @return the dateUpdated
     */
    public Date getDateUpdated() {
        return dateUpdated;
    }

    /**
     * @param dateUpdated the dateUpdated to set
     */
    public void setDateUpdated(Date dateUpdated) {
        this.dateUpdated = dateUpdated;
    }

    /**
     * @return the modelId
     */
    public Integer getModelId() {
        return modelId;
    }

    /**
     * @param modelId the modelId to set
     */
    public void setModelId(Integer modelId) {
        this.modelId = modelId;
    }

  /**
   * @return the tempImageFilename
   */
  public String getTempImageFilename() {
    if (tempImageFilename == null) {
      return "images/noImage.png";
    }
    return tempImageFilename;
  }

  /**
   * @param tempImageFilename the tempImageFilename to set
   */
  public void setTempImageFilename(String tempImageFilename) {
    this.tempImageFilename = tempImageFilename;
  }

}
