/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package iii.vop2016.verkeer2.ejb.components;

import java.util.List;
import java.util.Observer;

/**
 *
 * @author tobia
 */
public interface IThreshold{

    boolean isThresholdReached(IRoute route, int delay);
    
    long getId();
    
    void setId(long id);
    
    int getLevel();

    void setLevel(int level);

    int getDelayTriggerLevel();

    void setDelayTriggerLevel(int delayTriggerLevel);

    long getRouteId();

    void setRouteId(long routeId);
    
    List<Observer> getObservers();
    
    void setObservers(List<Observer> observers);
    
    void triggerThreshold(int difference);
    
    IRoute getRoute();
    
    void setRoute(IRoute route);
   
}
