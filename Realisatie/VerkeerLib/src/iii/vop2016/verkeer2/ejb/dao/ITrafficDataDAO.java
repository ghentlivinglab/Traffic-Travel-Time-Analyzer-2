/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package iii.vop2016.verkeer2.ejb.dao;

import iii.vop2016.verkeer2.ejb.components.IRoute;
import iii.vop2016.verkeer2.ejb.components.IRouteData;
import iii.vop2016.verkeer2.ejb.provider.ISourceAdapter;
import java.util.Date;
import java.util.List;

/**
 *
 * @author tobia
 */
public interface ITrafficDataDAO {
    
    public List<IRouteData> getAllData();
    public List<IRouteData> getData(IRoute route);
    public List<IRouteData> getData(ISourceAdapter adapter);
    public List<IRouteData> getData(Date time1, Date time2);
    public void addData(IRouteData data);
    
    
    
}
