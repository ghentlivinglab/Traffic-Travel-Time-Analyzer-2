/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package iii.vop2016.verkeer2.bean.APIKey;

import java.io.Serializable;
import javax.persistence.Access;
import javax.persistence.AccessType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

/**
 *
 * @author Mike
 */
@Entity
@Table(name = "apikeys")
@Access(AccessType.PROPERTY)
public class APIKey implements Serializable {
    
    private int id;
    private String keyString;
    private int active;
    
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    public int getId() {
        return this.id;
    }
    
    @Column(unique = true)
    public String getKeyString() {
        return this.keyString; 
    }
   
    
    public int getActive() {
        return this.active; 
    }
    
    public void setId(int id) {
        this.id = id;
    }

    public void setKeyString(String keyString) {
        this.keyString = keyString;
    }

    public void setActive(int active) {
        this.active=active;
    }

    
    
    
}
