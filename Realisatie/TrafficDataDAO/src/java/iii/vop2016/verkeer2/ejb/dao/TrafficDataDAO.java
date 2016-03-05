/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package iii.vop2016.verkeer2.ejb.dao;

import iii.vop2016.verkeer2.ejb.components.IGeoLocation;
import iii.vop2016.verkeer2.ejb.components.IRoute;
import iii.vop2016.verkeer2.ejb.components.IRouteData;
import iii.vop2016.verkeer2.ejb.components.RouteData;
import iii.vop2016.verkeer2.ejb.datasources.ISourceAdapter;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.annotation.PostConstruct;
import javax.ejb.Singleton;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.EntityTransaction;
import javax.persistence.Persistence;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

/**
 *
 * @author tobia
 */
@Singleton
public class TrafficDataDAO implements TrafficDataDAORemote {
    
    @PersistenceContext(name = "TrafficDataDBPU")
    EntityManager em;
    private InitialContext ctx;
    
    @PostConstruct
    public void init() {
        try {
            ctx = new InitialContext();
        } catch (NamingException ex) {
            Logger.getLogger(TrafficDataDAO.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        Logger.getLogger("logger").log(Level.INFO, "TrafficDataDAO has been initialized."); 
    }

            
    public TrafficDataDAO(){
        
    }

    @Override
    public List<IRouteData> getData(Date time1, Date time2) {
        List<IRouteData> routes = new ArrayList<>();
        try {
            //get all routes
            Query q = em.createQuery("SELECT r FROM RouteDataEntity r WHERE r.timestamp >= :time1 AND r.timestamp <= :time2");
            q.setParameter("time1", time1);
            q.setParameter("time2", time2);
            List<IRouteData> routesEntities = q.getResultList();
            for(IRouteData r : routesEntities)
                routes.add(new RouteData(r));
        } catch (Exception e) {
            Logger logger = Logger.getLogger(this.getClass().getName());
            logger.severe(e.getMessage());
        } finally {

        }
        return routes;
    }

    @Override
    public IRouteData addData(IRouteData data) {
        RouteDataEntity r = new RouteDataEntity(data);
        try {
            em.persist(r);
        } catch (Exception ex) {
            Logger.getLogger(TrafficDataDAO.class.getName()).log(Level.SEVERE, null, ex);
        }
        return new RouteData(r);
    }

    @Override
    public List<IRouteData> addData(List<IRouteData> allData) {
        List<IRouteData> data = new ArrayList<>();
        for(IRouteData d : allData){
            data.add(addData(d));
        }
        return data;
    }

    @Override
    public List<IRouteData> getData(IRoute route, Date time1, Date time2) {
        List<IRouteData> data = new ArrayList<>();
        try {
            //get all routes
            Query q = em.createQuery("SELECT r FROM RouteDataEntity r WHERE r.timestamp >= :time1 AND r.timestamp <= :time2 AND r.routeId = :routeID");
            q.setParameter("time1", time1);
            q.setParameter("time2", time2);
            q.setParameter("routeID", route.getId());
            List<IRouteData> routesEntities = q.getResultList();
            for(IRouteData r : routesEntities)
                data.add(new RouteData(r));
        } catch (Exception e) {
            Logger logger = Logger.getLogger(this.getClass().getName());
            logger.severe(e.getMessage());
        } finally {

        }
        return data;
    }

    @Override
    public List<IRouteData> getData(ISourceAdapter adapter, Date time1, Date time2) {       
        List<IRouteData> data = new ArrayList<>();
        try {
            //get all routes
            Query q = em.createQuery("SELECT r FROM RouteDataEntity r WHERE r.timestamp >= :time1 AND r.timestamp <= :time2 AND r.providerName = :providerName");
            q.setParameter("time1", time1);
            q.setParameter("time2", time2);
            q.setParameter("providerName", adapter.getProviderName());
            List<IRouteData> routesEntities = q.getResultList();
            for(IRouteData r : routesEntities)
                data.add(new RouteData(r));
        } catch (Exception e) {
            Logger logger = Logger.getLogger(this.getClass().getName());
            logger.severe(e.getMessage());
        } finally {

        }
        return data;
    }
}
