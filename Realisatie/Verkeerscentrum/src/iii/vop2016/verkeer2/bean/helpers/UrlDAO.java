/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package iii.vop2016.verkeer2.bean.helpers;

import iii.vop2016.verkeer2.ejb.helper.HelperFunctions;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.RequestScoped;
import javax.naming.InitialContext;
import javax.naming.NamingException;

/**
 *
 * @author Mike
 */
@ManagedBean
@RequestScoped
public class UrlDAO {
    
    protected static final String JNDILOOKUP_PROPERTYFILE = "resources/properties/WebSettings";

    protected static Properties prop;
   
    protected InitialContext ctx;
    
    /**
     * Creates a new instance of DataproviderDAO
     */
    public UrlDAO() {
        
        try {
            ctx = new InitialContext();
            prop = getProperties();
        } catch (NamingException ex) {
            Logger.getLogger(UrlDAO.class.getName()).log(Level.SEVERE, null, ex);
        }  
        
    }
        
    private Properties getProperties() {
        return HelperFunctions.RetrievePropertyFile(JNDILOOKUP_PROPERTYFILE, ctx, Logger.getGlobal());
    }
    
    public String getAllRoutes(){
        String url = prop.getProperty("urlRoutes");
        url = url.replaceAll("\\{id\\}", "all");
        return url;
    }
    
    public String getNewDataTimer(){
        return prop.getProperty("urlTimerNewData");
    }
    
    public String getGeoJSONAllRoutesCurrent(){
        String url = prop.getProperty("urlGeoJSONcurrent");
        url = url.replaceAll("\\{id\\}", "all");
        return url;
    }
    
    public String getGeoJSONAllRoutesAvg(){
        String url = prop.getProperty("urlGeoJSONavg");
        url = url.replaceAll("\\{id\\}", "all");
        return url;
    }
        
}
