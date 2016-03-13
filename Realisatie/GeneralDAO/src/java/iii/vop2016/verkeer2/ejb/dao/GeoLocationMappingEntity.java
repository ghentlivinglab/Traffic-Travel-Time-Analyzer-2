/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package iii.vop2016.verkeer2.ejb.dao;

import iii.vop2016.verkeer2.ejb.components.GeoLocation;
import iii.vop2016.verkeer2.ejb.components.IGeoLocation;
import iii.vop2016.verkeer2.ejb.components.IRoute;
import javax.persistence.Access;
import javax.persistence.AccessType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

/**
 *
 * @author tobia
 */
@Entity
@Table(name = "geolocationsMapping")
@Access(AccessType.PROPERTY)
public class GeoLocationMappingEntity extends GeoLocation {
    
    private RouteEntity route;

    public GeoLocationMappingEntity() {
        super();
    }

    public GeoLocationMappingEntity(IGeoLocation component,IRoute route) {
        super(component);
        if(route instanceof RouteEntity)
            this.route = (RouteEntity)route;
        else
            this.route = new RouteEntity(route);
        
    }
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Override
    public long getId() {
        return super.getId();
    }

    @Override
    public double getLongitude() {
        return super.getLongitude();
    }

    @Override
    public double getLatitude() {
        return super.getLatitude();
    }

    @Override
    public String getName() {
        return super.getName();
    }

    @Override
    public void setId(long id) {
        super.setId(id); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void setLatitude(double latitude) {
        super.setLatitude(latitude); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void setLongitude(double longitude) {
        super.setLongitude(longitude); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void setName(String name) {
        super.setName(name); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public int getSortRank() {
        return super.getSortRank(); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void setSortRank(int i) {
        super.setSortRank(i); //To change body of generated methods, choose Tools | Templates.
    }

    @ManyToOne(optional = false,fetch = FetchType.EAGER)
    @JoinColumn(name = "routeId")
    public RouteEntity getRoute() {
        return route;
    }

    public void setRoute(RouteEntity route) {
        this.route = route;
    }

}
