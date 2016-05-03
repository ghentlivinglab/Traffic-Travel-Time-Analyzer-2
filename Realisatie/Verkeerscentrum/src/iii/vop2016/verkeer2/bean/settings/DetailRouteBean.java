/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package iii.vop2016.verkeer2.bean.settings;


import iii.vop2016.verkeer2.bean.auth.Login;
import iii.vop2016.verkeer2.ejb.components.GeoLocation;
import iii.vop2016.verkeer2.ejb.components.IGeoLocation;
import iii.vop2016.verkeer2.ejb.components.IRoute;
import iii.vop2016.verkeer2.ejb.helper.BeanFactory;
import iii.vop2016.verkeer2.ejb.components.IThreshold;
import iii.vop2016.verkeer2.ejb.components.Route;
import iii.vop2016.verkeer2.ejb.dao.IGeneralDAO;
import iii.vop2016.verkeer2.ejb.threshold.IThresholdManager;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.RequestScoped;
import javax.faces.bean.SessionScoped;
import javax.naming.InitialContext;
import javax.naming.NamingException;


/**
 *
 * @author Mike
 */
@ManagedBean(name = "detailRouteBean", eager = true)
@RequestScoped
public class DetailRouteBean {

    @ManagedProperty(value="#{routeSettings}")
    protected RouteSettings routeSettings;
    /*
    protected long id;
    protected IRoute route;
    protected List<IThreshold> thresholds;
    */
    
    private InitialContext ctx;
    private BeanFactory beanFactory;


    public DetailRouteBean() {
        try {
            ctx = new InitialContext();
        } catch (NamingException ex) {
            Logger.getLogger(Login.class.getName()).log(Level.SEVERE, null, ex);
        }
        beanFactory = BeanFactory.getInstance(ctx, null);
        
    }
    
    public long getId() {
        return routeSettings.id;
    }
   
    public void setId(long id) {
        System.out.println("SET ID to "+id);
        routeSettings.id = id;
        IThresholdManager thmanager = beanFactory.getThresholdManager();
        setRoute(routeSettings.getRoute(id));
        setThresholds(thmanager.getThresholds(getRoute()));
    }
    
    public void setThresholds(List<IThreshold> thresholds) {
        System.out.println("SET THRESHOLDS");
        routeSettings.thresholds = thresholds;
    }

    public IRoute getRoute() {
        return routeSettings.route;
    }

    public void setRoute(IRoute route) {
        routeSettings.route = route;
    }
    
    public RouteSettings getRouteSettings() {
        return routeSettings;
    }

    public void setRouteSettings(RouteSettings routeSettings) {
        this.routeSettings = routeSettings;
    }

    public List<IThreshold> getThresholds() {
        return routeSettings.thresholds;
    }
    
    
    
    
    
}
