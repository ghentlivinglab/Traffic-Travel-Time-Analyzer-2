/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package iii.vop2016.verkeer2.ejb.dao;

import iii.vop2016.verkeer2.ejb.components.IRoute;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;
import javax.annotation.PostConstruct;
import javax.ejb.Singleton;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.EntityTransaction;
import javax.persistence.Persistence;

/**
 *
 * @author Tobias
 */
@Singleton
public class GeneralDAO implements GeneralDAORemote {

    private EntityManagerFactory emFactory;
            
    public GeneralDAO(){
        
    }
    
    @PostConstruct
    public void init(){
        emFactory = Persistence.createEntityManagerFactory("GeneralDBPU");
    }
    
    
    @Override
    public List<IRoute> getRoutes() {
        EntityManager em = null;
        List<IRoute> routes = new ArrayList<>();
        try {
            em = emFactory.createEntityManager();
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
        EntityManager em = null;
        try{
            em = emFactory.createEntityManager();
            em.getTransaction().begin();
            em.persist(new RouteEntity(route));
            em.getTransaction().commit();
        }catch(Exception e){
            e.printStackTrace();
            if (em != null) {
                em.getTransaction().rollback();
            }
        }finally{
            if (em != null) {
                em.close();
            }
        }
    }

    @Override
    public void removeRoute(IRoute route) {
        
    }

    // Add business logic below. (Right-click in editor and choose
    // "Insert Code > Add Business Method")
}
