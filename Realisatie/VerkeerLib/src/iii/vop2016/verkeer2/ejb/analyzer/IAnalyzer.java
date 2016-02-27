/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package iii.vop2016.verkeer2.ejb.analyzer;

import iii.vop2016.verkeer2.ejb.components.IGeoLocation;
import iii.vop2016.verkeer2.ejb.components.IRoute;

/**
 *
 * @author tobia
 */
public interface IAnalyzer {
    String getProjectName();
    public IRoute addRoute(IRoute route);

    public void addGeoLocation(IGeoLocation geolocation1);
}
