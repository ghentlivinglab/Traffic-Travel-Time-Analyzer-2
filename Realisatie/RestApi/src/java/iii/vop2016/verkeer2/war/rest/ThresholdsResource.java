/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package iii.vop2016.verkeer2.war.rest;

import iii.vop2016.verkeer2.ejb.components.IRoute;
import iii.vop2016.verkeer2.ejb.components.IThreshold;
import iii.vop2016.verkeer2.ejb.dao.IGeneralDAO;
import iii.vop2016.verkeer2.ejb.helper.BeanFactory;
import iii.vop2016.verkeer2.ejb.threshold.IThresholdHandler;
import iii.vop2016.verkeer2.ejb.threshold.IThresholdManager;
import java.io.StringReader;
import java.util.ArrayList;
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
import javax.json.JsonArray;
import javax.json.JsonArrayBuilder;
import javax.json.JsonObject;
import javax.json.JsonObjectBuilder;
import javax.json.JsonReader;
import javax.json.JsonStructure;
import javax.json.JsonValue;
import javax.json.stream.JsonParser;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.PathParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

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
    public String getAllForRoute() {
        return getAllForRoute("all");
    }

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response modifyThresholds(String body) {
        if (body == null || body.equals("")) {
            return Response.status(Response.Status.BAD_REQUEST).build();
        }

        List<IThreshold> list = new ArrayList<>();

        JsonReader reader = Json.createReader(new StringReader(body));
        JsonStructure obj = reader.read();
        if (obj.getValueType() == JsonValue.ValueType.ARRAY) {
            JsonArray arr = (JsonArray) obj;
            for (JsonValue val : arr) {
                if (val.getValueType() == JsonValue.ValueType.OBJECT) {
                    IThreshold th = Helper.ReadThreshold((JsonObject) val);
                    if (th == null) {
                        return Response.status(Response.Status.BAD_REQUEST).build();
                    } else {
                        list.add(th);
                    }
                }
            }
        } else if (obj.getValueType() == JsonValue.ValueType.OBJECT) {
            IThreshold th = Helper.ReadThreshold((JsonObject) obj);
            if (th == null) {
                return Response.status(Response.Status.BAD_REQUEST).build();
            } else {
                list.add(th);
            }
        }
        
        IThresholdManager man = beans.getThresholdManager();
        if(man.ModifyThresholds(list)){
                return Response.ok().entity("{\"status\":\"succes\"}").build();
        }else{
                return Response.status(Response.Status.BAD_REQUEST).build();
        }
    }

    @GET
    @Path("{id}")
    @Produces(MediaType.APPLICATION_JSON)
    public String getAllForRoute(@PathParam("id") String sid) {
        IThresholdManager man = beans.getThresholdManager();
        IGeneralDAO dao = beans.getGeneralDAO();

        List<IRoute> routes = Helper.getRoutes(sid, dao);

        JsonArrayBuilder arrAll = Json.createArrayBuilder();

        for (IRoute route : routes) {
            List<IThreshold> thList = man.getThresholds(route);

            JsonObjectBuilder objRoute = Json.createObjectBuilder();

            JsonArrayBuilder arr = Json.createArrayBuilder();
            for (IThreshold threshold : thList) {
                arr.add(Helper.BuildJsonThreshold(threshold));
            }
            objRoute.add("thresholds", arr);

            objRoute.add("route", Helper.BuildJsonRoute(route, beans.getDataProvider()));

            arrAll.add(objRoute);
        }

        if (routes.size() == 1) {
            return arrAll.build().getJsonObject(0).toString();
        } else {
            return arrAll.build().toString();
        }
    }

    @GET
    @Path("handlers")
    @Produces(MediaType.APPLICATION_JSON)
    public String getAllHandlers() {
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
            handler.notify(r, r.getId(), 1, 0, 1, (60 * 3) + 15);
        }

        return "{\"status\":\"ok\"}";
    }

}
