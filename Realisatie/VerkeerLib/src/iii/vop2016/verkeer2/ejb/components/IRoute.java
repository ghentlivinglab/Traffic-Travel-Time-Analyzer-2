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
public interface IRoute {
    
    public Long getId();
    public String getName();
    public IRoute getInverseRoute();
    public List<IGeoLocation> getGeolocations();
    public IGeoLocation getStartLocation();
    public IGeoLocation getEndLocation();
    
    public void setName(String name);
    public void setInverseRoute(IRoute route);
    public void setGeoLocations(IGeoLocation location);
    public void addGeolocation(IGeoLocation location); 
    public void addGeolocation(IGeoLocation location, int i); //i = rank, same counting method as ArrayList
    public void removeGeoLocation(IGeoLocation location);
    
    //data wordt niet opvraagbaar via deze klasse (later misschien nog eens over nadenken)
    
}
