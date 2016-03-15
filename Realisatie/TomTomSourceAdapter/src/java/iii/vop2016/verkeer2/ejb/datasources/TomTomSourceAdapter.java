/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package iii.vop2016.verkeer2.ejb.datasources;

import iii.vop2016.verkeer2.ejb.components.IGeoLocation;
import iii.vop2016.verkeer2.ejb.components.IRoute;
import iii.vop2016.verkeer2.ejb.components.IRouteData;
import iii.vop2016.verkeer2.ejb.components.RouteData;
import iii.vop2016.verkeer2.ejb.helper.DataAccessException;
import iii.vop2016.verkeer2.ejb.helper.URLException;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Date;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.annotation.PostConstruct;
import javax.ejb.Singleton;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 *
 * @author Simon
 */
@Singleton
public class TomTomSourceAdapter implements TomTomSourceAdapterRemote {

    private final String appCode = "rz6c5wupat8ts4wcq8yc8bwh";
    private static final String providerName = "TomTom";

    @PostConstruct
    public void init() {
        Logger.getLogger("logger").log(Level.INFO, "TomTomSourceAdapter has been initialized.");
    }

    @Override
    public IRouteData parse(IRoute route) throws URLException, DataAccessException {

        RouteData rd = null;
        try {

            //json.org.* moet geimporteerd worden
            //https://search.maven.org/#search%7Cgav%7C1%7Cg%3A%22org.json%22%20AND%20a%3A%22json%22
            //https://github.com/stleary/JSON-java
            //opletten voor decimale komma die moet vervangen worden door punt
            //mode = shortest
            //traffic = true
            List<IGeoLocation> waypoints = route.getGeolocations();
            IGeoLocation waypoint = null;
            StringBuilder builder = new StringBuilder("https://api.tomtom.com/routing/1/calculateRoute/");

            for (int i = 0; i < waypoints.size(); i++) {
                if (i != 0) {
                    builder.append(":");
                }
                waypoint = waypoints.get(i);
                builder.append(String.valueOf(waypoint.getLatitude()).replace(',', '.')).append(",").append(String.valueOf(waypoint.getLongitude()).replace(',', '.'));
            }

            builder.append("/json?routeType=shortest&traffic=true&travelMode=car&key=").append(appCode);
            //builder.append("&mode=fastest%3Bcar%3Btraffic%3Aenabled&app_id=KcOsDG6cNwwshKhALecH&app_code=K-gS30K9dbNrznv5TonvHQ&departure=now");
            //System.out.println(builder.toString());
            JSONObject json = new JSONObject(readUrl(builder.toString()));
            JSONArray routejson = json.getJSONArray("routes");
            JSONObject summary = routejson.getJSONObject(0);
            JSONObject data = summary.getJSONObject("summary");

            int seconds = data.getInt("travelTimeInSeconds");
            int distance = data.getInt("lengthInMeters");
            System.out.println(seconds + " "+ distance);
            rd = new RouteData();
            rd.setProvider(getProviderName());
            rd.setDistance(distance);
            rd.setDuration(seconds);
            rd.setRouteId(route.getId());
            rd.setTimestamp(new Date());
            //maakt nieuw Date object en initaliseert het met tijdstip van aanmaken
            // in principe kan je ook timestamp uit de json call zelf halen maar dit lijkt mij minder goed?
            //best is misschien zelfs om een timestamp mee te geven aan de methode parse(IRoute) zodat je makkelijker alle info van de
            //verschillende providers op 1 bepaalde timestamp kan vragen in je database

            //return null;

            /* 
        } catch (JSONException e) {
        e.printStackTrace();
        }
             */
        } catch (URLException e) {
            throw new URLException("Wrong URL for TomTom adapter");
        } catch (JSONException | DataAccessException e) {
            throw new DataAccessException("Cannot access data for TomTom adapter");
        }
        return rd;
    }

    private String readUrl(String urlString) throws URLException, DataAccessException {
        BufferedReader reader = null;
        try {
            URL url = new URL(urlString);
            reader = new BufferedReader(new InputStreamReader(url.openStream()));
            StringBuffer buffer = new StringBuffer();
            int read;
            char[] chars = new char[1024];
            while ((read = reader.read(chars)) != -1) {
                buffer.append(chars, 0, read);
            }
            //System.out.println(buffer.toString());
            return buffer.toString();
        } catch (MalformedURLException ex) {
            throw new URLException("Wrong URL for TomTom adapter");
        } catch (IOException e) {
            throw new DataAccessException("Cannot access data for TomTom adapter");
        } finally {
            try {
                if (reader != null) {
                    reader.close();
                }
            } catch (IOException e) {
                throw new DataAccessException("Cannot access data for TomTom adapter");
            }
        }
    }

    // Add business logic below. (Right-click in editor and choose
    // "Insert Code > Add Business Method")
    @Override
    public String getProviderName() {
        return providerName;
    }

    // Add business logic below. (Right-click in editor and choose
    // "Insert Code > Add Business Method")
}