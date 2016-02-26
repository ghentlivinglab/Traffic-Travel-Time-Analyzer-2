/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package iii.vop2016.verkeer2.ejb.components;

/**
 *
 * @author Mike
 */
public class GeoLocation implements IGeoLocation {

    private double longitude;
    private double latitude;
    private String name;
    private IRoute route;
    
    public GeoLocation(){
        
    }
    
    public GeoLocation(double longitude, double latitude){
        this.longitude = longitude;
        this.latitude = latitude;
    }
    
    @Override
    public double getLongitude() {
        return this.longitude;
    }

    @Override
    public double getLatitude() {
        return this.latitude;
    }

    @Override
    public String getName() {
        return this.name;
    }

    @Override
    public void setLongitude(double longitude) {
        this.longitude=longitude;
    }

    @Override
    public void setLatitude(double latitude) {
        this.latitude=latitude;
    }

    @Override
    public void setName(String name) {
        this.name=name;
    }

    @Override
    public IRoute getRoute() {
        return this.route;
    }

    @Override
    public void setRoute(IRoute route) {
        this.route = route;
    }
    
}
