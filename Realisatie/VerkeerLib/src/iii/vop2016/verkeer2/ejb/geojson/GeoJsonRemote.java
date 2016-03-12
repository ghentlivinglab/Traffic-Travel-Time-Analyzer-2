/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package iii.vop2016.verkeer2.ejb.geojson;

import iii.vop2016.verkeer2.ejb.components.IGeoLocation;
import iii.vop2016.verkeer2.ejb.components.IRoute;
import java.util.List;
import java.util.Map;
import javax.ejb.Remote;

/**
 *
 * @author tobia
 */
@Remote
public interface GeoJsonRemote {
    
    List<IGeoLocation> getRoutePlotGeoLocations(IRoute route);
    String getGeoJson(Map<IRoute,List<IGeoLocation>> list);
}
