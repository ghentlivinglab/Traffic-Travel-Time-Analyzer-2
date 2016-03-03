/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package iii.vop2016.verkeer2.ejb.dao;

import iii.vop2016.verkeer2.ejb.components.IGeoLocation;
import iii.vop2016.verkeer2.ejb.components.IRoute;
import iii.vop2016.verkeer2.ejb.components.Route;
import java.util.List;
import javax.persistence.Access;
import javax.persistence.AccessType;
import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Transient;

/**
 *
 * @author Mike
 */
@Entity
@Table(name = "routes")
@Access(AccessType.PROPERTY)
public class RouteEntity extends Route {
    
    public RouteEntity() {
        super();
    }

    public RouteEntity(IRoute component) {
        super(component);
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Override
    public long getId() {
        return super.getId();
    }

    @Override
    @OneToMany(cascade = CascadeType.ALL, targetEntity = GeoLocationEntity.class, fetch = FetchType.EAGER)
    @JoinColumn(name = "routeID")
    public List<IGeoLocation> getGeolocations() {
        return super.getGeolocations();
    }

    @Override
    @Transient
    public IGeoLocation getStartLocation() {
        return super.getStartLocation();
    }

    @Override
    @Transient
    public IGeoLocation getEndLocation() {
        return super.getEndLocation();
    }

    @Override
    public String getName() {
        return super.getName(); 
    }

    @Override
    public void setGeolocations(List<IGeoLocation> locations) {
        super.setGeolocations(locations); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void setId(long id) {
        super.setId(id); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void setName(String name) {
        super.setName(name); //To change body of generated methods, choose Tools | Templates.
    }
    
    
  
}
