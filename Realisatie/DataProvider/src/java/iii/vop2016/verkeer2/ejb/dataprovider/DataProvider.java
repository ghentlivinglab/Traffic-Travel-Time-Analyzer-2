/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package iii.vop2016.verkeer2.ejb.dataprovider;

import iii.vop2016.verkeer2.ejb.components.IRoute;
import iii.vop2016.verkeer2.ejb.components.Weekdays;
import java.util.Date;
import java.util.List;
import java.util.Map;
import javax.ejb.Singleton;

/**
 *
 * @author Tobias
 */
@Singleton
public class DataProvider implements DataProviderRemote {

    @Override
    public int getCurrentDuration(IRoute route, List<String> providers) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public int getCurrentVelocity(IRoute route, List<String> providers) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public int getOptimalDuration(IRoute route, List<String> providers) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public int getOptimalVelocity(IRoute route, List<String> providers) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public int getOptimalDuration(IRoute route, List<String> providers, Date start, Date end) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public int getOptimalVelocity(IRoute route, List<String> providers, Date start, Date end) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public int getAvgDuration(IRoute route, List<String> providers) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public int getAvgVelocity(IRoute route, List<String> providers) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public int getAvgDuration(IRoute route, List<String> providers, Date start, Date end) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public int getAvgVelocity(IRoute route, List<String> providers, Date start, Date end) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public int getCurrentDelayLevel(IRoute route, List<String> providers) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public int getDelayLevel(IRoute route, List<String> providers, Date start, Date end) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public int getDistance(IRoute route, List<String> providers) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public int getTrend(IRoute route, List<String> providers) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public int getTrend(IRoute route, List<String> providers, Date start, Date end) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public Map<Date, Integer> getRecentData(IRoute route, List<String> providers) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public Map<Date, Integer> getDataByDay(IRoute route, List<String> providers, Date start, Date end, Weekdays day) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public Map<Date, Integer> getDataVelocityByDay(IRoute route, List<String> providers, Date start, Date end, Weekdays day) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public Map<Date, Integer> getDataByDayInWorkWeek(IRoute route, List<String> providers, Date start, Date end) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public Map<Date, Integer> getDataVelocityByDayInWorkWeek(IRoute route, List<String> providers, Date start, Date end) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public Map<Date, Integer> getData(IRoute route, List<String> providers, Date start, Date end) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public Map<Date, Integer> getDataVelocity(IRoute route, List<String> providers, Date start, Date end) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public Map<Date, Integer> getDataByDayInWorkWeek(IRoute route, List<String> providers) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public Map<Date, Integer> getDataVelocityByDayInWorkWeek(IRoute route, List<String> providers) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
}
