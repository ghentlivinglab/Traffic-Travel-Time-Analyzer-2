/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package iii.vop2016.verkeer2.ejb.geojson;

import iii.vop2016.verkeer2.ejb.components.GeoLocation;
import iii.vop2016.verkeer2.ejb.components.IGeoLocation;
import iii.vop2016.verkeer2.ejb.components.IRoute;
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
public class GeoJsonProvider implements GeoJsonRemote {

    @Resource
    protected SessionContext ctxs;
    protected InitialContext ctx;

    protected static final String JNDILOOKUP_PROPERTYFILE = "resources/properties/GeoJsonProvider";
    protected Properties properties;
    protected Map<String, String> extraProperties;

    @PostConstruct
    protected void init() {
        try {
            ctx = new InitialContext();
        } catch (NamingException ex) {
            Logger.getLogger(GeoJsonProvider.class.getName()).log(Level.SEVERE, null, ex);
        }

        if (properties == null) {
            properties = HelperFunctions.RetrievePropertyFile(JNDILOOKUP_PROPERTYFILE, ctx, Logger.getLogger("logger"));
        }

        fillProperties();
    }

    @Override
    public List<IGeoLocation> getGeoJson(IRoute route) {
        try {
            String connectionString = getUrl();

            Map<String, String> routeProperties = getPropertiesFromRoute(route);

            URL url = new URL(connectionString + "?" + getProperties(routeProperties) + "&" + getProperties(extraProperties));

            String resp = getResponse(url);

            JSONObject root = new JSONObject(resp);

            String geojson = getGeoJsonFromObject(root);

            List<IGeoLocation> locations = DecodeGeoJson(geojson);

            return locations;

        } catch (MalformedURLException ex) {
            Logger.getLogger(GeoJsonProvider.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(GeoJsonProvider.class.getName()).log(Level.SEVERE, null, ex);
        } catch (InvalidCoordinateException ex) {
            Logger.getLogger(GeoJsonProvider.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }

    private String getUrl() {
        return properties.getProperty("url");
    }

    private void fillProperties() {
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

}
