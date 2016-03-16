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
    private IRoute route;
    private List<Observer> observers;

    public Threshold(){
    
    }
    
    public Threshold(IRoute route, int level, int delayTriggerLevel) {
        this.level = level;
        this.delayTriggerLevel = delayTriggerLevel;
    }
    
    public Threshold(IThreshold tr) {
        this.level = level;
        this.delayTriggerLevel = delayTriggerLevel;
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
    public IRoute getRoute() {
        return route;
    }

    @Override
    public void setRoute(IRoute route) {
        this.route = route;
    }

    public List<Observer> getObservers() {
        return observers;
    }

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
    public boolean isThresholdReached(IRoute route, IRouteData data) {
        return true;
    }
    
    
}
