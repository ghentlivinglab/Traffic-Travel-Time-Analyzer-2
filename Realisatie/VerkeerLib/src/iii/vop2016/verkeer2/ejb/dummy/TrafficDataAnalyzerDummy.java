/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package iii.vop2016.verkeer2.ejb.dummy;

import iii.vop2016.verkeer2.ejb.components.IGeoLocation;
import iii.vop2016.verkeer2.ejb.components.IRoute;
import iii.vop2016.verkeer2.ejb.components.IRouteData;
import java.util.List;
import iii.vop2016.verkeer2.ejb.downstream.ITrafficDataDownstreamAnalyser;

/**
 *
 * @author Tobias
 */
public class TrafficDataAnalyzerDummy implements ITrafficDataDownstreamAnalyser{

    @Override
    public String getProjectName() {
        return "Dummy";
    }

    @Override
    public IRouteData addData(IRouteData data) {
        return null;
    }

    @Override
    public List<IRouteData> addData(List<IRouteData> data) {
        return null;
    }

    
}
