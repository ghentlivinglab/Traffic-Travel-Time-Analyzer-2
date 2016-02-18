/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package iii.vop2016.verkeer2.ejb.components;

import java.util.Date;

/**
 *
 * @author Mike
 */
public interface IRouteData {
    
    
    public int getId();
    public int getRoute();
    public int getDuration();
    public int getDistance();
    public Date getTimestamp();
    
    public void setRoute(IRoute route);
    public void setDuration(int duration);
    public void setDistance(int distance);
    public void setTimestamp(Date timestamp);
      
    
    
}
