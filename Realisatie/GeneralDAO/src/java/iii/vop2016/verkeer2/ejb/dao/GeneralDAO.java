/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package iii.vop2016.verkeer2.ejb.dao;

import iii.vop2016.verkeer2.ejb.components.IGeoLocation;
import iii.vop2016.verkeer2.ejb.components.IRoute;
import java.util.ArrayList;
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
        List<IRoute> routes = null;
        try {
            //get all routes
            routes = em.createQuery("SELECT r FROM RouteEntity r").getResultList();
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
        return r;
    }

    @Override
    public void removeRoute(IRoute route) {
        if (route instanceof RouteEntity) {
            route = em.merge(route);
            em.remove(route);
        } else if (route.getId() != 0) {
            for(IGeoLocation loc : route.getGeolocations()){
                Query q = em.createQuery("Delete FROM GeoLocationEntity r WHERE r.id = :name");
                q.setParameter("name", loc.getId());
                q.executeUpdate();
            }
            Query q = em.createQuery("Delete FROM RouteEntity r WHERE r.id = :name");
            q.setParameter("name", route.getId());
            q.executeUpdate();
        }
    }
}
