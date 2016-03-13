/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package iii.vop2016.verkeer2.ejb.dao;

import iii.vop2016.verkeer2.ejb.components.GeoLocation;
import iii.vop2016.verkeer2.ejb.components.GeoLocationComparator;
import iii.vop2016.verkeer2.ejb.components.IGeoLocation;
import iii.vop2016.verkeer2.ejb.components.IRoute;
import iii.vop2016.verkeer2.ejb.components.Route;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.annotation.PostConstruct;
import javax.ejb.Singleton;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

/**
 *
 * @author Tobias
 */
@Singleton
public class GeneralDAO implements GeneralDAORemote {

    @PersistenceContext(name = "GeneralDBPU")
    EntityManager em;
    private InitialContext ctx;

    public GeneralDAO() {

    }

    @PostConstruct
    public void init() {
        try {
            ctx = new InitialContext();
        } catch (NamingException ex) {
            Logger.getLogger(GeneralDAO.class.getName()).log(Level.SEVERE, null, ex);
        }

        Logger.getLogger("logger").log(Level.INFO, "GeneralDAO has been initialized.");
    }

    @Override
    public List<IRoute> getRoutes() {
        List<IRoute> routes = new ArrayList<>();
        try {
            //get all routes
            List<IRoute> routesEntities = em.createQuery("SELECT r FROM RouteEntity r").getResultList();
            for (IRoute r : routesEntities) {
                IRoute newR = new Route(r);
                List<IGeoLocation> list = new ArrayList<>();
                for (IGeoLocation geo : r.getGeolocations()) {
                    list.add(new GeoLocation(geo));
                }
                newR.setGeolocations(list);
                routes.add(newR);
            }
        } catch (Exception e) {
            Logger logger = Logger.getLogger(this.getClass().getName());
            logger.severe(e.getMessage());
        } finally {

        }

        return routes;
    }

    @Override
    public IRoute getRoute(String name) {
        IRoute route = null;
        try {
            //get all routes
            Query q = em.createQuery("SELECT r FROM RouteEntity r WHERE r.name = :name");
            q.setParameter("name", name);

            List<IRoute> routes = q.getResultList();
            if (routes.size() >= 1) {
                route = routes.get(0);
                route = new Route(route);
                List<IGeoLocation> list = new ArrayList<>();
                for (IGeoLocation geo : route.getGeolocations()) {
                    list.add(new GeoLocation(geo));
                }
                route.setGeolocations(list);
            }
        } catch (Exception e) {
            Logger logger = Logger.getLogger(this.getClass().getName());
            logger.severe(e.getMessage());
        } finally {

        }
        return route;
    }

    @Override
    public IRoute getRoute(long id) {
        IRoute route = null;
        try {
            //get all routes
            Query q = em.createQuery("SELECT r FROM RouteEntity r WHERE r.id = :id");
            q.setParameter("id", id);
            List<IRoute> routes = q.getResultList();
            if (routes.size() >= 1) {
                route = routes.get(0);
                route = new Route(route);
                List<IGeoLocation> list = new ArrayList<>();
                for (IGeoLocation geo : route.getGeolocations()) {
                    list.add(new GeoLocation(geo));
                }
                route.setGeolocations(list);
            }
        } catch (Exception e) {
            Logger logger = Logger.getLogger(this.getClass().getName());
            logger.severe(e.getMessage());
        } finally {

        }
        return route;
    }

    @Override
    public IRoute addRoute(IRoute route) {
        RouteEntity r = new RouteEntity(route);
        List<IGeoLocation> l = new ArrayList<>();
        for (IGeoLocation loc : r.getGeolocations()) {
            l.add(new GeoLocationEntity(loc));
        }
        r.setGeolocations(l);

        try {
            em.persist(r);
        } catch (Exception ex) {
            Logger.getLogger(GeneralDAO.class.getName()).log(Level.SEVERE, null, ex);
        }

        route = new Route(r);
        List<IGeoLocation> list = new ArrayList<>();
        for (IGeoLocation geo : r.getGeolocations()) {
            list.add(new GeoLocation(geo));
        }
        route.setGeolocations(list);

        return route;
    }

    @Override
    public void removeRoute(IRoute route) {
        if (route instanceof RouteEntity) {
            route = em.merge(route);
            em.remove(route);
        } else if (route.getId() != 0) {
            for (IGeoLocation loc : route.getGeolocations()) {
                Query q = em.createQuery("Delete FROM GeoLocationEntity r WHERE r.id = :name");
                q.setParameter("name", loc.getId());
                q.executeUpdate();
            }
            Query q = em.createQuery("Delete FROM RouteEntity r WHERE r.id = :name");
            q.setParameter("name", route.getId());
            q.executeUpdate();
        }
    }

    @Override
    public List<IGeoLocation> getRouteMappingGeolocations(IRoute route) {
        Query q = em.createQuery("SELECT g FROM GeoLocationMappingEntity g WHERE g.route.id = :id");
        q.setParameter("id", route.getId());
        List<GeoLocationMappingEntity> list = q.getResultList();
        
        List<IGeoLocation> ret =  new ArrayList<>();
        for(GeoLocationMappingEntity e : list)
            ret.add(new GeoLocation(e));
        
        Collections.sort(ret, new GeoLocationComparator());
        
        return ret;
    }

    @Override
    public List<IGeoLocation> setRouteMappingGeolocations(IRoute route,List<IGeoLocation> geolocs) {
        route = new RouteEntity(route);
        List<IGeoLocation> retLocs = new ArrayList<>();
        List<IGeoLocation> storedLocs = getRouteMappingGeolocations(route);
        if(storedLocs.size() == 0){
            for(IGeoLocation geoloc : geolocs){
                GeoLocationMappingEntity location = new GeoLocationMappingEntity(geoloc, route);
                em.persist(location);
                retLocs.add(new GeoLocation(location));
            }
        }
        return retLocs;
    }
}
