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
    protected IRoute route;
    protected int duration;
    protected int distance;
    protected Date timestamp;
    protected String provider;

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
        this.distance = distance;
    }

    @Override
    public void setTimestamp(Date timestamp) {
        this.timestamp = timestamp;
    }

    @Override
    public String toString(){
        return "RouteData ["+route.getName()+"] ["+provider+"] (dis:"+distance+", dur:"+duration+")";
    }

    @Override
    public String getProviderName() {
        return this.provider;
    }

    @Override
    public void setProviderName(String providerName) {
        this.provider = providerName;
    }
    
}
