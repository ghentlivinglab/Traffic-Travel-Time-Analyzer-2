/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package iii.vop2016.verkeer2.ejb.datamanager;

import iii.vop2016.verkeer2.ejb.components.GeoLocation;
import iii.vop2016.verkeer2.ejb.components.IGeoLocation;
import iii.vop2016.verkeer2.ejb.components.IRoute;
import iii.vop2016.verkeer2.ejb.components.IRouteData;
import iii.vop2016.verkeer2.ejb.components.Route;
import iii.vop2016.verkeer2.ejb.dao.IGeneralDAO;
import iii.vop2016.verkeer2.ejb.dao.ITrafficDataDAO;
import iii.vop2016.verkeer2.ejb.helper.BeanFactory;
import iii.vop2016.verkeer2.ejb.provider.ISourceAdapter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorCompletionService;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeoutException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import javax.ejb.SessionContext;
import javax.ejb.Singleton;
import javax.ejb.Startup;
import javax.naming.InitialContext;
import javax.naming.NamingException;

/**
 *
 * @author Mike Brants
 */
@Singleton
@Startup
public class TrafficDataManager implements TrafficDataManagerRemote {

    
    @Resource
    private SessionContext ctxs;
    private InitialContext ctx;
    private BeanFactory beanFactory;
    private ISourceManager sourceManager;
    
    private IGeneralDAO generalDAO;
    private ITrafficDataDAO trafficDataDAO;
    
    public TrafficDataManager(){
        
    }
    
    
    @PostConstruct
    private void init() {
        
        Logger.getGlobal().log(Level.INFO, "TrafficDataManager init...");
        
         //Initialize bean and its context
        //Initialize bean and its context
        try {
            ctx = new InitialContext();
        } catch (NamingException ex) {
            Logger.getLogger(SourceManager.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        beanFactory = BeanFactory.getInstance(ctx, ctxs);
        
        generalDAO = beanFactory.getGeneralDAO();
        trafficDataDAO = beanFactory.getTrafficDataDAO();
        

        sourceManager = new SourceManager();
        

        
        Logger.getGlobal().log(Level.INFO, "TrafficDataManager has been initialized.");
    }
    

    //method triggered by timer
    @Override
    public void downloadNewData() {
        
        //Ophalen van alle routes
        List<IRoute> routes = generalDAO.getRoutes();

        List<IRouteData> data;
        for(IRoute route : routes){
            //opvragen van de data
             data = sourceManager.parse(route);
             //opslaan van de verkregen data
             trafficDataDAO.addData(data);
        }
        
        List<IRouteData> allData = trafficDataDAO.getAllData();
        for(IRouteData d : allData)
            System.out.println(d);
        
    }

    
    // Add business logic below. (Right-click in editor and choose
    // "Insert Code > Add Business Method")

    @Override
    public void initRoutes() {
        IRoute route = new Route("Test route 1");
       
        IGeoLocation geolocation = new GeoLocation(50.6565, 51.2566);
        route.addGeolocation(geolocation);
        generalDAO.addRoute(route);
    }
    
    
    
    
    
    
}
