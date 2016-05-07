/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package iii.vop2016.verkeer2.war.rest;

import iii.vop2016.verkeer2.ejb.components.IRoute;
import iii.vop2016.verkeer2.ejb.components.IRouteData;
import iii.vop2016.verkeer2.ejb.components.IThreshold;
import iii.vop2016.verkeer2.ejb.components.Route;
import iii.vop2016.verkeer2.ejb.components.RouteData;
import iii.vop2016.verkeer2.ejb.components.Threshold;
import iii.vop2016.verkeer2.ejb.dao.IGeneralDAO;
import iii.vop2016.verkeer2.ejb.helper.BeanFactory;
import iii.vop2016.verkeer2.ejb.helper.ParserException;
import iii.vop2016.verkeer2.ejb.helper.VerkeerLibToJson;
import iii.vop2016.verkeer2.ejb.threshold.IThresholdHandler;
import iii.vop2016.verkeer2.ejb.threshold.IThresholdManager;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
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
import org.json.JSONArray;
import org.json.JSONObject;

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
    public Response getAllForRoute() {
        return getAllForRoute("all");
    }

    @POST
    @Path("[id]/update")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response modifyThresholdsWithId(String body) {
        return modifyThresholds(body);
    }

    @POST
    @Path("update")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response modifyThresholds(String body) {
        try {
            if (body == null || body.equals("")) {
                return Response.status(Response.Status.BAD_REQUEST).build();
            }

            List<IThreshold> list = new ArrayList<>();
            List<JSONObject> listOfObjects = new ArrayList<>();
            try {
                JSONArray arr = VerkeerLibToJson.parseJsonAsArray(body);
                for(Object o:arr){
                    listOfObjects.add((JSONObject) o);
                }
            } catch (ParserException e) {
                try{
                    JSONObject o = VerkeerLibToJson.parseJsonAsObject(body);
                    listOfObjects.add(o);
                }catch(ParserException e1){
                    return Response.status(Response.Status.BAD_REQUEST).build();
                }
            }
            
            for(JSONObject o : listOfObjects){
                Map<IRoute, List<IThreshold>> m = VerkeerLibToJson.fromJson(o,new Route(), new Threshold());
                for(Map.Entry<IRoute, List<IThreshold>> entry : m.entrySet()){
                    for(IThreshold th:entry.getValue()){
                        list.add(th);
                    }
                }
            }

            IThresholdManager man = beans.getThresholdManager();
            if (man.ModifyThresholds(list)) {
                return Response.ok().entity("{\"status\":\"succes\"}").build();
            } else {
                return Response.status(Response.Status.BAD_REQUEST).build();
            }
        } catch (Exception e) {
            return Response.status(Response.Status.BAD_REQUEST).build();
        }
    }

    @GET
    @Path("{id}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getAllForRoute(@PathParam("id") String sid) {
        try {
            IThresholdManager man = beans.getThresholdManager();
            IGeneralDAO dao = beans.getGeneralDAO();

            List<IRoute> routes = Helper.getRoutes(sid, dao);

            if (routes.size() == 1) {
                IRoute route = routes.get(0);
                List<IThreshold> thList = man.getThresholds(route);
                return Response.status(Response.Status.OK).entity(VerkeerLibToJson.toJson(route, thList).toString()).build();
            } else {
                JSONArray arr = new JSONArray();
                for (IRoute route : routes) {
                    List<IThreshold> thList = man.getThresholds(route);
                    arr.put(VerkeerLibToJson.toJson(route, thList));
                }
                return Response.status(Response.Status.OK).entity(arr.toString()).build();
            }
        } catch (Exception e) {
            return Response.status(Response.Status.SERVICE_UNAVAILABLE).build();
        }
    }

    @GET
    @Path("handlers")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getAllHandlers() {
        try {
            JsonArrayBuilder arr = Json.createArrayBuilder();
            List<String> list = beans.getThresholdHandlers();
            for (String handler : list) {
                arr.add(handler);
            }

            return Response.status(Response.Status.OK).entity(arr.build().toString()).build();
        } catch (Exception e) {
            return Response.status(Response.Status.SERVICE_UNAVAILABLE).build();
        }
    }
}
