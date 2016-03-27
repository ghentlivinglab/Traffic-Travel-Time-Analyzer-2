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
import iii.vop2016.verkeer2.ejb.helper.HelperFunctions;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
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
    protected static final String JNDILOOKUP_PROPERTYFILE = "resources/properties/DataProvider";

    @PostConstruct
    private void init() {
        //Initialize bean and its context
        try {
            ctx = new InitialContext();
        } catch (NamingException ex) {
            Logger.getLogger(DataProvider.class.getName()).log(Level.SEVERE, null, ex);
        }
        beans = BeanFactory.getInstance(ctx, ctxs);
        optimalDuration = -1;

        Logger.getLogger("logger").log(Level.INFO, "DataProvider has been initialized.");
    }

    private Properties getProperties() {
        return HelperFunctions.RetrievePropertyFile(JNDILOOKUP_PROPERTYFILE, ctx, Logger.getGlobal());
    }

    @Override
    public int getCurrentDuration(IRoute route, List<String> providers) {
        ITrafficDataDAO dao = beans.getTrafficDataDAO();
        List<IRouteData> list = dao.getCurrentTrafficSituation(route, providers);

        //Weighted arithmetic mean ( sum(x*y)/sum(x) )
        long totalDistanceMulDurataion = 0;
        long totaldistance = 0;

        int i = 0;
        for (IRouteData d : list) {
            totalDistanceMulDurataion += d.getDistance() * d.getDuration();
            totaldistance += d.getDistance();
            i++;
        }

        return Math.toIntExact(totalDistanceMulDurataion / totaldistance);
    }

    @Override
    public int getCurrentVelocity(IRoute route, List<String> providers) {
        ITrafficDataDAO dao = beans.getTrafficDataDAO();
        List<IRouteData> list = dao.getCurrentTrafficSituation(route, providers);

        //Weighted arithmetic mean ( sum(x*y)/sum(x) )
        long totalDistanceMulVelocity = 0;
        long totaldistance = 0;

        int i = 0;
        for (IRouteData d : list) {
            totalDistanceMulVelocity += d.getDistance() / d.getDuration();
            totaldistance += d.getDistance();
            i++;
        }

        return Math.toIntExact(totalDistanceMulVelocity / totaldistance);
    }

    private int optimalDuration;

    @Override
    public int getOptimalDuration(IRoute route, List<String> providers) {
        //if (optimalDuration == -1) {
        Properties properties = getProperties();
        long timeframe = Integer.parseInt(properties.getProperty("OptimalDurationTimeFrame", "0")) * (long) 1000;
        long currentTime = beans.getTimer().getCurrentTime();
        Calendar startTime = GetTimeFromPropertyValue(properties.getProperty("OptimalDurationStartHour", "00-00"));
        Calendar endTime = GetTimeFromPropertyValue(properties.getProperty("OptimalDurationEndHour", "00-00"));

        Date startDate = new Date(currentTime - timeframe);
        Date endDate = new Date(currentTime);

        List<Date> startList = GenerateListForHourBetweenDates(startTime, startDate, endDate);
        List<Date> endList = GenerateListForHourBetweenDates(endTime, startDate, endDate);

        ITrafficDataDAO dao = beans.getTrafficDataDAO();

        List<IRouteData> list = dao.getData(route, startList, endList);

        //Weighted arithmetic mean ( sum(x*y)/sum(x) )
        long totalDistanceMulDuration = 0;
        long totaldistance = 0;

        int i = 0;
        for (IRouteData d : list) {
            totalDistanceMulDuration += d.getDistance() * d.getDuration();
            totaldistance += d.getDistance();
            i++;
        }

        optimalDuration =  Math.toIntExact(totalDistanceMulDuration / totaldistance);
        //}
        return optimalDuration;
    }
    
    private int optimalSpeed;

    @Override
    public int getOptimalVelocity(IRoute route, List<String> providers) {
        //if (optimalDuration == -1) {
        Properties properties = getProperties();
        long timeframe = Integer.parseInt(properties.getProperty("OptimalDurationTimeFrame", "0")) * (long) 1000;
        long currentTime = beans.getTimer().getCurrentTime();
        Calendar startTime = GetTimeFromPropertyValue(properties.getProperty("OptimalDurationStartHour", "00-00"));
        Calendar endTime = GetTimeFromPropertyValue(properties.getProperty("OptimalDurationEndHour", "00-00"));

        Date startDate = new Date(currentTime - timeframe);
        Date endDate = new Date(currentTime);

        List<Date> startList = GenerateListForHourBetweenDates(startTime, startDate, endDate);
        List<Date> endList = GenerateListForHourBetweenDates(endTime, startDate, endDate);

        ITrafficDataDAO dao = beans.getTrafficDataDAO();

        List<IRouteData> list = dao.getData(route, startList, endList);

        //Weighted arithmetic mean ( sum(x*y)/sum(x) )
        long totalDuration = 0;
        long totaldistance = 0;

        int i = 0;
        for (IRouteData d : list) {
            totalDuration += d.getDuration();
            totaldistance += d.getDistance();
            i++;
        }

        optimalSpeed =  Math.toIntExact(totalDuration / totaldistance);
        //}
        return optimalSpeed;
    }

    @Override
    public int getOptimalDuration(IRoute route, List<String> providers, Date start, Date end) {
        ITrafficDataDAO dao = beans.getTrafficDataDAO();
        return 0;
    }

    @Override
    public int getOptimalVelocity(IRoute route, List<String> providers, Date start, Date end) {
        ITrafficDataDAO dao = beans.getTrafficDataDAO();
        return 0;
    }

    @Override
    public int getAvgDuration(IRoute route, List<String> providers) {
        return 0;
    }

    @Override
    public int getAvgVelocity(IRoute route, List<String> providers) {
        ITrafficDataDAO dao = beans.getTrafficDataDAO();
        return 0;
    }

    @Override
    public int getAvgDuration(IRoute route, List<String> providers, Date start, Date end) {
        ITrafficDataDAO dao = beans.getTrafficDataDAO();
        return 0;
    }

    @Override
    public int getAvgVelocity(IRoute route, List<String> providers, Date start, Date end) {
        ITrafficDataDAO dao = beans.getTrafficDataDAO();
        return 0;
    }

    @Override
    public int getCurrentDelayLevel(IRoute route, List<String> providers) {
        ITrafficDataDAO dao = beans.getTrafficDataDAO();
        return 0;
    }

    @Override
    public int getDelayLevel(IRoute route, List<String> providers, Date start, Date end) {
        ITrafficDataDAO dao = beans.getTrafficDataDAO();
        return 0;
    }

    @Override
    public int getDistance(IRoute route, List<String> providers) {
        ITrafficDataDAO dao = beans.getTrafficDataDAO();
        return 0;
    }

    @Override
    public int getTrend(IRoute route, List<String> providers) {
        ITrafficDataDAO dao = beans.getTrafficDataDAO();
        return 0;
    }

    @Override
    public int getTrend(IRoute route, List<String> providers, Date start, Date end) {
        ITrafficDataDAO dao = beans.getTrafficDataDAO();
        return 0;
    }

    @Override
    public Map<Date, Integer> getRecentData(IRoute route, List<String> providers) {
        ITrafficDataDAO dao = beans.getTrafficDataDAO();
        return new HashMap<>();
    }

    @Override
    public Map<Date, Integer> getDataByDay(IRoute route, List<String> providers, Date start, Date end, Weekdays day) {
        ITrafficDataDAO dao = beans.getTrafficDataDAO();
        return new HashMap<>();
    }

    @Override
    public Map<Date, Integer> getDataVelocityByDay(IRoute route, List<String> providers, Date start, Date end, Weekdays day) {
        ITrafficDataDAO dao = beans.getTrafficDataDAO();
        return new HashMap<>();
    }

    @Override
    public Map<Date, Integer> getDataByDayInWorkWeek(IRoute route, List<String> providers, Date start, Date end) {
        ITrafficDataDAO dao = beans.getTrafficDataDAO();
        return new HashMap<>();
    }

    @Override
    public Map<Date, Integer> getDataVelocityByDayInWorkWeek(IRoute route, List<String> providers, Date start, Date end) {
        ITrafficDataDAO dao = beans.getTrafficDataDAO();
        return new HashMap<>();
    }

    @Override
    public Map<Date, Integer> getData(IRoute route, List<String> providers, Date start, Date end) {
        ITrafficDataDAO dao = beans.getTrafficDataDAO();
        return new HashMap<>();
    }

    @Override
    public Map<Date, Integer> getDataVelocity(IRoute route, List<String> providers, Date start, Date end) {
        ITrafficDataDAO dao = beans.getTrafficDataDAO();
        return new HashMap<>();
    }

    @Override
    public Map<Date, Integer> getDataByDayInWorkWeek(IRoute route, List<String> providers) {
        ITrafficDataDAO dao = beans.getTrafficDataDAO();
        return new HashMap<>();
    }

    @Override
    public Map<Date, Integer> getDataVelocityByDayInWorkWeek(IRoute route, List<String> providers) {
        ITrafficDataDAO dao = beans.getTrafficDataDAO();
        return new HashMap<>();
    }

    protected Pattern timeFormat = Pattern.compile("([0-9]{2})-([0-9]{2})");

    private Calendar GetTimeFromPropertyValue(String property) {
        Matcher m = timeFormat.matcher(property);
        if (m.matches()) {
            int hour = Integer.parseInt(m.group(1));
            int min = Integer.parseInt(m.group(2));
            Calendar c = new GregorianCalendar();
            c.set(0, 0, 0, hour, min, 0);
            return c;
        }
        return null;
    }

    private List<Date> GenerateListForHourBetweenDates(Calendar time, Date startDate, Date endDate) {
        List<Date> dates = new ArrayList<>();

        Calendar cStart = new GregorianCalendar();
        cStart.setTime(startDate);
        Calendar cEnd = new GregorianCalendar();
        cEnd.setTime(endDate);

        cStart.set(Calendar.HOUR_OF_DAY, time.get(Calendar.HOUR_OF_DAY));
        cStart.set(Calendar.MINUTE, time.get(Calendar.MINUTE));

        while (cStart.before(cEnd)) {
            dates.add(cStart.getTime());
            cStart.add(Calendar.DAY_OF_MONTH, 1);
        }
        return dates;
    }
}
