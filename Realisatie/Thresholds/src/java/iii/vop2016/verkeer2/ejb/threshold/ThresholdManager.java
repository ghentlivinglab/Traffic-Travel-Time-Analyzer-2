/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package iii.vop2016.verkeer2.ejb.threshold;

import iii.vop2016.verkeer2.ejb.components.IRoute;
import iii.vop2016.verkeer2.ejb.components.IThreshold;
import iii.vop2016.verkeer2.ejb.components.Threshold;
import iii.vop2016.verkeer2.ejb.dao.IGeneralDAO;
import iii.vop2016.verkeer2.ejb.helper.BeanFactory;
import iii.vop2016.verkeer2.ejb.helper.HelperFunctions;
import iii.vop2016.verkeer2.ejb.logger.ILogger;
import iii.vop2016.verkeer2.ejb.logger.LoggerRemote;
import iii.vop2016.verkeer2.ejb.properties.IProperties;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.function.Predicate;
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
 * @author Tobias
 */
@Singleton
@Lock(LockType.WRITE)
@AccessTimeout(value = 120000)
public class ThresholdManager implements ThresholdManagerRemote, ThresholdManagerLocal {

    @Resource
    protected SessionContext ctxs;
    protected InitialContext ctx;
    protected BeanFactory beans;
    protected Map<IRoute, List<IThreshold>> thresholdMap;
    protected Map<IRoute, Integer> prevThresholdLevel;
    protected static final String JNDILOOKUP_PROPERTYFILE = "resources/properties/ThresholdManager";

    @PostConstruct
    private void init() {
        //Initialize bean and its context
        try {
            ctx = new InitialContext();
        } catch (NamingException ex) {
            Logger.getLogger(ThresholdManager.class.getName()).log(Level.SEVERE, null, ex);
        }
        beans = BeanFactory.getInstance(ctx, ctxs);

        IProperties propCol = beans.getPropertiesCollection();
        if (propCol != null) {
            propCol.registerProperty(JNDILOOKUP_PROPERTYFILE);
        }

        thresholdMap = beans.getGeneralDAO().getThresholds();
        if (thresholdMap == null) {
            thresholdMap = new HashMap<>();
        }

        prevThresholdLevel = new HashMap<>();
        for (IRoute route : thresholdMap.keySet()) {
            prevThresholdLevel.put(route, -1);
        }

        beans.getLogger().log(Level.INFO, "ThresholManager has been initialized.");
    }

    private Properties getProperties() {
        return HelperFunctions.RetrievePropertyFile(JNDILOOKUP_PROPERTYFILE, ctx, Logger.getGlobal());
    }

    @Override
    public int getThresholdLevel(IRoute route, int delay) {
        List<IThreshold> thresholds = thresholdMap.get(route);

        if (thresholds == null) {
            //this can ony happen when a new route was added without alerting the thresholdmanager!
            thresholdMap = beans.getGeneralDAO().getThresholds();
            thresholds = thresholdMap.get(route);
            if (thresholds == null) {
                //if still null, then the thresholds have not been added to the database. an exception should have occured before getting here...
                addDefaultThresholds(route);
                thresholds = thresholdMap.get(route);
            }
        }

        List<IThreshold> passed = new ArrayList<>();
        List<IThreshold> notPassed = new ArrayList<>();
        for (IThreshold threshold : thresholds) {
            if (threshold.isThresholdReached(route, delay)) {
                passed.add(threshold);
            } else {
                notPassed.add(threshold);
            }
        }

        int highestPassed = 0;
        for (IThreshold threshold : passed) {
            if (threshold.getLevel() > highestPassed) {
                highestPassed = threshold.getLevel();
            }
        }

        return highestPassed;
    }

