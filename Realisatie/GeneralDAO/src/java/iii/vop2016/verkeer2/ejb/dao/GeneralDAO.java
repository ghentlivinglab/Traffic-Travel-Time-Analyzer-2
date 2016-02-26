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
import javax.annotation.Resource;
import javax.ejb.Singleton;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.transaction.HeuristicMixedException;
import javax.transaction.HeuristicRollbackException;
import javax.transaction.NotSupportedException;
import javax.transaction.RollbackException;
import javax.transaction.SystemException;
import javax.transaction.UserTransaction;

/**
 *
 * @author Tobias
 */
@Singleton
public class GeneralDAO implements GeneralDAORemote {

    @PersistenceContext(name="GeneralDBPU")
    EntityManager em;
    private InitialContext ctx;
    
    public GeneralDAO(){
        
    }
    
    @PostConstruct
    public void init(){
        try {
            //emFactory = Persistence.createEntityManagerFactory("GeneralDBPU");
            ctx = new InitialContext();
        } catch (NamingException ex) {
            Logger.getLogger(GeneralDAO.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    
    @Override
    public List<IRoute> getRoutes() {
        
        
        List<IRoute> routes = new ArrayList<>();
        try {
            em.getTransaction().begin();
            routes = em.createQuery("SELECT r FROM RouteEntity r").getResultList();
            em.getTransaction().commit();
        } catch (Exception e) {
            Logger logger = Logger.getLogger(this.getClass().getName());
            logger.severe(e.getMessage());
        } finally {
            if (em != null) {
                em.close();
            }
        }
        return routes;
    }

    @Override
    public IRoute getRoute(String name) {
        return null;
    }

    @Override
    public void addRoute(IRoute route) {

        try {
            for(IGeoLocation location : route.getGeolocations()){
                //GeoLocationEntity loc2 = new GeoLocationEntity(location);
                //loc2.setRoute(route);
                //addGeoLocation(location);
            }
            em.persist(new RouteEntity(route)); 
        } catch (Exception ex) {
            Logger.getLogger(GeneralDAO.class.getName()).log(Level.SEVERE, null, ex);
        }

        
    }

    @Override
    public void removeRoute(IRoute route) {
        
    }

    // Add business logic below. (Right-click in editor and choose
    // "Insert Code > Add Business Method")

    @Override
    public void addGeoLocation(IGeoLocation geolocation) {
 
            em.persist(new GeoLocationEntity(geolocation));

    }

    
}
