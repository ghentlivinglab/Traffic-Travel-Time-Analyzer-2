/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package iii.vop2016.verkeer2.ejb.components;

import java.io.Serializable;
import java.util.Date;

/**
 *
 * @author Mike
 */
public interface IRouteData extends Serializable {
    
    public long getId();
    public void setId(long id);
    
    public long getRouteId();
    public int getDuration();
    public int getDistance();
    public Date getTimestamp();
    public String getProvider();
    
    public void setRouteId(long id);
    public void setDuration(int duration);
    public void setDistance(int distance);
    public void setTimestamp(Date timestamp);
    public void setProvider(String provider);
      
    
    
}
