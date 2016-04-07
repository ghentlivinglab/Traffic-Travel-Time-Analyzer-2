/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package iii.vop2016.verkeer2.bean.analyse;

import iii.vop2016.verkeer2.bean.components.Route;
import iii.vop2016.verkeer2.bean.helpers.JSONMethods;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.RequestScoped;
import javax.faces.context.FacesContext;
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
    
    private static String urlAllRoutes = "http://localhost:8080/RestApi/v2/routes/all";

    public List<Route> getSelectedRoutes() {
        return selectedRoutes;
    }

    public List<Route> getAvailableRoutes() {
        return availableRoutes;
    }
    

    public RouteDAO() {
        availableRoutes = new ArrayList<>();
        selectedRoutes = new ArrayList<>();
        
        //
        // AJAX CALL OM ROUTES OP TE HALEN
        //
        
         
        // AVAILABLE ROUTES
        JSONArray routes = JSONMethods.getArrayFromURL(urlAllRoutes);
        for(int i=0; i<routes.length(); i++){
            JSONObject routeJSON = (JSONObject)routes.get(i);
            availableRoutes.add(JSONtoRoute(routeJSON));
        }
        
        // SELECTED ROUTES
        HttpServletRequest request = (HttpServletRequest) FacesContext.getCurrentInstance().getExternalContext().getRequest();        
        Map<String, String[]> parameterMap = request.getParameterMap();
        String[] ids = parameterMap.get("routeId");
        if(ids != null){
            for(String s : ids){
                long id = Long.parseLong(s);
                selectedRoutes.add(getRoute(id));
            }
        }
        
            
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
    
    
    
}
