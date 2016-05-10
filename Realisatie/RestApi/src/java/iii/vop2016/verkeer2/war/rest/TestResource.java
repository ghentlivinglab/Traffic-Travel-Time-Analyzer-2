/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package iii.vop2016.verkeer2.war.rest;

import iii.vop2016.verkeer2.ejb.components.IRoute;
import iii.vop2016.verkeer2.ejb.components.IRouteData;
import iii.vop2016.verkeer2.ejb.components.RouteData;
import iii.vop2016.verkeer2.ejb.downstream.ITrafficDataDownstreamAnalyser;
import iii.vop2016.verkeer2.ejb.helper.BeanFactory;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.annotation.PostConstruct;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.UriInfo;
import javax.ws.rs.Consumes;
import javax.ws.rs.Produces;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PUT;
import javax.enterprise.context.RequestScoped;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import org.json.JSONObject;

/**
 * REST Web Service
 *
 * @author Tobias
 */
@Path("test")
@RequestScoped
public class TestResource {

    @Context
    private UriInfo context;
    private InitialContext ctx;
    private BeanFactory beans;

    /**
     * Creates a new instance of TestResource
     */
    public TestResource() {
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

    /**
     * Retrieves representation of an instance of
     * iii.vop2016.verkeer2.war.rest.TestResource
     *
     * @return an instance of java.lang.String
     */
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response getJson() {
        try {
            List<IRoute> routes = beans.getGeneralDAO().getRoutes();
            ITrafficDataDownstreamAnalyser d = beans.getTrafficDataDownstreamAnalyser();
            d.startSession();

            List<IRouteData> data = new ArrayList<>();

            Date date = new Date();
            int time = 10;

            for (IRoute route : routes) {
                if (route.getName().startsWith("Martelaarslaan (R40)")) {
                    data.add(CreateData(route, date, 1000, 27*60));
                }
                if (route.getName().startsWith("Oudenaardsesteenweg (N60)")) {
                    data.add(CreateData(route, date, 1000, 22*60));
                }
                if (route.getName().startsWith("B401")) {
                    data.add(CreateData(route, date, 1000, 8*60));
                }
                if (route.getName().startsWith("Heernislaan (R40)")) {
                    data.add(CreateData(route, date, 1000, 8*60));
                }

            }

            data = d.addData(data, routes);

            d.endSession(data, routes);
            JSONObject o = new JSONObject();
            o.put("status", "succes");
            return Response.ok().entity(o.toString()).build();
        } catch (Exception e) {
            return Response.serverError().build();
        }
    }

    private IRouteData CreateData(IRoute route, Date date, int i, int time) {
        IRouteData d = new RouteData();
        d.setDistance(i);
        d.setDuration(time);
        d.setProvider("Here");
        d.setRouteId(route.getId());
        d.setTimestamp(date);
        d.setId(0);
        return d;
    }
}
