/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package iii.vop2016.verkeer2.ejb.dummy;

import iii.vop2016.verkeer2.ejb.components.IRoute;
import iii.vop2016.verkeer2.ejb.components.IRouteData;
import iii.vop2016.verkeer2.ejb.components.Route;
import iii.vop2016.verkeer2.ejb.components.RouteData;
import iii.vop2016.verkeer2.ejb.dao.AggregationContainer;
import iii.vop2016.verkeer2.ejb.dao.ITrafficDataDAO;
import iii.vop2016.verkeer2.ejb.datasources.ISourceAdapter;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Random;

/**
 *
 * @author Tobias
 */
public class TrafficDataDAODummy implements ITrafficDataDAO{

    private List<IRouteData> data;
    private Random r = new Random();

    public TrafficDataDAODummy() {
        data = new ArrayList<>();
        
        List<Date> dates = new ArrayList<>();
        dates.add(new Date(System.currentTimeMillis()));
        dates.add(new Date(System.currentTimeMillis() + 5*60000));
        dates.add(new Date(System.currentTimeMillis() + 10*60000));
        dates.add(new Date(System.currentTimeMillis() + 15*60000));
        dates.add(new Date(System.currentTimeMillis() + 20*60000));
        dates.add(new Date(System.currentTimeMillis() + 25*60000));
        dates.add(new Date(System.currentTimeMillis() + 30*60000));
        dates.add(new Date(System.currentTimeMillis() + 35*60000));
        dates.add(new Date(System.currentTimeMillis() + 40*60000));
        dates.add(new Date(System.currentTimeMillis() + 45*60000));
        
        
        GeneralDAODummy routes = new GeneralDAODummy();
        List<IRoute> rs = routes.getRoutes();
        for(IRoute r: rs){
            for(int i = 0;i<10;i++){
                RouteData d = new RouteData();
                d.setDistance(this.r.nextInt(100));
                d.setDuration(this.r.nextInt(100));
                d.setRouteId(r.getId());
                d.setTimestamp(dates.get(i));
                data.add(d);
            }
        }

    }

    public List<IRouteData> getData(Date time1, Date time2) {
        List<IRouteData> d = new ArrayList<>();
        for(IRouteData data : this.data){
            if(data.getTimestamp().after(time1) && data.getTimestamp().before(time2))
                d.add(data);
        }
        return d;
    }

    @Override
    public IRouteData addData(IRouteData data) {
        this.data.add(data);
        return data;
    }

    @Override
    public List<IRouteData> addData(List<IRouteData> allData) {
        for(IRouteData data : allData){
            addData(data);
        }
        return allData;
    }

    @Override
    public List<IRouteData> getData(IRoute route, Date time1, Date time2) {
        List<IRouteData> result = new ArrayList<>();
        for(IRouteData data : getData(time1,time2)){
            if(data.getRouteId() ==  route.getId())
                result.add(data);
        }
        return result;
    }

    @Override
    public List<IRouteData> getData(String adapter, Date time1, Date time2) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public List<IRouteData> getCurrentTrafficSituation(IRoute route) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public List<IRouteData> getCurrentTrafficSituation(IRoute route, List<String> adapter) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public IRouteData getDataByID(long id) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void fillDummyData(long i) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public List<IRouteData> getData(IRoute route, List<Date> startList, List<Date> endList) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public List<Long> getAggregateData(IRoute route, Date time1, Date time2, AggregationContainer... aggr) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public List<Long> getAggregateData(IRoute route, List<Date> startList, List<Date> endList, AggregationContainer... aggr) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public List<Long> getAggregateData(IRoute route, Date time1, Date time2, long groupbyTimeFrames, AggregationContainer... aggr) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public List<Long> getAggregateData(IRoute route, List<Date> startList, List<Date> endList, long groupbyTimeFrames, AggregationContainer... aggr) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public List<Long> getAggregateData(IRoute route, Date time1, Date time2, long groupbyTimeFrames, boolean truncateDate, AggregationContainer... aggr) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public List<Long> getAggregateData(IRoute route, List<Date> startList, List<Date> endList, long groupbyTimeFrames, boolean truncateDate, AggregationContainer... aggr) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    
    
    
}
