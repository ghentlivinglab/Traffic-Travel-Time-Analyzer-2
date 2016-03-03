/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package iii.vop2016.verkeer2.ejb.datadownloader;

import iii.vop2016.verkeer2.ejb.components.GeoLocation;
import iii.vop2016.verkeer2.ejb.components.IGeoLocation;
import iii.vop2016.verkeer2.ejb.components.IRoute;
import iii.vop2016.verkeer2.ejb.components.IRouteData;
import iii.vop2016.verkeer2.ejb.components.Route;
import iii.vop2016.verkeer2.ejb.components.RouteData;
import iii.vop2016.verkeer2.ejb.dao.IGeneralDAO;
import iii.vop2016.verkeer2.ejb.dao.ITrafficDataDAO;
import iii.vop2016.verkeer2.ejb.datadownloader.ISourceManager;
import iii.vop2016.verkeer2.ejb.datadownloader.TrafficDataDownloaderRemote;
import iii.vop2016.verkeer2.ejb.helper.BeanFactory;
import iii.vop2016.verkeer2.ejb.datasources.ISourceAdapter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
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
import javax.annotation.PreDestroy;
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
public class TrafficDataDownloader implements TrafficDataDownloaderRemote {

    @Resource
    private SessionContext ctxs;
    private InitialContext ctx;
    private BeanFactory beanFactory;
    private ISourceManager sourceManager;

    public TrafficDataDownloader() {

    }

    @PostConstruct
    private void init() {

        Logger.getGlobal().log(Level.INFO, "TrafficDataManager init...");

        //Initialize bean and its context
        try {
            ctx = new InitialContext();
        } catch (NamingException ex) {
            Logger.getLogger(SourceManager.class.getName()).log(Level.SEVERE, null, ex);
        }

        beanFactory = BeanFactory.getInstance(ctx, ctxs);

        sourceManager = new SourceManager();

        Logger.getLogger("logger").log(Level.INFO, "TrafficDataManager has been initialized.");

    }

    //method triggered by timer
    @Override
    public void downloadNewData(Date timestamp) {
        //Ophalen van alle routes
         Logger.getLogger("logger").log(Level.INFO,"Started data scrubbing");
        
        List<IRoute> routes = beanFactory.getGeneralDAO().getRoutes();
        if (routes != null) {
            List<IRouteData> data;
            
            for (IRoute route : routes) {
                
                //opvragen van de data
                data = sourceManager.parse(route);
                
                //opslaan van de verkregen data
                if(data != null && data.size() != 0){
                    for(IRouteData r : data)
                        r.setTimestamp(timestamp);
                    beanFactory.getTrafficDataDAO().addData(data);
                }
            }
        }else{
            Logger.getLogger("logger").log(Level.WARNING,"No routes available to scrape data for");
        }
        
         Logger.getLogger("logger").log(Level.INFO,"Data scrubbing done");

    }
    
    @PreDestroy
    private void destroy(){
        sourceManager.destroy();
    }
}
