/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package iii.vop2016.verkeer2.ejb.dao;

import iii.vop2016.verkeer2.ejb.components.IRoute;
import iii.vop2016.verkeer2.ejb.components.IRouteData;
import iii.vop2016.verkeer2.ejb.components.RouteData;
import iii.vop2016.verkeer2.ejb.helper.BeanFactory;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import javax.ejb.ConcurrencyManagement;
import javax.ejb.ConcurrencyManagementType;
import javax.ejb.Lock;
import javax.ejb.LockType;
import javax.ejb.SessionContext;
import javax.ejb.Singleton;
import javax.ejb.Startup;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

/**
 *
 * @author tobia
 */
@Singleton
@Startup
@ConcurrencyManagement(ConcurrencyManagementType.CONTAINER)
@Lock(LockType.READ)
public class TrafficDataDAO implements TrafficDataDAORemote, TrafficDataDAOLocal {

    @PersistenceContext(name = "TrafficDBPU")
    EntityManager em;
    @Resource
    private SessionContext sctx;
    private InitialContext ctx;
    private BlockList blocklist;
    private BeanFactory beans;

    @PostConstruct
    public void init() {
        try {
            ctx = new InitialContext();
        } catch (NamingException ex) {
            Logger.getLogger(TrafficDataDAO.class.getName()).log(Level.SEVERE, null, ex);
        }

        generateBlockList();

        beans = BeanFactory.getInstance(ctx, sctx);
        beans.getLogger().log(Level.INFO, "TrafficDataDAO has been initialized.");
    }

    public TrafficDataDAO() {

    }

    @Override
    public IRouteData addData(IRouteData data) {
        RouteDataEntity r = new RouteDataEntity(data);
        try {
            em.persist(r);
        } catch (Exception ex) {
            beans.getLogger().log(Level.SEVERE, "Could not persist routedata: " + data.toString());
        }
        return new RouteData(r);
    }

    @Override
    public List<IRouteData> addData(List<IRouteData> allData) {
        List<IRouteData> data = new ArrayList<>();
        for (IRouteData d : allData) {
            data.add(addData(d));
        }
        return data;
    }

    @Override
    public List<IRouteData> getData(IRoute route, Date time1, Date time2, List<String> adapter) {
        List<IRouteData> data = new ArrayList<>();
        try {
            long[] range = blocklist.getIdRange(time1, time2);
            if (range[0] == -1 || range[1] == -1) {
                throw new NoResultException("Could not retrieve id segment from blocklist");
            }

            int i = 0;
            Request r = new Request(true, 0);
            r.addParam(i++, new Parameter("id", range[0], range[1], Operation.between));
            r.addParam(i++, new Parameter("routeId", route.getId(), Operation.eq));
            if (adapter != null && !adapter.isEmpty()) {
                Parameter p = new Parameter("provider", adapter, Operation.eq);
                r.addParam(i++, p);
            }
            r.addParam(i++, new Parameter("timestamp", time1, time2, Operation.between));

            List<IRouteData> routesEntities = r.PrepareQuery(em).getResultList();
            for (IRouteData rdata : routesEntities) {
                data.add(new RouteData(rdata));
            }
        } catch (Exception e) {
            beans.getLogger().log(Level.SEVERE, "Could not retrieve raw data for: " + route.getName() + " (" + time1 + " - " + time2 + ").");
        } finally {

        }
        return data;
    }

    @Override
    public List<IRouteData> getData(String adapter, Date time1, Date time2) {
        List<IRouteData> data = new ArrayList<>();
        try {
            //get all routes
            Query q = em.createQuery("SELECT r FROM RouteDataEntity r WHERE r.timestamp >= :time1 AND r.timestamp <= :time2 AND r.providerName = :providerName");
            q.setParameter("time1", time1);
            q.setParameter("time2", time2);
            q.setParameter("providerName", adapter);
            List<IRouteData> routesEntities = q.getResultList();
            for (IRouteData r : routesEntities) {
                data.add(new RouteData(r));
            }
        } catch (Exception e) {
            beans.getLogger().log(Level.SEVERE, "Could not retrieve raw data for: " + adapter + " (" + time1 + " - " + time2 + ").");
        } finally {

        }
        return data;
    }

