/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package iii.vop2016.verkeer2.ejb.components;

import java.util.Date;

/**
 *
 * @author Mike
 */
public class RouteData implements IRouteData {

    protected long id;
    protected long routeId;
    protected int duration;
    protected int distance;
    protected Date timestamp;
    protected String provider;

    public RouteData(){
    
    }
    
    public RouteData(IRouteData component) {
        this.distance = component.getDistance();
        this.duration = component.getDuration();
        this.id = component.getId();
        this.routeId = component.getRouteId();
        this.timestamp = component.getTimestamp();
        this.provider = component.getProvider();
    }
    
    @Override
    public long getId() {
        return this.id;
    }

    @Override
    public void setId(long id) {
        this.id = id;
    }

    @Override
    public int getDuration() {
        return duration;
    }

    @Override
    public int getDistance() {
        return distance;
    }

    @Override
    public Date getTimestamp() {
        return timestamp;
    }

    @Override
    public void setDuration(int duration) {
        this.duration = duration;
    }

    @Override
    public void setDistance(int distance) {
        this.distance = distance;
    }

    @Override
    public void setTimestamp(Date timestamp) {
        this.timestamp = timestamp;
    }

    @Override
    public String toString(){
        return "RouteData ["+routeId+"] ["+provider+"] (dis:"+distance+", dur:"+duration+")";
    }

    @Override
    public String getProvider() {
        return provider;
    }

    @Override
    public void setProvider(String provider) {
        this.provider = provider;
    }

    @Override
    public long getRouteId() {
        return routeId;
    }

    @Override
    public void setRouteId(long id) {
        this.routeId = id;
    }

    
    
}
