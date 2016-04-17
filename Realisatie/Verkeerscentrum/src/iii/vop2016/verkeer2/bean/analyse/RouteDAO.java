/*
* To change this license header, choose License Headers in Project Properties.
* To change this template file, choose Tools | Templates
* and open the template in the editor.
*/
package iii.vop2016.verkeer2.bean.analyse;

import static iii.vop2016.verkeer2.bean.analyse.AnalysePage.JNDILOOKUP_PROPERTYFILE;
import iii.vop2016.verkeer2.bean.components.DataProvider;
import iii.vop2016.verkeer2.bean.components.Route;
import iii.vop2016.verkeer2.bean.helpers.JSONMethods;
import iii.vop2016.verkeer2.ejb.helper.HelperFunctions;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.RequestScoped;
import javax.faces.context.FacesContext;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.servlet.http.HttpServletRequest;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 *
 * @author Mike
 */
@ManagedBean(name = "routeDAO", eager = true)
@RequestScoped
public class RouteDAO {
    
    protected List<Route> availableRoutes;
    protected List<Route> selectedRoutes;
    protected boolean multiRoutes;
    
    protected static Properties prop;
    protected InitialContext ctx;
   
    protected static final String JNDILOOKUP_PROPERTYFILE = "resources/properties/WebSettings";
    
    public List<Route> getSelectedRoutes() {
        return selectedRoutes;
    }
    
    public List<Route> getAvailableRoutes() {
        return availableRoutes;
    }
    
    
    public RouteDAO() {
        availableRoutes = new ArrayList<>();
        selectedRoutes = new ArrayList<>();
        
        
        try {
            ctx = new InitialContext();
            prop = getProperties();
         
        
            // AVAILABLE ROUTES
            JSONArray routes = JSONMethods.getArrayFromURL(getUrlAllRoutes());
            for(int i=0; i<routes.length(); i++){
                JSONObject routeJSON = (JSONObject)routes.get(i);
                availableRoutes.add(JSONtoRoute(routeJSON));
            }
            

            HttpServletRequest request = (HttpServletRequest) FacesContext.getCurrentInstance().getExternalContext().getRequest();
            Map<String, String[]> parameterMap = request.getParameterMap();
            // SELECTED ROUTES
            String[] ids = parameterMap.get("routeId");
            if(ids != null){
                for(String s : ids){
                    long id = Long.parseLong(s);
                    selectedRoutes.add(getRoute(id));
                }
            }
            
            

            // ROUTETYPE
            String[] stype = parameterMap.get("routetype");
            if(stype != null && stype.length>0 && stype[0].equals("multi")){
                multiRoutes = true;
            }else{
                multiRoutes = false;
            }
        
        } catch (NamingException ex) {
            Logger.getLogger(AnalysePage.class.getName()).log(Level.SEVERE, null, ex);
        }    


    }
    
    private Properties getProperties() {
        return HelperFunctions.RetrievePropertyFile(JNDILOOKUP_PROPERTYFILE, ctx, Logger.getGlobal());
    }
    
    public Route JSONtoRoute(JSONObject obj){
        long id = (long)obj.getInt("id");
        String name = obj.getString("name");
        Route route = new Route(id, name);
        return route;
    }
    
    private Route getRoute(long id) {
        int i=0;
        while(i < availableRoutes.size()){
            if(availableRoutes.get(i).getId()==id){
                return availableRoutes.get(i);
            }
            i++;
        }
        return null;
    }
    
    public boolean isSelected(Route route) {
        int i=0;
        while(i<selectedRoutes.size()){
            if(selectedRoutes.get(i).equals(route)) return true;
            i++;
        }
        return false;
    }
    
    
    public boolean isMultiRoutes() {
        return multiRoutes;
    }

    public static String getUrlAllRoutes() {
        String url = prop.getProperty("urlRoutes");
        url = url.replaceAll("\\{id\\}", "all");
        return url;
    }
    
    
}
