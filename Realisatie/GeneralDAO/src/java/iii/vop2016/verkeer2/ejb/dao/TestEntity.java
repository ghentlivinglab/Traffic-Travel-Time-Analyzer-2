/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package iii.vop2016.verkeer2.ejb.dao;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.Transient;

/**
 *
 * @author Mike
 */
@Entity
public class TestEntity implements Serializable {

    private static final long serialVersionUID = 1L;
    
    
    private long id;
    private List<RouteEntity> route;
    
    public TestEntity(){
        route = new ArrayList<>();;
    }

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
        if (!(object instanceof TestEntity)) {
            return false;
        }
        TestEntity other = (TestEntity) object;
        if (this.id != other.id) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "iii.vop2016.verkeer2.ejb.dao.TestEntity[ id=" + id + " ]";
    }
    
    /*
    @Transient
    //@ManyToMany(targetEntity = RouteEntity.class, fetch = FetchType.LAZY)
    //@JoinTable(name="BOEKEN_PER_AUTEUR", joinColumns=@JoinColumn(name="tests"),
    //        inverseJoinColumns=@JoinColumn(name="route"))
    public List<RouteEntity> getRoute(){
        return route;
    }
    
    public void setRoute(List<RouteEntity> list){
        this.route = route;
    }
    */
    
}
