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

        if(properties == null)
            properties = HelperFunctions.RetrievePropertyFile(JNDILOOKUP_PROPERTYFILE, ctx, Logger.getLogger("logger"));

        fillProperties();
    }

    @Override
    public void getGeoJson(IRoute route) {
        try {
            HttpURLConnection connection = null;

            String connectionString = getUrl();

            Map<String, String> routeProperties = getPropertiesFromRoute(route);

            URL url = new URL(connectionString + "?" + getProperties(routeProperties) + getProperties(extraProperties));

        } catch (MalformedURLException ex) {
            Logger.getLogger(GeoJsonProvider.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private String getUrl() {
        return properties.getProperty("url");
    }

    private void fillProperties() {
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
        return loc.getLatitude()+","+loc.getLongitude();
    }

}
