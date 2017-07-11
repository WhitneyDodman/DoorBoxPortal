/**
 * <h1>MyAccountBean</h1>
 * This bean backs the myAccount.xhtml view under the My Account subsection of
 * the website. myAccount is used to view and alter the current account settings
 * such as billing address, phone numbers, etc.
 *
 * @author Torin Walker
 * @version 1.0
 * @since 2016-08-03
 */
package doorbox.portal.beans;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;
import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import org.apache.http.HttpEntity;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;


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
public class PaypalPDTBean extends BaseBean {
    private final static Logger logger = Logger.getLogger(PaypalPDTBean.class.getName());
    private String IDENTITY_TOKEN = "RlB2xt7DPEOyVQQJIw74gpZ1BW5cGxewVyC3e2cjBAB94ZtTCGYZCfctsDC";
    private String transactionId;
    private String status;
    
    @PostConstruct
    public void init() {
        transactionId = FacesContext.getCurrentInstance().getExternalContext().getRequestParameterMap().get("tx");
        status = FacesContext.getCurrentInstance().getExternalContext().getRequestParameterMap().get("st");
    }
    
    public boolean callBlockingPostRequest() {
        CloseableHttpClient client = HttpClients.createDefault();
        HttpPost post = new HttpPost("https://www.sandbox.paypal.com/cgi-bin/webscr");
        post.addHeader("Accept", "application/json");
        
        List<NameValuePair> urlParameters = new ArrayList<NameValuePair>();
        urlParameters.add(new BasicNameValuePair("cmd", "_notify_synch"));
        urlParameters.add(new BasicNameValuePair("tx", getTransactionId()));
        urlParameters.add(new BasicNameValuePair("at", IDENTITY_TOKEN));
        
        CloseableHttpResponse response = null;
        boolean result = false;
        
        try {
            post.setEntity(new UrlEncodedFormEntity(urlParameters));
            response = client.execute(post);

            HttpEntity entity = response.getEntity();
            System.out.println("response code: " + response.getStatusLine().getStatusCode());

            BufferedReader rd = new BufferedReader(new InputStreamReader(response.getEntity().getContent()));            
            StringBuffer sb = new StringBuffer();
            String line = "";
            while ((line = rd.readLine()) != null) {
                sb.append(line);
            }
            System.out.println("response body: " + sb.toString());
                        
            EntityUtils.consume(entity);
            
            return sb.toString().startsWith("SUCCESS");
            
        } catch (Exception e) {
            System.out.println("Error executing API call: " + e.getMessage());
            try {
                response.close();
            } catch (Exception ignored) {
                // ignored
            }
            try {
                client.close();
            } catch (Exception ignored) {
                // ignored
            }
        }
        return result;
    }
    
    /**
     * @return the transactionId
     */
    public String getTransactionId() {
        return transactionId;
    }

    /**
     * @param transactionId the transactionId to set
     */
    public void setTransactionId(String transactionId) {
        this.transactionId = transactionId;
    }

    /**
     * @return the status
     */
    public String getStatus() {
        return status;
    }

    /**
     * @param status the status to set
     */
    public void setStatus(String status) {
        this.status = status;
    }
    
}
