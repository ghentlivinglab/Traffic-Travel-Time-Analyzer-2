/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package iii.vop2016.verkeer2.war.rest;

import iii.vop2016.verkeer2.ejb.components.GeoLocation;
import iii.vop2016.verkeer2.ejb.components.IGeoLocation;
import iii.vop2016.verkeer2.ejb.components.IRoute;
import iii.vop2016.verkeer2.ejb.components.IRouteData;
import iii.vop2016.verkeer2.ejb.components.Route;
import iii.vop2016.verkeer2.ejb.dummy.BeanFactoryDummy;
import iii.vop2016.verkeer2.ejb.helper.BeanFactory;
import iii.vop2016.verkeer2.ejb.helper.InvalidCoordinateException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import javax.ejb.SessionContext;
import javax.faces.bean.RequestScoped;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.UriInfo;
import javax.ws.rs.PathParam;
import javax.ws.rs.Consumes;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.GET;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Response;
import org.json.JSONArray;
import org.json.JSONObject;
import iii.vop2016.verkeer2.ejb.downstream.ITrafficDataDownstreamAnalyser;

/**
 * REST Web Service
 *
 * @author tobia
 */
@Path("routes")
@RequestScoped
public class RoutesResource {

    @Context
    private UriInfo context;
    
    @Resource
    private SessionContext sctx;
    private InitialContext ctx;
    private static BeanFactory beans;
    
    private Map<String, Boolean> visibleFields;
    Date startTime;
    Date endTime;
    List<String> providers;
    String dataType; //data, current, summary

    /**
     * Creates a new instance of RoutesResource
     */
    public RoutesResource() {
        
    }
    
    @PostConstruct
    private void init() {
        try {
            ctx = new InitialContext();
        } catch (NamingException ex) {
            Logger.getLogger(RoutesResource.class.getName()).log(Level.SEVERE, null, ex);
        }
        beans = BeanFactory.getInstance(ctx, sctx);
        visibleFields = new HashMap<>();
        providers = new ArrayList<>();
        initVisibleFields();
        setVisibleFields();
        setParameters();
        setStartTime("1456761535931");
        setEndTime("2456761635931");
        dataType = "data";
    }
    
    private void initVisibleFields() {
        visibleFields.put("route.data", Boolean.FALSE);
        visibleFields.put("route.id", Boolean.TRUE);
        visibleFields.put("route.name", Boolean.TRUE);
        visibleFields.put("route.inverseRoute", Boolean.TRUE);
        visibleFields.put("route.geolocations", Boolean.TRUE);
    }
    
    /**
     * Retrieves representation of an instance of iii.vop2016.verkeer2.war.rest.RoutesResource
     * @return an instance of java.lang.String
     */
    @GET
    @Path("init")
    //@Produces("application/xml")
    public Response initRoutes() throws InvalidCoordinateException {
                
        try {
            ITrafficDataDownstreamAnalyser analyser = beans.getTrafficDataDownstreamAnalyser();
            
            IRoute r = new Route("R4 Zelzate");
            
            //r.setInverseRoute(r);
            
            IGeoLocation geolocation1 = new GeoLocation(51.192226, 3.776342);
            IGeoLocation geolocation2 = new GeoLocation(51.086447, 3.672188);
            geolocation1.setName("Zelzate");
            geolocation2.setName("Gent");
            r.addGeolocation(geolocation1);
            r.addGeolocation(geolocation2);
            r = beans.getGeneralDAO().addRoute(r);
            
            
            List<IRoute> routes = beans.getGeneralDAO().getRoutes();
            for(IRoute route : routes){
                System.out.println("");
                System.out.println("Ik ben een Route met volgende eigenschappen:");
                System.out.println("ID: "+route.getId());
                System.out.println("Name: "+route.getName());
                System.out.println("Geolocaties: "+route.getGeolocations());
            }
            
            
            
            //TODO return proper representation object
            return Response.status(Response.Status.OK).entity("Routes have been initialised").build();
        } catch (Exception ex) {
            Logger.getLogger(RoutesResource.class.getName()).log(Level.SEVERE, null, ex);
            return Response.status(Response.Status.SERVICE_UNAVAILABLE).entity("An error has occured").build();
        }
    }

    @GET
    @Path("all")
    @Produces("application/json")
    public String getAllRoutes() {
        List<IRoute> routes = beans.getGeneralDAO().getRoutes();
        return JSONRoutes(routes);
    }
    
    
    
    @GET
    @Path("{id}")
    @Produces("application/json")
    public String getRoutes(@PathParam("id") String sid) {
        List<IRoute> routes = new ArrayList<>();
        List<Long> ids = getIds(sid);
        for (int i=0; i<ids.size(); i++) {
            IRoute r = beans.getGeneralDAO().getRoute(ids.get(i));
            if(r != null)
                routes.add(r);
        }
        return JSONRoutes(routes);
    }
    
    
    @GET
    @Path("{id}/data/current")
    @Produces("application/json")
    public String getCurrentTrafficData(@PathParam("id") String sid) {
        visibleFields.put("route.data", Boolean.TRUE);
        dataType = "current";
        List<IRoute> routes;
        if(sid.equals("all")){
            routes = beans.getGeneralDAO().getRoutes();
        }else{
            routes = new ArrayList<>();
            List<Long> ids = getIds(sid);
            for (int i=0; i<ids.size(); i++) {
                IRoute r = beans.getGeneralDAO().getRoute(ids.get(i));
                if(r != null)
                    routes.add(r);
            }
        }
        JSONArray result = new JSONArray();
        for(IRoute route : routes){
            result.put(transformRoute(route));
        }
        return result.toString(1);
    }
    
    
    @GET
    @Path("{id}/data/summary")
    @Produces("application/json")
    public String getTrafficSummary(@PathParam("id") String id) {
        
        List<IRoute> routes = beans.getGeneralDAO().getRoutes();
        
        return null;
        
    }
    
    
    
