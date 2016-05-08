/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package iii.vop2016.verkeer2.dummy.datasources;

import iii.vop2016.verkeer2.ejb.components.IRoute;
import iii.vop2016.verkeer2.ejb.components.IRouteData;
import iii.vop2016.verkeer2.ejb.components.RouteData;
import iii.vop2016.verkeer2.ejb.helper.DataAccessException;
import iii.vop2016.verkeer2.ejb.helper.URLException;
import javax.ejb.Singleton;

/**
 *
 * @author tobia
 */
@Singleton
public class DummySourceAdapter implements DummySourceAdapterRemote {

    int count = 0;
    IRoute route;

    @Override
    public IRouteData parse(IRoute route, String sessionID) throws URLException, DataAccessException {
        if (this.route == null) {
            this.route = route;
        }

        RouteData r = new RouteData();
        r.setDistance(Math.toIntExact(route.getId() * 1000));
        r.setDuration(Math.toIntExact((route.getId() * 100) + (count * 30)));
        r.setProvider(getProviderName());
        r.setRouteId(route.getId());

        if (this.route.equals(route)) {
            if(count == 5){
                r.setDistance(Math.toIntExact(route.getId() * 2 * 1000));
            }
            if (count < 50) {
                count++;
            }else{
                count = 0;
            }
        }

        return r;
    }

    @Override
    public String getProviderName() {
        return "Dummy";
    }
}
