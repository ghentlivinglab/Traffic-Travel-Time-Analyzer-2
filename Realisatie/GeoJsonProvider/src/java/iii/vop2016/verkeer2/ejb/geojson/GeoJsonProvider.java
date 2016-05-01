/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package iii.vop2016.verkeer2.ejb.geojson;

import iii.vop2016.verkeer2.ejb.components.GeoLocation;
import iii.vop2016.verkeer2.ejb.components.IGeoLocation;
import iii.vop2016.verkeer2.ejb.components.IRoute;
import iii.vop2016.verkeer2.ejb.dao.IGeneralDAO;
import iii.vop2016.verkeer2.ejb.helper.BeanFactory;
import iii.vop2016.verkeer2.ejb.helper.HelperFunctions;
import iii.vop2016.verkeer2.ejb.helper.InvalidCoordinateException;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import javax.ejb.SessionContext;
import javax.ejb.Singleton;
import javax.ejb.Startup;
import javax.json.Json;
import javax.json.JsonArrayBuilder;
import javax.json.JsonObjectBuilder;
import javax.json.JsonValue;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.net.ssl.HttpsURLConnection;
import org.json.JSONArray;
import org.json.JSONObject;

/**
 *
 * @author Tobias
 */
@Singleton
public class GeoJsonProvider implements GeoJsonRemote, GeoJsonLocal {

    @Resource
    protected SessionContext ctxs;
    protected InitialContext ctx;
    protected BeanFactory beans;

    protected static final String JNDILOOKUP_PROPERTYFILE = "resources/properties/GeoJsonProvider";
    protected Map<String, String> extraProperties;

    @PostConstruct
    protected void init() {
        try {
            ctx = new InitialContext();
        } catch (NamingException ex) {
            Logger.getLogger(GeoJsonProvider.class.getName()).log(Level.SEVERE, null, ex);
        }

        beans = BeanFactory.getInstance(ctx, ctxs);

        fillProperties();

        beans.getLogger().log(Level.INFO, "GeoJsonProvider has been initialized.");
    }

    private Properties getProperties() {
        return HelperFunctions.RetrievePropertyFile(JNDILOOKUP_PROPERTYFILE, ctx, Logger.getLogger("logger"));
    }

    @Override
    public List<IGeoLocation> getRoutePlotGeoLocations(IRoute route) {
        IGeneralDAO dao = beans.getGeneralDAO();
        List<IGeoLocation> locations = dao.getRouteMappingGeolocations(route);

        if (locations == null || locations.size() == 0) {
            locations = getRoutePlotGeoLocationsFromWeb(route);
            dao.setRouteMappingGeolocations(route, locations);
        }

        return locations;
    }

    private List<IGeoLocation> getRoutePlotGeoLocationsFromWeb(IRoute route) {
        try {
            String connectionString = getUrl();

            Map<String, String> routeProperties = getPropertiesFromRoute(route);

            URL url = new URL(connectionString + "?" + getProperties(routeProperties) + "&" + getProperties(extraProperties));

            String resp = getResponse(url);

            JSONObject root = new JSONObject(resp);

            String geojson = getGeoJsonFromObject(root);

            List<IGeoLocation> locations = DecodeGeoJson(geojson);

            beans.getLogger().log(Level.FINE, "GeoJson routes retrieved from web for route " + route.getName() + ".");

            return locations;

        } catch (MalformedURLException ex) {
            beans.getLogger().log(Level.SEVERE, "GeoJson routes could not be retrieved from web for route " + route.getName() + ": URL exception");
        } catch (IOException ex) {
            beans.getLogger().log(Level.SEVERE, "GeoJson routes could not be retrieved from web for route " + route.getName() + ": IO exception");
        } catch (InvalidCoordinateException ex) {
            beans.getLogger().log(Level.SEVERE, "GeoJson routes could not be retrieved from web for route " + route.getName() + ": Invalid coordinate exception");
        }
        return null;
    }

    @Override
    public String getGeoJson(Map<IRoute, List<IGeoLocation>> list, Map<IRoute, Integer> delays) {
        if (list.size() == 1) {
            Map.Entry<IRoute, List<IGeoLocation>> vals = list.entrySet().iterator().next();
            return getGeoJsonFeature(vals.getValue(), vals.getKey(), delays.get(vals.getKey())).build().toString();
        }

        JsonArrayBuilder arr = Json.createArrayBuilder();
        for (Map.Entry<IRoute, List<IGeoLocation>> val : list.entrySet()) {
            arr.add(getGeoJsonFeature(val.getValue(), val.getKey(), delays.get(val.getKey())));
        }
        return arr.build().toString();
    }

    private String getUrl() {
        return getProperties().getProperty("url");
    }

    private void fillProperties() {
        Properties properties = getProperties();
        extraProperties = new HashMap<>();
        String parser = properties.getProperty("properties");
        if (parser != null && (!parser.equals(""))) {
            String[] parserArray = parser.split(",");
            for (String prop : parserArray) {
                extraProperties.put(prop, properties.getProperty(prop));
            }
        }
    }

