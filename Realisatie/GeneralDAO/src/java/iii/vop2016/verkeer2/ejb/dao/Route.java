/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package iii.vop2016.verkeer2.ejb.dao;

import iii.vop2016.verkeer2.ejb.components.IGeoLocation;
import iii.vop2016.verkeer2.ejb.components.IRoute;
import java.io.Serializable;
import java.util.List;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

/**
 *
 * @author Mike
 */
@Entity
public class Route implements Serializable, IRoute {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (id != null ? id.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof Route)) {
            return false;
        }
        Route other = (Route) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "iii.vop2016.verkeer2.ejb.dao.Route[ id=" + id + " ]";
    }

    @Override
    public String getName() {
        return null;   
    }

    @Override
    public IRoute getInverseRoute() {
        return null;
    }

    @Override
    public List<IGeoLocation> getGeolocations() {
        return null;
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
        
    }

    @Override
    public void setInverseRoute(IRoute route) {
        
    }

    @Override
    public void setGeoLocations(IGeoLocation location) {
        
    }

    @Override
    public void addGeolocation(IGeoLocation location) {
        
    }

    @Override
    public void addGeolocation(IGeoLocation location, int i) {
        
    }

    @Override
    public void removeGeoLocation(IGeoLocation location) {
        
    }
    
}
