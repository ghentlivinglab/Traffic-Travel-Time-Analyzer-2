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
import iii.vop2016.verkeer2.ejb.dao.ITrafficDataDAO;
import iii.vop2016.verkeer2.ejb.components.Weekdays;
import iii.vop2016.verkeer2.ejb.datasources.ISourceAdapter;
import iii.vop2016.verkeer2.ejb.helper.BeanFactory;
import iii.vop2016.verkeer2.ejb.helper.InvalidCoordinateException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import javax.faces.bean.RequestScoped;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.UriInfo;
import javax.ws.rs.PathParam;
import javax.ws.rs.Path;
import javax.ws.rs.GET;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Response;
import org.json.JSONArray;
import org.json.JSONObject;
import iii.vop2016.verkeer2.ejb.downstream.ITrafficDataDownstreamAnalyser;
import java.util.Calendar;
import java.util.Set;
import javax.xml.ws.WebServiceContext;

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

    //@Resource
    //private WebServiceContext webServiceContext;
    private InitialContext ctx;
    private static BeanFactory beans;

    Date startTime;
    Date endTime;
    
    Date avgStartTime;
    Date avgEndTime;
    
    Date optimalStartTime;
    Date optimalEndTime;
    
    List<Date> startTimes;
    List<Date> endTimes;
    
    int precision = -1;
    
    List<String> providers;

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
        beans = BeanFactory.getInstance(ctx, null);
        setNoProviders();
        setProviders();
        startTimes = new ArrayList<>();
        endTimes = new ArrayList<>();
    }

    /**
     * Retrieves representation of an instance of
     * iii.vop2016.verkeer2.war.rest.RoutesResource
     *
     * @return an instance of java.lang.String
     */
    @GET
    @Path("init")
    //@Produces("application/xml")
    public Response initRoutes() throws InvalidCoordinateException {

        try {
            
            IRoute r1 = initRoute("Rooigemlaan (R40) northbound",51.0560905,3.6951634,51.0663037,3.6996797,"Drongensesteenweg","Palinghuizen");
            IRoute r2 = initRoute("Rooigemlaan (R40) southbound",51.066296,3.699685,51.056104,3.695152,"Palinghuizen","Drongensesteenweg");
            IRoute r3 = initRoute("Gasmeterlaan (R40) eastbound",51.066271,3.699709,51.067505,3.727959,"Palinghuizen","Neuseplein");
            IRoute r4 = initRoute("Nieuwevaart (R40) westbound",51.067690,3.727868,51.066271,3.699709,"Neuseplein","Palinghuizen");
            IRoute r5 = initRoute("Dok-Noord (R40) southbound",51.067137,3.726568,51.056536,3.738477,"Neuseplein","Dampoort");
            IRoute r6 = initRoute("Dok-Noord (R40) northbound",51.057116,3.738622,51.067633,3.727523,"Dampoort","Neuseplein");
            IRoute r7 = initRoute("Heernislaan (R40) southbound",51.056536,3.738477,51.038613,3.736007,"Dampoort","Zuidparklaan");
            
            
            
            return Response.status(Response.Status.OK).entity("Routes have been initialised").build();
        } catch (Exception ex) {
            Logger.getLogger(RoutesResource.class.getName()).log(Level.SEVERE, null, ex);
            return Response.status(Response.Status.SERVICE_UNAVAILABLE).entity("An error has occured").build();
        }
    }
    
    private IRoute initRoute(String routeName, double startlat, double startlong, double endlat, double endlong, String startName, String endName) throws InvalidCoordinateException{
        IRoute r = new Route(routeName);
        //try {
            ITrafficDataDownstreamAnalyser analyser = beans.getTrafficDataDownstreamAnalyser();

            //IRoute r = new Route("Rooigemlaan (R40) northbound");

            //r.setInverseRoute(r);
            IGeoLocation geolocation1 = new GeoLocation(startlat, startlong);
            IGeoLocation geolocation2 = new GeoLocation(endlat, endlong);
            geolocation1.setName(startName);
            geolocation2.setName(endName);
            r.addGeolocation(geolocation1);
            r.addGeolocation(geolocation2);
            r = beans.getGeneralDAO().addRoute(r);
            if (r.getId() != 0) {
                beans.getThresholdManager().addDefaultThresholds(r);

                List<IRoute> routes = beans.getGeneralDAO().getRoutes();
                for (IRoute route : routes) {
                    System.out.println("");
                    System.out.println("Ik ben een Route met volgende eigenschappen:");
                    System.out.println("ID: " + route.getId());
                    System.out.println("Name: " + route.getName());
                    System.out.println("Geolocaties: " + route.getGeolocations());
                }
                return r;
                //TODO return proper representation object
                //return Response.status(Response.Status.OK).entity("Routes have been initialised").build();
            }else{
                return null;
                //return Response.status(Response.Status.EXPECTATION_FAILED).entity("The provided data is already in the database (same name?)").build();
            }
            
            
        //} catch (Exception ex) {
            //Logger.getLogger(RoutesResource.class.getName()).log(Level.SEVERE, null, ex);
            //return Response.status(Response.Status.SERVICE_UNAVAILABLE).entity("An error has occured").build();
        //}
      
    }

    @GET
    @Path("all")
    @Produces("application/json")
    public String getAllRoutesData() {
        //Parameters
        setBasicParameters();

        List<IRoute> routes = getRoutes("all");
        return JSONRoutes(routes).toString();
    }

    @GET
    @Path("{id}")
    @Produces("application/json")
    public String getRoutesData(@PathParam("id") String sid) {
        setBasicParameters();

        List<IRoute> routes = getRoutes(sid);

        return JSONRoutes(routes).toString();
    }

    @GET
    @Path("{id}/days")
    @Produces("application/json")
    public String getDayData(@PathParam("id") String sid) {
        setAnalysisParameters();

        List<IRoute> routes = getRoutes(sid);

        JSONArray result = JSONRoutes(routes);
        for (int i = 0; i < result.length(); i++) {
            result.getJSONObject(i).put("data", JSONDaysData(routes.get(i)));

        }

        return result.toString();
    }

    @GET
    @Path("{id}/rushhours")
    @Produces("application/json")
    public String getRushhourData(@PathParam("id") String sid) {
        setAnalysisParameters();

        List<IRoute> routes = getRoutes(sid);

        JSONArray result = JSONRoutes(routes);
        for (int i = 0; i < result.length(); i++) {
            result.getJSONObject(i).put("data", JSONRushhourData(routes.get(i)));
        }

        return result.toString();
    }

    @GET
    @Path("{id}/periodDifference/{startTimes}/{endTimes}")
    @Produces("application/json")
    public String getPeriodData(@PathParam("id") String sid, @PathParam("startTimes") String startTimes, @PathParam("endTimes") String endTimes) {
        setBasicParameters();

        setStartTimes(startTimes);
        setEndTimes(endTimes);

        setPrecision();
        
        List<IRoute> routes = getRoutes(sid);

        JSONArray result = JSONRoutes(routes);
        for (int i = 0; i < result.length(); i++) {
            result.getJSONObject(i).put("data", JSONPeriodsData(routes.get(i)));
        }
        return result.toString();
    }

    @GET
    @Path("{id}/providerDifference")
    @Produces("application/json")
    public String getProviderData(@PathParam("id") String sid) {
        setAnalysisParameters();
        
        setPrecision();

        List<IRoute> routes = getRoutes(sid);

        JSONArray result = JSONRoutes(routes);
        for (int i = 0; i < result.length(); i++) {
            result.getJSONObject(i).put("data", JSONProvidersData(routes.get(i)));
        }

        return result.toString();
    }

    //convertIDs
    private List<IRoute> getRoutes(String ids) {
        List<IRoute> result = new ArrayList<>();
        if (ids.equals("all")) {
            result = beans.getGeneralDAO().getRoutes();
        } else {
            List<Long> idslist = new ArrayList<>();
            String[] parts = ids.split(",");
            for (String s : parts) {
                System.out.println(s);
                try {
                    idslist.add(Long.parseLong(s, 10));
                } catch (NumberFormatException e) {
                    Logger.getGlobal().log(Level.WARNING, s + " could not be converted to Long");
                }
            }
            result = beans.getGeneralDAO().getRoutes(idslist);
        }

        return result;
    }

    //JSONTRANSFORMATIONS
    private JSONObject transformGeoLocation(IGeoLocation location) {
        JSONObject obj = new JSONObject();
        obj.put("name", location.getName());
        obj.put("latitude", location.getLatitude());
        obj.put("longitude", location.getLongitude());
        return obj;
    }

    private JSONArray transformGeoLocations(List<IGeoLocation> geolocations) {
        JSONArray result = new JSONArray();
        for (IGeoLocation loc : geolocations) {
            result.put(transformGeoLocation(loc));
        }
        return result;
    }

    private JSONArray JSONRoutes(List<IRoute> routes) {
        JSONArray result = new JSONArray();
        for (IRoute route : routes) {
            result.put(JSONRoute(route));
        }
        return result;
    }

    private JSONObject JSONRoute(IRoute route) {
        JSONObject obj = new JSONObject();
        obj.put("id", route.getId());
        obj.put("name", route.getName());
        List<IGeoLocation> geolocs = route.getGeolocations();
        obj.put("geolocations", transformGeoLocations(geolocs));

        obj.put("currentDuration", beans.getDataProvider().getCurrentDuration(route, providers));
        obj.put("currentVelocity", beans.getDataProvider().getCurrentVelocity(route, providers));
        
        if(optimalStartTime != null && optimalEndTime != null){
            obj.put("optimalDuration", beans.getDataProvider().getOptimalDuration(route, providers,optimalStartTime,optimalEndTime));
            obj.put("optimalVelocity", beans.getDataProvider().getOptimalVelocity(route, providers,optimalStartTime,optimalEndTime));
        }else{
            obj.put("optimalDuration", beans.getDataProvider().getOptimalDuration(route, providers));
            obj.put("optimalVelocity", beans.getDataProvider().getOptimalVelocity(route, providers));
        }

        if (avgStartTime != null && avgEndTime != null) {
            obj.put("avgDuration", beans.getDataProvider().getAvgDuration(route, providers, avgStartTime, avgEndTime));
            obj.put("avgVelocity", beans.getDataProvider().getAvgVelocity(route, providers, avgStartTime, avgEndTime));
        } else {
            obj.put("avgDuration", beans.getDataProvider().getAvgDuration(route, providers));
            obj.put("avgVelocity", beans.getDataProvider().getAvgVelocity(route, providers));
        }

        obj.put("trend", beans.getDataProvider().getTrend(route, providers));
        obj.put("recentData", JSONRecentData(route));

        obj.put("currentDelayLevel", beans.getDataProvider().getCurrentDelayLevel(route, providers));
        obj.put("avgDelayLevel",beans.getDataProvider().getAvgDelayLevel(route, providers));
        obj.put("distance", beans.getDataProvider().getDistance(route, providers));

        return obj;
    }

    private JSONObject JSONRecentData(IRoute route) {
        JSONObject result = new JSONObject();
        Map<Date, Integer> recentData = beans.getDataProvider().getRecentData(route, providers);


        result.put("duration", JSONData("recentData " + route.getId(),
                "This data are the durations over the last hour for route " + route.getId(),
                recentData));
        return result;
    }

    private JSONObject JSONData(String name, String description, Map<Date,Integer> dataMap) {
        JSONObject result = new JSONObject();
        result.put("name", name);
        result.put("description", description);
        
        JSONObject data= new JSONObject();
        List<Date> timestamps=new ArrayList<>();
        timestamps.addAll(dataMap.keySet());
        for (Date timestamp : timestamps) {
            data.put(String.valueOf(timestamp.getTime()),dataMap.get(timestamp));
        }
        
        
        result.put("data", data);
        return result;
    }
    
    private JSONObject JSONDayData(String name, String description, List<Integer> dataList, List<String> hours){
        JSONObject result = new JSONObject();
        result.put("name", name);
        result.put("description",description);
        
        JSONObject data = new JSONObject();
        for (int i = 0; i<dataList.size();i++){
            data.put(hours.get(i),dataList.get(i));
        }
        
        result.put("data",data);
        return result;
    }

    /*private JSONObject JSONDaysData(IRoute route) {
        JSONObject result = new JSONObject();
        Weekdays[] days = Weekdays.values();
        for (Weekdays day : days) {
            result.put(day.toString(), JSONDayData(route, day));
        }
        return result;
    }*/

    private JSONObject JSONDaysData(IRoute route) {
        JSONObject result = new JSONObject();
        Map<Weekdays, List<Integer>> mapDurations = null;
        Map<Weekdays, List<Integer>> mapVelocities = null;
        List<String> hours = beans.getDataProvider().getDataByDayHours();
        
        if (startTime != null && endTime != null) {
            mapDurations = beans.getDataProvider().getDataByDay(route, providers, startTime, endTime,Weekdays.MONDAY,Weekdays.TUESDAY,Weekdays.WEDNESDAY,Weekdays.THURSDAY,Weekdays.FRIDAY,Weekdays.SATERDAY,Weekdays.SUNDAY);
            mapVelocities = beans.getDataProvider().getDataVelocityByDay(route, providers, startTime, endTime,Weekdays.MONDAY,Weekdays.TUESDAY,Weekdays.WEDNESDAY,Weekdays.THURSDAY,Weekdays.FRIDAY,Weekdays.SATERDAY,Weekdays.SUNDAY);
        } else {
            mapDurations=beans.getDataProvider().getDataByDay(route,providers,Weekdays.MONDAY,Weekdays.TUESDAY,Weekdays.WEDNESDAY,Weekdays.THURSDAY,Weekdays.FRIDAY,Weekdays.SATERDAY,Weekdays.SUNDAY);
            mapVelocities=beans.getDataProvider().getDataVelocityByDay(route,providers,Weekdays.MONDAY,Weekdays.TUESDAY,Weekdays.WEDNESDAY,Weekdays.THURSDAY,Weekdays.FRIDAY,Weekdays.SATERDAY,Weekdays.SUNDAY);            
        }
        
        Set<Weekdays> days = mapDurations.keySet();
        for(Weekdays day:days){
            JSONObject dayObject = new JSONObject();
            dayObject.put("duration", JSONDayData("durations " + day.toString() + " " + route.getId(),
                "This data are the durations on a " + day.toString() + " for route " + route.getId(),
                mapDurations.get(day),hours));
            dayObject.put("velocity", JSONDayData("velocities " + day.toString() + " " + route.getId(),
                "This data are the velocities on a " + day.toString() + " for route " + route.getId(),
                mapVelocities.get(day),hours));
            
            result.put(day.toString(),dayObject);
        }
        
        return result;
    }

    //TODO
    private JSONObject JSONRushhourData(IRoute route) {
        JSONObject result = new JSONObject();
        
        List<Integer> listDurations;
        List<Integer> listVelocities;
        
        List<String> hours = beans.getDataProvider().getDataByDayHours();
        
        if (startTime != null && endTime != null) {
            listDurations = beans.getDataProvider().getDataByCombinedDay(route, providers, startTime, endTime);
            listVelocities = beans.getDataProvider().getDataVelocityByCombinedDay(route, providers, startTime, endTime);
        } else {
            listDurations = beans.getDataProvider().getDataByCombinedDay(route, providers);
            listVelocities = beans.getDataProvider().getDataVelocityByCombinedDay(route, providers);
        }

        
        result.put("duration", JSONDayData("rushhourDurations " + route.getId(),
                "This data are the durations for a workday for route " + route.getId(),
                listDurations,hours));
        result.put("velocity", JSONDayData("rushhourVelocities " + route.getId(),
                "This data are the velocities for a workday for route " + route.getId(),
                listVelocities,hours));
        return result;
    }

    private JSONArray JSONPeriodsData(IRoute route) {
        JSONArray result = new JSONArray();
        for (int i = 0; i < startTimes.size(); i++) {
            result.put(JSONPeriodData(route, i));
        }
        return result;
    }

    private JSONObject JSONPeriodData(IRoute route, int periodnumber) {
        JSONObject result = new JSONObject();
        int rank=periodnumber+1;
        result.put("period", "period" + rank);
        result.put("start", startTimes.get(periodnumber).getTime());
        result.put("end", endTimes.get(periodnumber).getTime());

        JSONObject obj = new JSONObject();
        Map<Date, Integer> mapDurations;
        Map<Date, Integer> mapVelocities;

        if(precision != -1){
            mapDurations = beans.getDataProvider().getData(route, providers, precision, startTimes.get(periodnumber), endTimes.get(periodnumber));
            mapVelocities = beans.getDataProvider().getDataVelocity(route, providers, precision, startTimes.get(periodnumber), endTimes.get(periodnumber));
        }else{
        mapDurations = beans.getDataProvider().getData(route, providers, startTimes.get(periodnumber), endTimes.get(periodnumber));
        mapVelocities = beans.getDataProvider().getDataVelocity(route, providers, startTimes.get(periodnumber), endTimes.get(periodnumber));
        }
        

        obj.put("duration", JSONData("Period" + rank + "Durations " + route.getId(),
                "This data are the durations for period " + rank + " for route " + route.getId(),
                mapDurations));
        obj.put("velocity", JSONData("Period" + rank + "Velocities " + route.getId(),
                "This data are the velocities for period " + rank + " for route " + route.getId(),
                mapVelocities));

        result.put("data", obj);
        return result;
    }

    private JSONArray JSONProvidersData(IRoute route) {
        JSONArray result = new JSONArray();
        for (String provider : providers) {
            result.put(JSONProviderData(route, provider));
        }
        return result;
    }

    private JSONObject JSONProviderData(IRoute route, String provider) {
        List<String> lprovider = new ArrayList<>();
        lprovider.add(provider);

        JSONObject result = new JSONObject();
        result.put("provider", provider);

        JSONObject obj = new JSONObject();
        Map<Date, Integer> mapDurations = null;
        Map<Date, Integer> mapVelocities = null;

        if (precision != -1){
            if (startTime != null && endTime != null){
                mapDurations= beans.getDataProvider().getData(route,lprovider,precision,startTime,endTime);
                mapVelocities=beans.getDataProvider().getDataVelocity(route,lprovider,precision, startTime, endTime);
            }else{
                mapDurations=beans.getDataProvider().getData(route, providers,precision);
                mapVelocities=beans.getDataProvider().getDataVelocity(route, providers,precision);
            }
        }
        else{
            if (startTime != null && endTime != null){
                mapDurations= beans.getDataProvider().getData(route,lprovider,startTime,endTime);
                mapVelocities=beans.getDataProvider().getDataVelocity(route,lprovider, startTime, endTime);
            }else{
                mapDurations=beans.getDataProvider().getData(route, providers);
                mapVelocities=beans.getDataProvider().getDataVelocity(route, providers);
            }
        }
        


        obj.put("duration", JSONData("providerDurations " + provider + " " + route.getId(),
                "This data are the duration provided by " + provider + " for route " + route.getId(),
                mapDurations));
        obj.put("velocity", JSONData("providerVelocities " + provider + " " + route.getId(),
                "This data are the velocities provided by " + provider + " for route " + route.getId(),
                mapVelocities));

        result.put("data", obj);
        return result;
    }

    //SET PARAMETERS
    private void setAnalysisParameters() {
        setBasicParameters();
        String start = context.getQueryParameters().getFirst("start");
        String end = context.getQueryParameters().getFirst("end");
        if (start != null && end != null) {
            setStartTime(start);
            setEndTime(end);
        } else {
            startTime = null;
            endTime = null;
        }
    }

    private void setBasicParameters() {
        setProviders();
        String avgStart = context.getQueryParameters().getFirst("avgstart");
        String avgEnd = context.getQueryParameters().getFirst("avgend");
        if (avgStart != null && avgEnd != null) {
            setAVGStartTime(avgStart);
            setAVGEndTime(avgEnd);
        } else {
            avgStartTime = null;
            avgEndTime = null;
        }
        String optimalStart = context.getQueryParameters().getFirst("optimalstart");
        String optimalEnd = context.getQueryParameters().getFirst("optimalend");
        if (optimalStart != null && optimalEnd != null) {
            setOptimalStartTime(optimalStart);
            setOptimalEndTime(optimalEnd);
        } else {
            optimalStartTime = null;
            optimalEndTime = null;
        }

    }

    private void setAVGStartTime(String startTime) {
        try {
            this.avgStartTime = new Date(Long.parseLong(startTime, 10));
        } catch (NumberFormatException e) {
            Logger.getGlobal().log(Level.WARNING, startTime + " could not be converted to Long");
        }
    }

    private void setAVGEndTime(String endTime) {
        try {
            this.avgEndTime = new Date(Long.parseLong(endTime, 10));

        } catch (NumberFormatException e) {
            Logger.getGlobal().log(Level.WARNING, endTime + " could not be converted to Long");
        }
    }
    
    private void setOptimalStartTime(String startTime) {
        try {
            this.optimalStartTime = new Date(Long.parseLong(startTime, 10));
        } catch (NumberFormatException e) {
            Logger.getGlobal().log(Level.WARNING, startTime + " could not be converted to Long");
        }
    }

    private void setOptimalEndTime(String endTime) {
        try {
            this.optimalEndTime = new Date(Long.parseLong(endTime, 10));

        } catch (NumberFormatException e) {
            Logger.getGlobal().log(Level.WARNING, endTime + " could not be converted to Long");
        }
    }

    private void setProviders() {
        /* PROVIDERS */
        String providers = context.getQueryParameters().getFirst("providers");
        if (providers != null) {
            this.providers = new ArrayList<>();
            String[] parts = providers.split(",");
            for (String s : parts) {
                this.providers.add(s);
            }
        } else {
            setAllProviders();
        }
    }

    private void setAllProviders() {
        providers = new ArrayList<>();
    }
    
    private void setNoProviders(){
        providers = new ArrayList<>();
    }
    
    private void setPrecision(){
        String precision = context.getQueryParameters().getFirst("precision");
        if(precision != null){
            this.precision=Integer.parseInt(precision);
        }else{
            this.precision=-1;
        }
    }


    private void setStartTime(String startTime) {
        try {
            this.startTime = new Date(Long.parseLong(startTime, 10));
        } catch (NumberFormatException e) {
            Logger.getGlobal().log(Level.WARNING, startTime + " could not be converted to Long");
        }
    }

    private void setEndTime(String endTime) {
        try {
            this.endTime = new Date(Long.parseLong(endTime, 10));
        } catch (NumberFormatException e) {
            Logger.getGlobal().log(Level.WARNING, endTime + " could not be converted to Long");
        }
    }

    private void setStartTimes(String startTimes) {

        this.startTimes = new ArrayList<>();
        String[] parts = startTimes.split(",");
        for (String s : parts) {
            try {
                this.startTimes.add(new Date(Long.parseLong(s, 10)));
            } catch (NumberFormatException e) {
                Logger.getGlobal().log(Level.WARNING, s + " could not be converted to Long");
            }
        }
    }

    private void setEndTimes(String endTimes) {

        this.endTimes = new ArrayList<>();
        String[] parts = endTimes.split(",");
        for (String s : parts) {
            try {
                this.endTimes.add(new Date(Long.parseLong(s, 10)));
            } catch (NumberFormatException e) {
                Logger.getGlobal().log(Level.WARNING, s + " could not be converted to Long");
            }
        }
    }

}
