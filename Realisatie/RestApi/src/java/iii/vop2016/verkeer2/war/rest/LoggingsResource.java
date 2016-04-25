/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package iii.vop2016.verkeer2.war.rest;

import iii.vop2016.verkeer2.ejb.components.Log;
import iii.vop2016.verkeer2.ejb.helper.BeanFactory;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.annotation.PostConstruct;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.UriInfo;
import javax.ws.rs.Produces;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PUT;
import javax.enterprise.context.RequestScoped;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.ws.rs.core.MediaType;
import org.json.JSONArray;

/**
 * REST Web Service
 *
 * @author Gebruiker
 */
@Path("loggings")
@RequestScoped
public class LoggingsResource {

    @Context
    private UriInfo context;

    private InitialContext ctx;
    private static BeanFactory beans;
    
    int amount;
    int offset;
    Level filter;
    String containing;
    
    Map<String,Level> levelMap;
    
    /**
     * Creates a new instance of LoggingsResource
     */
    public LoggingsResource() {
    }

    
    @PostConstruct
    private void init() {
        try {
            ctx = new InitialContext();
        } catch (NamingException ex) {
            Logger.getLogger(RoutesResource.class.getName()).log(Level.SEVERE, null, ex);
        }
        beans = BeanFactory.getInstance(ctx, null);
        levelMap=new HashMap<String,Level>();
        levelMap.put("severe",Level.SEVERE);
        levelMap.put("warning",Level.WARNING);
        levelMap.put("info",Level.INFO);
        levelMap.put("config",Level.CONFIG);
        levelMap.put("fine",Level.FINE);
        levelMap.put("finer",Level.FINER);
        levelMap.put("finest",Level.FINEST);
    }
    /**
     * Retrieves representation of an instance of iii.vop2016.verkeer2.war.rest.LoggingsResource
     * @return an instance of java.lang.String
     */
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public String getLogs() {
        setParameters();
        JSONArray result = new JSONArray();
        List<Log> logs = beans.getLogger().getLogs(amount, offset, filter, containing);
        for(Log log: logs){
            String logOutput= log.getL().toString()+ "\t"+log.getMessage();
            result.put(logOutput);
        }
        return result.toString();
    }
    
    private void setParameters(){
        String amount = context.getQueryParameters().getFirst("amount");
        if (amount != null) {
            this.amount = Integer.parseInt(amount);
        } else {
            this.amount = 100;
        }
        
        String offset = context.getQueryParameters().getFirst("offset");
        if (offset != null) {
            this.offset = Integer.parseInt(offset);
        } else {
            this.offset = 0;
        }
        
        String filter= context.getQueryParameters().getFirst("filter");
        if (filter != null){
            Level l = levelMap.get(filter);
            if (l != null){
                this.filter=l;
            }else{
                this.filter=Level.FINEST;
            }
        }else{
            this.filter= Level.FINEST;
        }
        
        String containing = context.getQueryParameters().getFirst("containing");
        if (containing != null){
            this.containing=containing;
        }
        else{
            this.containing="";
        }
    }

}
