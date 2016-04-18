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
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

/**
 *
 * @author tobia
 */
@Entity
@Table(name = "geolocationsMappings")
@Access(AccessType.PROPERTY)
public class GeoLocationMappingEntity extends GeoLocation {

    public GeoLocationMappingEntity() {
        super();
    }

    public GeoLocationMappingEntity(IGeoLocation component,IRoute route) {
        super(component);
        this.name = route.getId()+"";
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

}
