/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package iii.vop2016.verkeer2.ejb.datadownloader;

import iii.vop2016.verkeer2.ejb.components.IRoute;
import iii.vop2016.verkeer2.ejb.components.IRouteData;
import iii.vop2016.verkeer2.ejb.downstream.ITrafficDataDownstreamAnalyser;
import iii.vop2016.verkeer2.ejb.helper.BeanFactory;
import iii.vop2016.verkeer2.ejb.logger.ILogger;
import iii.vop2016.verkeer2.ejb.logger.LoggerRemote;
import iii.vop2016.verkeer2.ejb.threshold.IThresholdManager;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
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
public class TrafficDataDownloader implements TrafficDataDownloaderRemote,TrafficDataDownloaderLocal {

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

        beanFactory.getLogger().log(Level.INFO, "TrafficDataDownloader has been initialized.");

    }

    //method triggered by timer
    @Override
    public void downloadNewData(Date timestamp) {
        //Ophalen van alle routes
        ILogger logger = beanFactory.getLogger();
        
        logger.log(Level.FINER, "Started data scrubbing");

        List<IRoute> routes = beanFactory.getGeneralDAO().getRoutes();
        ITrafficDataDownstreamAnalyser analyzer = beanFactory.getTrafficDataDownstreamAnalyser();
        if (routes != null) {
            List<IRouteData> allData = new ArrayList<>();
            analyzer.startSession();
            for (IRoute route : routes) {

                //opvragen van de data
                List<IRouteData> data = sourceManager.parse(route);

                //opslaan van de verkregen data
                if (data != null && data.size() != 0) {
                    for (IRouteData r : data) {
                        r.setTimestamp(timestamp);
                    }
                    allData.addAll(analyzer.addData(data));
                }
            }
            analyzer.endSession(allData, routes);
        } else {
            logger.log(Level.WARNING, "No routes available to scrape data for");
        }

        logger.log(Level.FINER, "Data scrubbing done");

    }

    @PreDestroy
    private void destroy() {
        sourceManager.destroy();
    }
}
