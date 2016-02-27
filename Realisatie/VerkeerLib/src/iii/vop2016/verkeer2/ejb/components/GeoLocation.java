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

    private long id;
    private double longitude;
    private double latitude;
    private String name;
    private IRoute route;
    
    public GeoLocation(){
        this.longitude = 0;
        this.latitude = 0;
        this.id = 0;
        this.name = null;
        this.route = null;
    }
    
    public GeoLocation(double longitude, double latitude){
        this.longitude = longitude;
        this.latitude = latitude;
        this.id = 0;
        this.name = null;
        this.route = null;
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
    
    @Override
    public String toString(){
        return "GeoLocatie \""+name+"\" ("+longitude+", "+latitude+")";
    }

    @Override
    public int hashCode() {
        int hash = 5;
        hash = 59 * hash + (int) (this.id ^ (this.id >>> 32));
        hash = 59 * hash + (int) (Double.doubleToLongBits(this.longitude) ^ (Double.doubleToLongBits(this.longitude) >>> 32));
        hash = 59 * hash + (int) (Double.doubleToLongBits(this.latitude) ^ (Double.doubleToLongBits(this.latitude) >>> 32));
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final GeoLocation other = (GeoLocation) obj;
        if (this.id != other.id) {
            return false;
        }
        if (Double.doubleToLongBits(this.longitude) != Double.doubleToLongBits(other.longitude)) {
            return false;
        }
        if (Double.doubleToLongBits(this.latitude) != Double.doubleToLongBits(other.latitude)) {
            return false;
        }
        return true;
    }

    

    
}
