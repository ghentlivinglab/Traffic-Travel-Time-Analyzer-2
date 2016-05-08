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
import javax.faces.bean.ManagedBean;
import javax.faces.bean.RequestScoped;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import org.json.JSONArray;
import org.json.JSONObject;
import iii.vop2016.verkeer2.bean.helpers.Pair;

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

    protected Map<String, String> jndis;
    
    protected Map<String, List<Pair<String,String>>> vars;


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
        jndis = new HashMap<>();
        vars = new HashMap<>();
        
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
                List<Pair<String,String>> temp = new ArrayList<>();
                for (Map.Entry<Object, Object> e : properties.entrySet()) {
                    String key = (String) e.getKey();
                    String value = (String) e.getValue();
                    Pair<String,String> pair = new Pair<String,String>(key, value); 
                    temp.add(pair);
                }
                vars.put(name, temp);
                jndis.put(name, array.getJSONObject(i).getString("jndi"));
            }
            
        }
    }

    
    public String submit(){
        
        JSONArray array = new JSONArray();
        for (Map.Entry<String, List<Pair<String, String>>> var : vars.entrySet()) {
            Properties properties = new Properties();
            String name = var.getKey();
            List<Pair<String, String>> list = var.getValue();
            for(int i=0; i<list.size(); i++){
                Pair<String,String> pair = list.get(i);
                properties.put(pair.getLeft(), pair.getRight());
            }
            JSONObject obj = VerkeerLibToJson.toJson(properties, jndis.get(var.getKey()));
            array.put(obj);
	}
        String url = prop.getProperty("urlUpdateProperties");
        url = url.replaceAll("\\{apikey\\}", ""+prop.getProperty("apiKey"));
        JSONMethods.postArrayToURL(url, array, prop);
        
        return "pretty:settings-properties";
    }

    public Map<String, List<Pair<String, String>>> getVars() {
        return vars;
    }

    
    
    
  
    
}
