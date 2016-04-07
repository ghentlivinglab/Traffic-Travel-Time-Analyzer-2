/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package iii.vop2016.verkeer2.bean.analyse;

import iii.vop2016.verkeer2.bean.components.Route;
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
import javafx.util.Pair;
import javax.faces.bean.ManagedProperty;
import javax.faces.context.FacesContext;
import javax.servlet.http.HttpServletRequest;
import org.json.JSONException;
import org.json.JSONObject;

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