    @Override
    public List<IRouteData> getCurrentTrafficSituation(IRoute route) {
        return getCurrentTrafficSituation(route, null);
    }

    @Override
    public List<IRouteData> getCurrentTrafficSituation(IRoute route, List<String> adapters) {
        List<IRouteData> data = new ArrayList<>();
        try {
            //get last record for the specified routeid by inverting table, limit to 1 result
            Parameter p1 = new Parameter("routeId", route.getId(), Operation.eq);
            Request r = new Request(false, 1);
            r.addParam(0, p1);
            if (adapters != null && !adapters.isEmpty()) {
                Parameter p2 = new Parameter("provider", adapters, Operation.eq);
                r.addParam(1, p2);
            }
            List<IRouteData> routesEntities = r.PrepareQuery(em).getResultList();

            //only continue is there is a result
            if (routesEntities.size() != 1) {
                return data;
            }

            //get all data for that route with specified timestamp and source adaptors
            Parameter p0 = new Parameter("id", routesEntities.get(0).getId() - 1000, Operation.gt);
            Date tbegin = new Date(routesEntities.get(0).getTimestamp().getTime() - 30000);
            Date tend = new Date(routesEntities.get(0).getTimestamp().getTime() + 30000);
            Parameter p2 = new Parameter("timestamp", tbegin, tend, Operation.between);
            r = new Request(true, 0).addParam(0, p0).addParam(1, p1).addParam(2, p2);
            if (adapters != null && adapters.size() != 0) {
                Parameter p3 = new Parameter("provider", adapters, Operation.eq);
                r.addParam(3, p3);
            }
            routesEntities = r.PrepareQuery(em).getResultList();

            //transform all database objects to library objects via copy constructor
            for (IRouteData rdata : routesEntities) {
                data.add(new RouteData(rdata));
            }

        } catch (Exception e) {
            beans.getLogger().log(Level.SEVERE, "Could not retrieve current data for: " + route.getName() + ".");
        } finally {

        }
        return data;
    }

    private void generateBlockList() {
        Query q = em.createQuery("SELECT COUNT(r) FROM RouteDataEntity r");
        Object obj = q.getSingleResult();
        if (obj != null && obj instanceof Long) {
            long size = (long) obj;
            this.blocklist = new BlockList(size, this);
        }
    }

    @Override
    public IRouteData getDataByID(long id) throws NoResultException {
        try {
            Parameter p0 = new Parameter("id", id, Operation.eq);
            Request r = new Request(true, 1).addParam(0, p0);
            Object o = r.PrepareQuery(em).getSingleResult();
            if (o != null && o instanceof IRouteData) {
                return (IRouteData) o;
            }
        } catch (Exception e) {
            beans.getLogger().log(Level.SEVERE, "Could not retrieve data for id: " + id + ".");
        }
        return null;
    }

    @Deprecated
    public void fillDummyData(long i) {
        for (long x = 1; x <= 30; x++) {
            RouteDataEntity e = new RouteDataEntity();
            e.setRouteId(x);
            e.setDistance(1000 + Math.toIntExact(x));
            e.setDuration(600 + Math.toIntExact(x));
            e.setProvider("GoogleMaps");
            e.setTimestamp(new Date(i));
            em.persist(e);

            RouteDataEntity e1 = new RouteDataEntity();
            e1.setRouteId(x);
            e1.setDistance(2000 + Math.toIntExact(x));
            e1.setDuration(800 + Math.toIntExact(x));
            e1.setProvider("Here");
            e1.setTimestamp(new Date(i));
            em.persist(e1);

            RouteDataEntity e2 = new RouteDataEntity();
            e2.setRouteId(x);
            e2.setDistance(3000 + Math.toIntExact(x));
            e2.setDuration(1000 + Math.toIntExact(x));
            e2.setProvider("TomTom");
            e2.setTimestamp(new Date(i));
            em.persist(e2);
        }
    }

