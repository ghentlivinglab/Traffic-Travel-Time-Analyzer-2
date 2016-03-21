/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package iii.vop2016.verkeer2.ejb.dataprovider;

import iii.vop2016.verkeer2.ejb.components.IRoute;
import iii.vop2016.verkeer2.ejb.components.IRouteData;
import iii.vop2016.verkeer2.ejb.components.Weekdays;
import iii.vop2016.verkeer2.ejb.dao.ITrafficDataDAO;
import iii.vop2016.verkeer2.ejb.datasources.ISourceAdapter;
import iii.vop2016.verkeer2.ejb.helper.BeanFactory;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import javax.ejb.SessionContext;
import javax.ejb.Singleton;
import javax.naming.InitialContext;
import javax.naming.NamingException;

/**
 *
 * @author Tobias
 */
@Singleton
public class DataProvider implements DataProviderRemote {

    @Resource
    protected SessionContext ctxs;
    protected InitialContext ctx;
    protected BeanFactory beans;

    @PostConstruct
    private void init() {
        //Initialize bean and its context
        try {
            ctx = new InitialContext();
        } catch (NamingException ex) {
            Logger.getLogger(DataProvider.class.getName()).log(Level.SEVERE, null, ex);
        }
        beans = BeanFactory.getInstance(ctx, ctxs);

        Logger.getLogger("logger").log(Level.INFO, "DataProvider has been initialized.");
    }

    @Override
    public int getCurrentDuration(IRoute route, List<String> providers) {
        ITrafficDataDAO dao = beans.getTrafficDataDAO();
        List<IRouteData> list = dao.getCurrentTrafficSituation(route, providers);
        
        //Weighted arithmetic mean ( sum(x*y)/sum(x) )
        long totalDistanceMulDurataion = 0;       
        long totaldistance = 0;
        
        int i =0;
        for(IRouteData d : list){
            totalDistanceMulDurataion += d.getDistance() * d.getDuration();            
            totaldistance += d.getDistance();
            i++;
        }
        
        return Math.toIntExact(totalDistanceMulDurataion/totaldistance);  
    }

    @Override
    public int getCurrentVelocity(IRoute route, List<String> providers) {
        ITrafficDataDAO dao = beans.getTrafficDataDAO();
        List<IRouteData> list = dao.getCurrentTrafficSituation(route, providers);
        
        //Weighted arithmetic mean ( sum(x*y)/sum(x) )
        long totalDistanceMulVelocity = 0;       
        long totaldistance = 0;
        
        int i =0;
        for(IRouteData d : list){
            totalDistanceMulVelocity += d.getDistance()/d.getDuration();            
            totaldistance += d.getDistance();
            i++;
        }
        
        return Math.toIntExact(totalDistanceMulVelocity/totaldistance);  
    }

    
    @Override
    public int getOptimalDuration(IRoute route, List<String> providers) {
        ITrafficDataDAO dao = beans.getTrafficDataDAO();
    }

    @Override
    public int getOptimalVelocity(IRoute route, List<String> providers) {
        ITrafficDataDAO dao = beans.getTrafficDataDAO();
    }

    @Override
    public int getOptimalDuration(IRoute route, List<String> providers, Date start, Date end) {
        ITrafficDataDAO dao = beans.getTrafficDataDAO();
    }

    @Override
    public int getOptimalVelocity(IRoute route, List<String> providers, Date start, Date end) {
        ITrafficDataDAO dao = beans.getTrafficDataDAO();
    }

    @Override
    public int getAvgDuration(IRoute route, List<String> providers) {
        ITrafficDataDAO dao = beans.getTrafficDataDAO();
    }

    @Override
    public int getAvgVelocity(IRoute route, List<String> providers) {
        ITrafficDataDAO dao = beans.getTrafficDataDAO();
    }

    @Override
    public int getAvgDuration(IRoute route, List<String> providers, Date start, Date end) {
        ITrafficDataDAO dao = beans.getTrafficDataDAO();
    }

    @Override
    public int getAvgVelocity(IRoute route, List<String> providers, Date start, Date end) {
        ITrafficDataDAO dao = beans.getTrafficDataDAO();
    }

    @Override
    public int getCurrentDelayLevel(IRoute route, List<String> providers) {
        ITrafficDataDAO dao = beans.getTrafficDataDAO();
    }

    @Override
    public int getDelayLevel(IRoute route, List<String> providers, Date start, Date end) {
        ITrafficDataDAO dao = beans.getTrafficDataDAO();
    }

    @Override
    public int getDistance(IRoute route, List<String> providers) {
        ITrafficDataDAO dao = beans.getTrafficDataDAO();
    }

    @Override
    public int getTrend(IRoute route, List<String> providers) {
        ITrafficDataDAO dao = beans.getTrafficDataDAO();
    }

    @Override
    public int getTrend(IRoute route, List<String> providers, Date start, Date end) {
        ITrafficDataDAO dao = beans.getTrafficDataDAO();
    }

    @Override
    public Map<Date, Integer> getRecentData(IRoute route, List<String> providers) {
        ITrafficDataDAO dao = beans.getTrafficDataDAO();
    }

    @Override
    public Map<Date, Integer> getDataByDay(IRoute route, List<String> providers, Date start, Date end, Weekdays day) {
        ITrafficDataDAO dao = beans.getTrafficDataDAO();
    }

    @Override
    public Map<Date, Integer> getDataVelocityByDay(IRoute route, List<String> providers, Date start, Date end, Weekdays day) {
        ITrafficDataDAO dao = beans.getTrafficDataDAO();
    }

    @Override
    public Map<Date, Integer> getDataByDayInWorkWeek(IRoute route, List<String> providers, Date start, Date end) {
        ITrafficDataDAO dao = beans.getTrafficDataDAO();
    }

    @Override
    public Map<Date, Integer> getDataVelocityByDayInWorkWeek(IRoute route, List<String> providers, Date start, Date end) {
        ITrafficDataDAO dao = beans.getTrafficDataDAO();
    }

    @Override
    public Map<Date, Integer> getData(IRoute route, List<String> providers, Date start, Date end) {
        ITrafficDataDAO dao = beans.getTrafficDataDAO();
    }

    @Override
    public Map<Date, Integer> getDataVelocity(IRoute route, List<String> providers, Date start, Date end) {
        ITrafficDataDAO dao = beans.getTrafficDataDAO();
    }

    @Override
    public Map<Date, Integer> getDataByDayInWorkWeek(IRoute route, List<String> providers) {
        ITrafficDataDAO dao = beans.getTrafficDataDAO();
    }

    @Override
    public Map<Date, Integer> getDataVelocityByDayInWorkWeek(IRoute route, List<String> providers) {
        ITrafficDataDAO dao = beans.getTrafficDataDAO();
    }
}
