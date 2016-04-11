/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package iii.vop2016.verkeer2.bean.analyse;

import iii.vop2016.verkeer2.bean.components.Route;
import iii.vop2016.verkeer2.ejb.helper.HelperFunctions;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.security.Timestamp;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.util.Pair;
import javax.faces.bean.ManagedProperty;
import javax.faces.context.FacesContext;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.servlet.http.HttpServletRequest;
import org.json.JSONException;
import org.json.JSONObject;

/**
 *
 * @author Mike
 */
abstract class AnalysePage {
    
    protected static final String JNDILOOKUP_PROPERTYFILE = "resources/properties/WebSettings";
    
    @ManagedProperty(value="#{routeDAO}")
    private RouteDAO routeDAO;
    @ManagedProperty(value="#{periodDAO}")
    private PeriodDAO periodDAO;
    @ManagedProperty(value="#{dataproviderDAO}")
    private DataproviderDAO dataproviderDAO;
    
    protected Properties prop;

    protected InitialContext ctx;
           
    public abstract String getTitle();
    public abstract String getSubTitle();
    public abstract String getDataURL();
        
    public AnalysePage(){
         try {
            ctx = new InitialContext();
            prop = getProperties();
        } catch (NamingException ex) {
            Logger.getLogger(AnalysePage.class.getName()).log(Level.SEVERE, null, ex);
        }     
    }
    
    private Properties getProperties() {
        return HelperFunctions.RetrievePropertyFile(JNDILOOKUP_PROPERTYFILE, ctx, Logger.getGlobal());
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
    
    
    
    public JSONObject getJSON(String url_string){
        JSONObject jsonobj = new JSONObject();
        try{
                URL url = new URL(url_string);
                HttpURLConnection con = (HttpURLConnection) url.openConnection();
                
                //Optional, GET is default
                con.setRequestMethod("GET");

                StringBuilder response;
                try ( //This sections puts the HttpAnswer in a String
                        BufferedReader in = new BufferedReader(
                                new InputStreamReader(con.getInputStream()))) {
                    String inputLine;
                    response = new StringBuilder();
                    while ((inputLine = in.readLine()) != null) {
                        response.append(inputLine);
                    }
                }

                //This section interpretes the JSON
                jsonobj = new JSONObject(response.toString());
                
        }catch(IOException | JSONException ex){
            
        }
        return jsonobj;
    }
    
    
    
}
