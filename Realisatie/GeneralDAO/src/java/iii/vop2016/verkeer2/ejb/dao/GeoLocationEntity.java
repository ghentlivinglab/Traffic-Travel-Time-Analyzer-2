/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package iii.vop2016.verkeer2.ejb.dao;

import iii.vop2016.verkeer2.ejb.components.GeoLocation;
import iii.vop2016.verkeer2.ejb.components.IGeoLocation;
import iii.vop2016.verkeer2.ejb.components.IRoute;
import java.io.Serializable;
import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Transient;

/**
 *
 * @author Mike
 */
@Entity
@Table(name="geolocations")
public class GeoLocationEntity implements Serializable, IGeoLocation {
    
    private IGeoLocation component;
    private IRoute route;
    
    public GeoLocationEntity() {
        component = new GeoLocation();
        route = null;
    }

    public GeoLocationEntity(IGeoLocation component) {
        this.component = component;
        route = null;
    }

    private static final long serialVersionUID = 1L;
    
    private long id;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (int) id;
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof GeoLocationEntity)) {
            return false;
        }
        GeoLocationEntity other = (GeoLocationEntity) object;
        if (this.id != other.id) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "iii.vop2016.verkeer2.ejb.dao.GeoLocationEntity[ id=" + id + " ]";
    }

    @Override
    public double getLongitude() {
        return this.component.getLongitude();
    }

    @Override
    public double getLatitude() {
        return this.component.getLatitude();
    }

    @Override
    public String getName() {
        return this.component.getName();
    }

    @Override
    public void setLongitude(double longitude) {
        this.component.setLongitude(longitude);
    }

    @Override
    public void setLatitude(double latitude) {
        this.component.setLatitude(latitude);
    }

    @Override
    public void setName(String name) {
        this.component.setName(name);
    }
    
    @Transient
    public IGeoLocation getComponent(){
        return this.component;
    }
    
    
    @Override
    @ManyToOne(targetEntity = RouteEntity.class, optional = false, cascade=CascadeType.ALL, fetch=FetchType.EAGER)
    @JoinColumn(referencedColumnName="id")
    public IRoute getRoute(){
        return route;
    }
    
    @Override
    public void setRoute(IRoute route){
        this.route = route;
    }
    
    
}
