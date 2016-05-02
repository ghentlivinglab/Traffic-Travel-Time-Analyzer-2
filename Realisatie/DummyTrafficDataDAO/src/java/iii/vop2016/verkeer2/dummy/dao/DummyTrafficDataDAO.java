/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package iii.vop2016.verkeer2.dummy.dao;

import iii.vop2016.verkeer2.ejb.components.IRoute;
import iii.vop2016.verkeer2.ejb.components.IRouteData;
import iii.vop2016.verkeer2.ejb.dao.AggregationContainer;
import iii.vop2016.verkeer2.ejb.dao.TrafficDataDAORemote;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import javax.annotation.PostConstruct;
import javax.ejb.Singleton;

/**
 *
 * @author tobia
 */
@Singleton
public class DummyTrafficDataDAO implements TrafficDataDAORemote {

    private List<IRouteData> data;
    private int count = 0;

    @PostConstruct
    public void init() {
        data = new ArrayList<>();
    }

    @Override
    public IRouteData addData(IRouteData data) {
        this.data.add(data);
        data.setId(count++);
        return data;
    }

    @Override
    public List<IRouteData> addData(List<IRouteData> data) {
        for (IRouteData d : data) {
            addData(d);
        }
        return data;
    }

    @Override
    public List<IRouteData> getData(IRoute route, Date time1, Date time2, List<String> adapter) {
        List<IRouteData> ret = new ArrayList<>();
        for (IRouteData data : this.data) {
            if (data.getRouteId() == route.getId() && data.getTimestamp().before(time2) && data.getTimestamp().after(time1)) {
                ret.add(data);
            }
        }
        return ret;
    }

    @Override
    public List<IRouteData> getData(String adapter, Date time1, Date time2) {
        List<IRouteData> ret = new ArrayList<>();
        for (IRouteData data : this.data) {
            if (data.getProvider().equals(adapter) && data.getTimestamp().before(time2) && data.getTimestamp().after(time1)) {
                ret.add(data);
            }
        }
        return ret;
    }

    @Override
    public List<IRouteData> getCurrentTrafficSituation(IRoute route) {
        List<IRouteData> ret = new ArrayList<>();
        if (this.data.size() == 0) {
            return ret;
        }

        Date currentDate = data.get(data.size() - 1).getTimestamp();

        for (IRouteData data : this.data) {
            if (data.getRouteId() == route.getId() && data.getTimestamp().equals(currentDate)) {
                ret.add(data);
            }
        }
        return ret;
    }

    @Override
    public List<IRouteData> getCurrentTrafficSituation(IRoute route, List<String> adapter) {
        List<IRouteData> ret = new ArrayList<>();
        if (this.data.size() == 0) {
            return ret;
        }

        Date currentDate = data.get(data.size() - 1).getTimestamp();

        for (IRouteData data : this.data) {
            if (adapter == null || adapter.size() == 0) {
                if (data.getRouteId() == route.getId() && data.getTimestamp().equals(currentDate)) {
                    ret.add(data);
                }
            } else if (data.getRouteId() == route.getId() && data.getTimestamp().equals(currentDate) && adapter.contains(data.getProvider())) {
                ret.add(data);
            }
        }
        return ret;
    }

    @Override
    public IRouteData getDataByID(long id) {
        for (IRouteData data : this.data) {
            if (data.getId() == id) {
                return data;
            }
        }
        return null;
    }

    @Override
    public void fillDummyData(long i) {

    }

    @Override
    public List<IRouteData> getData(IRoute route, List<Date> startList, List<Date> endList, List<String> adapter) {
        List<IRouteData> ret = new ArrayList<>();

        if (startList.size() != endList.size()) {
            return ret;
        }

        for (int i = 0; i < startList.size(); i++) {
            Date time1 = startList.get(i);
            Date time2 = endList.get(i);
            for (IRouteData data : this.data) {
                if (data.getId() == route.getId() && data.getTimestamp().before(time2) && data.getTimestamp().after(time1)) {
                    ret.add(data);
                }
            }
        }
        return ret;
    }

