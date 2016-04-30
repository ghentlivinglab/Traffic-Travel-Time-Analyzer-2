/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package iii.vop2016.verkeer2.war.rest;

import iii.vop2016.verkeer2.ejb.helper.BeanFactory;
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
import org.json.JSONObject;

/**
 * REST Web Service
 *
 * @author Gebruiker
 */
@Path("settings")
@RequestScoped
public class SettingsResource {

    @Context
    private UriInfo context;
    
    private InitialContext ctx;
    private static BeanFactory beans;

    /**
     * Creates a new instance of SettingsResource
     */
    public SettingsResource() {
    }
    
     @PostConstruct
    private void init() {
        try {
            ctx = new InitialContext();
        } catch (NamingException ex) {
            Logger.getLogger(RoutesResource.class.getName()).log(Level.SEVERE, null, ex);
        }
        beans = BeanFactory.getInstance(ctx, null);
    }

    
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public String getAPIKey() {
        JSONObject result = new JSONObject();
        String keyString = beans.getAPIKeyDAO().getKey();
        //String keyString = "rootkeytest";
        //String keyString = "keynotactive";
        result.put("key",keyString);
        if (beans.getAPIKeyDAO().validate(keyString)){
            result.put("klopt","ja");
        }
        else{
            result.put("klopt","nee");
        }
        return result.toString();
    }
    
    

}
