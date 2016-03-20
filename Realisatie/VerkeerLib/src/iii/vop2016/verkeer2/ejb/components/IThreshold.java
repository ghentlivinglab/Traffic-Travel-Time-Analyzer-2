/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package iii.vop2016.verkeer2.ejb.components;

/**
 *
 * @author tobia
 */
public interface IThreshold{

    boolean isThresholdReached(IRoute route, IRouteData data);
    
    long getId();
    
    void setId(long id);
    
    int getLevel();

    void setLevel(int level);

    int getDelayTriggerLevel();

    void setDelayTriggerLevel(int delayTriggerLevel);

    IRoute getRoute();

    void setRoute(IRoute route);
   
}
