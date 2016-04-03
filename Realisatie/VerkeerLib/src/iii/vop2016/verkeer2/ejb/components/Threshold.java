/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package iii.vop2016.verkeer2.ejb.components;

import java.util.List;
import java.util.Observable;
import java.util.Observer;

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
    private List<Observer> observers;

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
    public List<Observer> getObservers() {
        return observers;
    }

    @Override
    public void setObservers(List<Observer> observers) {
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
    public void triggerThreshold(int difference) {
        
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
