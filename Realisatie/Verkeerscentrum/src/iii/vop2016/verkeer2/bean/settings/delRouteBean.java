/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package iii.vop2016.verkeer2.bean.settings;

import iii.vop2016.verkeer2.bean.settings.*;
import iii.vop2016.verkeer2.bean.auth.Login;
import iii.vop2016.verkeer2.bean.helpers.JSONMethods;
import static iii.vop2016.verkeer2.bean.settings.RouteSettings.prop;
import iii.vop2016.verkeer2.ejb.components.IRoute;
import iii.vop2016.verkeer2.ejb.components.IThreshold;
import iii.vop2016.verkeer2.ejb.dao.IGeneralDAO;
import iii.vop2016.verkeer2.ejb.helper.BeanFactory;
import iii.vop2016.verkeer2.ejb.helper.VerkeerLibToJson;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.RequestScoped;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import org.json.JSONObject;

/**
 *
 * @author Mike
 */
@ManagedBean(name = "delRouteBean", eager = true)
@RequestScoped
public class delRouteBean {

    @ManagedProperty(value="#{routeSettings}")
    protected RouteSettings routeSettings;
    
    private InitialContext ctx;
    private BeanFactory beanFactory;


    public delRouteBean() {
        System.out.println("del route bean init");
        try {
            ctx = new InitialContext();
        } catch (NamingException ex) {
            Logger.getLogger(Login.class.getName()).log(Level.SEVERE, null, ex);
        }
        beanFactory = BeanFactory.getInstance(ctx, null);
    }
   
    public String submit(){
        //doe een rest-call om route op te slaan
        
        JSONObject obj = VerkeerLibToJson.toJson(getRoute());
        String url = prop.getProperty("urlRemoveRoute");
        url = url.replaceAll("\\{apikey\\}", ""+prop.getProperty("apiKey"));
        url = url.replaceAll("\\{id\\}", ""+getRoute().getId());
        JSONMethods.postObjectToURL(url, obj, prop);
    
        return "pretty:settings-routes";
    }
    

    /*
    ID
    */ 
    public long getId() {
        return routeSettings.id;
    }
   
    public void setId(long id) {
        routeSettings.id = id;
    }
    
    /*
    THRESHOLDS
    */
    public void setThresholds(List<IThreshold> thresholds) {
        routeSettings.thresholds = thresholds;
    }
    
    public List<IThreshold> getThresholds() {
        return routeSettings.thresholds;
    }

    /*
    ROUTES
    */
    public IRoute getRoute() {
        return routeSettings.route;
    }

    public void setRoute(IRoute route) {
        routeSettings.route = route;
    }
    
    /*
    ROUTESETTINGS (SUPER)
    */
    public RouteSettings getRouteSettings() {
        return routeSettings;
    }

    public void setRouteSettings(RouteSettings routeSettings) {
        this.routeSettings = routeSettings;
    }
    
    
    
    
}
