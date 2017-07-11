/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package doorbox.portal.utils;

import static doorbox.portal.utils.NetCodeAPI.API_KEY;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URLEncoder;
import org.apache.http.HttpEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;

/**
 *
 * @author torinw
 */
public class SMSGateWayAPI {
/*    public static String SMSGATEWAY_API_KEY = "y015k6qcQ2g25qs8ELj0h22MGDW1YQN6"; */
    public static String SMSGATEWAY_API_KEY = "fpc2QI58G47V34MicE6363U5S2AmtzO2";
    
    public static String sendSMS(String smsNumber, String smsMessage) {
        
        CloseableHttpClient client = HttpClients.createDefault();
        CloseableHttpResponse response = null;
        String message = null;
        
        try {
            String url = "http://smsgateway.ca/sendsms.aspx?CellNumber=" + smsNumber + "&MessageBody=" + URLEncoder.encode(smsMessage, "UTF-8") + "&AccountKey=" + SMSGATEWAY_API_KEY;
            System.out.println("request url: " + url);
            
            HttpGet request = new HttpGet(url);
            request.addHeader("Accept", "application/json");
            
            response = client.execute(request);
            HttpEntity entity = response.getEntity();
            System.out.println("response code: " + response.getStatusLine().getStatusCode());

            BufferedReader rd = new BufferedReader(new InputStreamReader(response.getEntity().getContent()));

            StringBuffer result = new StringBuffer();
            String line;
            while ((line = rd.readLine()) != null) {
                result.append(line);
            }
            System.out.println("response body: '" + result.toString() + "'");
            if (result.toString().equals("Message queued successfully")) {                               
                message = "SMS Queued Successfully";
            } else if (result.toString().equals("Cell number is invalid")) {
                message = result.toString();
            } else if (result.toString().equals("CellNumber has opted-out")) {
                message = result.toString();
            } else if (result.toString().equals("Message body was missing")) {
                throw new Exception(result.toString());
            } else if (result.toString().equals("The account key parameter was not valid")) {
                throw new Exception(result.toString());
            } else if (result.toString().equals("This account is disabled")) {
                throw new Exception(result.toString());
            } else if (result.toString().equals("Account key not found")) {
                throw new Exception(result.toString());
            } else if (result.toString().equals("There are no messages remaining on this account")) {
                throw new Exception(result.toString());
            } else if (result.toString().equals("No authorized credit plan found to match this phone number. Please check the number, and ensure you are authorized to send messages to this region")) {
                throw new Exception(result.toString());
            } else if (result.toString().equals("Message content is not acceptable, message not sent")) {
                throw new Exception(result.toString());
            } else if (result.toString().equals("Time of day at destination falls outside the established time restrictions for your account, message not sent.")) {
                throw new Exception(result.toString());
            } else {
                // Unhandled response
                throw new Exception("Unhandled SMS Gateway response: '" + result.toString() + "'");
            }
            EntityUtils.consume(entity);
            return message;
        } catch (Exception e) {
            System.out.println("Error executing API call: " + e.getMessage());
            e.printStackTrace();
        } finally {
            try {
                if (client != null) client.close();
            } catch (Exception ignored) {            
            }
            try {
                if (response != null) response.close();
            } catch (Exception ignored) {            
            }
        }
        return null;
    }
}