    @GET
    @Path("{id}/data/{timeStart}")
    @Produces("application/json")
    public String getTrafficData(@PathParam("id") String sid, @PathParam("timeStart") String stimeStart) {
        return getTrafficData(sid, stimeStart, ""+(new Date()).getTime());
    }
    
    
    
    
    
    @GET
    @Path("{id}/data/{timeStart}/{timeEnd}")
    @Produces("application/json")
    public String getTrafficData(@PathParam("id") String sid, @PathParam("timeStart") String stimeStart, @PathParam("timeEnd") String stimeEnd) {
        visibleFields.put("route.data", Boolean.TRUE);
        setStartTime(stimeStart);
        setEndTime(stimeEnd);
        List<IRoute> routes;
        if(sid.equals("all")){
            routes = beans.getGeneralDAO().getRoutes();
        }else{
            routes = new ArrayList<>();
            List<Long> ids = getIds(sid);
            for (int i=0; i<ids.size(); i++) {
                IRoute r = beans.getGeneralDAO().getRoute(ids.get(i));
                if(r != null)
                    routes.add(r);
            }
        }
        JSONArray result = new JSONArray();
        for(IRoute route : routes){
            result.put(transformRoute(route));
        }
        return result.toString(1);
    }
    
    
    
    private List<Long> getIds(String ids){
        List<Long> result = new ArrayList<>();
        String[] parts = ids.split(",");
        for(String s : parts){
            try{
                result.add(Long.parseLong(s, 10));
            }catch(NumberFormatException e){
                Logger.getGlobal().log(Level.WARNING, s+" could not be converted to Long");
            }
        }
        return result;
    }
    
    
    private JSONObject transformRoute(IRoute route){
        JSONObject obj = new JSONObject();
        if(visibleFields.get("route.id"))
            obj.put("id", route.getId());
        if(visibleFields.get("route.name"))
            obj.put("name", route.getName());
        if(visibleFields.get("route.geolocations"))
            obj.put("geolocations", transformGeoLocations(route.getGeolocations()));                    
        if(visibleFields.get("route.data"))
            addRouteData(obj,route);
         
        return obj;
    }
    
    private JSONObject transformGeoLocation(IGeoLocation location){
        JSONObject obj = new JSONObject();
        obj.put("name",location.getName());
        obj.put("latitude",location.getLatitude());
        obj.put("longitude",location.getLongitude());
        return obj;
    }
    
    private JSONArray transformGeoLocations(List<IGeoLocation> geolocations){
        JSONArray result = new JSONArray();
        for(IGeoLocation loc : geolocations){
            result.put(transformGeoLocation(loc));
        }
        return result;
    }
    
    
    
    private JSONObject transformRouteData(IRouteData data){
        JSONObject obj = new JSONObject();
        obj.put("distance",data.getDistance());
        obj.put("duration",data.getDuration());
        obj.put("provider",data.getProvider());
        obj.put("timestamp",data.getTimestamp().getTime());
        return obj;
    }
    
    private JSONArray transformRouteData(List<IRouteData> data){
        JSONArray result = new JSONArray();
        for(IRouteData d : data){
            result.put(transformRouteData(d));
        }
        return result;
    }

    private void setVisibleFields() {
        String fields = context.getQueryParameters().getFirst("fields");
        if(fields != null){
            for (String key : visibleFields.keySet()) {
                visibleFields.put(key,Boolean.FALSE);
            }
            String[] parts = fields.split(",");
            for(String s : parts){
                visibleFields.put(s, Boolean.TRUE);
            }
        }
    }

    private void setStartTime(String startTime) {
        try{
            this.startTime = new Date(Long.parseLong(startTime, 10));
        }catch(NumberFormatException e){
            Logger.getGlobal().log(Level.WARNING, startTime+" could not be converted to Long");
        }
    }

    private void setEndTime(String endTime) {
        try{
            this.endTime = new Date();
        }catch(NumberFormatException e){
            Logger.getGlobal().log(Level.WARNING, endTime+" could not be converted to Long");
        }
    }

    private void setParameters() {
        /* PROVIDERS */
        String providers = context.getQueryParameters().getFirst("provider");
        if(providers != null){
            String[] parts = providers.split(",");
            for(String s : parts){
                this.providers.add(s);
            }
        }
    }

    private void addRouteData(JSONObject obj, IRoute route) {
        List<IRouteData> data = null;
        switch(dataType){
            case "data" : data = beans.getTrafficDataDAO().getData(route, startTime, endTime); break;
            case "current" : data = beans.getTrafficDataDAO().getCurrentTrafficSituation(route); break;
            case "summary" : data = beans.getTrafficDataDAO().getCurrentTrafficSituation(route); break;
        }
        List<IRouteData> result = new ArrayList<>();
        for(IRouteData d : data){
            if(providers.size()==0 || providers.contains(d.getProvider()))
                result.add(d);
        }
        obj.put("data", transformRouteData(result)); 
    }

    private String JSONRoutes(List<IRoute> routes) {
        JSONArray result = new JSONArray();
        for(IRoute route : routes){
            result.put(transformRoute(route));
        }
        return result.toString(1);
    }






}
