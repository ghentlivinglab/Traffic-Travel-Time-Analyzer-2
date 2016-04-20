/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package iii.vop2016.verkeer2.war.rest;

import iii.vop2016.verkeer2.ejb.components.IRoute;
import iii.vop2016.verkeer2.ejb.helper.BeanFactory;
import iii.vop2016.verkeer2.ejb.threshold.IThresholdHandler;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.annotation.PostConstruct;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.UriInfo;
import javax.ws.rs.Produces;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.enterprise.context.RequestScoped;
import javax.json.Json;
import javax.json.JsonArrayBuilder;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.ws.rs.core.MediaType;

/**
 * REST Web Service
 *
 * @author Tobias
 */
@Path("thresholds")
@RequestScoped
public class ThresholdsResource {

    @Context
    private UriInfo context;
    private InitialContext ctx;
    private BeanFactory beans;

    /**
     * Creates a new instance of ThresholdsResource
     */
    public ThresholdsResource() {
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
    public String get() {
        return getAll();
    }

    @GET
    @Path("all")
    @Produces(MediaType.APPLICATION_JSON)
    public String getAll() {
        JsonArrayBuilder arr = Json.createArrayBuilder();
        List<String> list = beans.getThresholdHandlers();
        for (String handler : list) {
            arr.add(handler);
        }

        return arr.build().toString();
    }
    
    @GET
    @Path("test")
    @Produces(MediaType.APPLICATION_JSON)
    public String getTest() {
        IRoute r = beans.getGeneralDAO().getRoute(1);
        List<String> list = beans.getThresholdHandlers();
        List<IThresholdHandler> handlers = beans.getThresholdHandlers(list);
        for (IThresholdHandler handler : handlers) {
            handler.notify(r, r.getId(), 1, 0, 1, (60*3)+15);
        }

        return "{\"status\":\"ok\"}";
    }

}
