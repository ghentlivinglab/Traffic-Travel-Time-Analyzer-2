/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package iii.vop2016.verkeer2.ejb.dao;

import iii.vop2016.verkeer2.ejb.components.IRoute;
import java.util.ArrayList;
import java.util.List;
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
        return new ArrayList<>();
    }

    @Override
    public IRoute getRoute(String name) {
        return null;
    }

    @Override
    public void addRoute(IRoute route) {
        EntityManager manager = emFactory.createEntityManager();
        EntityTransaction tr = manager.getTransaction();
        tr.begin();
        try{
            manager.persist(new RouteEntity(route));
            tr.commit();
        }catch(Exception e){
            e.printStackTrace();
            tr.rollback();
        }finally{
            manager.close();
        }
    }

    @Override
    public void removeRoute(IRoute route) {
        
    }

    // Add business logic below. (Right-click in editor and choose
    // "Insert Code > Add Business Method")
}
