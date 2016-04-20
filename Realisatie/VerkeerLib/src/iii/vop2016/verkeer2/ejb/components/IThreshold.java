/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package iii.vop2016.verkeer2.ejb.components;

import iii.vop2016.verkeer2.ejb.helper.BeanFactory;
import java.io.Serializable;
import java.util.List;
import java.util.Observer;
import javax.naming.InitialContext;

/**
 *
 * @author tobia
 */
public interface IThreshold extends Serializable{

    boolean isThresholdReached(IRoute route, int delay);
    
    long getId();
    
    void setId(long id);
    
    int getLevel();

    void setLevel(int level);

    int getDelayTriggerLevel();

    void setDelayTriggerLevel(int delayTriggerLevel);

    long getRouteId();

    void setRouteId(long routeId);
    
    List<String> getObservers();
    
    void setObservers(List<String> observers);
    
    void triggerThreshold(int difference,int delay,  BeanFactory fac);
    
    IRoute getRoute();
    
    void setRoute(IRoute route);
   
}