    @Override
    public List<Long> getAggregateData(IRoute route, List<String> adapter, Date time1, Date time2, AggregationContainer... aggr) {
        List<Long> ret = new ArrayList<>();
        for (AggregationContainer container : aggr) {
            switch (container.aggregation) {
                case sum:
                    long res = 0;

                    switch (container.attr) {
                        case "duration * distance":
                            for (IRouteData data : this.data) {
                                if (data.getRouteId() == route.getId() && data.getTimestamp().before(time2) && data.getTimestamp().after(time1)) {
                                    res += (data.getDuration() * data.getDistance());
                                }
                            }
                            break;
                        case "distance * distance / duration ":
                            for (IRouteData data : this.data) {
                                if (data.getRouteId() == route.getId() && data.getTimestamp().before(time2) && data.getTimestamp().after(time1)) {
                                    res += (data.getDistance() * data.getDistance() / data.getDuration());
                                }
                            }
                            break;
                        case "distance":
                            for (IRouteData data : this.data) {
                                if (data.getRouteId() == route.getId() && data.getTimestamp().before(time2) && data.getTimestamp().after(time1)) {
                                    res += (data.getDistance());
                                }
                            }
                            break;
                        default:
                            throw new UnknownError(container.attr + "not known in dummy...");
                    }

                    ret.add(res);
                    break;
            }
        }
        return ret;
    }

    @Override
    public List<Long> getAggregateData(IRoute route, List<String> adapter, List<Date> startList, List<Date> endList, AggregationContainer... aggr) {
        List<Long> ret = new ArrayList<>();

        if (startList.size() != endList.size()) {
            return ret;
        }

        for (AggregationContainer container : aggr) {
            switch (container.aggregation) {
                case sum:
                    long res = 0;

                    switch (container.attr) {
                        case "duration * distance":
                            for (int i = 0; i < startList.size(); i++) {
                                Date time1 = startList.get(i);
                                Date time2 = endList.get(i);
                                for (IRouteData data : this.data) {
                                    if (data.getRouteId() == route.getId() && data.getTimestamp().before(time2) && data.getTimestamp().after(time1)) {
                                        res += (data.getDuration() * data.getDistance());
                                    }
                                }
                            }
                            break;
                        case "distance * distance / duration ":
                            for (int i = 0; i < startList.size(); i++) {
                                Date time1 = startList.get(i);
                                Date time2 = endList.get(i);
                                for (IRouteData data : this.data) {
                                    if (data.getRouteId() == route.getId() && data.getTimestamp().before(time2) && data.getTimestamp().after(time1)) {
                                        res += (data.getDistance() * data.getDistance() / data.getDuration());
                                    }
                                }
                            }
                            break;
                        case "distance":
                            for (int i = 0; i < startList.size(); i++) {
                                Date time1 = startList.get(i);
                                Date time2 = endList.get(i);
                                for (IRouteData data : this.data) {
                                    if (data.getRouteId() == route.getId() && data.getTimestamp().before(time2) && data.getTimestamp().after(time1)) {
                                        res += (data.getDistance());
                                    }
                                }
                            }
                            break;
                        default:
                            throw new UnknownError(container.attr + "not known in dummy...");
                    }

                    ret.add(res);
                    break;
            }
        }
        return ret;
    }

    @Override
    public List<Long> getAggregateData(IRoute route, List<String> adapter, Date time1, Date time2, long groupbyTimeFrames, AggregationContainer... aggr) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public List<Long> getAggregateData(IRoute route, List<String> adapter, List<Date> startList, List<Date> endList, long groupbyTimeFrames, AggregationContainer... aggr) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public List<Long> getAggregateData(IRoute route, List<String> adapter, Date time1, Date time2, long groupbyTimeFrames, boolean truncateDate, AggregationContainer... aggr) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public List<Long> getAggregateData(IRoute route, List<String> adapter, List<Date> startList, List<Date> endList, long groupbyTimeFrames, boolean truncateDate, AggregationContainer... aggr) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public List<IRouteData> getRawData(IRoute route, Date time1, Date time2, List<String> adapter, int page) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void updateBlockList() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

}
