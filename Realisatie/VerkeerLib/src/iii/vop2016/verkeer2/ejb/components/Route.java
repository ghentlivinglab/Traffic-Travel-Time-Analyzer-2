/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package iii.vop2016.verkeer2.ejb.components;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;

/**
 *
 * @author Mike
 */
public class Route implements IRoute{

    private long id;
    private String name;
    private IRoute inv;
    private Set<IGeoLocation> geolocations;

    public Route() {
        geolocations = new HashSet<>();
        this.name = null;
        this.inv = null;
        this.id = 0;
    }
    
    public Route(String name) {
        this.name = name;
        geolocations = new HashSet<>();
        this.inv = null;
        this.id = 0;
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
    public String getName() {
        return name;
    }

    @Override
    public IRoute getInverseRoute() {
        return inv;
    }

    @Override
    public Set<IGeoLocation> getGeolocations() {
        return geolocations;
    }

    @Override
    public IGeoLocation getStartLocation() {
        return null;
    }

    @Override
    public IGeoLocation getEndLocation() {
        return null;
    }

    @Override
    public void setName(String name) {
        this.name = name;
    }

    @Override
    public void setInverseRoute(IRoute route) {
        inv = route;
    }

    @Override
    public void setGeolocations(Set<IGeoLocation> locations) {
        geolocations = locations;
    }

    @Override
    public void addGeolocation(IGeoLocation location) {
        geolocations.add(location);
    }

    @Override
    public void addGeolocation(IGeoLocation location, int i) {
        geolocations.add(location);
    }

    @Override
    public void removeGeoLocation(IGeoLocation location) {
        geolocations.remove(location);
    }
    
    @Override
    public String toString(){
        return "Route ("+id+", "+name+")" + hashCode();
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 79 * hash + (int) (this.id ^ (this.id >>> 32));
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
        final Route other = (Route) obj;
        if (this.id != other.id) {
            return false;
        }
        return true;
    }

    
    
    
    


    
}
