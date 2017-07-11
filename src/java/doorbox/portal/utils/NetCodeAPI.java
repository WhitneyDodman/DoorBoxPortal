/*
 * Copyright (C) 2016 TheDoorbox.com - All rights reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 */
package doorbox.portal.utils;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.text.SimpleDateFormat;
import java.time.ZoneId;
import java.util.Date;
import java.util.TimeZone;
import javax.net.ssl.SSLContext;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.ssl.SSLContexts;
import org.apache.http.util.EntityUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

/**
 *
 * @author torinw
 */
public class NetCodeAPI {
    public static String API_KEY = "snRIkyi8XN9QiU6UTVY9x9cFsElsztKj2Hqjpf07";
    public static String API_HOST = "https://5qpe04f0od.execute-api.eu-west-1.amazonaws.com";
    
    private static final Logger logger = LogManager.getLogger(NetCodeAPI.class.getName());
        
    public static enum LockModels {
        KL1000NC1F,
        KL1060
    }
    
    public static String[] DURATION_VALUES = new String[] {
        "1 Hour",
        "2 Hours",
        "3 Hours",
        "4 Hours",
        "5 Hours",
        "6 Hours",
        "7 Hours",
        "8 Hours",
        "9 Hours",
        "10 Hours",
        "11 Hours",
        "12 Hours",
        "1 Day",
        "2 Days",
        "3 Days",
        "4 Days",
        "5 Days",
        "6 Days",
        "7 Days"
    };
    
    public static void main(String[] args) {        
        //String actualNetCode = getActualNetCode("{\"Startdate\":\"2013-08-29T04:00:00\",\"DurationHours\":2880,\"DurationDays\":120,\"Mode\":null,\"SubMode\":null,\"Timecode\":\"2016/08/27/04:00\",\"ActualNetcode\":\"358428003884\",\"Resultstatus\":null,\"LockId\":\"2016/08/27/04:00\"}");
        //System.out.println(actualNetCode);
        try {
            Date initDate = new SimpleDateFormat("yyyy-MM-dd HH:mm zzz").parse("2016-08-31 13:01 GMT");
            Date startDate = new SimpleDateFormat("yyyy-MM-dd HH:mm zzz").parse("2016-08-31 14:00 GMT");
            JSONParser parser = new JSONParser();
            for (int i = 1; i < 51; i++) {
                String response = getNetCode(initDate, startDate, i, "123456");                
                JSONObject jsonObject = (JSONObject)parser.parse(response);
                System.out.println(i + "," + (String)jsonObject.get("ActualNetcode"));
            }
//            System.out.println("response: " + response);
        } catch (Exception e) {
            System.out.println("Error running test: " + e.getMessage());
        }
    }
    
    public static String getNetCode(Date initDate, Date startDate, int durationId, String identifier) {
        SimpleDateFormat initFormat = new SimpleDateFormat("yyyyMMddHHmm");
        initFormat.setTimeZone(TimeZone.getTimeZone("GMT"));
        SimpleDateFormat startFormat = new SimpleDateFormat("yyyy-MM-dd'%20'HH'%3A'mm");
        startFormat.setTimeZone(TimeZone.getTimeZone("GMT"));
        String url = API_HOST + "/prod/netcode/" + initFormat.format(initDate) + "?start=" + startFormat.format(startDate) + "&duration=" + durationId + "&lockmodel=" + LockModels.KL1060 + "&identifier=" + identifier;
        return callBlockingGetRequest(url);
    }    

     public static String getInitCode(String masterCode) {
        String url = API_HOST + "/prod/init?lockmodel=" + LockModels.KL1060 + "&mastercode=" + masterCode + "&api_key=" + API_KEY;
        return callBlockingGetRequest(url);
    }
     
    public static String callBlockingGetRequest(String url) {
        System.out.println("request url: " + url);
        CloseableHttpClient client = HttpClients.createDefault();
        HttpGet request = new HttpGet(url);
        request.addHeader("Accept", "application/json");
        request.addHeader("x-api-key", API_KEY);        
        CloseableHttpResponse response = null;
        
        try {
            response = client.execute(request);
            HttpEntity entity = response.getEntity();
            System.out.println("response code: " + response.getStatusLine().getStatusCode());

            BufferedReader rd = new BufferedReader(new InputStreamReader(response.getEntity().getContent()));

            StringBuffer result = new StringBuffer();
            String line = "";
            while ((line = rd.readLine()) != null) {
                result.append(line);
            }
            System.out.println("response body: " + result.toString());
            EntityUtils.consume(entity);
            return result.toString();
        } catch (Exception e) {
            System.out.println("Error executing API call: " + e.getMessage());
            try {
                response.close();
            } catch (Exception ignored) {
                // ignored
            }
        }
        return null;
    }
    
    public static String getActualNetCode(String json) {
        System.out.println("json " + json);
        String rawString = json.substring(1, json.length() - 1);
        System.out.println("rawString " + rawString);
        String quotedString = rawString.split(",")[6].split(":")[1];
        return quotedString.substring(1, quotedString.length() - 1);
    }
        
}
