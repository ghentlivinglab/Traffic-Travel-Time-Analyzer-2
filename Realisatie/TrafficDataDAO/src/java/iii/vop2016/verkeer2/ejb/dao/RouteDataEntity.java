/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package iii.vop2016.verkeer2.ejb.dao;

import iii.vop2016.verkeer2.ejb.components.IRoute;
import iii.vop2016.verkeer2.ejb.components.IRouteData;
import iii.vop2016.verkeer2.ejb.components.RouteData;
import java.io.Serializable;
import java.util.Date;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Transient;

/**
 *
 * @author Mike
 */
@Entity
@Table(name="routedata")
public class RouteDataEntity implements Serializable, IRouteData {

    private IRouteData component;

    public RouteDataEntity() {
        component = new RouteData();
    }

    public RouteDataEntity(IRouteData component) {
        this.component = component;
    }
    
    
    private static final long serialVersionUID = 1L;
  
    private long id;

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
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
        if (!(object instanceof RouteDataEntity)) {
            return false;
        }
        RouteDataEntity other = (RouteDataEntity) object;
        if (this.id != other.id) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "iii.vop2016.verkeer2.ejb.dao.TrafficDataEntity[ id=" + id + " ]";
    }

    @Override
    public IRoute getRoute() {
        return this.component.getRoute();
    }

    @Override
    public int getDuration() {
        return this.component.getDuration();
    }

    @Override
    public int getDistance() {
        return this.component.getDistance();
    }

    @Override
    public Date getTimestamp() {
        return this.component.getTimestamp();
    }

    @Override
    public void setRoute(IRoute route) {
        this.component.setRoute(route);
    }

    @Override
    public void setDuration(int duration) {
        this.component.setDuration(duration);
    }

    @Override
    public void setDistance(int distance) {
        this.component.setDistance(distance);
    }

    @Override
    public void setTimestamp(Date timestamp) {
        this.component.setTimestamp(timestamp);
    }
    
    @Transient
    public IRouteData getComponent(){
        return this.component;
    }
    
}
