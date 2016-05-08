/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package iii.vop2016.verkeer2.bean.settings;

import iii.vop2016.verkeer2.bean.auth.Login;
import iii.vop2016.verkeer2.bean.helpers.JSONMethods;
import iii.vop2016.verkeer2.ejb.components.IRoute;
import iii.vop2016.verkeer2.ejb.components.IThreshold;
import iii.vop2016.verkeer2.ejb.components.Route;
import iii.vop2016.verkeer2.ejb.components.Threshold;
import iii.vop2016.verkeer2.ejb.dao.IGeneralDAO;
import iii.vop2016.verkeer2.ejb.helper.BeanFactory;
import iii.vop2016.verkeer2.ejb.helper.HelperFunctions;
import iii.vop2016.verkeer2.ejb.helper.VerkeerLibToJson;
import iii.vop2016.verkeer2.ejb.threshold.IThresholdManager;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.faces.bean.ApplicationScoped;
import javax.faces.bean.ManagedBean;
import javax.inject.Named;
import javax.faces.bean.RequestScoped;
import javax.faces.bean.SessionScoped;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import org.json.JSONArray;
import org.json.JSONObject;

/**
 *
 * @author Mike
 */
@ManagedBean(name = "routeSettings", eager = true)
@RequestScoped
public class RouteSettings implements Serializable{

    protected static Properties prop;
    protected InitialContext ctx;
    protected BeanFactory beanFactory;
    protected static final String JNDILOOKUP_PROPERTYFILE = "resources/properties/WebSettings";
    
    protected long id;
    protected IRoute route;
    protected List<IThreshold> thresholds;
    
    
    protected List<IRoute> routes;

    public RouteSettings() {
        try {
            beanFactory = BeanFactory.getInstance(ctx, null);
            ctx = new InitialContext();
            prop = getProperties();
            
            routes = getAllRoutes();
            
        } catch (NamingException ex) {
            Logger.getLogger(RouteSettings.class.getName()).log(Level.SEVERE, null, ex);
        }  
    }
    
    /*
    ID
    */
    public long getId() {
        return id;
    }
   
    public void setId(long id) {
        this.id = id;
        setRoute(getRoute(id));
        setThresholds(downloadThresholds(getRoute(id)));
    }
    
   

    
    /*
    ROUTE
    */
    public IRoute getRoute() {
        return route;
    }

    public void setRoute(IRoute route) {
        this.route = route;
    }
    
    
    /*
    THRESHOLDS
    */
    public void setThresholds(List<IThreshold> thresholds) {
        this.thresholds = thresholds;
    }

    public List<IThreshold> getThresholds() {
        return thresholds;
    }
     
    
     
    
    
    private Properties getProperties() {
        return HelperFunctions.RetrievePropertyFile(JNDILOOKUP_PROPERTYFILE, ctx, Logger.getGlobal());
    }
    
    public List<IRoute> getAllRoutes(){
        // AVAILABLE ROUTES
        List<IRoute> result = new ArrayList<>();
        String urlAllRoutes = prop.getProperty("urlRoutes");
        urlAllRoutes = urlAllRoutes.replaceAll("\\{id\\}", "all");
        JSONArray routesArray = JSONMethods.getArrayFromURL(urlAllRoutes, prop);
        for(int i=0; i<routesArray.length(); i++){
            IRoute route_temp = new Route();
            result.add(VerkeerLibToJson.fromJson(routesArray.getJSONObject(i),route_temp));
        }
        /*
        IGeneralDAO generalDAO = beanFactory.getGeneralDAO();
        result = generalDAO.getRoutes();
        */
        return result;
    }
    
    public IRoute getRoute(long id){
        
        int i=0;
        while(i<routes.size()){
            if(routes.get(i).getId() == id){
                return routes.get(i);
            }
            i++;
        }
        
        return null;
    }
    

    private List<IThreshold> downloadThresholds(IRoute route) {
        List<IThreshold> result = new ArrayList<>();
        String url = prop.getProperty("urlThresholds");
        url = url.replaceAll("\\{id\\}", ""+route.getId());
        JSONArray array = JSONMethods.getObjectFromURL(url, prop).getJSONArray("thresholds");
        for(int i=0; i<array.length(); i++){
            result.add(VerkeerLibToJson.fromJson(array.getJSONObject(i),new Threshold()));
        }
        return result;
    }

  
    
}
