/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package iii.vop2016.verkeer2.ejb.dataprovider;

import iii.vop2016.verkeer2.ejb.components.IRoute;
import iii.vop2016.verkeer2.ejb.components.IRouteData;
import iii.vop2016.verkeer2.ejb.components.Weekdays;
import iii.vop2016.verkeer2.ejb.dao.Aggregation;
import iii.vop2016.verkeer2.ejb.dao.AggregationContainer;
import iii.vop2016.verkeer2.ejb.dao.ITrafficDataDAO;
import iii.vop2016.verkeer2.ejb.helper.BeanFactory;
import iii.vop2016.verkeer2.ejb.helper.HelperFunctions;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.function.Function;
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

        Logger.getLogger("logger").log(Level.INFO, "DataProvider has been initialized.");
    }

    private Properties getProperties() {
        return HelperFunctions.RetrievePropertyFile(JNDILOOKUP_PROPERTYFILE, ctx, Logger.getGlobal());
    }

    @Deprecated
    private int CalculateArithmaticMean(List<IRouteData> data, Function<IRouteData, Long> var, Function<IRouteData, Long> weight) {
        long totalNum = 0;
        long totalDiv = 0;
        int i = 0;
        for (IRouteData d : data) {
            long w = weight.apply(d);
            long v = var.apply(d);
            totalNum += w * v;
            totalDiv += w;
            i++;
        }

        if (totalDiv == 0) {
            return -1;
        }

        return Math.toIntExact(totalNum / totalDiv);
    }

    private static Function<IRouteData, Long> function_distance = new Function<IRouteData, Long>() {
        @Override
        public Long apply(IRouteData t) {
            return new Long(t.getDistance());
        }
    };

    private static Function<IRouteData, Long> function_distanceMulDuration = new Function<IRouteData, Long>() {
        @Override
        public Long apply(IRouteData t) {
            return new Long(t.getDistance() * t.getDuration());
        }
    };

    @Override
    public int getMeanDurationFromRouteData(List<IRouteData> routeData) {
        if (routeData == null || routeData.size() == 0) {
            return -1;
        }

        long routeId = routeData.get(0).getRouteId();

        for (IRouteData data : routeData) {
            if (data.getRouteId() != routeId) {
                return -1;
            }
        }

        return CalculateArithmaticMean(routeData, new Function<IRouteData, Long>() {
            @Override
            public Long apply(IRouteData t) {
                return new Long(t.getDuration());
            }
        }, function_distance);
    }

    private static Function<IRouteData, Long> function_distanceMulDistanceDivDuration = new Function<IRouteData, Long>() {
        @Override
        public Long apply(IRouteData t) {
            return new Long(t.getDistance() * t.getDistance() / t.getDuration());
        }
    };

    private Map<IRoute, Integer> currentDuration = new HashMap<>();

    @Override
    public int getCurrentDuration(IRoute route, List<String> providers) {
        if (!currentDuration.containsKey(route)) {
            ITrafficDataDAO dao = beans.getTrafficDataDAO();
            List<IRouteData> list = dao.getCurrentTrafficSituation(route, providers);

            if (list == null || list.size() == 0) {
                return -1;
            }

            int ret = CalculateArithmaticMean(list, new Function<IRouteData, Long>() {
                @Override
                public Long apply(IRouteData t) {
                    return new Long(t.getDuration());
                }
            }, function_distance);

            if (ret != -1) {
                currentDuration.put(route, ret);
            }

            return ret;
        }
        return currentDuration.get(route);
    }

    private Map<IRoute, Integer> currentSpeed = new HashMap<>();

    @Override
    public int getCurrentVelocity(IRoute route, List<String> providers) {
        if (!currentSpeed.containsKey(route)) {
            ITrafficDataDAO dao = beans.getTrafficDataDAO();
            List<IRouteData> list = dao.getCurrentTrafficSituation(route, providers);

            if (list == null || list.size() == 0) {
                return -1;
            }

            int ret = CalculateArithmaticMean(list, new Function<IRouteData, Long>() {
                @Override
                public Long apply(IRouteData t) {
                    return (long) t.getDistance() / (long) t.getDuration();
                }
            }, function_distance);

            if (ret != -1) {
                currentSpeed.put(route, ret);
            }

            return ret;
        }
        return currentSpeed.get(route);
    }

    private Map<IRoute, Integer> optimalDuration = new HashMap<>();

    @Override
    public int getOptimalDuration(IRoute route, List<String> providers) {
        if (!optimalDuration.containsKey(route)) {
            Properties properties = getProperties();
            long timeframe = Integer.parseInt(properties.getProperty("OptimalDurationTimeFrame", "0")) * (long) 1000;
            long currentTime = beans.getTimer().getCurrentTime();
            Date startDate = new Date(currentTime - timeframe);
            Date endDate = new Date(currentTime);

            int dur = getOptimalDuration(route, providers, startDate, endDate);
            if (dur != -1) {
                optimalDuration.put(route, dur);
            }

            return dur;
        }
        return optimalDuration.get(route);
    }

    private Map<IRoute, Integer> optimalSpeed = new HashMap<>();

    @Override
    public int getOptimalVelocity(IRoute route, List<String> providers) {
        if (!optimalSpeed.containsKey(route)) {
            Properties properties = getProperties();
            long timeframe = Integer.parseInt(properties.getProperty("OptimalDurationTimeFrame", "0")) * (long) 1000;
            long currentTime = beans.getTimer().getCurrentTime();
            Date startDate = new Date(currentTime - timeframe);
            Date endDate = new Date(currentTime);

            int dur = getOptimalVelocity(route, providers, startDate, endDate);
            if (dur != -1) {
                optimalSpeed.put(route, dur);
            }
            return dur;
        }
        return optimalSpeed.get(route);
    }

    @Override
    public int getOptimalDuration(IRoute route, List<String> providers, Date startDate, Date endDate) {
        Properties properties = getProperties();
        Calendar startTime = GetTimeFromPropertyValue(properties.getProperty("OptimalDurationStartHour", "00-00"));
        Calendar endTime = GetTimeFromPropertyValue(properties.getProperty("OptimalDurationEndHour", "00-00"));

        List<Date> startList = GenerateListForHourBetweenDates(startTime, startDate, endDate);
        List<Date> endList = GenerateListForHourBetweenDates(endTime, startDate, endDate);

        ITrafficDataDAO dao = beans.getTrafficDataDAO();

        List<Long> list = dao.getAggregateData(route, startList, endList, new AggregationContainer(Aggregation.sum, "duration * distance"), new AggregationContainer(Aggregation.sum, "distance"));

        if (list == null || list.size() == 0) {
            return -1;
        }

        if (list.size() == 2 && list.get(1) != 0) {
            return Math.toIntExact(list.get(0) / list.get(1));
        }
        return -1;
    }

    @Override
    public int getOptimalVelocity(IRoute route, List<String> providers, Date startDate, Date endDate) {
        Properties properties = getProperties();
        Calendar startTime = GetTimeFromPropertyValue(properties.getProperty("OptimalDurationStartHour", "00-00"));
        Calendar endTime = GetTimeFromPropertyValue(properties.getProperty("OptimalDurationEndHour", "00-00"));

        List<Date> startList = GenerateListForHourBetweenDates(startTime, startDate, endDate);
        List<Date> endList = GenerateListForHourBetweenDates(endTime, startDate, endDate);

        ITrafficDataDAO dao = beans.getTrafficDataDAO();

        List<Long> list = dao.getAggregateData(route, startList, endList, new AggregationContainer(Aggregation.sum, "distance * distance / duration "), new AggregationContainer(Aggregation.sum, "distance"));

        if (list == null || list.size() == 0) {
            return -1;
        }

        if (list.size() == 2 && list.get(1) != 0) {
            return Math.toIntExact(list.get(0) / list.get(1));
        }
        return -1;
    }

    private Map<IRoute, Integer> avgDuration = new HashMap<>();

    @Override
    public int getAvgDuration(IRoute route, List<String> providers) {
        if (!avgDuration.containsKey(route)) {
            Properties properties = getProperties();
            long timeframe = Integer.parseInt(properties.getProperty("AvgDurationTimeFrame", "0")) * (long) 1000;
            long currentTime = beans.getTimer().getCurrentTime();
            Date startDate = new Date(currentTime - timeframe);
            Date endDate = new Date(currentTime);

            int dur = getAvgDuration(route, providers, startDate, endDate);
            if (dur != -1) {
                avgDuration.put(route, dur);
            }
            return dur;
        }
        return avgDuration.get(route);
    }

    private Map<IRoute, Integer> avgSpeed = new HashMap<>();

    @Override
    public int getAvgVelocity(IRoute route, List<String> providers) {
        if (!avgSpeed.containsKey(route)) {
            Properties properties = getProperties();
            long timeframe = Integer.parseInt(properties.getProperty("AvgDurationTimeFrame", "0")) * (long) 1000;
            long currentTime = beans.getTimer().getCurrentTime();
            Date startDate = new Date(currentTime - timeframe);
            Date endDate = new Date(currentTime);

            int dur = getAvgVelocity(route, providers, startDate, endDate);
            if (dur != -1) {
                avgSpeed.put(route, dur);
            }
            return dur;
        }
        return avgSpeed.get(route);
    }

    @Override
    public int getAvgDuration(IRoute route, List<String> providers, Date start, Date end) {
        ITrafficDataDAO dao = beans.getTrafficDataDAO();

        List<Long> list = dao.getAggregateData(route, start, end, new AggregationContainer(Aggregation.sum, "duration * distance"), new AggregationContainer(Aggregation.sum, "distance"));

        if (list == null || list.size() == 0) {
            return -1;
        }

        if (list.size() == 2 && list.get(1) != 0) {
            return Math.toIntExact(list.get(0) / list.get(1));
        }
        return -1;
    }

    @Override
    public int getAvgVelocity(IRoute route, List<String> providers, Date start, Date end) {
        ITrafficDataDAO dao = beans.getTrafficDataDAO();

        List<Long> list = dao.getAggregateData(route, start, end, new AggregationContainer(Aggregation.sum, "distance * distance / duration "), new AggregationContainer(Aggregation.sum, "distance"));

        if (list == null || list.size() == 0) {
            return -1;
        }

        if (list.size() == 2 && list.get(1) != 0) {
            return Math.toIntExact(list.get(0) / list.get(1));
        }
        return -1;
    }

    @Override
    public int getCurrentDelayLevel(IRoute route, List<String> providers) {
        int avg = this.getAvgDuration(route, providers);
        int current = getCurrentDuration(route, providers);
        return beans.getThresholdManager().getThresholdLevel(route, current - avg);
    }

    @Override
    public int getDelayLevel(IRoute route, List<String> providers, Date start, Date end) {
        int avg = this.getAvgDuration(route, providers);
        int pastAvg = getAvgDuration(route, providers, start, end);
        return beans.getThresholdManager().getThresholdLevel(route, pastAvg - avg);
    }

    @Override
    public int getDistance(IRoute route, List<String> providers) {
        ITrafficDataDAO dao = beans.getTrafficDataDAO();
        List<IRouteData> list = dao.getCurrentTrafficSituation(route, providers);

        long distance = 0;

        int i = 0;
        for (IRouteData r : list) {
            distance += r.getDistance();
            i++;
        }

        if (i == 0) {
            return -1;
        }

        return Math.toIntExact(distance / i);

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
        Map<Date, Integer> ret = new HashMap<>();
        ITrafficDataDAO dao = beans.getTrafficDataDAO();
        List<IRouteData> list = dao.getData(route, start, end);

        //map all retrieved data to specified date
        Map<Date, List<IRouteData>> reduce = new HashMap<>();
        for (IRouteData r : list) {
            List<IRouteData> data = reduce.get(r.getTimestamp());
            if (data == null) {
                data = new ArrayList<>();
                data.add(r);
                reduce.put(r.getTimestamp(), data);
            } else {
                data.add(r);
            }
        }

        //reduce data to mean for that day
        for (Map.Entry<Date, List<IRouteData>> entry : reduce.entrySet()) {
            ret.put(entry.getKey(), CalculateArithmaticMean(entry.getValue(), function_distanceMulDuration, function_distance));
        }

        return ret;
    }

    @Override
    public Map<Date, Integer> getDataVelocity(IRoute route, List<String> providers, Date start, Date end) {
        Map<Date, Integer> ret = new HashMap<>();
        ITrafficDataDAO dao = beans.getTrafficDataDAO();
        List<IRouteData> list = dao.getData(route, start, end);

        //map all retrieved data to specified date
        Map<Date, List<IRouteData>> reduce = new HashMap<>();
        for (IRouteData r : list) {
            List<IRouteData> data = reduce.get(r.getTimestamp());
            if (data == null) {
                data = new ArrayList<>();
                data.add(r);
                reduce.put(r.getTimestamp(), data);
            } else {
                data.add(r);
            }
        }

        //reduce data to mean for that day
        for (Map.Entry<Date, List<IRouteData>> entry : reduce.entrySet()) {
            ret.put(entry.getKey(), CalculateArithmaticMean(entry.getValue(), function_distanceMulDistanceDivDuration, function_distance));
        }

        return ret;
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

    @Override
    public void invalidateCurrentData() {
        this.currentDuration.clear();
        this.currentSpeed.clear();
    }

}
