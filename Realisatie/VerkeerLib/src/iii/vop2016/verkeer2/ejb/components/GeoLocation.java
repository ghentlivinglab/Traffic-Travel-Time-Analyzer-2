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
    protected int sortRank;
    
    public GeoLocation(){
        this.latitude = 0;
        this.longitude = 0;
        this.id = 0;
        this.name = null;
    }
    
    public GeoLocation(double latitude, double longitude){
        this.latitude = latitude;
        this.longitude = longitude;
        this.id = 0;
        this.name = null;
    }
    
    public GeoLocation(IGeoLocation component) {
        this.id = component.getId();
        this.longitude = component.getLongitude();
        this.latitude = component.getLatitude();
        this.name = component.getName();
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
        return "GeoLocatie \""+name+"\" ("+latitude+", "+longitude+")";
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

    @Override
    public void setSortRank(int i) {
        this.sortRank = i;
    }

    @Override
    public int getSortRank() {
        return this.sortRank;
    }
    
    
}
