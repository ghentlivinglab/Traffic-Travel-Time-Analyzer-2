/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package iii.vop2016.verkeer2.ejb.components;

import iii.vop2016.verkeer2.ejb.helper.BeanFactory;
import iii.vop2016.verkeer2.ejb.helper.HelperFunctions;
import iii.vop2016.verkeer2.ejb.threshold.IThresholdHandler;
import java.util.List;
import java.util.Observable;
import java.util.Observer;
import java.util.Properties;
import java.util.logging.Logger;
import javax.naming.InitialContext;

/**
 *
 * @author tobia
 */
public class Threshold extends Observable implements IThreshold{
    private long id;
    private int level;
    private int delayTriggerLevel;
    private long routeId;
    private IRoute route;
    private List<String> observers;

    public Threshold(){
    
    }
    
    public Threshold(IRoute route, int level, int delayTriggerLevel) {
        this.routeId = route.getId();
        this.level = level;
        this.delayTriggerLevel = delayTriggerLevel;
    }
    
    public Threshold(IThreshold tr) {
        this.delayTriggerLevel = tr.getDelayTriggerLevel();
        this.id = tr.getId();
        this.level = tr.getLevel();
        this.observers = tr.getObservers();
        this.routeId = tr.getRouteId();
        this.route = tr.getRoute();
    }
    
    @Override
    public int getLevel() {
        return level;
    }

    @Override
    public void setLevel(int level) {
        this.level = level;
    }

    @Override
    public int getDelayTriggerLevel() {
        return delayTriggerLevel;
    }

    @Override
    public void setDelayTriggerLevel(int delayTriggerLevel) {
        this.delayTriggerLevel = delayTriggerLevel;
    }

    @Override
    public long getRouteId() {
        return routeId;
    }

    @Override
    public void setRouteId(long routeId) {
        this.routeId = routeId;
    }

    @Override
    public List<String> getObservers() {
        return observers;
    }

    @Override
    public void setObservers(List<String> observers) {
        this.observers = observers;
    }

    @Override
    public long getId() {
        return id;
    }

    @Override
    public void setId(long id) {
        this.id = id;
    }

    @Override
    public boolean isThresholdReached(IRoute route, int delay) {
        if(delay < delayTriggerLevel)
            return false;
        return true;
    }

    @Override
    public void triggerThreshold(int prevLevel,int delay, BeanFactory fac) {
        List<IThresholdHandler> beans = fac.getThresholdHandlers(observers);
        
        if(this.route == null && this.routeId != 0){
            this.route = fac.getGeneralDAO().getRoute(this.routeId);
        }
        
        for(IThresholdHandler handler:beans){
            handler.notify(this.route, this.routeId,this.level,this.delayTriggerLevel,prevLevel,delay);
        }
    }

    @Override
    public IRoute getRoute() {
        return route;
    }

    @Override
    public void setRoute(IRoute route) {
        this.route = route;
    }
    
    
}
