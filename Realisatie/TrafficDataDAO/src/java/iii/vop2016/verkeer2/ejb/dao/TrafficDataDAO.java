/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package iii.vop2016.verkeer2.ejb.dao;

import iii.vop2016.verkeer2.ejb.components.IRoute;
import iii.vop2016.verkeer2.ejb.components.IRouteData;
import iii.vop2016.verkeer2.ejb.provider.ISourceAdapter;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import javax.ejb.Singleton;

/**
 *
 * @author tobia
 */
@Singleton
public class TrafficDataDAO implements TrafficDataDAORemote {

    @Override
    public List<IRouteData> getAllData() {
        return new ArrayList<>();
    }

    @Override
    public List<IRouteData> getData(IRoute route) {
        return new ArrayList<>();
    }

    @Override
    public List<IRouteData> getData(ISourceAdapter adapter) {
        return new ArrayList<>();
    }

    @Override
    public List<IRouteData> getData(Date time1, Date time2) {
        return new ArrayList<>();
    }

    @Override
    public void addData(IRouteData data) {
        
    }

    // Add business logic below. (Right-click in editor and choose
    // "Insert Code > Add Business Method")
}
