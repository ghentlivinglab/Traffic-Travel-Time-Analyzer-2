/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package iii.vop2016.verkeer2.bean.settings;

import iii.vop2016.verkeer2.bean.APIKey.APIKey;
import iii.vop2016.verkeer2.bean.auth.AuthHelpers;
import iii.vop2016.verkeer2.bean.auth.AuthUser;
import iii.vop2016.verkeer2.bean.helpers.JSONMethods;
import iii.vop2016.verkeer2.ejb.helper.BeanFactory;
import iii.vop2016.verkeer2.ejb.helper.HelperFunctions;
import iii.vop2016.verkeer2.ejb.helper.VerkeerLibToJson;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.RequestScoped;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import org.json.JSONArray;
import org.json.JSONObject;

/**
 *
 * @author tobia
 */
@ManagedBean(name = "GebruikersOverviewBean", eager = true)
@RequestScoped
public class GebruikersBean implements Serializable{


    protected static Properties prop;
    protected InitialContext ctx;
    protected BeanFactory beanFactory;
    protected static final String JNDILOOKUP_PROPERTYFILE = "resources/properties/WebSettings";

    protected List<JSONObject> gebruikers;

    public GebruikersBean() {
        try {
            beanFactory = BeanFactory.getInstance(ctx, null);
            ctx = new InitialContext();

            fillKeys();

        } catch (NamingException ex) {
            Logger.getLogger(ApiKeyBean.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private Properties getProperties() {
        return HelperFunctions.RetrievePropertyFile(JNDILOOKUP_PROPERTYFILE, ctx, Logger.getGlobal());
    }

    public List<JSONObject> getGebruikers() {
        return gebruikers;
    }
 
    public void genereerNieuw() {
        prop = getProperties();

        String apikey = prop.getProperty("apiKey");
        String urlApi = prop.getProperty("urlGebruikersNieuw");
        urlApi = urlApi.replaceAll("\\{apikey\\}", apikey);

        AuthUser u = new AuthUser();
        u.setId(0);
        u.setName(gebruikersNieuwName);
        u.setUsername(gebruikersNieuwUsername);
        u.setPassword(AuthHelpers.getMD5(gebruikersNieuwPassword));
        
        JSONObject o = VerkeerLibToJson.toJson(u);
        
        JSONMethods.postObjectToURL(urlApi, o, prop);

        try {
            Thread.sleep(300);
        } catch (InterruptedException ex) {
            Logger.getLogger(ApiKeyBean.class.getName()).log(Level.SEVERE, null, ex);
        }
        
         fillKeys();

    }
    
    public String gebruikersNieuwName = "";
    public String gebruikersNieuwUsername = "";
    public String gebruikersNieuwPassword = "";

    private void fillKeys() {
        prop = getProperties();

        String apikey = prop.getProperty("apiKey");
        String urlApi = prop.getProperty("urlGebruikers");
        urlApi = urlApi.replaceAll("\\{apikey\\}", apikey);

        gebruikers = new ArrayList<>();
        JSONArray array = JSONMethods.getArrayFromURL(urlApi, prop);
        for (int i = 0; i < array.length(); i++) {
            gebruikers.add(array.getJSONObject(i));
        }
    }

    public String getGebruikersNieuwName() {
        return gebruikersNieuwName;
    }

    public void setGebruikersNieuwName(String gebruikersNieuwName) {
        this.gebruikersNieuwName = gebruikersNieuwName;
    }

    public String getGebruikersNieuwUsername() {
        return gebruikersNieuwUsername;
    }

    public void setGebruikersNieuwUsername(String gebruikersNieuwUsername) {
        this.gebruikersNieuwUsername = gebruikersNieuwUsername;
    }

    public String getGebruikersNieuwPassword() {
        return gebruikersNieuwPassword;
    }

    public void setGebruikersNieuwPassword(String gebruikersNieuwPassword) {
        this.gebruikersNieuwPassword = gebruikersNieuwPassword;
    }
    
    
}
