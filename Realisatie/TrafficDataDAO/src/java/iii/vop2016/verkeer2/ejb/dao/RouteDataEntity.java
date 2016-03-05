/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package iii.vop2016.verkeer2.ejb.dao;

import iii.vop2016.verkeer2.ejb.components.IRoute;
import iii.vop2016.verkeer2.ejb.components.IRouteData;
import iii.vop2016.verkeer2.ejb.components.Route;
import iii.vop2016.verkeer2.ejb.components.RouteData;
import java.util.Date;
import javax.persistence.Access;
import javax.persistence.AccessType;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Transient;

/**
 *
 * @author Mike
 */
@Entity
@Table(name="routedata")
@Access(AccessType.PROPERTY)
public class RouteDataEntity extends RouteData{


    public RouteDataEntity() {
        super();
    }

    public RouteDataEntity(IRouteData component) {
        super(component);
    }
  
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Override
    public long getId() {
        return super.getId();
    }

    @Override
    public int getDuration() {
        return super.getDuration();
    }

    @Override
    public int getDistance() {
        return super.getDistance();
    }

    @Override
    @Temporal(TemporalType.TIMESTAMP)
    public Date getTimestamp() {
        return super.getTimestamp();
    }

    @Override
    public void setDistance(int distance) {
        super.setDistance(distance); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void setDuration(int duration) {
        super.setDuration(duration); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void setId(long id) {
        super.setId(id); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void setTimestamp(Date timestamp) {
        super.setTimestamp(timestamp); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public String getProvider() {
        return super.getProvider(); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void setProvider(String provider) {
        super.setProvider(provider); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public long getRouteId() {
        return super.getRouteId(); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void setRouteId(long id) {
        super.setRouteId(id); //To change body of generated methods, choose Tools | Templates.
    }
    
}
