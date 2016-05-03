/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package iii.vop2016.verkeer2.ejb.downstream;

import iii.vop2016.verkeer2.ejb.components.IGeoLocation;
import iii.vop2016.verkeer2.ejb.components.IRoute;
import iii.vop2016.verkeer2.ejb.components.IRouteData;
import iii.vop2016.verkeer2.ejb.dao.ITrafficDataDAO;
import iii.vop2016.verkeer2.ejb.dataprovider.IDataProvider;
import iii.vop2016.verkeer2.ejb.helper.BeanFactory;
import iii.vop2016.verkeer2.ejb.helper.HelperFunctions;
import iii.vop2016.verkeer2.ejb.logger.ILogger;
import iii.vop2016.verkeer2.ejb.logger.LoggerRemote;
import iii.vop2016.verkeer2.ejb.properties.IProperties;
import iii.vop2016.verkeer2.ejb.threshold.IThresholdManager;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import javax.ejb.AccessTimeout;
import javax.ejb.Lock;
import javax.ejb.LockType;
import javax.ejb.SessionContext;
import javax.ejb.Singleton;
import javax.naming.InitialContext;
import javax.naming.NamingException;

/**
 *
 * @author tobia
 */
@Singleton
@Lock(LockType.WRITE)
@AccessTimeout(value = 60000)
public class TrafficDataDownstreamAnalyser implements TrafficDataDownstreamAnalyserRemote, TrafficDataDownstreamAnalyserLocal {

    @Resource
    private SessionContext sctx;
    private InitialContext ctx;

    private BeanFactory beans;
    protected static final String JNDILOOKUP_PROPERTYFILE = "resources/properties/TDDAnalyser";

    @PostConstruct
    private void init() {
        try {
            ctx = new InitialContext();
        } catch (NamingException ex) {
            Logger.getLogger(TrafficDataDownstreamAnalyser.class.getName()).log(Level.SEVERE, null, ex);
        }
        beans = BeanFactory.getInstance(ctx, sctx);

        IProperties propCol = beans.getPropertiesCollection();
        if (propCol != null) {
            propCol.registerProperty(JNDILOOKUP_PROPERTYFILE);
        }

        beans.getLogger().log(Level.INFO, "TrafficDataDownstreamAnalyzer has been initialized.");
    }

    private Properties getProperties() {
        return HelperFunctions.RetrievePropertyFile(JNDILOOKUP_PROPERTYFILE, ctx, Logger.getGlobal());
    }

    @Override
    public String getProjectName() {
        return "Verkeer-2";
    }

    @Override
    public IRouteData addData(IRouteData data, List<IRoute> routes) {
        Properties prop = getProperties();
        String isDistanceFilterEnabledStr = prop.getProperty("isDistanceFilterEnabled", "false");
        boolean isDistanceFilterEnabled = Boolean.parseBoolean(isDistanceFilterEnabledStr);

        if (!isDistanceFilterEnabled) {
            return beans.getTrafficDataDAO().addData(data);
        } else {
            IRoute route = getRouteForData(data.getRouteId(), routes);
            if (route == null) {
                rejectData(data, route,data.getDistance(),0);
                return null;
            }
            double distance = beans.getDataProvider().getDistance(route, new ArrayList<String>());
            //no prev value present: allow
            if (distance == -1) {
                return beans.getTrafficDataDAO().addData(data);
            }

            double diff = getDistanceDifference(prop);
            if (diff == -1) {
                rejectData(data, route,data.getDistance(),0);
                return null;
            }
            distance *= diff;

            //reject if bigger
            if (data.getDistance() > distance) {
                rejectData(data, route,data.getDistance(),distance);
                return null;
            }

            return beans.getTrafficDataDAO().addData(data);

        }
    }

