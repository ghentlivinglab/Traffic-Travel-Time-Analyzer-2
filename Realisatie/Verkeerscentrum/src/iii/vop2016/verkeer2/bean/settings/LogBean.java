/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package iii.vop2016.verkeer2.bean.settings;

import iii.vop2016.verkeer2.bean.helpers.JSONMethods;
import static iii.vop2016.verkeer2.bean.settings.RouteSettings.prop;
import iii.vop2016.verkeer2.ejb.helper.BeanFactory;
import iii.vop2016.verkeer2.ejb.helper.HelperFunctions;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.inject.Named;
import javax.enterprise.context.Dependent;
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

@ManagedBean(name = "serverLogBean", eager = true)
@RequestScoped
public class LogBean {
    
    protected static Properties prop;
    protected InitialContext ctx;
    protected BeanFactory beanFactory;
    protected static final String JNDILOOKUP_PROPERTYFILE = "resources/properties/WebSettings";
    
    private List<JSONObject> logs;
    /**
     * Creates a new instance of LogBean
     */
    public LogBean() {
        try {
            beanFactory = BeanFactory.getInstance(ctx, null);
            ctx = new InitialContext();
            prop = getProperties();
            
            String apikey = prop.getProperty("apiKey");
            String urlLogging = prop.getProperty("urlServerLogs");
            urlLogging = urlLogging.replaceAll("\\{apikey\\}", apikey);
            
            logs = new ArrayList<>();
            JSONArray logsarray = JSONMethods.getArrayFromURL(urlLogging, prop);
            for(int i=0; i<logsarray.length(); i++){
                logs.add(logsarray.getJSONObject(i));
            }
            
            JSONObject obj = new JSONObject("");
            String url = "http://localhost:8080/RestApi/v2/routes/init?key=mike";
            JSONMethods.postObjectToURL(url, obj, prop);

        } catch (NamingException ex) {
            Logger.getLogger(RouteSettings.class.getName()).log(Level.SEVERE, null, ex);
        }  
        
    }
    
        
    private Properties getProperties() {
        return HelperFunctions.RetrievePropertyFile(JNDILOOKUP_PROPERTYFILE, ctx, Logger.getGlobal());
    }

    public List<JSONObject> getLogs() {
        return logs;
    }

    
    
    
}
