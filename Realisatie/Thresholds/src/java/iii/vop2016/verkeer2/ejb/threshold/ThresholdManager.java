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
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
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
 * @author Tobias
 */
@Singleton
public class ThresholdManager implements ThresholdManagerRemote {

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

        thresholdMap = beans.getGeneralDAO().getThresholds();
        if (thresholdMap == null) {
            thresholdMap = new HashMap<>();
        }
        
        prevThresholdLevel = new HashMap<>();
        for (IRoute route : thresholdMap.keySet()) {
            prevThresholdLevel.put(route, -1);
        }
    }

    private Properties getProperties() {
        return HelperFunctions.RetrievePropertyFile(JNDILOOKUP_PROPERTYFILE, ctx, Logger.getGlobal());
    }

    @Override
    public int getThresholdLevel(IRoute route, int delay) {
        List<IThreshold> thresholds = thresholdMap.get(route);
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
        int currentLevel = getThresholdLevel(route, delay);
        int prevLevel = prevThresholdLevel.get(route);
        if (prevLevel != currentLevel) {
            prevThresholdLevel.put(route, currentLevel);

            List<IThreshold> thresholdList = getThresholds(route, prevLevel, currentLevel);
            for (IThreshold threshold : thresholdList) {
                threshold.triggerThreshold(prevLevel - threshold.getLevel());
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
                System.err.println("Invalid config line for defaultThresholdLevels (" + def + ")");
            }
        }
    }
}
