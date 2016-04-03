/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package iii.vop2016.verkeer2.bean.analyse;

import iii.vop2016.verkeer2.bean.components.Route;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.RequestScoped;
import javax.faces.context.FacesContext;
import javax.servlet.http.HttpServletRequest;

/**
 *
 * @author Mike
 */
@ManagedBean(name = "routeDAO", eager = true)
@RequestScoped
public class RouteDAO {

    protected List<Route> availableRoutes;
    protected List<Route> selectedRoutes;

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
        for(int i=0; i<10; i++){
            long id = (long)i;
            availableRoutes.add(new Route(id, "Route "+id));
        }
        
        HttpServletRequest request = (HttpServletRequest) FacesContext.getCurrentInstance().getExternalContext().getRequest();        
        Map<String, String[]> parameterMap = request.getParameterMap();
        String[] ids = parameterMap.get("routeId");
        if(ids != null){
            for(String s : ids){
                long id = Long.parseLong(s);
                selectedRoutes.add(new Route(id, "Route "+id));
            }
        }
        
            
    }
    
}
