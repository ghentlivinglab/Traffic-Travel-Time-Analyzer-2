/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package iii.vop2016.verkeer2.ejb.dao.dummy;

import iii.vop2016.verkeer2.ejb.components.IRoute;
import iii.vop2016.verkeer2.ejb.components.IRouteData;
import iii.vop2016.verkeer2.ejb.dao.AggregationContainer;
import iii.vop2016.verkeer2.ejb.datasources.ISourceAdapter;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import javax.annotation.PostConstruct;
import javax.ejb.Singleton;

/**
 *
 * @author Mike
 */
@Singleton
public class TrafficDataDAONoDB implements TrafficDataDAONoDBRemote {

    List<IRouteData> data;
    
    private long lastIndex;
    
    @PostConstruct
    private void init(){
        data = new ArrayList<>();
        lastIndex = 0;
    }
    

    public List<IRouteData> getData(Date time1, Date time2) {
        List<IRouteData> results = new ArrayList<>();
        for(IRouteData d : data){
            if(d.getTimestamp().compareTo(time1) > 0 && d.getTimestamp().compareTo(time2) < 0)
                results.add(d);
        }
        return results;
    }

    @Override
    public IRouteData addData(IRouteData data) {
        this.data.add(data);
        data.setId(++lastIndex);
        return data;
    }

    @Override
    public List<IRouteData> addData(List<IRouteData> allData) {
        
        for(IRouteData d : allData){
            this.data.add(d);
            d.setId(++lastIndex);
        }
        
        return allData;
    }

    // Add business logic below. (Right-click in editor and choose
    // "Insert Code > Add Business Method")

    @Override
    public List<IRouteData> getData(IRoute route, Date time1, Date time2) {
        List<IRouteData> results = new ArrayList<>();
        for(IRouteData d : data){
            if(d.getTimestamp().compareTo(time1) > 0 && d.getTimestamp().compareTo(time2) < 0)
                if(d.getRouteId() == route.getId())
                    results.add(d);
        }
        return results;
    }

    public List<IRouteData> getData(ISourceAdapter adapter, Date time1, Date time2) {
        List<IRouteData> results = new ArrayList<>();
        for(IRouteData d : data){
            if(d.getTimestamp().compareTo(time1) > 0 && d.getTimestamp().compareTo(time2) < 0)
                if(d.getProvider().equals(adapter.getProviderName()))
                    results.add(d);
        }
        return results;
    }

    @Override
    public List<IRouteData> getCurrentTrafficSituation(IRoute route) {
        return null;
    }

    public List<IRouteData> getCurrentTrafficSituation(IRoute route, ISourceAdapter adapter) {
        return null;
    }

    @Override
    public List<IRouteData> getData(String adapter, Date time1, Date time2) {
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

    
}
