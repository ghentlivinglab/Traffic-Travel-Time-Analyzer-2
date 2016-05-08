/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package iii.vop2016.verkeer2.bean.helpers;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.json.JSONArray;
import org.json.JSONObject;

/**
 *
 * @author Mike
 */
public class JSONMethods {
    
    
    
    public static JSONObject getObjectFromURL(String url, Properties prop){
        JSONObject jsonobj;

        //This section interpretes the JSON
        jsonobj = new JSONObject(getContentFromURL(url, prop));
       
        return jsonobj;
    }
    
    public static JSONArray getArrayFromURL(String url, Properties prop){
        JSONArray jsonobj;

        //This section interpretes the JSON
        String s = getContentFromURL(url, prop);
        if(s.length()==0){
            jsonobj = new JSONArray();
        }else{
            jsonobj = new JSONArray(s);
        }

        return jsonobj;
    }
    
    public static String getContentFromURL(String url_string, Properties prop){
        StringBuilder response = new StringBuilder();
        try {
            URL url = new URL(url_string);
            HttpURLConnection con = (HttpURLConnection) url.openConnection();
            
            //Optional, GET is default
            con.setRequestMethod("GET");
            String basicAuth = "Basic "+prop.getProperty("AuthorizationBasic","");
            con.setRequestProperty ("Authorization", basicAuth);

            try ( BufferedReader in = new BufferedReader(
                            new InputStreamReader(con.getInputStream()))) {
                String inputLine;
                while ((inputLine = in.readLine()) != null) {
                    response.append(inputLine);
                }
            } catch (IOException ex) {
                Logger.getLogger(JSONMethods.class.getName()).log(Level.SEVERE, null, ex);
            }
        }catch (Exception ex) {
            Logger.getLogger(JSONMethods.class.getName()).log(Level.SEVERE, null, ex);
        }
        return response.toString();
    }
        
    public static void postObjectToURL(String surl, JSONObject obj, Properties prop){
        
        
        postStringToURL(surl, obj.toString(), prop);
        
    }
    
    public static void postArrayToURL(String surl, JSONArray obj, Properties prop){
        
        postStringToURL(surl, obj.toString(), prop);
        
    }
    
    public static void postStringToURL(String surl, String obj, Properties prop){
        try {
                       
            String basicAuth = "Basic "+prop.getProperty("AuthorizationBasic","");
            URL url = new URL( surl );
            HttpURLConnection con= (HttpURLConnection) url.openConnection();

            //add request header
            con.setRequestMethod("POST");
            con.setRequestProperty("Accept-Language", "en-US,en;q=0.5");
            con.setRequestProperty( "Content-Type", "application/json");
            con.setRequestProperty("Authorization", basicAuth);

            String urlParameters = obj;

            // Send post request
            con.setDoOutput(true);
            DataOutputStream wr = new DataOutputStream(con.getOutputStream());
            wr.writeBytes(urlParameters);
            wr.flush();
            wr.close();

            int responseCode = con.getResponseCode();
            System.out.println("\nSending 'POST' request to URL : " + url);
            System.out.println("Post parameters : " + urlParameters);
            System.out.println("Response Code : " + responseCode);

            BufferedReader in = new BufferedReader(
                    new InputStreamReader(con.getInputStream()));
            String inputLine;
            StringBuffer response = new StringBuffer();

            while ((inputLine = in.readLine()) != null) {
                response.append(inputLine);
            }
            in.close();

            //print result
            System.out.println(response.toString());
        } catch (MalformedURLException ex) {
            Logger.getLogger(JSONMethods.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(JSONMethods.class.getName()).log(Level.SEVERE, null, ex);
        }
                
    }
    
}
