/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package iii.vop2016.verkeer2.ejb.provider;

import iii.vop2016.verkeer2.ejb.components.IRoute;
import iii.vop2016.verkeer2.ejb.components.IRouteData;
import java.util.concurrent.Future;
import javax.ejb.Singleton;

/**
 *
 * @author tobia
 */
@Singleton
public class GoogleMapsSourceAdapter implements GoogleMapsSourceAdapterRemote {

    @Override
    public Future<IRouteData> parse(IRoute route) {
        return null;
    }

    // Add business logic below. (Right-click in editor and choose
    // "Insert Code > Add Business Method")
}
