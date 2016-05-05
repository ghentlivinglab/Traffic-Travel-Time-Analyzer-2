/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package iii.vop2016.verkeer2.ejb.dummy;

import iii.vop2016.verkeer2.ejb.components.IGeoLocation;
import iii.vop2016.verkeer2.ejb.components.IRoute;
import iii.vop2016.verkeer2.ejb.components.IThreshold;
import iii.vop2016.verkeer2.ejb.components.Route;
import iii.vop2016.verkeer2.ejb.dao.IGeneralDAO;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 *
 * @author Tobias
 */
public class GeneralDAODummy implements IGeneralDAO{

    private List<IGeoLocation> geolocations;
    private List<IRoute> routes;

    public GeneralDAODummy(){
        geolocations = new ArrayList<>();
        routes = new ArrayList<>();
    }
    
    @Override
    public List<IRoute> getRoutes() {
        return routes;
    }

    @Override
    public IRoute getRoute(String name) {
        return null;
    }
    
    @Override
    public IRoute getRoute(long id) {
        return null;
    }

    @Override
    public IRoute addRoute(IRoute route) {
        if(route != null){
            routes.add(route);
            for(IGeoLocation location : route.getGeolocations()){
                addGeoLocation(location);
            }
            route.setId(routes.size()-1);
        }
        return route;
    }

    @Override
    public void removeRoute(IRoute route) {
        int i = routes.indexOf(route);
        routes.add(i, new Route());
    }

    private void addGeoLocation(IGeoLocation geolocation) {
        geolocations.add(geolocation);
        geolocation.setId(geolocations.size()-1);
    }

    @Override
    public List<IGeoLocation> getRouteMappingGeolocations(IRoute route) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public List<IGeoLocation> setRouteMappingGeolocations(IRoute route, List<IGeoLocation> geolocs) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public List<IRoute> getRoutes(List<Long> ids) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public Map<IRoute, List<IThreshold>> getThresholds() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public IThreshold addThreshold(IThreshold threshold) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void updateThreshold(IThreshold th) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void updateRoute(IRoute route) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }


    
}