    //this function return the difference in level sinds the last check
    @Override
    public int EvalThresholdLevel(IRoute route, int delay) {
        ILogger logger = beans.getLogger();

        int currentLevel = getThresholdLevel(route, delay);
        int prevLevel = prevThresholdLevel.get(route);
        if (prevLevel != currentLevel) {
            prevThresholdLevel.put(route, currentLevel);
            logger.log(Level.FINER, "Threshold changed for " + route.getName() + " to " + currentLevel);

            List<IThreshold> thresholdList = getThresholds(route, prevLevel, currentLevel);
            for (IThreshold threshold : thresholdList) {
                threshold.triggerThreshold(prevLevel - threshold.getLevel(), delay, beans);
            }

            return currentLevel - prevLevel;
        }
        return 0;
    }

    private List<IThreshold> getThresholds(IRoute route, int prevLevel, int currentLevel) {
        List<IThreshold> ret = new ArrayList<>();
        List<IThreshold> thresholds = thresholdMap.get(route);
        if (thresholds == null) {
            return ret;
        }

        int low = Math.min(prevLevel, currentLevel);
        int high = Math.max(prevLevel, currentLevel);

        for (IThreshold threshold : thresholds) {
            if (threshold.getLevel() > low && threshold.getLevel() <= high) {
                ret.add(threshold);
            }
        }
        return ret;
    }

    @Override
    public void addDefaultThresholds(IRoute route) {
        IGeneralDAO dao = beans.getGeneralDAO();
        Properties prop = getProperties();
        ILogger logger = beans.getLogger();

        String defaults = prop.getProperty("defaultThresholdLevels", "120,240,480,1200");
        String[] arr = defaults.split(",");
        int i = 1;
        for (String def : arr) {
            try {
                IThreshold th = new Threshold(route, i++, Integer.parseInt(def));
                th = dao.addThreshold(th);
                if (thresholdMap.containsKey(th.getRoute())) {
                    thresholdMap.get(th.getRoute()).add(th);
                } else {
                    ArrayList<IThreshold> list = new ArrayList<IThreshold>();
                    list.add(th);
                    thresholdMap.put(th.getRoute(), list);
                    prevThresholdLevel.put(th.getRoute(), -1);
                }
            } catch (NumberFormatException e) {
                logger.log(Level.SEVERE, "Invalid config line for defaultThresholdLevels (" + def + ")");
            }
        }
        logger.log(Level.FINE, "Added default Threshold for " + route.getName());
    }

    @Override
    public List<IThreshold> getThresholds(IRoute r) {
        if (thresholdMap.containsKey(r)) {
            return thresholdMap.get(r);
        } else {
            return new ArrayList<>();
        }
    }

    @Override
    public boolean ModifyThresholds(List<IThreshold> list) {
        IGeneralDAO dao = beans.getGeneralDAO();
        boolean success = true;
        for (IThreshold th : list) {
            if (th.getRouteId() > 0) {
                if (th.getId() == 0) {
                    dao.addThreshold(th);
                } else {
                    dao.updateThreshold(th);
                }
                if (!InsertIntoMap(th)) {
                    success = false;
                }
            }
        }
        return success;
    }

    private boolean InsertIntoMap(final IThreshold th) {
        boolean found = false;
        for (Map.Entry<IRoute, List<IThreshold>> entry : thresholdMap.entrySet()) {
            if (entry.getKey().getId() == th.getRouteId()) {
                found = true;
                List<IThreshold> entryList = entry.getValue();
                entryList.removeIf(new Predicate<IThreshold>() {
                    @Override
                    public boolean test(IThreshold t) {
                        if (t.getId() == th.getId()) {
                            return true;
                        }
                        return false;
                    }
                });
                entryList.add(th);
                return true;
            }
        }
        if (!found) {
            IRoute route = beans.getGeneralDAO().getRoute(th.getRouteId());
            if (route != null) {
                ArrayList<IThreshold> arr = new ArrayList<>();
                arr.add(th);
                thresholdMap.put(route, arr);
                return true;
            } else {
                return false;
            }
        }
        return false;
    }
}
