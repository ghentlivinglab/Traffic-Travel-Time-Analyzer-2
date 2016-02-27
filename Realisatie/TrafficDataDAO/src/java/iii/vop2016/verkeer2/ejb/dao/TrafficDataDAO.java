/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package iii.vop2016.verkeer2.ejb.dao;

import iii.vop2016.verkeer2.ejb.components.IRoute;
import iii.vop2016.verkeer2.ejb.components.IRouteData;
import iii.vop2016.verkeer2.ejb.provider.ISourceAdapter;
import java.util.ArrayList;
import java.util.Date;
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
 * @author tobia
 */
@Singleton
public class TrafficDataDAO implements TrafficDataDAORemote {
    
    private EntityManagerFactory emFactory;
            
    public TrafficDataDAO(){
        
    }
    
    @PostConstruct
    public void init(){
        emFactory = Persistence.createEntityManagerFactory("TrafficDataDBPU");
    }

    @Override
    public List<IRouteData> getAllData() {
        EntityManager em = null;
        List<IRouteData> data = new ArrayList<>();
        try {
            em = emFactory.createEntityManager();
            em.getTransaction().begin();
            data = em.createQuery("SELECT d FROM RouteDataEntity d").getResultList();
            em.getTransaction().commit();
        } catch (Exception e) {
            Logger logger = Logger.getLogger(this.getClass().getName());
            logger.severe(e.getMessage());
        } finally {
            if (em != null) {
                em.close();
            }
        }
        return data;
    }

    @Override
    public List<IRouteData> getData(IRoute route) {
        return new ArrayList<>();
    }

    @Override
    public List<IRouteData> getData(ISourceAdapter adapter) {
        return new ArrayList<>();
    }

    @Override
    public List<IRouteData> getData(Date time1, Date time2) {
        return new ArrayList<>();
    }

    @Override
    public IRouteData addData(IRouteData data) {
        List<IRouteData> allData = new ArrayList<>();
        allData.add(data);
        addData(allData);
        return data;
    }

    // Add business logic below. (Right-click in editor and choose
    // "Insert Code > Add Business Method")

    @Override
    public List<IRouteData> addData(List<IRouteData> allData) {
        EntityManager em = null;
        try{
            em = emFactory.createEntityManager();
            em.getTransaction().begin();
            for(IRouteData data : allData){
                em.persist(new RouteDataEntity(data));
            }
            em.getTransaction().commit();
        }catch(Exception e){
            e.printStackTrace();
            if(em != null){
                em.getTransaction().rollback();                
            }
        }finally{
            if(em != null){
                em.close();
            }
        }
        return allData;
    }
}
