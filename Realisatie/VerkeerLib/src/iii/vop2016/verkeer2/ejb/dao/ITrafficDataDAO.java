/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package iii.vop2016.verkeer2.ejb.dao;

import iii.vop2016.verkeer2.ejb.components.IRoute;
import iii.vop2016.verkeer2.ejb.components.IRouteData;
import iii.vop2016.verkeer2.ejb.datasources.ISourceAdapter;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 *
 * @author tobia
 */
public interface ITrafficDataDAO {

    public IRouteData addData(IRouteData data);
    public List<IRouteData> addData(List<IRouteData> data);
    
    public List<IRouteData> getData(IRoute route, Date time1, Date time2);
    public List<IRouteData> getData(String adapter, Date time1, Date time2);

    public List<IRouteData> getCurrentTrafficSituation(IRoute route);
    public List<IRouteData> getCurrentTrafficSituation(IRoute route, List<String> adapter);
    
    public IRouteData getDataByID(long id);
    
    void fillDummyData(long i);

    public List<IRouteData> getData(IRoute route, List<Date> startList, List<Date> endList);
    
    public List<Long> getAggregateData(IRoute route, Date time1, Date time2, AggregationContainer... aggr);
    
    public List<Long> getAggregateData(IRoute route, List<Date> startList, List<Date> endList, AggregationContainer... aggr);
    
}
