/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package iii.vop2016.verkeer2.ejb.dao.dummy;

import iii.vop2016.verkeer2.ejb.components.IGeoLocation;
import iii.vop2016.verkeer2.ejb.components.IRoute;
import iii.vop2016.verkeer2.ejb.components.Route;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.annotation.PostConstruct;
import javax.ejb.Singleton;

/**
 *
 * @author Mike
 */
@Singleton
public class GeneralDAONoDB implements GeneralDAONoDBRemote {

    private List<IGeoLocation> geolocations;
    private List<IRoute> routes;
    
    private long lastRouteIndex;
    private long lastGeoLocationIndex;
    
    
    @PostConstruct
    private void init(){
        geolocations = new ArrayList<>();
        routes = new ArrayList<>();
        lastRouteIndex = 0;
        lastGeoLocationIndex = 0;
        Logger.getLogger("logger").log(Level.INFO, "GeneralNoDB has been initialized.");  
    }
    
    @Override
    public List<IRoute> getRoutes() {
        return routes;
    }

    @Override
    public IRoute getRoute(String name) {
        return null;
    }
    
    public IRoute getRoute(long id) {
        IRoute result = null;
        int i = 0;
        while(i < routes.size()){
            if(routes.get(i).getId() == id){
                result = routes.get(i);
                i = routes.size();
            }
            i++;
        }
        return result;  
    }

    @Override
    public IRoute addRoute(IRoute route) {
        System.out.println("Routes momenteel aanwezig voor toevoegen");
        for(IRoute r : routes){
            System.out.println(" - " + r);
        }
        for(IGeoLocation location : route.getGeolocations()){
            addGeoLocation(location);
        }
        routes.add(route); 
        route.setId(++lastRouteIndex);
        System.out.println("Nieuwe route toegevoegd: "+route);
        return route;
    }

    @Override
    public void removeRoute(IRoute route) {

    }

    private void addGeoLocation(IGeoLocation geolocation) {
        geolocations.add(geolocation);
        geolocation.setId(++lastGeoLocationIndex);
    }

    private boolean isRouteStored(IRoute route) {
        for(IRoute r : routes){
            if(r.equals(route))
                return true;
        }
        return false;
    }

}
