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
import java.util.TreeMap;
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

    private int getDataFromBuffer(Map<Long, Map<IRoute, Integer>> buffer, IRoute route, List<String> providers, long hash) {
        if (buffer.containsKey(hash)) {
            Map<IRoute, Integer> lowerBuffer = buffer.get(hash);
            if (lowerBuffer.containsKey(route)) {
                return lowerBuffer.get(route);
            }
        }
        return -1;
    }

    private void setDataInBuffer(int value, Map<Long, Map<IRoute, Integer>> buffer, IRoute route, List<String> providers, long hash) {
        setDataInBuffer(value, buffer, route, providers, hash, -1);
    }

    private void setDataInBuffer(int value, Map<Long, Map<IRoute, Integer>> buffer, IRoute route, List<String> providers, long hash, int err) {
        if (value != err) {
            if (buffer.containsKey(hash)) {
                buffer.get(hash).put(route, value);
            } else {
                Map<IRoute, Integer> m = new HashMap<>();
                m.put(route, value);
                buffer.put(hash, m);
            }
        }
    }

    private Map<Long, Map<IRoute, Integer>> currentDuration = new HashMap<>();

    @Override
    public int getCurrentDuration(IRoute route, List<String> providers) {
        long hash = calculateHash(providers);
        int buffer = getDataFromBuffer(currentDuration, route, providers, hash);
        if (buffer == -1) {
            ITrafficDataDAO dao = beans.getTrafficDataDAO();
            List<IRouteData> list = dao.getCurrentTrafficSituation(route, providers);

            if (list == null || list.isEmpty()) {
                return -1;
            }

            buffer = CalculateArithmaticMean(list, new Function<IRouteData, Long>() {
                @Override
                public Long apply(IRouteData t) {
                    return new Long(t.getDuration());
                }
            }, function_distance);

            setDataInBuffer(buffer, currentDuration, route, providers, hash);
        }
        return buffer;
    }

    private Map<Long, Map<IRoute, Integer>> currentSpeed = new HashMap<>();

    @Override
    public int getCurrentVelocity(IRoute route, List<String> providers) {
        long hash = calculateHash(providers);
        int buffer = getDataFromBuffer(currentSpeed, route, providers, hash);

        if (buffer == -1) {
            ITrafficDataDAO dao = beans.getTrafficDataDAO();
            List<IRouteData> list = dao.getCurrentTrafficSituation(route, providers);

            if (list == null || list.size() == 0) {
                return -1;
            }

            buffer = CalculateArithmaticMean(list, new Function<IRouteData, Long>() {
                @Override
                public Long apply(IRouteData t) {
                    return (long) t.getDistance() / (long) t.getDuration();
                }
            }, function_distance);

            setDataInBuffer(buffer, currentSpeed, route, providers, hash);
        }
        return buffer;
    }

    private Map<Long, Map<IRoute, Integer>> optimalDuration = new HashMap<>();

    @Override
    public int getOptimalDuration(IRoute route, List<String> providers) {
        long hash = calculateHash(providers);
        int buffer = getDataFromBuffer(optimalDuration, route, providers, hash);

        if (buffer == -1) {
            Properties properties = getProperties();
            long timeframe = Integer.parseInt(properties.getProperty("OptimalDurationTimeFrame", "0")) * (long) 1000;
            long currentTime = beans.getTimer().getCurrentTime();
            Date startDate = new Date(currentTime - timeframe);
            Date endDate = new Date(currentTime);

            buffer = getOptimalDuration(route, providers, startDate, endDate);
            setDataInBuffer(buffer, optimalDuration, route, providers, hash);
        }
        return buffer;
    }

    private Map<Long, Map<IRoute, Integer>> optimalSpeed = new HashMap<>();

    @Override
    public int getOptimalVelocity(IRoute route, List<String> providers) {
        long hash = calculateHash(providers);
        int buffer = getDataFromBuffer(optimalSpeed, route, providers, hash);

        if (buffer == -1) {
            Properties properties = getProperties();
            long timeframe = Integer.parseInt(properties.getProperty("OptimalDurationTimeFrame", "0")) * (long) 1000;
            long currentTime = beans.getTimer().getCurrentTime();
            Date startDate = new Date(currentTime - timeframe);
            Date endDate = new Date(currentTime);

            buffer = getOptimalVelocity(route, providers, startDate, endDate);
            setDataInBuffer(buffer, optimalSpeed, route, providers, hash);
        }
        return buffer;
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

    private Map<Long, Map<IRoute, Integer>> avgDuration = new HashMap<>();

    @Override
    public int getAvgDuration(IRoute route, List<String> providers) {
        long hash = calculateHash(providers);
        int buffer = getDataFromBuffer(avgDuration, route, providers, hash);

        if (buffer == -1) {
            Properties properties = getProperties();
            long timeframe = Integer.parseInt(properties.getProperty("AvgDurationTimeFrame", "0")) * (long) 1000;
            long currentTime = beans.getTimer().getCurrentTime();
            Date startDate = new Date(currentTime - timeframe);
            Date endDate = new Date(currentTime);

            buffer = getAvgDuration(route, providers, startDate, endDate);
            setDataInBuffer(buffer, avgDuration, route, providers, hash);
        }
        return buffer;
    }

    private Map<Long, Map<IRoute, Integer>> avgSpeed = new HashMap<>();

    @Override
    public int getAvgVelocity(IRoute route, List<String> providers) {
        long hash = calculateHash(providers);
        int buffer = getDataFromBuffer(avgSpeed, route, providers, hash);

        if (buffer == -1) {
            Properties properties = getProperties();
            long timeframe = Integer.parseInt(properties.getProperty("AvgDurationTimeFrame", "0")) * (long) 1000;
            long currentTime = beans.getTimer().getCurrentTime();
            Date startDate = new Date(currentTime - timeframe);
            Date endDate = new Date(currentTime);

            buffer = getAvgVelocity(route, providers, startDate, endDate);
            setDataInBuffer(buffer, avgSpeed, route, providers, hash);
        }
        return buffer;
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
        int opt = this.getOptimalDuration(route, providers);

        if (opt == -1) {
            return -1;
        }

        int current = getCurrentDuration(route, providers);

        return beans.getThresholdManager().getThresholdLevel(route, current - opt);
    }

    @Override
    public int getDelayLevel(IRoute route, List<String> providers, Date start, Date end) {
        int opt = this.getOptimalDuration(route, providers);
        if (opt == -1) {
            return -1;
        }

        int pastAvg = getAvgDuration(route, providers, start, end);
        if (opt == -1) {
            return -1;
        }

        return beans.getThresholdManager().getThresholdLevel(route, pastAvg - opt);
    }

    @Override
    public int getAvgDelayLevel(IRoute route, List<String> providers) {
        int opt = this.getOptimalDuration(route, providers);
        if (opt == -1) {
            return -1;
        }

        int avg = getAvgDuration(route, providers);
        if (opt == -1) {
            return -1;
        }

        return beans.getThresholdManager().getThresholdLevel(route, avg - opt);
    }

    private Map<Long, Map<IRoute, Integer>> distance = new HashMap<>();

    @Override
    public int getDistance(IRoute route, List<String> providers) {
        long hash = calculateHash(providers);
        int buffer = getDataFromBuffer(distance, route, providers, hash);

        if (buffer == -1) {
            ITrafficDataDAO dao = beans.getTrafficDataDAO();
            List<IRouteData> list = dao.getCurrentTrafficSituation(route, providers);

            long distance = 0;

            int i = 0;
            for (IRouteData r : list) {
                distance += r.getDistance();
                i++;
            }

            buffer = Math.toIntExact(distance / i);
            setDataInBuffer(buffer, this.distance, route, providers, hash);
        }
        return buffer;
    }

    private Map<Long, Map<IRoute, Integer>> trend = new HashMap<>();

    @Override
    public int getTrend(IRoute route, List<String> providers) {
        long hash = calculateHash(providers);
        int buffer = getDataFromBuffer(trend, route, providers, hash);

        if (buffer == -1) {
            Properties properties = getProperties();
            long timeframe = Integer.parseInt(properties.getProperty("TrendTimeFrame", "0")) * (long) 1000;
            long currentTime = beans.getTimer().getCurrentTime();
            Date startDate = new Date(currentTime - timeframe);
            Date endDate = new Date(currentTime);

            buffer = getTrend(route, providers, startDate, endDate);
            setDataInBuffer(buffer, trend, route, providers, hash, 0);
        }
        return buffer;
    }

    @Override
    public int getTrend(IRoute route, List<String> providers, Date start, Date end) {
        ITrafficDataDAO dao = beans.getTrafficDataDAO();
        List<IRouteData> data = dao.getData(route, start, end);

        Map<Date, List<IRouteData>> sortedData = new HashMap<>();

        if (data == null || data.isEmpty()) {
            return 0;
        }

        for (IRouteData d : data) {
            if (sortedData.containsKey(d.getTimestamp())) {
                sortedData.get(d.getTimestamp()).add(d);
            } else {
                List<IRouteData> arr = new ArrayList<>();
                arr.add(d);
                sortedData.put(d.getTimestamp(), arr);
            }
        }

        Map<Date, Integer> aggrData = new TreeMap<>();

        for (Map.Entry<Date, List<IRouteData>> entry : sortedData.entrySet()) {
            aggrData.put(entry.getKey(), getMeanDurationFromRouteData(entry.getValue()));
        }
        
        List<Integer> delta = new ArrayList<>();
        int prev = -1;
        for (Map.Entry<Date, Integer> entry : aggrData.entrySet()) {
            if(prev == -1){
                prev = entry.getValue();
            }else{
                delta.add(entry.getValue() - prev);
                prev = entry.getValue();
            }
        }

        return 0;
    }

    @Override
    public Map<Date, Integer> getRecentData(IRoute route, List<String> providers) {
        ITrafficDataDAO dao = beans.getTrafficDataDAO();
        return new HashMap<>();
    }

    @Override
    public Map<Weekdays, List<Integer>> getDataByDay(IRoute route, List<String> providers, Weekdays... days) {
        Properties properties = getProperties();
        long timeframe = Integer.parseInt(properties.getProperty("DataByDayTimeFrame", "0")) * (long) 1000;
        long currentTime = beans.getTimer().getCurrentTime();
        Date startDate = new Date(currentTime - timeframe);
        Date endDate = new Date(currentTime);

        return getDataByDay(route, providers, startDate, endDate, days);
    }

    @Override
    public Map<Weekdays, List<Integer>> getDataVelocityByDay(IRoute route, List<String> providers, Weekdays... days) {
        Properties properties = getProperties();
        long timeframe = Integer.parseInt(properties.getProperty("DataByDayTimeFrame", "0")) * (long) 1000;
        long currentTime = beans.getTimer().getCurrentTime();
        Date startDate = new Date(currentTime - timeframe);
        Date endDate = new Date(currentTime);

        return getDataVelocityByDay(route, providers, startDate, endDate, days);
    }

    @Override
    public Map<Weekdays, List<Integer>> getDataByDay(IRoute route, List<String> providers, Date start, Date end, Weekdays... days) {
        ITrafficDataDAO dao = beans.getTrafficDataDAO();
        Map<Weekdays, List<Integer>> ret = new HashMap<>();

        GregorianCalendar startHour = new GregorianCalendar(0, 0, 0, 6, 0, 0);
        GregorianCalendar endHour = new GregorianCalendar(0, 0, 0, 2, 0);
        List<List<List<Date>>> list = generateListsForDayOfWeek(start, end, startHour, endHour, true);

        for (Weekdays day : days) {
            int weekday = day.ordinal();
            List<Long> data = dao.getAggregateData(route, list.get(0).get(weekday), list.get(1).get(weekday), 3600, true, new AggregationContainer(Aggregation.none, "timestamp"), new AggregationContainer(Aggregation.sum, "duration * distance"), new AggregationContainer(Aggregation.sum, "distance"));

            ret.put(day, MapDataByDay(data));
        }
        return ret;
    }

    @Override
    public Map<Weekdays, List<Integer>> getDataVelocityByDay(IRoute route, List<String> providers, Date start, Date end, Weekdays... days) {
        ITrafficDataDAO dao = beans.getTrafficDataDAO();
        Map<Weekdays, List<Integer>> ret = new HashMap<>();

        GregorianCalendar startHour = new GregorianCalendar(0, 0, 0, 6, 0, 0);
        GregorianCalendar endHour = new GregorianCalendar(0, 0, 0, 2, 0);
        List<List<List<Date>>> list = generateListsForDayOfWeek(start, end, startHour, endHour, true);

        for (Weekdays day : days) {
            int weekday = day.ordinal();
            List<Long> data = dao.getAggregateData(route, list.get(0).get(weekday), list.get(1).get(weekday), 3600, true, new AggregationContainer(Aggregation.none, "timestamp"), new AggregationContainer(Aggregation.sum, "distance * distance / duration "), new AggregationContainer(Aggregation.sum, "distance"));

            ret.put(day, MapDataByDay(data));
        }
        return ret;
    }

    private List<Integer> MapDataByDay(List<Long> data) {
        int index = 0;
        int hour = 6;
        Calendar cal = new GregorianCalendar();
        List<Integer> arr = new ArrayList<>();
        while (index < data.size()) {
            Date d = new Date(data.get(index++));
            cal.setTime(d);

            Long exp = data.get(index++);
            Long div = data.get(index++);
            int res = Math.toIntExact(exp / div);

            int nextHour = cal.get(GregorianCalendar.HOUR_OF_DAY);

            for (int i = hour + 1; i < nextHour; i++) {
                arr.add(-1);
            }
            arr.add(res);
            hour = nextHour;

        }
        for (int i = hour + 1; i <= (24 + 2); i++) {
            arr.add(-1);
        }

        return arr;
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

    //generate a list with all specific hours between 2 dates (for example all 06:00 bewteen 02/02/16 04/04/16)
    private List<Date> GenerateListForHourBetweenDates(Calendar hour, Date startDate, Date endDate) {
        List<Date> dates = new ArrayList<>();

        Calendar cStart = new GregorianCalendar();
        cStart.setTime(startDate);
        Calendar cEnd = new GregorianCalendar();
        cEnd.setTime(endDate);

        cStart.set(Calendar.HOUR_OF_DAY, hour.get(Calendar.HOUR_OF_DAY));
        cStart.set(Calendar.MINUTE, hour.get(Calendar.MINUTE));

        while (cStart.before(cEnd)) {
            dates.add(cStart.getTime());
            cStart.add(Calendar.DAY_OF_MONTH, 1);
        }
        return dates;
    }

    //List contains 2 list, 1 for startDates, other for endDates. In those dates there is a list for every day of the week.
    //nextday indicated that the endHour is in the next day
    private List<List<List<Date>>> generateListsForDayOfWeek(Date startDate, Date endDate, Calendar startHour, Calendar endHour, boolean nextDay) {
        List<List<List<Date>>> ret = new ArrayList<>();
        for (int j = 0; j < 2; j++) {
            ret.add(new ArrayList<List<Date>>());
            for (int i = 0; i < 7; i++) {
                ret.get(j).add(new ArrayList<Date>());
            }
        }

        GregorianCalendar start = new GregorianCalendar();
        GregorianCalendar end = new GregorianCalendar();

        start.setTime(startDate);
        //hour and minutes are adjusted, not seconds
        start.set(GregorianCalendar.SECOND, 0);

        end.setTime(endDate);

        while (start.before(end)) {
            int weekday = start.get(GregorianCalendar.DAY_OF_WEEK);

            //correct weekday to 0 based index, and shift monday to first
            weekday -= 2;
            if (weekday < 0) {
                weekday = 6;
            }

            start.set(GregorianCalendar.HOUR_OF_DAY, startHour.get(GregorianCalendar.HOUR_OF_DAY));
            start.set(GregorianCalendar.MINUTE, startHour.get(GregorianCalendar.MINUTE));
            ret.get(0).get(weekday).add(start.getTime());

            //adds the endDate if the hour is in same day
            if (!nextDay) {
                start.set(GregorianCalendar.HOUR_OF_DAY, endHour.get(GregorianCalendar.HOUR_OF_DAY));
                start.set(GregorianCalendar.MINUTE, endHour.get(GregorianCalendar.MINUTE));
                ret.get(1).get(weekday).add(start.getTime());
            }

            start.add(GregorianCalendar.DAY_OF_MONTH, 1);

            //adds the endDate if the hour was in the next day
            if (nextDay) {
                start.set(GregorianCalendar.HOUR_OF_DAY, endHour.get(GregorianCalendar.HOUR_OF_DAY));
                start.set(GregorianCalendar.MINUTE, endHour.get(GregorianCalendar.MINUTE));
                ret.get(1).get(weekday).add(start.getTime());
            }

            //for eval of end, so that the last day is certainly included in the generation
            start.set(GregorianCalendar.HOUR_OF_DAY, 0);
        }

        return ret;
    }

    @Override
    public void invalidateCurrentData() {
        this.currentDuration.clear();
        this.currentSpeed.clear();
        this.distance.clear();
        this.trend.clear();
    }

    private long calculateHash(List<String> providers) {
        if (providers == null) {
            return 0;
        }
        long hash = 0;
        for (String s : providers) {
            hash += s.hashCode();
        }
        return hash;
    }
}