    private String getProperties(Map<String, String> extraProperties) {
        StringBuilder b = new StringBuilder();
        for (Map.Entry<String, String> entry : extraProperties.entrySet()) {
            b.append(entry.getKey());
            b.append("=");
            b.append(entry.getValue());
            b.append("&");
        }
        b.deleteCharAt(b.length() - 1);
        return b.toString();
    }

    private Map<String, String> getPropertiesFromRoute(IRoute route) {
        if (route.getGeolocations().size() < 2) {
            return null;
        }

        Map<String, String> map = new HashMap<>();
        map.put("origin", FormatGeoLocation(route.getStartLocation()));
        map.put("destination", FormatGeoLocation(route.getEndLocation()));

        if (route.getGeolocations().size() > 2) {
            String waypoints = "";
            for (int i = 1; i < (route.getGeolocations().size() - 1); i++) {

            }
            map.put("waypoints", waypoints);
        }
        return map;
    }

    private String FormatGeoLocation(IGeoLocation loc) {
        return loc.getLatitude() + "," + loc.getLongitude();
    }

    private String getResponse(URL url) throws IOException {
        HttpsURLConnection connection = null;
        connection = (HttpsURLConnection) url.openConnection();
        InputStream is = connection.getInputStream();
        BufferedReader rd = new BufferedReader(new InputStreamReader(is));
        StringBuilder response = new StringBuilder();
        String line;
        while ((line = rd.readLine()) != null) {
            response.append(line);
            response.append('\n');
        }
        rd.close();
        return response.toString();
    }

    private String getGeoJsonFromObject(JSONObject root) {
        if (!root.isNull("routes")) {
            JSONArray array = root.getJSONArray("routes");
            if (array != null && !array.isNull(0)) {
                JSONObject obj = (JSONObject) array.get(0);
                if (!obj.isNull("overview_polyline")) {
                    JSONObject polyline = obj.getJSONObject("overview_polyline");
                    if (polyline != null && !polyline.isNull("points")) {
                        return polyline.getString("points");
                    }
                }
            }
        }
        return null;
    }

    private List<IGeoLocation> DecodeGeoJson(String encoded) throws InvalidCoordinateException {
        List<IGeoLocation> locations = new ArrayList<IGeoLocation>();

        int index = 0;
        int order = 0;
        int lat = 0, lng = 0;

        while (index < encoded.length()) {
            int b, shift = 0, result = 0;
            do {
                b = encoded.charAt(index++) - 63;
                result |= (b & 0x1f) << shift;
                shift += 5;
            } while (b >= 0x20);
            int dlat = ((result & 1) != 0 ? ~(result >> 1) : (result >> 1));
            lat += dlat;

            shift = 0;
            result = 0;

            do {
                b = encoded.charAt(index++) - 63;
                result |= (b & 0x1f) << shift;
                shift += 5;
            } while (b >= 0x20);
            int dlng = ((result & 1) != 0 ? ~(result >> 1) : (result >> 1));
            lng += dlng;

            IGeoLocation p = new GeoLocation((double) lat / 100000, (double) lng / 100000);
            p.setSortRank(order);
            order++;

            locations.add(p);

        }
        return locations;

    }

    private JsonArrayBuilder getGeoJsonFeaturesArray(List<IGeoLocation> locations, IRoute route, int delayLevel) {
        JsonArrayBuilder b = Json.createArrayBuilder();
        b.add(getGeoJsonFeature(locations, route, delayLevel));
        return b;
    }

    private JsonObjectBuilder getGeoJsonFeature(List<IGeoLocation> l, IRoute route, int delayLevel) {
        JsonObjectBuilder b = Json.createObjectBuilder();
        b.add("type", "Feature");
        b.add("geometry", getGeoJsonGeometry(l));
        b.add("properties", getGeoJsonProperties(l, route, delayLevel));

        return b;
    }

    private JsonObjectBuilder getGeoJsonGeometry(List<IGeoLocation> l) {
        JsonObjectBuilder b = Json.createObjectBuilder();
        b.add("type", "LineString");
        b.add("coordinates", getGeoJsonLineString(l));
        return b;
    }

    private JsonObjectBuilder getGeoJsonProperties(List<IGeoLocation> l, IRoute route, int delayLevel) {
        JsonObjectBuilder b = Json.createObjectBuilder();
        b.add("id", route.getId());
        b.add("currentDelayLevel", delayLevel);
        return b;
    }

    private JsonArrayBuilder getGeoJsonLineString(List<IGeoLocation> l) {
        JsonArrayBuilder array = Json.createArrayBuilder();
        if (l.size() > 1) {
            for (IGeoLocation geoloc : l) {
                JsonArrayBuilder geoloc_array = Json.createArrayBuilder();
                geoloc_array.add(geoloc.getLongitude());
                geoloc_array.add(geoloc.getLatitude());
                array.add(geoloc_array);
            }
        }
        return array;
    }

}
