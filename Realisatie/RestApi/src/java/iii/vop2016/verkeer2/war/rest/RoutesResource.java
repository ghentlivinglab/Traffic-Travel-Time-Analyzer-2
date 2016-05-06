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
import java.util.GregorianCalendar;
import java.util.Set;
import javax.json.Json;
import javax.json.JsonArrayBuilder;
import javax.json.JsonObjectBuilder;
import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.core.MediaType;
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
    private Helper helper;

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
        helper = new Helper();
    }

    /**
     * Retrieves representation of an instance of
     * iii.vop2016.verkeer2.war.rest.RoutesResource
     *
     * @return an instance of java.lang.String
     */
    @POST
    @Path("init")
    //@Produces("application/xml")
    public Response initRoutes() {
        if (!helper.validateAPIKey(context, beans)) {
            return Response.status(Response.Status.UNAUTHORIZED).build();
        } else {
            try {

                IRoute r1 = initRoute("Rooigemlaan (R40) northbound", 51.056104, 3.695152, 51.066296, 3.699685, "Drongensesteenweg", "Palinghuizen");
                IRoute r2 = initRoute("Rooigemlaan (R40) southbound", 51.066296, 3.699685, 51.056011, 3.695028, "Palinghuizen", "Drongensesteenweg");

                IRoute r3 = initRoute("Gasmeterlaan (R40) eastbound", 51.066271, 3.699709, 51.067505, 3.727959, "Palinghuizen", "Neuseplein");
                IRoute r4 = initRoute("Nieuwevaart (R40) westbound", 51.067690, 3.727868, 51.066271, 3.699709, "Neuseplein", "Palinghuizen");

                IRoute r5 = initRoute("Dok-Noord (R40) southbound", 51.067137, 3.726568, 51.056536, 3.738477, "Neuseplein", "Dampoort");
                IRoute r6 = initRoute("Dok-Noord (R40) northbound", 51.057116, 3.738622, 51.067633, 3.727523, "Dampoort", "Neuseplein");

                IRoute r7 = initRoute("Heernislaan (R40) southbound", 51.056536, 3.738477, 51.038613, 3.736007, "Dampoort", "Zuidparklaan");
                IRoute r8 = initRoute("Heernislaan (R40) northbound", 51.038573, 3.736090, 51.056261, 3.739244, "Zuidparklaan", "Dampoort");

                IRoute r9 = initRoute("Martelaarslaan (R40) northbound", 51.038575, 3.735757, 51.056459, 3.694655, "Zuidparklaan", "Drongensesteenweg");
                IRoute r10 = initRoute("Martelaarslaan (R40) southbound", 51.055999, 3.695036, 51.038567, 3.736090, "Drongensesteenweg", "Zuidparklaan");

                IRoute r11 = initRoute("Blaisantvest (N430) eastbound", 51.052567, 3.699973, 51.067425, 3.727551, "Einde Were", "Neuseplein");
                IRoute r12 = initRoute("Blaisantvest (N430) westbound", 51.067500, 3.727196, 51.052629, 3.699846, "Neuseplein", "Einde Were");

                IRoute r13 = initRoute("Keizer Karelstraat northbound", 51.038381, 3.736839, 51.067327, 3.727170, "Sint-Lievenslaan", "Neuseplein");
                IRoute r14 = initRoute("Keizer Karelstraat southbound", 51.067260, 3.726990, 51.038756, 3.736237, "Neuseplein", "Sint-Lievenslaan");

                IRoute r15 = initRoute("Kennedylaan (R4) southbound", 51.193143, 3.829968, 51.073780, 3.733642, "E34", "Port Arthurlaan");
                IRoute r16 = initRoute("Kennedylaan (R4) northbound", 51.073780, 3.733642, 51.193057, 3.830188, "Port Arthurlaan", "E34");

                IRoute r17 = initRoute("Eisenhowerlaan (R4) southbound", 51.087102, 3.757119, 51.014070, 3.726539, "Kennedylaan", "E17");
                IRoute r18 = initRoute("Eisenhowerlaan (R4) northbound", 51.013386, 3.726426, 51.087582, 3.756799, "E17", "Kennedylaan");

                IRoute r19 = initRoute("Binnenring-Drongen (R4) northbound", 51.014007, 3.729114, 51.086197, 3.671618, "Sluisweg", "Industrieweg");
                IRoute r20 = initRoute("Buitenring-Drongen (R4) southbound", 51.086325, 3.671591, 51.013397, 3.726802, "Industrieweg", "Sluisweg");

                IRoute r21 = initRoute("Paryslaan (R4) northbound", 51.085744, 3.669526, 51.197610, 3.785651, "Industrieweg", "E34");
                IRoute r22 = initRoute("Paryslaan (R4) southbound", 51.197553, 3.785324, 51.086307, 3.671539, "E34", "Industrieweg");

                IRoute r23 = initRoute("Drongensesteenweg (N466) eastbound", 51.038840, 3.627002, 51.056213, 3.695414, "E40", "Rooigemlaan");
                IRoute r24 = initRoute("Drongensesteenweg (N466) westbound", 51.056213, 3.695414, 51.038840, 3.627002, "Rooigemlaan", "E40");

                IRoute r25 = initRoute("Antwerpsesteenweg (N70) westbound", 51.083877, 3.794991, 51.057178, 3.739016, "R4", "Dampoort");
                IRoute r26 = initRoute("Antwerpsesteenweg (N70) eastbound", 51.056310, 3.739370, 51.083877, 3.794991, "Dampoort", "R4");

                IRoute r27 = initRoute("B401 (northbound)", 51.022863, 3.735335, 51.048579, 3.731411, "E17", "Vlaanderenstraat");
                IRoute r28 = initRoute("B401 (southbound)", 51.048579, 3.731411, 51.023146, 3.733962, "Vlaanderenstraat", "E17");

                IRoute r29 = initRoute("Brusselsesteenweg (N9) westbound", 51.010225, 3.791267, 51.041526, 3.740892, "R4", "Scheldekaai");
                IRoute r30 = initRoute("Brusselsesteenweg (N9) eastbound", 51.041526, 3.740892, 51.010200, 3.790972, "Scheldekaai", "R4");

                IRoute r31 = initRoute("Oudenaardsesteenweg (N60) northbound", 50.980166, 3.667916, 51.010200, 3.726065, "E17", "R40");
                IRoute r32 = initRoute("Oudenaardsesteenweg (N60) southbound", 51.038648, 3.724860, 50.982372, 3.670051, "R40", "E17");

                IRoute r33 = initRoute("Brugsevaart (N9) southbound", 51.085364, 3.663483, 51.064294, 3.702872, "R4", "Gebroeders de Smetstraat");
                IRoute r34 = initRoute("Brugsevaart (N9) northbound", 51.064261, 3.702575, 51.085364, 3.663483, "Gebroeders de Smetstraat", "R4");

                return Response.status(Response.Status.OK).entity("Routes have been initialised").build();
            } catch (Exception ex) {
                Logger.getLogger(RoutesResource.class.getName()).log(Level.SEVERE, null, ex);
                return Response.status(Response.Status.SERVICE_UNAVAILABLE).entity("An error has occured").build();
            }
        }
    }

    private IRoute initRoute(String routeName, double startlat, double startlong, double endlat, double endlong, String startName, String endName) throws InvalidCoordinateException {
        IRoute r = new Route(routeName);
        //try {
        //ITrafficDataDownstreamAnalyser analyser = beans.getTrafficDataDownstreamAnalyser();

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
        } else {
            return null;
            //return Response.status(Response.Status.EXPECTATION_FAILED).entity("The provided data is already in the database (same name?)").build();
        }

        //} catch (Exception ex) {
        //Logger.getLogger(RoutesResource.class.getName()).log(Level.SEVERE, null, ex);
        //return Response.status(Response.Status.SERVICE_UNAVAILABLE).entity("An error has occured").build();
        //}
    }

    @POST
    @Path("new")
    @Consumes({MediaType.APPLICATION_JSON})
    public Response addRoute(JSONObject route) {
        if (!helper.validateAPIKey(context, beans)) {
            return Response.status(Response.Status.UNAUTHORIZED).build();
        } else {
            try {
                String name = route.getString("name");

                double startlat = route.getDouble("startlatitude");
                double startlong = route.getDouble("startlongitude");
                String startname = route.getString("startname");

                double endlat = route.getDouble("endlatitude");
                double endlong = route.getDouble("endlongitude");
                String endname = route.getString("endname");

                IRoute r = initRoute(name, startlat, startlong, endlat, endlong, startname, endname);
                return Response.status(Response.Status.OK).entity("Routes have been initialised").build();
            } catch (Exception ex) {
                Logger.getLogger(RoutesResource.class.getName()).log(Level.SEVERE, null, ex);
                return Response.status(Response.Status.SERVICE_UNAVAILABLE).entity("An error has occured").build();
            }
        }
    }

    @POST
    @Path("{id}/remove")
    public Response removeRoutes(@PathParam("id") String sid) {
        if (!helper.validateAPIKey(context, beans)) {
            return Response.status(Response.Status.UNAUTHORIZED).build();
        } else {
            try {
                List<IRoute> routes = getRoutes(sid);
                for (IRoute r : routes) {
                    beans.getGeneralDAO().removeRoute(r);
                }
                return Response.status(Response.Status.OK).entity("Route has been removed").build();
            } catch (Exception ex) {
                Logger.getLogger(RoutesResource.class.getName()).log(Level.SEVERE, null, ex);
                return Response.status(Response.Status.SERVICE_UNAVAILABLE).entity("An error has occured").build();
            }
        }
    }

    @POST
    @Path("{id}/update")
    @Consumes({MediaType.APPLICATION_JSON})
    public Response updateRoute(JSONObject jroute,
            @PathParam("id") String sid) {
        if (!helper.validateAPIKey(context, beans)) {
            return Response.status(Response.Status.UNAUTHORIZED).build();
        } else {
            try {
                List<IRoute> routes = getRoutes(sid);
                for (IRoute r : routes) {
                    r.setName(jroute.getString("name"));
                    JSONArray geolocations = jroute.getJSONArray("geolocations");
                    List<IGeoLocation> geolist = new ArrayList<>();
                    for (int i = 0; i < geolocations.length(); i++) {
                        IGeoLocation geolocation = new GeoLocation(geolocations.getJSONObject(i).getDouble("latitude"), geolocations.getJSONObject(i).getDouble("longitude"));
                        geolocation.setName(geolocations.getJSONObject(i).getString("name"));
                        geolist.add(geolocation);
                    }
                    r.setGeolocations(geolist);
                    beans.getGeneralDAO().updateRoute(r);
                }
                return Response.status(Response.Status.OK).entity("Route has been updated").build();
            } catch (Exception ex) {
                Logger.getLogger(RoutesResource.class.getName()).log(Level.SEVERE, null, ex);
                return Response.status(Response.Status.SERVICE_UNAVAILABLE).entity("An error has occured").build();
            }
        }
    }

    @GET
    @Path("all")
    @Produces("application/json")
    public Response getAllRoutesData() {
        try {
            //Parameters
            setBasicParameters();

            List<IRoute> routes = getRoutes("all");
            return Response.status(Response.Status.OK).entity(JSONRoutes(routes).toString()).build();
        } catch (NumberFormatException e) {
            return Response.status(Response.Status.BAD_REQUEST).build();
        } catch (Exception e) {
            return Response.status(Response.Status.SERVICE_UNAVAILABLE).build();
        }
    }

    @GET
    @Path("{id}")
    @Produces("application/json")
    public Response getRoutesData(@PathParam("id") String sid
    ) {
        try {
            setBasicParameters();

            List<IRoute> routes = getRoutes(sid);

            return Response.status(Response.Status.OK).entity(JSONRoutes(routes).toString()).build();
        } catch (NumberFormatException e) {
            return Response.status(Response.Status.BAD_REQUEST).build();
        } catch (Exception e) {
            return Response.status(Response.Status.SERVICE_UNAVAILABLE).build();
        }
    }

    @GET
    @Path("all/raw")
    @Produces("application/json")
    public Response getAllRoutesDataRaw() {
        return getRoutesDataRaw("all");
    }

    @GET
    @Path("{id}/raw")
    @Produces("application/json")
    public Response getRoutesDataRaw(@PathParam("id") String sid) {
        if (!helper.validateAPIKey(context, beans)) {
            return Response.status(Response.Status.UNAUTHORIZED).build();
        } else {
            try {
                setBasicParameters();
                String pageStr = context.getQueryParameters().getFirst("page");
                int page = 0;
                if (pageStr != null && (!pageStr.equals(""))) {
                    try {
                        page = Integer.parseInt(pageStr);
                    } catch (NumberFormatException e) {
                        return Response.status(Response.Status.BAD_REQUEST).build();
                    }
                }

                List<IRoute> routes = getRoutes(sid);

                JSONArray res = JSONRoutes(routes);
                for (int i = 0; i < res.length(); i++) {
                    JSONArray arr = new JSONArray(JSONRawData(routes.get(i), page).build().toString());
                    res.getJSONObject(i).put("rawdata", arr);
                }
                return Response.ok().entity(res.toString()).build();
            } catch (NumberFormatException e) {
                return Response.status(Response.Status.BAD_REQUEST).build();
            } catch (Exception e) {
                return Response.status(Response.Status.SERVICE_UNAVAILABLE).build();
            }
        }
    }

    @GET
    @Path("{id}/days")
    @Produces("application/json")
    public Response getDayData(@PathParam("id") String sid
    ) {
        try {
            setAnalysisParameters();

            List<IRoute> routes = getRoutes(sid);

            JSONArray result = JSONRoutes(routes);
            for (int i = 0; i < result.length(); i++) {
                result.getJSONObject(i).put("data", JSONDaysData(routes.get(i)));

            }

            return Response.status(Response.Status.OK).entity(result.toString()).build();
        } catch (NumberFormatException e) {
            return Response.status(Response.Status.BAD_REQUEST).build();
        } catch (Exception e) {
            return Response.status(Response.Status.SERVICE_UNAVAILABLE).build();
        }
    }

    @GET
    @Path("{id}/rushhours")
    @Produces("application/json")
    public Response getRushhourData(@PathParam("id") String sid
    ) {
        try {
            setAnalysisParameters();

            List<IRoute> routes = getRoutes(sid);

            JSONArray result = JSONRoutes(routes);
            for (int i = 0; i < result.length(); i++) {
                result.getJSONObject(i).put("data", JSONRushhourData(routes.get(i)));
            }

            return Response.status(Response.Status.OK).entity(result.toString()).build();
        } catch (NumberFormatException e) {
            return Response.status(Response.Status.BAD_REQUEST).build();
        } catch (Exception e) {
            return Response.status(Response.Status.SERVICE_UNAVAILABLE).build();
        }
    }

    @GET
    @Path("{id}/periodDifference/{startTimes}/{endTimes}")
    @Produces("application/json")
    public Response getPeriodData(@PathParam("id") String sid,
            @PathParam("startTimes") String startTimes,
            @PathParam("endTimes") String endTimes
    ) {
        try {
            setBasicParameters();

            setStartTimes(startTimes);
            setEndTimes(endTimes);

            setPrecision();

            List<IRoute> routes = getRoutes(sid);

            JSONArray result = JSONRoutes(routes);
            for (int i = 0; i < result.length(); i++) {
                result.getJSONObject(i).put("data", JSONPeriodsData(routes.get(i)));
            }
            return Response.status(Response.Status.OK).entity(result.toString()).build();
        } catch (NumberFormatException e) {
            return Response.status(Response.Status.BAD_REQUEST).build();
        } catch (Exception e) {
            return Response.status(Response.Status.SERVICE_UNAVAILABLE).build();
        }
    }

    @GET
    @Path("{id}/providerDifference")
    @Produces("application/json")
    public Response getProviderData(@PathParam("id") String sid
    ) {
        try {
            setAnalysisParameters();

            setPrecision();

            List<IRoute> routes = getRoutes(sid);

            JSONArray result = JSONRoutes(routes);
            for (int i = 0; i < result.length(); i++) {
                result.getJSONObject(i).put("data", JSONProvidersData(routes.get(i)));
            }

            return Response.status(Response.Status.OK).entity(result.toString()).build();
        } catch (NumberFormatException e) {
            return Response.status(Response.Status.BAD_REQUEST).build();
        } catch (Exception e) {
            return Response.status(Response.Status.SERVICE_UNAVAILABLE).build();
        }
    }
    //convertIDs

    private List<IRoute> getRoutes(String ids) throws NumberFormatException {
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
                    throw e;
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

        if (optimalStartTime != null && optimalEndTime != null) {
            obj.put("optimalDuration", beans.getDataProvider().getOptimalDuration(route, providers, optimalStartTime, optimalEndTime));
            obj.put("optimalVelocity", beans.getDataProvider().getOptimalVelocity(route, providers, optimalStartTime, optimalEndTime));
        } else {
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
        obj.put("avgDelayLevel", beans.getDataProvider().getAvgDelayLevel(route, providers));
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

    private JSONObject JSONData(String name, String description, Map<Date, Integer> dataMap) {
        JSONObject result = new JSONObject();
        result.put("name", name);
        result.put("description", description);

        JSONObject data = new JSONObject();
        List<Date> timestamps = new ArrayList<>();
        timestamps.addAll(dataMap.keySet());
        for (Date timestamp : timestamps) {
            data.put(String.valueOf(timestamp.getTime()), dataMap.get(timestamp));
        }

        result.put("data", data);
        return result;
    }

    private JSONObject JSONRushhourDayData(String name, String description, List<Integer> dataList, List<String> hours) {
        JSONObject result = new JSONObject();
        result.put("name", name);
        result.put("description", description);

        JSONObject data = new JSONObject();

        JSONArray hourslist = new JSONArray();
        for (String s : hours) {
            hourslist.put(s);
        }
        data.put("x-ax", hourslist);

        JSONArray rushdata = new JSONArray();
        for (Integer i : dataList) {
            rushdata.put(i);
        }
        data.put("workday", rushdata);

        result.put("data", data);
        return result;
    }

    private JSONObject JSONDayData(String name, String description, Map<Weekdays, List<Integer>> dataMap, List<String> hours) {
        JSONObject result = new JSONObject();
        result.put("name", name);
        result.put("description", description);

        JSONObject data = new JSONObject();

        JSONArray hourslist = new JSONArray();
        for (String s : hours) {
            hourslist.put(s);
        }
        data.put("x-ax", hourslist);

        Set<Weekdays> days = dataMap.keySet();
        for (Weekdays day : days) {
            List<Integer> dataList = dataMap.get(day);
            JSONArray dayData = new JSONArray();
            for (Integer i : dataList) {
                dayData.put(i);
            }
            data.put(day.toString(), dayData);
        }

        result.put("data", data);
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
            mapDurations = beans.getDataProvider().getDataByDay(route, providers, startTime, endTime, Weekdays.MONDAY, Weekdays.TUESDAY, Weekdays.WEDNESDAY, Weekdays.THURSDAY, Weekdays.FRIDAY, Weekdays.SATERDAY, Weekdays.SUNDAY);
            mapVelocities = beans.getDataProvider().getDataVelocityByDay(route, providers, startTime, endTime, Weekdays.MONDAY, Weekdays.TUESDAY, Weekdays.WEDNESDAY, Weekdays.THURSDAY, Weekdays.FRIDAY, Weekdays.SATERDAY, Weekdays.SUNDAY);
        } else {
            mapDurations = beans.getDataProvider().getDataByDay(route, providers, Weekdays.MONDAY, Weekdays.TUESDAY, Weekdays.WEDNESDAY, Weekdays.THURSDAY, Weekdays.FRIDAY, Weekdays.SATERDAY, Weekdays.SUNDAY);
            mapVelocities = beans.getDataProvider().getDataVelocityByDay(route, providers, Weekdays.MONDAY, Weekdays.TUESDAY, Weekdays.WEDNESDAY, Weekdays.THURSDAY, Weekdays.FRIDAY, Weekdays.SATERDAY, Weekdays.SUNDAY);
        }

        result.put("duration", JSONDayData("dayDataDurations " + route.getId(),
                "This data are the durations for every day of the week for route " + route.getId(),
                mapDurations, hours));
        result.put("velocity", JSONDayData("dayDataVelocities " + route.getId(),
                "This data are the velocities for every day of the week for route " + route.getId(),
                mapVelocities, hours));

        return result;
    }

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

        result.put("duration", JSONRushhourDayData("rushhourDurations " + route.getId(),
                "This data are the durations for a workday for route " + route.getId(),
                listDurations, hours));
        result.put("velocity", JSONRushhourDayData("rushhourVelocities " + route.getId(),
                "This data are the velocities for a workday for route " + route.getId(),
                listVelocities, hours));
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
        int rank = periodnumber + 1;
        result.put("period", "period" + rank);
        result.put("start", startTimes.get(periodnumber).getTime());
        result.put("end", endTimes.get(periodnumber).getTime());

        JSONObject obj = new JSONObject();
        Map<Date, Integer> mapDurations;
        Map<Date, Integer> mapVelocities;

        if (precision != -1) {
            mapDurations = beans.getDataProvider().getData(route, providers, precision, startTimes.get(periodnumber), endTimes.get(periodnumber));
            mapVelocities = beans.getDataProvider().getDataVelocity(route, providers, precision, startTimes.get(periodnumber), endTimes.get(periodnumber));
        } else {
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

        if (precision != -1) {
            if (startTime != null && endTime != null) {
                mapDurations = beans.getDataProvider().getData(route, lprovider, precision, startTime, endTime);
                mapVelocities = beans.getDataProvider().getDataVelocity(route, lprovider, precision, startTime, endTime);
            } else {
                mapDurations = beans.getDataProvider().getData(route, providers, precision);
                mapVelocities = beans.getDataProvider().getDataVelocity(route, providers, precision);
            }
        } else if (startTime != null && endTime != null) {
            mapDurations = beans.getDataProvider().getData(route, lprovider, startTime, endTime);
            mapVelocities = beans.getDataProvider().getDataVelocity(route, lprovider, startTime, endTime);
        } else {
            mapDurations = beans.getDataProvider().getData(route, providers);
            mapVelocities = beans.getDataProvider().getDataVelocity(route, providers);
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
    private void setAnalysisParameters() throws NumberFormatException {
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

    private void setBasicParameters() throws NumberFormatException {
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

    private void setAVGStartTime(String startTime) throws NumberFormatException {
        try {
            this.avgStartTime = new Date(Long.parseLong(startTime, 10));
        } catch (NumberFormatException e) {
            Logger.getGlobal().log(Level.WARNING, startTime + " could not be converted to Long");
            throw e;
        }
    }

    private void setAVGEndTime(String endTime) throws NumberFormatException {
        try {
            this.avgEndTime = new Date(Long.parseLong(endTime, 10));

        } catch (NumberFormatException e) {
            Logger.getGlobal().log(Level.WARNING, endTime + " could not be converted to Long");
            throw e;
        }
    }

    private void setOptimalStartTime(String startTime) throws NumberFormatException {
        try {
            this.optimalStartTime = new Date(Long.parseLong(startTime, 10));
        } catch (NumberFormatException e) {
            Logger.getGlobal().log(Level.WARNING, startTime + " could not be converted to Long");
            throw e;
        }
    }

    private void setOptimalEndTime(String endTime) throws NumberFormatException {
        try {
            this.optimalEndTime = new Date(Long.parseLong(endTime, 10));

        } catch (NumberFormatException e) {
            Logger.getGlobal().log(Level.WARNING, endTime + " could not be converted to Long");
            throw e;
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

    private void setNoProviders() {
        providers = new ArrayList<>();
    }

    private void setPrecision() throws NumberFormatException {
        String precision = context.getQueryParameters().getFirst("precision");
        if (precision != null) {
            this.precision = Integer.parseInt(precision);
        } else {
            this.precision = -1;
        }
    }

    private void setStartTime(String startTime) throws NumberFormatException {
        try {
            this.startTime = new Date(Long.parseLong(startTime, 10));
        } catch (NumberFormatException e) {
            Logger.getGlobal().log(Level.WARNING, startTime + " could not be converted to Long");
            throw e;
        }
    }

    private void setEndTime(String endTime) throws NumberFormatException {
        try {
            this.endTime = new Date(Long.parseLong(endTime, 10));
        } catch (NumberFormatException e) {
            Logger.getGlobal().log(Level.WARNING, endTime + " could not be converted to Long");
            throw e;
        }
    }

    private void setStartTimes(String startTimes) throws NumberFormatException {

        this.startTimes = new ArrayList<>();
        String[] parts = startTimes.split(",");
        for (String s : parts) {
            try {
                Date start = new Date(Long.parseLong(s, 10));
                Calendar cal = new GregorianCalendar();
                cal.setTime(start);
                cal.set(GregorianCalendar.SECOND, 0);
                cal.set(GregorianCalendar.MINUTE, 0);
                cal.set(GregorianCalendar.HOUR_OF_DAY, 0);
                start = cal.getTime();
                this.startTimes.add(start);
            } catch (NumberFormatException e) {
                Logger.getGlobal().log(Level.WARNING, s + " could not be converted to Long");
                throw e;
            }
        }
    }

    private void setEndTimes(String endTimes) throws NumberFormatException {

        this.endTimes = new ArrayList<>();
        String[] parts = endTimes.split(",");
        for (String s : parts) {
            try {
                Date end = new Date(Long.parseLong(s, 10));
                Calendar cal = new GregorianCalendar();
                cal.setTime(end);
                cal.set(GregorianCalendar.SECOND, 59);
                cal.set(GregorianCalendar.MINUTE, 59);
                cal.set(GregorianCalendar.HOUR_OF_DAY, 23);
                end = cal.getTime();
                this.endTimes.add(end);
            } catch (NumberFormatException e) {
                Logger.getGlobal().log(Level.WARNING, s + " could not be converted to Long");
                throw e;
            }
        }
    }

    private JsonArrayBuilder JSONRawData(IRoute route, int page) {
        List<IRouteData> res = null;

        if (startTime != null && endTime != null) {
            res = beans.getTrafficDataDAO().getRawData(route, startTime, endTime, providers, page);
        } else if (startTime != null) {
            res = beans.getTrafficDataDAO().getRawData(route, startTime, new Date(), providers, page);
        } else if (endTime != null) {
            res = beans.getTrafficDataDAO().getRawData(route, new Date(0), endTime, providers, page);
        } else {
            res = beans.getTrafficDataDAO().getRawData(route, new Date(0), new Date(), providers, page);
        }

        JsonArrayBuilder list = Json.createArrayBuilder();
        for (IRouteData s : res) {
            JsonObjectBuilder o = Json.createObjectBuilder();
            o.add("Timestamp", s.getTimestamp().toString());
            o.add("Distance", s.getDistance());
            o.add("Duration", s.getDuration());
            o.add("Id", s.getId());
            o.add("Provider", s.getProvider());
            o.add("RouteId", s.getRouteId());
            list.add(o);
        }

        return list;
    }

}
