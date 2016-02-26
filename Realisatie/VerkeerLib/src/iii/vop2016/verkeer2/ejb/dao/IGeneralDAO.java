/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package iii.vop2016.verkeer2.ejb.dao;

import iii.vop2016.verkeer2.ejb.components.IGeoLocation;
import iii.vop2016.verkeer2.ejb.components.IRoute;
import java.util.List;

/**
 *
 * @author tobia
 */
public interface IGeneralDAO{
    
    public List<IRoute> getRoutes();
    public IRoute getRoute(String name);
    public void addRoute(IRoute route);
    public void removeRoute(IRoute route);

    public void addGeoLocation(IGeoLocation geolocation);
    
}
