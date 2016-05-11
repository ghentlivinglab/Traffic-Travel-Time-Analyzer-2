/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package iii.vop2016.verkeer2.bean.settings;

import com.sun.faces.facelets.tag.jsf.core.ViewHandler;
import iii.vop2016.verkeer2.bean.APIKey.APIKey;
import iii.vop2016.verkeer2.bean.settings.*;
import iii.vop2016.verkeer2.bean.helpers.JSONMethods;
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
import javax.faces.component.UIViewRoot;
import javax.faces.context.FacesContext;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import org.json.JSONArray;
import org.json.JSONObject;

/**
 *
 * @author Mike
 */
@ManagedBean(name = "apikeyBean", eager = true)
@RequestScoped
public class ApiKeyBean implements Serializable {

    protected static Properties prop;
    protected InitialContext ctx;
    protected BeanFactory beanFactory;
    protected static final String JNDILOOKUP_PROPERTYFILE = "resources/properties/WebSettings";

    protected List<JSONObject> apikeys;

    public ApiKeyBean() {
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

    public List<JSONObject> getApikeys() {
        return apikeys;
    }

    public void genereerNieuw() {
        prop = getProperties();

        String apikey = prop.getProperty("apiKey");
        String urlApi = prop.getProperty("urlApiKeysGenerate");
        urlApi = urlApi.replaceAll("\\{apikey\\}", apikey);

        JSONObject o = JSONMethods.getObjectFromURL(urlApi, prop);

        try {
            Thread.sleep(300);
        } catch (InterruptedException ex) {
            Logger.getLogger(ApiKeyBean.class.getName()).log(Level.SEVERE, null, ex);
        }
        
         fillKeys();

    }

    public void ongeldigMaken(String key, String id, String active) {

        prop = getProperties();

        String apikey = prop.getProperty("apiKey");
        String urlApi = prop.getProperty("urlApiKeysInvalidate");
        urlApi = urlApi.replaceAll("\\{apikey\\}", apikey);

        APIKey k = new APIKey();
        k.setActive(Integer.parseInt(active));
        k.setKeyString(key);
        k.setId(Integer.parseInt(id));

        JSONObject o = VerkeerLibToJson.toJson(k);
        JSONMethods.postObjectToURL(urlApi, o, prop);

        try {
            Thread.sleep(300);
        } catch (InterruptedException ex) {
            Logger.getLogger(ApiKeyBean.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        fillKeys();
    }

    private void fillKeys() {
        prop = getProperties();

        String apikey = prop.getProperty("apiKey");
        String urlApi = prop.getProperty("urlApiKeys");
        urlApi = urlApi.replaceAll("\\{apikey\\}", apikey);

        apikeys = new ArrayList<>();
        JSONArray array = JSONMethods.getArrayFromURL(urlApi, prop);
        for (int i = 0; i < array.length(); i++) {
            apikeys.add(array.getJSONObject(i));
        }
    }

}
