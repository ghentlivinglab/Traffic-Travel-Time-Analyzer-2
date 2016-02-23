/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package iii.vop2016.verkeer2.ejb.components;

import java.util.List;

/**
 *
 * @author Mike
 */
public class Route implements IRoute{

    private String name;
    private long id;
    private IRoute inv;
    private List<IGeoLocation> geolocations;

    public Route() {
        id = 0;
    }
    
    
    
    @Override
    public long getId() {
        return id;
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
    public List<IGeoLocation> getGeolocations() {
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
    public void setGeoLocations(List<IGeoLocation> locations) {
        geolocations = locations;
    }

    @Override
    public void addGeolocation(IGeoLocation location) {
        geolocations.add(location);
    }

    @Override
    public void addGeolocation(IGeoLocation location, int i) {
        geolocations.add(i,location);
    }

    @Override
    public void removeGeoLocation(IGeoLocation location) {
        geolocations.remove(location);
    }

    @Override
    public void setId(long id) {
        this.id = id;
    }
    
}
