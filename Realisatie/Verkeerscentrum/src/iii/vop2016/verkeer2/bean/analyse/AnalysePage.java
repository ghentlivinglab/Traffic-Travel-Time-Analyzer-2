/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package iii.vop2016.verkeer2.bean.analyse;

import iii.vop2016.verkeer2.ejb.components.Route;
import java.security.Timestamp;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Map;
import javafx.util.Pair;
import javax.faces.context.FacesContext;
import javax.servlet.http.HttpServletRequest;

/**
 *
 * @author Mike
 */
abstract class AnalysePage {
    
    protected List<Route> routes;
    protected List<Pair<Date,Date>> periods;
    protected List<String> dataProviders;
    
    protected List<Route> availableRoutes;
    protected List<String> availableDataProviders;
    
    
    protected abstract String getTitle();
    protected abstract String getSubTitle();
    
    
    public AnalysePage(){
        routes = new ArrayList<>();
        dataProviders = new ArrayList<>();
        periods = new ArrayList<>();
        availableDataProviders = new ArrayList<>();
        availableRoutes = new ArrayList<>();
        
        try{
            // AVAILABLE ROUTES
            for(int i=0; i<10; i++){
                long id = (long)i;
                availableRoutes.add(new Route(id, "Route "+id));
            }
            // AVAILABLE DATAPROVIDERS
            availableDataProviders.add("GoogleMaps");
            availableDataProviders.add("Here");
            availableDataProviders.add("TomTom");
            
            // URL PARSEN
            HttpServletRequest request = (HttpServletRequest) FacesContext.getCurrentInstance().getExternalContext().getRequest();        
            Map<String, String[]> parameterMap = request.getParameterMap();
            
            // ROUTES
            String[] ids = parameterMap.get("routeId");
            for(String s : ids){
                long id = Long.parseLong(s);
                routes.add(new Route(id, "Route "+id));
            }
            
            // DATAPROVIDERS
            String[] providers = parameterMap.get("providers");
            if(providers != null){
                this.dataProviders.addAll(Arrays.asList(providers));
            }
            this.dataProviders.addAll(availableDataProviders);
            
            // PERIODES
            String[] periodsStart = parameterMap.get("periodStart");
            String[] periodsEnd = parameterMap.get("periodEnd");
            for(int i=0; i<periodsStart.length; i++){
                periods.add(new Pair(new Date(Long.parseLong(periodsStart[i])),new Date(Long.parseLong(periodsEnd[i]))));
            }
        }catch(Exception ex){
            System.out.println(ex);
        }
        
    }

    public List<Route> getRoutes() {
        return routes;
    }

    public List<Pair<Date,Date>> getPeriods() {
        return periods;
    }

    public List<String> getDataProviders() {
        return dataProviders;
    }

    public List<Route> getAvailableRoutes() {
        return availableRoutes;
    }

    public List<String> getAvailableDataProviders() {
        return availableDataProviders;
    }
    
    
    
    
}
