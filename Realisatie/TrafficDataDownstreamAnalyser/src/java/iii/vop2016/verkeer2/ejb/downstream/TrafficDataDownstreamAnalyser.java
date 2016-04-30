/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package iii.vop2016.verkeer2.ejb.downstream;

import iii.vop2016.verkeer2.ejb.components.IGeoLocation;
import iii.vop2016.verkeer2.ejb.components.IRoute;
import iii.vop2016.verkeer2.ejb.components.IRouteData;
import iii.vop2016.verkeer2.ejb.dataprovider.IDataProvider;
import iii.vop2016.verkeer2.ejb.helper.BeanFactory;
import iii.vop2016.verkeer2.ejb.logger.ILogger;
import iii.vop2016.verkeer2.ejb.logger.LoggerRemote;
import iii.vop2016.verkeer2.ejb.threshold.IThresholdManager;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import javax.ejb.SessionContext;
import javax.ejb.Singleton;
import javax.naming.InitialContext;
import javax.naming.NamingException;

/**
 *
 * @author tobia
 */
@Singleton
public class TrafficDataDownstreamAnalyser implements TrafficDataDownstreamAnalyserRemote,TrafficDataDownstreamAnalyserLocal {

    @Resource
    private SessionContext sctx;
    private InitialContext ctx;

    private BeanFactory beans;

    @PostConstruct
    private void init() {
        try {
            ctx = new InitialContext();
        } catch (NamingException ex) {
            Logger.getLogger(TrafficDataDownstreamAnalyser.class.getName()).log(Level.SEVERE, null, ex);
        }
        beans = BeanFactory.getInstance(ctx, sctx);

        beans.getLogger().log(Level.INFO, "TrafficDataDownstreamAnalyzer has been initialized.");
    }

    @Override
    public String getProjectName() {
        return "Verkeer-2";
    }

    @Override
    public IRouteData addData(IRouteData data) {
        return beans.getTrafficDataDAO().addData(data);
    }

    @Override
    public List<IRouteData> addData(List<IRouteData> data) {
        return beans.getTrafficDataDAO().addData(data);
    }

    @Override
    public void startSession() {
        ILogger logger = beans.getLogger();
        logger.log(Level.FINER, "Starting data scrub session.");
    }

    @Override
    public void endSession(List<IRouteData> data, List<IRoute> routes) {
        ILogger logger = beans.getLogger();
        logger.log(Level.FINER, "Ending analysis session.");
        IDataProvider dataProvider = beans.getDataProvider();
        IThresholdManager threshold = beans.getThresholdManager();

        Map<Long, List<IRouteData>> mapping = MapDataToRoutes(data);

        for (IRoute route : routes) {
            List<IRouteData> routeData = mapping.get(route.getId());
            if (routeData != null) {
                int opt = dataProvider.getOptimalDuration(route, null);
                int mean = dataProvider.getMeanDurationFromRouteData(routeData);
                if (opt != -1 && mean != -1) {
                    int delay = mean - opt;
                    threshold.EvalThresholdLevel(route, delay);
                }
            }
        }

        dataProvider.invalidateCurrentData();
        logger.log(Level.FINER, "Ended analysis session.");
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
}
