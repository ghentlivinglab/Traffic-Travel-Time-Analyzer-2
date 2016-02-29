/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package iii.vop2016.verkeer2.ejb.dao;

import iii.vop2016.verkeer2.ejb.components.IGeoLocation;
import iii.vop2016.verkeer2.ejb.components.IRoute;
import iii.vop2016.verkeer2.ejb.components.IRouteData;
import iii.vop2016.verkeer2.ejb.provider.ISourceAdapter;
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
    }

            
    public TrafficDataDAO(){
        
    }

    @Override
    public List<IRouteData> getData(Date time1, Date time2) {
        List<IRouteData> routes = null;
        try {
            //get all routes
            Query q = em.createQuery("SELECT r FROM RouteDataEntity r WHERE r.timestamp >= :time1 AND r.timestamp <= :time2");
            q.setParameter("time1", time1);
            q.setParameter("time2", time2);
            routes = q.getResultList();
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
        return r;
    }

    @Override
    public List<IRouteData> addData(List<IRouteData> allData) {
        List<IRouteData> data = new ArrayList<>();
        for(IRouteData d : allData){
            data.add(addData(d));
        }
        return data;
    }
}
