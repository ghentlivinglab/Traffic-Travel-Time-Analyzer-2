/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package iii.vop2016.verkeer2.bean.analyse;

import iii.vop2016.verkeer2.bean.components.Route;
import java.security.Timestamp;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Map;
import javafx.util.Pair;
import javax.faces.bean.ManagedProperty;
import javax.faces.context.FacesContext;
import javax.servlet.http.HttpServletRequest;

/**
 *
 * @author Mike
 */
abstract class AnalysePage {
    
    @ManagedProperty(value="#{routeDAO}")
    protected RouteDAO routeDAO;
    @ManagedProperty(value="#{periodDAO}")
    private PeriodDAO periodDAO;
    @ManagedProperty(value="#{dataproviderDAO}")
    private DataproviderDAO dataproviderDAO;

           
    protected abstract String getTitle();
    protected abstract String getSubTitle();
        
    public AnalysePage(){
      
        
    }
    
    public void setPeriodDAO(PeriodDAO periodDAO) {
        this.periodDAO = periodDAO;
    }

    public void setDataproviderDAO(DataproviderDAO dataproviderDAO) {
        this.dataproviderDAO = dataproviderDAO;
    }

    public void setRouteDAO(RouteDAO routeDAO) {
        this.routeDAO = routeDAO;
    }
    
    
    
    public RouteDAO getRouteDAO() {
        return routeDAO;
    }
    
    public PeriodDAO getPeriodDAO() {
        return periodDAO;
    }

    public DataproviderDAO getDataproviderDAO() {
        return dataproviderDAO;
    }
    
    
    
    
}
