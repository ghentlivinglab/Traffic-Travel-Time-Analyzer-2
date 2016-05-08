/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package iii.vop2016.verkeer2.bean.settings;

import iii.vop2016.verkeer2.bean.settings.*;
import iii.vop2016.verkeer2.bean.helpers.JSONMethods;
import static iii.vop2016.verkeer2.bean.settings.RouteSettings.prop;
import iii.vop2016.verkeer2.ejb.components.IRoute;
import iii.vop2016.verkeer2.ejb.components.IThreshold;
import iii.vop2016.verkeer2.ejb.components.Route;
import iii.vop2016.verkeer2.ejb.components.Threshold;
import iii.vop2016.verkeer2.ejb.helper.BeanFactory;
import iii.vop2016.verkeer2.ejb.helper.HelperFunctions;
import iii.vop2016.verkeer2.ejb.helper.VerkeerLibToJson;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.util.Pair;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.RequestScoped;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import org.json.JSONArray;
import org.json.JSONObject;

/**
 *
 * @author Mike
 */

@ManagedBean(name = "propertiesSettings", eager = true)
@RequestScoped
public class PropertiesSettings implements Serializable{

    protected static Properties prop;
    protected InitialContext ctx;
    protected BeanFactory beanFactory;
    protected static final String JNDILOOKUP_PROPERTYFILE = "resources/properties/WebSettings";

    protected Map<String, List<String>> keys;
    protected Map<String, List<String>> values;
    protected Map<String, Map<List<String>,List<String>>> files;
    protected Map<String, String> jndis;


    public PropertiesSettings() {
        try {
            beanFactory = BeanFactory.getInstance(ctx, null);
            ctx = new InitialContext();
            prop = getProperties();
            
            downloadProperties();

        } catch (NamingException ex) {
            Logger.getLogger(PropertiesSettings.class.getName()).log(Level.SEVERE, null, ex);
        }  
    }
    
    
    private Properties getProperties() {
        return HelperFunctions.RetrievePropertyFile(JNDILOOKUP_PROPERTYFILE, ctx, Logger.getGlobal());
    }
    
       

    private void downloadProperties() {
        keys = new HashMap<>();
        values = new HashMap<>();
        jndis = new HashMap<>();
        files =  new HashMap<>();
        
        String url = prop.getProperty("urlProperties");
        url = url.replaceAll("\\{apikey\\}", ""+prop.getProperty("apiKey"));
        JSONArray array = JSONMethods.getArrayFromURL(url, prop);
        for(int i=0; i<array.length(); i++){
            //
            // VERSCHILLENDE BESTANDEN EXTRAHEREN UIT JSON
            //
            String name = null;
            if(array.getJSONObject(i).getString("jndi").equals("resources/properties/Properties")){
                name = "Live-modus";
            }
            if(array.getJSONObject(i).getString("jndi").equals("resources/properties/Logger")){
                name = "Logging";
            }
            if(array.getJSONObject(i).getString("jndi").equals("resources/properties/TimerScheduler")){
                name = "Timescheduler";
            }
            if(array.getJSONObject(i).getString("jndi").equals("resources/properties/ThresholdManager")){
                name = "Thresholds";
            }
            if(array.getJSONObject(i).getString("jndi").equals("resources/properties/DataProvider")){
                name = "Core";
            }
            //
            // TOEVOEGEN AAN LIJST
            //
            if(name != null){
                Properties properties = VerkeerLibToJson.fromJson(array.getJSONObject(i), new Properties());
                Map<List<String>, List<String>> pair = new HashMap();
                pair.put(new ArrayList<String>(), new ArrayList<String>());
                for (Map.Entry<Object, Object> e : properties.entrySet()) {
                    String key = (String) e.getKey();
                    String value = (String) e.getValue();
                    for (Map.Entry<List<String>, List<String>> a : pair.entrySet()) {
                        a.getKey().add(key);
                        a.getValue().add(value);
                    }
                }
                files.put(name, pair);
                jndis.put(name, array.getJSONObject(i).getString("jndi"));
            }
            
        }
    }

    
    public String submit(){
        /*
        JSONArray array = new JSONArray();
        for (Map.Entry<String, Map<String, String>> file : files.entrySet()) {
            Properties properties = new Properties();
            Map<String, String> map = file.getValue();
            for (Map.Entry<String, String> entry : map.entrySet()) {
                String key = (String) entry.getKey();
                String value = (String) entry.getValue();
                properties.put(key, value);
            }
            JSONObject obj = VerkeerLibToJson.toJson(properties, jndis.get(file.getKey()));
            array.put(obj);
	}
        String url = prop.getProperty("urlUpdateProperties");
        url = url.replaceAll("\\{apikey\\}", ""+prop.getProperty("apiKey"));
        JSONMethods.postArrayToURL(url, array, prop);
        */
        return "pretty:settings-properties";
    }

    public Map<String, List<String>> getKeys() {
        return keys;
    }

    public Map<String, List<String>> getValues() {
        return values;
    }

    public Map<String, Map<List<String>, List<String>>> getFiles() {
        return files;
    }
    
    
    
  
    
}
