/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package iii.vop2016.verkeer2.ejb.components;

import java.io.Serializable;

/**
 *
 * @author Mike
 */
public interface IGeoLocation extends Serializable{
    
    
    public double getLongitude();
    public double getLatitude();
    public String getName();
    public void setLongitude(double longitude);
    public void setLatitude(double latitude);  
    public void setName(String name);    
    public long getId();
    public void setId(long id);
    
}
