/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package iii.vop2016.verkeer2.ejb.downstream;

import iii.vop2016.verkeer2.ejb.components.IGeoLocation;
import iii.vop2016.verkeer2.ejb.components.IRoute;
import iii.vop2016.verkeer2.ejb.components.IRouteData;
import java.util.List;

/**
 *
 * @author tobia
 */
public interface ITrafficDataDownstreamAnalyzer {
    String getProjectName();
    
    public IRouteData addData(IRouteData data);
    public List<IRouteData> addData(List<IRouteData> data);
}