    @Override
    public List<IRouteData> getData(IRoute route, List<Date> startList, List<Date> endList, List<String> adapter) {

        List<IRouteData> data = new ArrayList<>();
        try {
            if (route == null) {
                return data;
            }
            if (startList == null || endList == null || startList.size() == 0 || endList.size() == 0 || startList.size() != endList.size()) {
                return data;
            }
            long[] idRange = blocklist.getIdRange(startList.get(0), endList.get(endList.size() - 1));

            if (idRange == null || idRange[0] == -1 || idRange[1] == -1) {
                return data;
            }

            Request r = new Request(true, 0);
            int i = 0;
            r.addParam(i++, new Parameter("id", idRange[0], idRange[1], Operation.between));
            r.addParam(i++, new Parameter("routeId", route.getId(), Operation.eq));
            if (adapter != null && !adapter.isEmpty()) {
                Parameter p = new Parameter("provider", adapter, Operation.eq);
                r.addParam(i++, p);
            }
            r.addParam(i++, new Parameter("timestamp", startList, endList, Operation.between));

            List<RouteDataEntity> routesEntities = r.PrepareQuery(em).getResultList();

            //transform all database objects to library objects via copy constructor
            for (IRouteData rdata : routesEntities) {
                data.add(new RouteData(rdata));
            }
        } catch (Exception e) {
            beans.getLogger().log(Level.SEVERE, "Could not retrieve raw data for: " + route.getName() + " (" + startList.toString() + " - " + endList.toString() + ").");
        }
        return data;
    }

    @Override
    public List<Long> getAggregateData(IRoute route, List<String> adapter, List<Date> startList, List<Date> endList, AggregationContainer... aggr) {
        return getAggregateData(route, adapter, startList, endList, -1, false, aggr);
    }

    @Override
    public List<Long> getAggregateData(IRoute route, List<String> adapter, Date time1, Date time2, AggregationContainer... aggr) {
        return getAggregateData(route, adapter, time1, time2, -1, false, aggr);
    }

    @Override
    public List<Long> getAggregateData(IRoute route, List<String> adapter, Date time1, Date time2, long groupbyTimeFrames, AggregationContainer... aggr) {
        return getAggregateData(route, adapter, time1, time2, groupbyTimeFrames, false, aggr);
    }

    @Override
    public List<Long> getAggregateData(IRoute route, List<String> adapter, List<Date> startList, List<Date> endList, long groupbyTimeFrames, AggregationContainer... aggr) {
        return getAggregateData(route, adapter, startList, endList, groupbyTimeFrames, false, aggr);
    }

    @Override
    public List<Long> getAggregateData(IRoute route, List<String> adapter, Date time1, Date time2, long groupbyTimeFrames, boolean truncateDate, AggregationContainer... aggr) {
        List<Long> data = new ArrayList<>();
        try {
            long[] range = blocklist.getIdRange(time1, time2);
            if (range[0] == -1 || range[1] == -1) {
                throw new NoResultException("Could not retrieve id segment from blocklist");
            }

            int i = 0;
            Request r = new Request(true, 0);
            r.addParam(i++, new Parameter("id", range[0], range[1], Operation.between));
            r.addParam(i++, new Parameter("routeId", route.getId(), Operation.eq));
            if (adapter != null && !adapter.isEmpty()) {
                Parameter p = new Parameter("provider", adapter, Operation.eq);
                r.addParam(i++, p);
            }
            r.addParam(i++, new Parameter("timestamp", time1, time2, Operation.between));
            for (AggregationContainer c : aggr) {
                r.addParam(i++, new Parameter(c.aggregation, c.attr));
            }

            r.setGroupBy(groupbyTimeFrames);
            r.setDataless(truncateDate);

            List<Object> objList = new ArrayList<>();
            if (groupbyTimeFrames == -1) {
                Object obj = r.PrepareQuery(em).getSingleResult();
                objList.add(obj);
            } else {
                objList = r.PrepareQuery(em).getResultList();
            }
            for (Object obj : objList) {
                if (obj instanceof Object[]) {
                    Object[] arr = (Object[]) obj;
                    for (Object l : arr) {
                        if (l instanceof Long) {
                            data.add((Long) l);
                        } else if (l instanceof Date) {
                            data.add(((Date) l).getTime());
                        }
                    }
                }
            }

        } catch (Exception e) {
            beans.getLogger().log(Level.SEVERE, "Could not retrieve agregated data for: " + route.getName() + " (" + time1 + " - " + time2 + ").");
        } finally {

        }
        return data;
    }

