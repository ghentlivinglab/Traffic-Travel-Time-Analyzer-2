/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package iii.vop2016.verkeer2.war.rest;

import iii.vop2016.verkeer2.ejb.components.IGeoLocation;
import iii.vop2016.verkeer2.ejb.components.IRoute;
import iii.vop2016.verkeer2.ejb.components.IThreshold;
import iii.vop2016.verkeer2.ejb.components.Threshold;
import iii.vop2016.verkeer2.ejb.dao.IGeneralDAO;
import iii.vop2016.verkeer2.ejb.dataprovider.IDataProvider;
import iii.vop2016.verkeer2.ejb.helper.BeanFactory;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.json.Json;
import javax.json.JsonArray;
import javax.json.JsonArrayBuilder;
import javax.json.JsonObject;
import javax.json.JsonObjectBuilder;
import javax.json.JsonValue;
import org.json.JSONString;

/**
 *
 * @author tobia
 */
public class Helper {

    protected static List<IRoute> getRoutes(String ids, IGeneralDAO dao) {
        List<IRoute> result = new ArrayList<>();
        if (ids.equals("all")) {
            result = dao.getRoutes();
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
            result = dao.getRoutes(idslist);
        }

        return result;
    }

    protected static JsonObjectBuilder BuildJsonRoute(IRoute route, IDataProvider data) {
        JsonObjectBuilder obj = Json.createObjectBuilder();
        obj.add("id", route.getId());
        obj.add("name", route.getName());
        List<IGeoLocation> geolocs = route.getGeolocations();
        //obj.add("geolocations", transformGeoLocations(geolocs));

        List<String> providers = new ArrayList<>();

        obj.add("currentDuration", data.getCurrentDuration(route, providers));
        obj.add("currentVelocity", data.getCurrentVelocity(route, providers));

        obj.add("optimalDuration", data.getOptimalDuration(route, providers));
        obj.add("optimalVelocity", data.getOptimalVelocity(route, providers));

        obj.add("avgDuration", data.getAvgDuration(route, providers));
        obj.add("avgVelocity", data.getAvgVelocity(route, providers));

        obj.add("trend", data.getTrend(route, providers));
        //obj.put("recentData", JSONRecentData(route));

        obj.add("currentDelayLevel", data.getCurrentDelayLevel(route, providers));
        obj.add("avgDelayLevel", data.getAvgDelayLevel(route, providers));
        obj.add("distance", data.getDistance(route, providers));

        return obj;
    }

    protected static JsonObjectBuilder BuildJsonThreshold(IThreshold threshold) {
        JsonObjectBuilder obj = Json.createObjectBuilder();
        obj.add("id", threshold.getId() + "");
        obj.add("level", threshold.getLevel());
        obj.add("delayTrigger", threshold.getDelayTriggerLevel());
        obj.add("routeId", threshold.getRouteId() + "");

        JsonArrayBuilder obsArray = Json.createArrayBuilder();
        if (threshold.getObservers() != null) {
            for (String s : threshold.getObservers()) {
                obsArray.add(s);
            }
        }
        obj.add("handlers", obsArray);

        return obj;
    }

    static IThreshold ReadThreshold(JsonObject jsonObject) {
        IThreshold r = new Threshold();

        try {
            //parse data
            long id = Long.parseLong(jsonObject.getString("id"));
            int level = jsonObject.getInt("level");
            int delayTrigger = jsonObject.getInt("delayTrigger");
            long routeId = Long.parseLong(jsonObject.getString("routeId"));
            
            List<String> handlers = new ArrayList<>();
            JsonArray arr = jsonObject.getJsonArray("handlers");
            for(JsonValue o : arr){
                if(o.getValueType() == JsonValue.ValueType.STRING){
                    handlers.add(o.toString().replace("\"", ""));
                }
            }
            
            //fill data into new threshold obj
            r.setId(id);
            r.setDelayTriggerLevel(delayTrigger);
            r.setLevel(level);
            r.setObservers(handlers);
            r.setRouteId(routeId);

            return r;
        } catch (Exception e) {
            return null;
        }
    }
}
