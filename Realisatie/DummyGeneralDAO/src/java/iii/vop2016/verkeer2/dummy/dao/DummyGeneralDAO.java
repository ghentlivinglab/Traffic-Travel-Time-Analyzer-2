/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package iii.vop2016.verkeer2.dummy.dao;

import iii.vop2016.verkeer2.ejb.components.IGeoLocation;
import iii.vop2016.verkeer2.ejb.components.IRoute;
import iii.vop2016.verkeer2.ejb.components.IThreshold;
import iii.vop2016.verkeer2.ejb.dao.GeneralDAORemote;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.annotation.PostConstruct;
import javax.ejb.Singleton;

/**
 *
 * @author tobia
 */
@Singleton
public class DummyGeneralDAO implements GeneralDAORemote {

    private List<IRoute> routes;
    private Map<IRoute, List<IGeoLocation>> geolocationsMappings;
    private Map<IRoute, List<IThreshold>> thresholds;
    private int index = 0;
    private int indexGeo = 0;
     private int indexTh = 0;

    @PostConstruct
    public void init() {
        routes = new ArrayList<>();
        geolocationsMappings = new HashMap<>();
        thresholds = new HashMap<>();
    }

    @Override
    public List<IRoute> getRoutes() {
        return routes;
    }

    @Override
    public List<IRoute> getRoutes(List<Long> ids) {
        List<IRoute> ret = new ArrayList<>();
        for (IRoute r : routes) {
            if (ids.contains(r.getId())) {
                ret.add(r);
            }
        }
        return ret;
    }

    @Override
    public IRoute getRoute(String name) {
        IRoute ret = null;
        for (IRoute r : routes) {
            if (r.getName().equals(name)) {
                ret = r;
            }
        }
        return ret;
    }

    @Override
    public IRoute getRoute(long id) {
        IRoute ret = null;
        for (IRoute r : routes) {
            if (r.getId() == id) {
                ret = r;
            }
        }
        return ret;
    }

    @Override
    public IRoute addRoute(IRoute route) {
        route.setId(index++);
        routes.add(route);
        return route;
    }

    @Override
    public void removeRoute(IRoute route) {
        routes.remove(route);
    }

    @Override
    public List<IGeoLocation> getRouteMappingGeolocations(IRoute route) {
        return geolocationsMappings.get(route);
    }

    @Override
    public List<IGeoLocation> setRouteMappingGeolocations(IRoute route, List<IGeoLocation> geolocs) {
        for (IGeoLocation geoloc  : geolocs) {
            geoloc.setId(indexGeo++);
        }
        geolocationsMappings.put(route, geolocs);
        return geolocs;
    }

    @Override
    public Map<IRoute, List<IThreshold>> getThresholds() {
        return thresholds;
    }

    @Override
    public IThreshold addThreshold(IThreshold threshold) {
        if(thresholds.containsKey(threshold.getRoute())){
            thresholds.get(threshold.getRoute()).add(threshold);
        }else{
            ArrayList<IThreshold> arr = new ArrayList<>();
            arr.add(threshold);
            thresholds.put(threshold.getRoute(), arr);
        }
        threshold.setId(indexTh++);
        return threshold;
    }

    @Override
    public void updateThreshold(IThreshold th) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void updateRoute(IRoute route) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void removeRouteMappingGeolocations(IRoute r) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

}
