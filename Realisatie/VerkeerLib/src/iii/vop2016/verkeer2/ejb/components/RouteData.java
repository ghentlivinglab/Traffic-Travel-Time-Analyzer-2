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

    private long id;
    private IRoute route;
    private int duration;
    private int distance;
    private Date timestamp;
    
    
    
    @Override
    public long getId() {
        return this.id;
    }

    @Override
    public void setId(long id) {
        this.id = id;
    }
    
    @Override
    public IRoute getRoute() {
        return route;
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
    public void setRoute(IRoute route) {
        this.route = route;
    }

    @Override
    public void setDuration(int duration) {
        this.duration = duration;
    }

    @Override
    public void setDistance(int distance) {
        this.distance = duration;
    }

    @Override
    public void setTimestamp(Date timestamp) {
        this.timestamp = timestamp;
    }

    
}