    @Override
    public List<Long> getAggregateData(IRoute route, List<String> adapter, List<Date> startList, List<Date> endList, long groupbyTimeFrames, boolean truncateDate, AggregationContainer... aggr) {
        List<Long> data = new ArrayList<>();
        try {
            if (route == null) {
                return data;
            }
            if (startList == null || endList == null || startList.size() == 0 || endList.size() == 0 || startList.size() != endList.size()) {
                return data;
            }
            long[] idRange = blocklist.getIdRange(startList.get(0), endList.get(endList.size() - 1));

            if (idRange == null || idRange[0] == -1 || idRange[1] == -1) {
                return data;
            }

            Request r = new Request(true, 0);
            int i = 0;

            if (idRange[0] == -1 || idRange[1] == -1) {
                throw new NoResultException("Could not retrieve id segment from blocklist");
            }

            r.addParam(i++, new Parameter("id", idRange[0], idRange[1], Operation.between));
            r.addParam(i++, new Parameter("routeId", route.getId(), Operation.eq));
            if (adapter != null && !adapter.isEmpty()) {
                Parameter p = new Parameter("provider", adapter, Operation.eq);
                r.addParam(i++, p);
            }
            r.addParam(i++, new Parameter("timestamp", startList, endList, Operation.between));
            for (AggregationContainer c : aggr) {
                r.addParam(i++, new Parameter(c.aggregation, c.attr));
            }

            r.setGroupBy(groupbyTimeFrames);
            r.setDataless(truncateDate);

            List<Object> objList = new ArrayList<>();
            if (groupbyTimeFrames == -1) {
                Object obj = r.PrepareQuery(em).getSingleResult();
                objList.add(obj);
            } else {
                objList = r.PrepareQuery(em).getResultList();
            }
            for (Object obj : objList) {
                if (obj instanceof Object[]) {
                    Object[] arr = (Object[]) obj;
                    for (Object l : arr) {
                        if (l instanceof Long) {
                            data.add((Long) l);
                        } else if (l instanceof Date) {
                            data.add(((Date) l).getTime());
                        }
                    }
                }
            }

        } catch (Exception e) {
            beans.getLogger().log(Level.SEVERE, "Could not retrieve agregated data for: " + route.getName() + " (" + startList.toString() + " - " + endList.toString() + ").");
        } finally {

        }
        return data;
    }

    @Override
    public List<IRouteData> getRawData(IRoute route, Date time1, Date time2, List<String> adapter, int page) {
        List<IRouteData> data = new ArrayList<>();
        try {
            long[] range = blocklist.getIdRange(time1, time2);
            if (range[0] == -1 || range[1] == -1) {
                throw new NoResultException("Could not retrieve id segment from blocklist");
            }

            int i = 0;
            Request r = new Request(true, 1000);
            r.addParam(i++, new Parameter("id", range[0], range[1], Operation.between));
            r.addParam(i++, new Parameter("routeId", route.getId(), Operation.eq));
            if (adapter != null && !adapter.isEmpty()) {
                Parameter p = new Parameter("provider", adapter, Operation.eq);
                r.addParam(i++, p);
            }
            r.addParam(i++, new Parameter("timestamp", time1, time2, Operation.between));

            r.setSkipResults(page);

            List<IRouteData> routesEntities = r.PrepareQuery(em).getResultList();
            for (IRouteData rdata : routesEntities) {
                data.add(new RouteData(rdata));
            }
        } catch (Exception e) {
            beans.getLogger().log(Level.SEVERE, "Could not retrieve raw data for: " + route.getName() + " (" + time1 + " - " + time2 + ").");
        } finally {

        }
        return data;
    }

    @Override
    public void updateBlockList() {
        Query q = em.createQuery("SELECT COUNT(r) FROM RouteDataEntity r");
        Object obj = q.getSingleResult();
        if (obj != null && obj instanceof Long && this.blocklist != null) {
            long size = (long) obj;
            this.blocklist.update(size, this);
        }
    }
}
