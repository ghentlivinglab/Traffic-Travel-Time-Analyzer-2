/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package iii.vop2016.verkeer2.ejb.dao;

import iii.vop2016.verkeer2.ejb.components.IGeoLocation;
import iii.vop2016.verkeer2.ejb.components.IRoute;
import iii.vop2016.verkeer2.ejb.components.Route;
import java.io.Serializable;
import java.util.List;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Transient;

/**
 *
 * @author Mike
 */
@Entity
public class RouteEntity implements Serializable, IRoute {

    
    private IRoute component;
    
    private static final long serialVersionUID = 1L;
    
    
    public RouteEntity(){
        component = new Route();
    }
    
    public RouteEntity(IRoute component){
        this.component = component;
    }
    
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    public Long getId() {
        return component.getId();
    }

    public void setId(Long id) {
        component.setId(id);
    }

    @Transient
    public IRoute getComponent() {
        return component;
    }


    @Override
    public String getName() {
        return this.component.getName();
    }

    @Override
    public IRoute getInverseRoute() {
        return this.getInverseRoute();
    }

    @Override
    public List<IGeoLocation> getGeolocations() {
        return this.component.getGeolocations();
    }

    @Override
    @Transient
    public IGeoLocation getStartLocation() {
        return this.component.getStartLocation();
    }

    @Override
    @Transient
    public IGeoLocation getEndLocation() {
        return this.getEndLocation();
    }

    @Override
    public void setName(String name) {
        component.setName(name);
    }

    @Override
    public void setInverseRoute(IRoute route) {
        this.component.setInverseRoute(route);
    }

    @Override
    public void setGeoLocations(IGeoLocation location) {
        this.component.setGeoLocations(location);
    }

    @Override
    public void addGeolocation(IGeoLocation location) {
         this.component.addGeolocation(location);
    }

    @Override
    public void addGeolocation(IGeoLocation location, int i) {
        this.component.addGeolocation(location, i);
    }

    @Override
    public void removeGeoLocation(IGeoLocation location) {
        this.component.removeGeoLocation(location);
    }
    
    
    
    @Override
    public int hashCode() {
        Long id = getId();
        int hash = 0;
        hash += (id != null ? id.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        Long id = getId();
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof RouteEntity)) {
            return false;
        }
        RouteEntity other = (RouteEntity) object;
        if ((getId() == null && other.getId() != null) || (id != null && !id.equals(other.getId()))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "iii.vop2016.verkeer2.ejb.dao.RouteEntity[ id=" + getId() + " ]";
    }
    
}
