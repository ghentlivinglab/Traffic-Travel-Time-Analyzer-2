/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package iii.vop2016.verkeer2.bean.helpers;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 *
 * @author Mike
 */
public class JSONMethods {
    
    public static JSONObject getObjectFromURL(String url){
        JSONObject jsonobj;

        //This section interpretes the JSON
        jsonobj = new JSONObject(getContentFromURL(url));
       
        return jsonobj;
    }
    
    public static JSONArray getArrayFromURL(String url){
        JSONArray jsonobj;

        //This section interpretes the JSON
        jsonobj = new JSONArray(getContentFromURL(url));

        return jsonobj;
    }
    
    public static String getContentFromURL(String url_string){
        StringBuilder response = new StringBuilder();
        try {
            URL url = new URL(url_string);
            HttpURLConnection con = (HttpURLConnection) url.openConnection();
            
            //Optional, GET is default
            con.setRequestMethod("GET");

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
}
