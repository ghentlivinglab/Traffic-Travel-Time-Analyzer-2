/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package iii.vop2016.verkeer2.ejb.components;

import java.util.Objects;

/**
 *
 * @author Mike
 */
public class GeoLocation implements IGeoLocation {

    protected long id;
    protected double longitude;
    protected double latitude;
    protected String name;
    
    public GeoLocation(){
        this.longitude = 0;
        this.latitude = 0;
        this.id = 0;
        this.name = null;
    }
    
    public GeoLocation(double longitude, double latitude){
        this.longitude = longitude;
        this.latitude = latitude;
        this.id = 0;
        this.name = null;
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
    public String toString(){
        return "GeoLocatie \""+name+"\" ("+longitude+", "+latitude+")";
    }

    @Override
    public int hashCode() {
        int hash = 3;
        hash = 97 * hash + (int) (Double.doubleToLongBits(this.longitude) ^ (Double.doubleToLongBits(this.longitude) >>> 32));
        hash = 97 * hash + (int) (Double.doubleToLongBits(this.latitude) ^ (Double.doubleToLongBits(this.latitude) >>> 32));
        hash = 97 * hash + Objects.hashCode(this.name);
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
        if (!(obj instanceof IGeoLocation)) {
            return false;
        }
        final IGeoLocation other = (IGeoLocation) obj;
        if (Double.doubleToLongBits(this.longitude) != Double.doubleToLongBits(other.getLongitude())) {
            return false;
        }
        if (Double.doubleToLongBits(this.latitude) != Double.doubleToLongBits(other.getLatitude())) {
            return false;
        }
        if (!Objects.equals(this.name, other.getName())) {
            return false;
        }
        return true;
    }
    
    
}
