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

    protected long id;
    protected String name;
    protected List<IGeoLocation> geolocations;

    public Route() {
        geolocations = new ArrayList<>();
        this.name = null;
        this.id = 0;
    }
    
    public Route(String name) {
        this.name = name;
        geolocations = new ArrayList<>();
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
    public List<IGeoLocation> getGeolocations() {
        geolocations.sort(new GeoLocationComparator());
        return geolocations;
    }

    @Override
    public IGeoLocation getStartLocation() {
        if(geolocations.size()>0)
            return geolocations.get(0);
        else
            return null;
    }

    @Override
    public IGeoLocation getEndLocation() {
        if(geolocations.size()>0)
            return geolocations.get(geolocations.size()-1);
        else
            return null;
    }

    @Override
    public void setName(String name) {
        this.name = name;
    }

    @Override
    public void setGeolocations(List<IGeoLocation> locations) {
        geolocations = locations;
        for(int i=0; i<geolocations.size(); i++){
            geolocations.get(i).setSortRank(i+1);
        }
    }

    @Override
    public void addGeolocation(IGeoLocation location) {
        geolocations.add(location);
        location.setSortRank(geolocations.size());
    }

    @Override
    public void addGeolocation(IGeoLocation location, int i) {
        for(int j=i; j<geolocations.size(); j++){
            geolocations.get(j).setSortRank(geolocations.get(j).getSortRank()+1);
        }
        geolocations.add(i, location);
        location.setSortRank(i+1);
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
        if (!(obj instanceof IRoute)) {
            return false;
        }
        final IRoute other = (IRoute) obj;
        if (!Objects.equals(this.name, other.getName())) {
            return false;
        }
        return true;
    }

    

    
    
    
    


    
}
