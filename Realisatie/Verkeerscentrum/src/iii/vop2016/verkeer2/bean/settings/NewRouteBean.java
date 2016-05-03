/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package iii.vop2016.verkeer2.bean.settings;

import iii.vop2016.verkeer2.bean.auth.Login;
import iii.vop2016.verkeer2.bean.helpers.JSONMethods;
import iii.vop2016.verkeer2.ejb.components.GeoLocation;
import iii.vop2016.verkeer2.ejb.components.IGeoLocation;
import iii.vop2016.verkeer2.ejb.components.IRoute;
import iii.vop2016.verkeer2.ejb.components.IThreshold;
import iii.vop2016.verkeer2.ejb.components.Route;
import iii.vop2016.verkeer2.ejb.components.Threshold;
import iii.vop2016.verkeer2.ejb.dao.IGeneralDAO;
import iii.vop2016.verkeer2.ejb.dao.ILoginDAO;
import iii.vop2016.verkeer2.ejb.helper.BeanFactory;
import iii.vop2016.verkeer2.ejb.threshold.IThresholdManager;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.inject.Named;
import javax.enterprise.context.Dependent;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.RequestScoped;
import javax.faces.bean.SessionScoped;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import org.json.JSONArray;
import org.json.JSONObject;

/**
 *
 * @author Mike
 */
@ManagedBean(name = "newRouteBean", eager = true)
@RequestScoped
public class NewRouteBean extends RouteSettings {

    protected String name;
    protected String startLocationName;
    protected String endLocationName;
    protected String startLocationGeo;
    protected String endLocationGeo;
    protected List<IThreshold> thresholds;
    protected List<String> thresholdObservers;
    
    private InitialContext ctx;
    private BeanFactory beanFactory;


    public NewRouteBean() {
        try {
            ctx = new InitialContext();
        } catch (NamingException ex) {
            Logger.getLogger(Login.class.getName()).log(Level.SEVERE, null, ex);
        }
        beanFactory = BeanFactory.getInstance(ctx, null);
        thresholds = new ArrayList<>();
        
        IThreshold threshold = new Threshold();
        threshold.setLevel(1);
        threshold.setDelayTriggerLevel(0);
        thresholds.add(threshold);
        threshold = new Threshold();
        threshold.setLevel(2);
        threshold.setDelayTriggerLevel(60);
        thresholds.add(threshold);
        threshold = new Threshold();
        threshold.setLevel(3);
        threshold.setDelayTriggerLevel(120);
        thresholds.add(threshold);
        threshold = new Threshold();
        threshold.setLevel(4);
        threshold.setDelayTriggerLevel(560);
        thresholds.add(threshold);
        threshold = new Threshold();
        threshold.setLevel(5);
        threshold.setDelayTriggerLevel(1052);
        thresholds.add(threshold);
        
        thresholdObservers = new ArrayList<>();
        thresholdObservers.add("");
        thresholdObservers.add("");
        thresholdObservers.add("");
        thresholdObservers.add("");
        thresholdObservers.add("");    
    }
   
    public String submit(){
        IGeneralDAO generalDAO = beanFactory.getGeneralDAO();
        //IThresholdManager thresholdManager = beanFactory.get();
        //doe een rest-call om route op te slaan
        IRoute route = new Route();
        route.setName(name);
        
        String[] partsGeoStart = startLocationGeo.split(",");
        String[] partsGeoEnd = endLocationGeo.split(",");
        
        IGeoLocation locationStart = new GeoLocation();
        locationStart.setLatitude(Double.parseDouble(partsGeoStart[0]));
        locationStart.setLongitude(Double.parseDouble(partsGeoStart[1]));
        locationStart.setName(startLocationName);
        
        IGeoLocation locationEnd = new GeoLocation();
        locationEnd.setLatitude(Double.parseDouble(partsGeoEnd[0]));
        locationEnd.setLongitude(Double.parseDouble(partsGeoEnd[1]));
        locationEnd.setName(endLocationName);
        
        route.addGeolocation(locationStart);
        route.addGeolocation(locationEnd);
     
        route = generalDAO.addRoute(route);
        
        /*
        for(int i=0; i<thresholds.size(); i++){
            List<String> observers = Arrays.asList(thresholdObservers.get(i).split(","));
            thresholds.get(i).setObservers(observers);
            thresholds.get(i).setRoute(route);
            generalDAO.addThreshold(thresholds.get(i));
        }
        */
        
        return "pretty:settings-routes";
    }
    

    public String getName() {
        return name;
    }

    public String getStartLocationName() {
        return startLocationName;
    }

    public String getEndLocationName() {
        return endLocationName;
    }

    public String getStartLocationGeo() {
        return startLocationGeo;
    }

    public String getEndLocationGeo() {
        return endLocationGeo;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setStartLocationName(String startLocationName) {
        this.startLocationName = startLocationName;
    }

    public void setEndLocationName(String endLocationName) {
        this.endLocationName = endLocationName;
    }

    public void setStartLocationGeo(String startLocationGeo) {
        this.startLocationGeo = startLocationGeo;
    }

    public void setEndLocationGeo(String endLocationGeo) {
        this.endLocationGeo = endLocationGeo;
    }

    public List<IThreshold> getThresholds() {
        return thresholds;
    }

    public void setThresholds(List<IThreshold> thresholds) {
        this.thresholds = thresholds;
    }

    public List<String> getThresholdObservers() {
        return thresholdObservers;
    }

    public void setThresholdObservers(List<String> thresholdObservers) {
        this.thresholdObservers = thresholdObservers;
    }

    
    
    
    
}