    @Override
    public List<IRouteData> addData(List<IRouteData> data, List<IRoute> routes) {
        Properties prop = getProperties();
        String isDistanceFilterEnabledStr = prop.getProperty("isDistanceFilterEnabled", "false");
        boolean isDistanceFilterEnabled = Boolean.parseBoolean(isDistanceFilterEnabledStr);

        IDataProvider dataProvider = beans.getDataProvider();
        ITrafficDataDAO dao = beans.getTrafficDataDAO();

        if (dao == null || dataProvider == null) {
            for (IRouteData d : data) {
                rejectData(d, null,d.getDistance(),0);
            }
            return null;
        }

        if (!isDistanceFilterEnabled) {
            return dao.addData(data);
        } else {
            //map data to routes
            Map<IRoute, List<IRouteData>> list = MapDataToRoutes(data, routes);
            List<IRouteData> reject = new ArrayList<>();

            //get allowed difference
            double diff = getDistanceDifference(prop);
            if (diff == -1) {
                for (IRouteData d : data) {
                    rejectData(d, null,d.getDistance(),0);
                }
                return null;
            }

            for (Map.Entry<IRoute, List<IRouteData>> entry : list.entrySet()) {
                int distance = dataProvider.getDistance(entry.getKey(), new ArrayList<String>());
                if (distance == -1) {
                    //allow because there is no prev value
                } else {
                    distance *= diff;
                    for (IRouteData rData : entry.getValue()) {
                        if (rData.getDistance() > distance) {
                            rejectData(rData, entry.getKey(),rData.getDistance(),distance);
                            reject.add(rData);
                        }
                    }
                }
            }
            
            data.removeAll(reject);
            
            return dao.addData(data);
            
        }
    }

    @Override
    public void startSession() {
        ILogger logger = beans.getLogger();
        logger.log(Level.FINER, "Starting data scrub session.");
    }

    @Override
    public void endSession(List<IRouteData> data, List<IRoute> routes) {
        ILogger logger = beans.getLogger();
        logger.log(Level.FINER, "Ending data scrub session.");

        IDataProvider dataProvider = beans.getDataProvider();
        IThresholdManager threshold = beans.getThresholdManager();

        Map<Long, List<IRouteData>> mapping = MapDataToRoutes(data);

        for (IRoute route : routes) {
            List<IRouteData> routeData = mapping.get(route.getId());
            if (routeData != null && !routeData.isEmpty()) {
                int opt = dataProvider.getOptimalDuration(route, new ArrayList<String>());
                int mean = dataProvider.getMeanDurationFromRouteData(routeData);
                if (opt != -1 && mean != -1) {
                    int delay = mean - opt;
                    threshold.EvalThresholdLevel(route, delay);
                }
            }
        }

        dataProvider.invalidateCurrentData();
        logger.log(Level.FINER, "Ended data scrub session.");
    }

    private Map<Long, List<IRouteData>> MapDataToRoutes(List<IRouteData> routeData) {
        Map<Long, List<IRouteData>> ret = new HashMap<>();
        for (IRouteData r : routeData) {
            List<IRouteData> data = ret.get(r.getRouteId());
            if (data == null) {
                data = new ArrayList<>();
                data.add(r);
                ret.put(r.getRouteId(), data);
            } else {
                data.add(r);
            }
        }

        return ret;
    }

    private Map<IRoute, List<IRouteData>> MapDataToRoutes(List<IRouteData> routeData, List<IRoute> routes) {
        Map<IRoute, List<IRouteData>> ret = new HashMap<>();
        Map<Long, List<IRouteData>> list = MapDataToRoutes(routeData);
        for (Map.Entry<Long, List<IRouteData>> entry : list.entrySet()) {
            IRoute r = getRouteForData(entry.getKey(), routes);
            if (r != null) {
                ret.put(r, entry.getValue());
            }
        }
        return ret;
    }

    private IRoute getRouteForData(long id, List<IRoute> routes) {
        for (IRoute route : routes) {
            if (route.getId() == id) {
                return route;
            }
        }
        return null;
    }

    private void rejectData(IRouteData data, IRoute route, int distance, double maxDistance) {
        if(route == null || maxDistance == 0){
            beans.getLogger().log(Level.WARNING, data + " rejected. Unknown reason.");
        }else{
            beans.getLogger().log(Level.WARNING, data + " rejected. Distance was " + distance + " while max should be " + maxDistance);
        }
    }

    private double getDistanceDifference(Properties prop) {
        String distanceDifStr = prop.getProperty("distanceDif", "");
        if (distanceDifStr.equals("")) {
            return -1;
        }
        double distanceDif;
        try {
            distanceDif = Double.parseDouble(distanceDifStr);
        } catch (NumberFormatException e) {
            return -1;
        }
        return distanceDif;
    }
}
