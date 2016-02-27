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
import javax.annotation.PostConstruct;
import javax.ejb.Singleton;

/**
 *
 * @author Mike
 */
@Singleton
public class GeneralDAONoDB implements GeneralDAONoDBRemote {

    private Set<IGeoLocation> geolocations;
    private Set<IRoute> routes;
    
    private long lastRouteIndex;
    private long lastGeoLocationIndex;
    
    
    @PostConstruct
    private void init(){
        geolocations = new HashSet<>();
        routes = new HashSet<>();
        lastRouteIndex = 0;
        lastGeoLocationIndex = 0;
    }
    
    @Override
    public List<IRoute> getRoutes() {
        return new ArrayList<>(routes);
    }

    @Override
    public IRoute getRoute(String name) {
        return null;
    }

    @Override
    public IRoute addRoute(IRoute route) {
        System.out.println("Routes momenteel aanwezig voor toevoegen");
        for(IRoute r : routes){
            System.out.println(" - " + r);
        }
        for(IGeoLocation location : route.getGeolocations()){
            addGeoLocation(location);
            location.setRoute(route);
        }
        System.out.println("Bevat systeem "+route.getInverseRoute()+"?: " + routes.contains(route.getInverseRoute()));
        if(route.getInverseRoute() != null && !route.getInverseRoute().equals(route) && !isRouteStored(route.getInverseRoute())){
            addRoute(route.getInverseRoute());          
        }
        routes.add(route); 
        route.setId(++lastRouteIndex);
        System.out.println("Nieuwe route toegevoegd: "+route);
        return route;
    }

    @Override
    public void removeRoute(IRoute route) {

    }

    @Override
    public void addGeoLocation(IGeoLocation geolocation) {
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
