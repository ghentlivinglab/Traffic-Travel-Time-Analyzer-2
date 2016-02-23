/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package iii.vop2016.verkeer2.ejb.dummy;

import iii.vop2016.verkeer2.ejb.components.IRoute;
import iii.vop2016.verkeer2.ejb.components.IRouteData;
import iii.vop2016.verkeer2.ejb.dao.ITrafficDataDAO;
import iii.vop2016.verkeer2.ejb.provider.ISourceAdapter;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 *
 * @author Tobias
 */
public class TrafficDataDAODummy implements ITrafficDataDAO{

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

    @Override
    public void addData(List<IRouteData> allData) {
        for(IRouteData data : allData){
            addData(data);
        }
    }
    
}
