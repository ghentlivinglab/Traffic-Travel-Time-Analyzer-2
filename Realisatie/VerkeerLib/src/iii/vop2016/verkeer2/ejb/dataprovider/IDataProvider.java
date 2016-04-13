/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package iii.vop2016.verkeer2.ejb.dataprovider;

import iii.vop2016.verkeer2.ejb.components.IRoute;
import iii.vop2016.verkeer2.ejb.components.IRouteData;
import iii.vop2016.verkeer2.ejb.components.Weekdays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 *
 * @author Tobias
 */
public interface IDataProvider {

    //get data for last entries, weigthed aritmetic mean
    int getCurrentDuration(IRoute route, List<String> providers);

    int getCurrentVelocity(IRoute route, List<String> providers);

    //get data for optimal entries. Timeperiod specified in property file (2 weeks)
    int getOptimalDuration(IRoute route, List<String> providers);

    int getOptimalVelocity(IRoute route, List<String> providers);

    int getOptimalDuration(IRoute route, List<String> providers, Date start, Date end);

    int getOptimalVelocity(IRoute route, List<String> providers, Date start, Date end);

    //get data for optimal entries. Timeperiod specified in start and end
    int getAvgDuration(IRoute route, List<String> providers);

    int getAvgVelocity(IRoute route, List<String> providers);

    int getAvgDuration(IRoute route, List<String> providers, Date start, Date end);

    int getAvgVelocity(IRoute route, List<String> providers, Date start, Date end);

    //get the current delay level via threshold manager
    int getCurrentDelayLevel(IRoute route, List<String> providers);

    int getDelayLevel(IRoute route, List<String> providers, Date start, Date end);

    int getAvgDelayLevel(IRoute route, List<String> providers);

    //return the mean distance for this route
    int getDistance(IRoute route, List<String> providers);

    //return if traffic is rising, steady or dropping
    int getTrend(IRoute route, List<String> providers);

    int getTrend(IRoute route, List<String> providers, Date start, Date end);

    //get the recent mean durations (1h)
    Map<Date, Integer> getRecentData(IRoute route, List<String> providers);

    //get the list of mean data for the specific day
    Map<Weekdays, List<Integer>> getDataByDay(IRoute route, List<String> providers, Weekdays... days);

    Map<Weekdays, List<Integer>> getDataVelocityByDay(IRoute route, List<String> providers, Weekdays... days);

    Map<Weekdays, List<Integer>> getDataByDay(IRoute route, List<String> providers, Date start, Date end, Weekdays... days);

    Map<Weekdays, List<Integer>> getDataVelocityByDay(IRoute route, List<String> providers, Date start, Date end, Weekdays... days);

    
    //get the list of mean data for the specific day
    List<Integer> getDataByCombinedDay(IRoute route, List<String> providers);

    List<Integer> getDataVelocityByCombinedDay(IRoute route, List<String> providers);

    List<Integer> getDataByCombinedDay(IRoute route, List<String> providers, Date start, Date end);

    List<Integer> getDataVelocityByCombinedDay(IRoute route, List<String> providers, Date start, Date end);

    
    List<String> getDataByDayHours();
    
    //get the list of mean data for a mean day in duration (2week)
    Map<Date, Integer> getData(IRoute route, List<String> providers, int precision, Date start, Date end);

    Map<Date, Integer> getDataVelocity(IRoute route, List<String> providers, int precision, Date start, Date end);
    
    Map<Date, Integer> getData(IRoute route, List<String> providers, int precision);

    Map<Date, Integer> getDataVelocity(IRoute route, List<String> providers, int precision);
    
    Map<Date, Integer> getData(IRoute route, List<String> providers, Date start, Date end);

    Map<Date, Integer> getDataVelocity(IRoute route, List<String> providers, Date start, Date end);
    
    Map<Date, Integer> getData(IRoute route, List<String> providers);

    Map<Date, Integer> getDataVelocity(IRoute route, List<String> providers);

    //helper function for datadownstream
    int getMeanDurationFromRouteData(List<IRouteData> routeData);

    void invalidateCurrentData();

}
