/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package iii.vop2016.verkeer2.ejb.dao;

import iii.vop2016.verkeer2.ejb.components.IRoute;
import iii.vop2016.verkeer2.ejb.components.IRouteData;
import java.util.Date;
import java.util.List;

/**
 *
 * @author tobia
 */
public interface ITrafficDataDAO {

    public IRouteData addData(IRouteData data);

    public List<IRouteData> addData(List<IRouteData> data);
    
    public List<IRouteData> getRawData(IRoute route, Date time1, Date time2, List<String> adapter, int page);

    public List<IRouteData> getData(IRoute route, Date time1, Date time2, List<String> adapter);

    public List<IRouteData> getData(String adapter, Date time1, Date time2);

    public List<IRouteData> getCurrentTrafficSituation(IRoute route);

    public List<IRouteData> getCurrentTrafficSituation(IRoute route, List<String> adapter);

    public IRouteData getDataByID(long id);

    void fillDummyData(long i);
    
    void updateBlockList();

    public List<IRouteData> getData(IRoute route, List<Date> startList, List<Date> endList, List<String> adapter);

    public List<Long> getAggregateData(IRoute route, List<String> adapter, Date time1, Date time2, AggregationContainer... aggr);

    public List<Long> getAggregateData(IRoute route, List<String> adapter, List<Date> startList, List<Date> endList, AggregationContainer... aggr);

    public List<Long> getAggregateData(IRoute route, List<String> adapter, Date time1, Date time2, long groupbyTimeFrames, AggregationContainer... aggr);

    public List<Long> getAggregateData(IRoute route, List<String> adapter, List<Date> startList, List<Date> endList, long groupbyTimeFrames, AggregationContainer... aggr);

    public List<Long> getAggregateData(IRoute route, List<String> adapter, Date time1, Date time2, long groupbyTimeFrames, boolean truncateDate, AggregationContainer... aggr);

    public List<Long> getAggregateData(IRoute route, List<String> adapter, List<Date> startList, List<Date> endList, long groupbyTimeFrames, boolean truncateDate, AggregationContainer... aggr);

}
