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
import javax.annotation.Resource;
import javax.ejb.SessionContext;
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
import javax.ws.rs.POST;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import org.json.JSONObject;

/**
 * REST Web Service
 *
 * @author Gebruiker
 */
@Path("timers")
@RequestScoped
public class TimersResource {

    @Context
    private UriInfo context;

    private InitialContext ctx;
    private static BeanFactory beans;
    private Helper helper;

    /**
     * Creates a new instance of TimersResource
     */
    public TimersResource() {
    }

    @PostConstruct
    private void init() {
        try {
            ctx = new InitialContext();
        } catch (NamingException ex) {
            Logger.getLogger(RoutesResource.class.getName()).log(Level.SEVERE, null, ex);
        }
        beans = BeanFactory.getInstance(ctx, null);
        helper=new Helper();
    }

    /**
     * Retrieves representation of an instance of
     * iii.vop2016.verkeer2.war.rest.TimersResource
     *
     * @return an instance of java.lang.String
     */
    @GET
    @Path("newdata")
    @Produces("application/json")
    public Response getTimerData() {
        try {
            JSONObject result = new JSONObject();
            result.put("time", beans.getTimer().getCurrentTime());
            result.put("active", beans.getTimer().isTimerRunning());
            result.put("interval", beans.getTimer().getCurrentInterval());
            result.put("percentDone", beans.getTimer().getPercentDoneToNextInterval());
            return Response.status(Response.Status.OK).entity(result.toString()).build();
        } catch (Exception e) {
            return Response.status(Response.Status.SERVICE_UNAVAILABLE).build();
        }
    }

    @GET
    @Path("newdata/start")
    @Produces("application/json")
    public Response startTimer() {
        if (!helper.validateAPIKey(context, beans)) {
            return Response.status(Response.Status.UNAUTHORIZED).build();
        } else {
            try {
                beans.getTimer().StartTimer();
                JSONObject result = new JSONObject();
                result.put("status", "OK");
                return Response.status(Response.Status.OK).entity(result.toString()).build();
            } catch (Exception e) {
                return Response.status(Response.Status.SERVICE_UNAVAILABLE).build();
            }
        }
    }

    @GET
    @Path("newdata/stop")
    @Produces("application/json")
    public Response stopTimer() {
        if (!helper.validateAPIKey(context, beans)) {
            return Response.status(Response.Status.UNAUTHORIZED).build();
        } else {
        try {
            beans.getTimer().StopTimer();
            JSONObject result = new JSONObject();
            result.put("status", "OK");
            return Response.status(Response.Status.OK).entity(result.toString()).build();
        } catch (Exception e) {
            return Response.status(Response.Status.SERVICE_UNAVAILABLE).build();
        }
        }
    }

}
