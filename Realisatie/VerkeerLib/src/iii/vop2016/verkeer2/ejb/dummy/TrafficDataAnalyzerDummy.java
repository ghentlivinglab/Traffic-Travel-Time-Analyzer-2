/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package iii.vop2016.verkeer2.ejb.dummy;

import iii.vop2016.verkeer2.ejb.analyzer.IAnalyzer;
import iii.vop2016.verkeer2.ejb.components.IGeoLocation;
import iii.vop2016.verkeer2.ejb.components.IRoute;

/**
 *
 * @author Tobias
 */
public class TrafficDataAnalyzerDummy implements IAnalyzer{

    @Override
    public String getProjectName() {
        return "Dummy";
    }

    @Override
    public boolean addRoute(IRoute route) {
        return true;
    }

    @Override
    public void addGeoLocation(IGeoLocation geolocation1) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
    
}
