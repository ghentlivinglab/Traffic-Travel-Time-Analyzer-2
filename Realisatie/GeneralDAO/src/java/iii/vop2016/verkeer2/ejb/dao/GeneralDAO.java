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
import iii.vop2016.verkeer2.ejb.components.IThreshold;
import iii.vop2016.verkeer2.ejb.components.Route;
import iii.vop2016.verkeer2.ejb.components.Threshold;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
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
        try {
            em.persist(r);
            em.flush();
        } catch (Exception ex) {
            Logger.getLogger(GeneralDAO.class.getName()).log(Level.SEVERE, null, ex);
        }

        route = new Route(r);
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

        List<IGeoLocation> ret = new ArrayList<>();
        for (GeoLocationMappingEntity e : list) {
            ret.add(new GeoLocation(e));
        }

        Collections.sort(ret, new GeoLocationComparator());

        return ret;
    }

    @Override
    public List<IGeoLocation> setRouteMappingGeolocations(IRoute route, List<IGeoLocation> geolocs) {
        List<IGeoLocation> retLocs = new ArrayList<>();
        try {

            route = new RouteEntity(route);
            //vind ge dit wel veilig om zo met route te werken? ja, is handig, ok,
            List<IGeoLocation> storedLocs = getRouteMappingGeolocations(route);
            if (storedLocs.size() == 0) {
                for (IGeoLocation geoloc : geolocs) {
                    GeoLocationMappingEntity location = new GeoLocationMappingEntity(geoloc, route);
                    em.persist(location);
                    retLocs.add(new GeoLocation(location));
                }
            }

            em.flush();
        } catch (Exception ex) {
            Logger.getLogger(GeneralDAO.class.getName()).log(Level.SEVERE, null, ex);
        }

        return retLocs;
    }

    @Override
    public List<IRoute> getRoutes(List<Long> ids) {
        List<IRoute> ret = new ArrayList<>();
        try {
            Query q = em.createQuery("SELECT g FROM RouteEntity g WHERE g.id in :id");
            q.setParameter("id", ids);
            List<IRoute> routes = q.getResultList();

            for (IRoute r : routes) {
                IRoute newR = new Route(r);
                List<IGeoLocation> list = new ArrayList<>();
                for (IGeoLocation geo : r.getGeolocations()) {
                    list.add(new GeoLocation(geo));
                }
                newR.setGeolocations(list);
                ret.add(newR);
            }
        } catch (Exception e) {
            Logger logger = Logger.getLogger(this.getClass().getName());
            logger.severe(e.getMessage());
        } finally {

        }
        return ret;
    }

    @Override
    public Map<IRoute, List<IThreshold>> getThresholds() {
        Map<IRoute, List<IThreshold>> ret = new HashMap<>();
        List<IRoute> routes = getRoutes();
        try {
            Query q = em.createQuery("SELECT t FROM ThresholdEntity t");
            List<ThresholdEntity> resultList = q.getResultList();

            for (ThresholdEntity entity : resultList) {
                IThreshold t = new Threshold(entity);
                t.setRoute(getRoute(routes, t.getRouteId()));
                if (ret.containsKey(t.getRoute())) {
                    ret.get(t.getRoute()).add(t);
                } else {
                    ArrayList<IThreshold> l = new ArrayList<>();
                    l.add(t);
                    ret.put(t.getRoute(), l);
                }
            }

        } catch (Exception e) {
            Logger logger = Logger.getLogger(this.getClass().getName());
            logger.severe(e.getMessage());
        } finally {

        }
        return ret;
    }

    @Override
    public IThreshold addThreshold(IThreshold threshold) {
        ThresholdEntity th = new ThresholdEntity(threshold);
        try {
            em.persist(th);
            threshold = new Threshold(th);
        } catch (Exception e) {
            Logger logger = Logger.getLogger(this.getClass().getName());
            logger.severe(e.getMessage());
        } finally {

        }
        return threshold;
    }

    private IRoute getRoute(List<IRoute> routes, long routeId) {
        for (IRoute r : routes) {
            if (r.getId() == routeId) {
                return r;
            }
        }
        return null;
    }
}
