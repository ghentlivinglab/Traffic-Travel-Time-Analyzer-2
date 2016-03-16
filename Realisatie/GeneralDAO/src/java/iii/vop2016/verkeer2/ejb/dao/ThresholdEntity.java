/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package iii.vop2016.verkeer2.ejb.dao;

import iii.vop2016.verkeer2.ejb.components.IRoute;
import iii.vop2016.verkeer2.ejb.components.Threshold;
import java.util.List;
import java.util.Observer;
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
@Table(name = "thresholds")
@Access(AccessType.PROPERTY)
public class ThresholdEntity extends Threshold {

    public ThresholdEntity(IRoute route, int level, int delayTriggerLevel) {
        super(route, level, delayTriggerLevel);
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Override
    public long getId() {
        return super.getId();
    }

    @Override
    public void setId(long id) {
        super.setId(id);
    }

    @Override
    public int getLevel() {
        return super.getLevel(); //To change body of generated methods, choose Tools | Templates.
    }

    @ManyToOne(optional = false, fetch = FetchType.EAGER)
    @JoinColumn(name = "routeId")
    @Override
    public IRoute getRoute() {
        return super.getRoute(); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void setLevel(int level) {
        super.setLevel(level); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void setRoute(IRoute route) {
        super.setRoute(route); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void setDelayTriggerLevel(int delayTriggerLevel) {
        super.setDelayTriggerLevel(delayTriggerLevel); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public int getDelayTriggerLevel() {
        return super.getDelayTriggerLevel(); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public List<Observer> getObservers() {
        return super.getObservers(); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void setObservers(List<Observer> observers) {
        super.setObservers(observers); //To change body of generated methods, choose Tools | Templates.
    }
}
