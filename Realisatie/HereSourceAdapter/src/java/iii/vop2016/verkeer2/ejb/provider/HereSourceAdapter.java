/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package iii.vop2016.verkeer2.ejb.provider;

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
import java.util.Iterator;
import java.util.Set;
import javax.ejb.Singleton;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 *
 * @author Simon
 */
@Singleton
public class HereSourceAdapter implements HereSourceAdapterRemote {

    private final String appId = "KcOsDG6cNwwshKhALecH";
    private final String appCode = "K-gS30K9dbNrznv5TonvHQ";

    @Override
    public IRouteData parse(IRoute route) throws URLException,DataAccessException {

        RouteData rd = null;
        try {

            //json.org.* moet geimporteerd worden
            //https://search.maven.org/#search%7Cgav%7C1%7Cg%3A%22org.json%22%20AND%20a%3A%22json%22
            //https://github.com/stleary/JSON-java
            //opletten voor decimale komma die moet vervangen worden door punt
            //AppId = KcOsDG6cNwwshKhALecH
            //AppCode = K-gS30K9dbNrznv5TonvHQ
            //mode = fastest
            //traffic = enabled
            Set<IGeoLocation> waypoints = route.getGeolocations();
            IGeoLocation waypoint = null;
            StringBuilder builder = new StringBuilder("https://route.cit.api.here.com/routing/7.2/calculateroute.json?");
            
            int i = 0;
            for (Iterator<IGeoLocation> it = waypoints.iterator(); it.hasNext();) {
                waypoint = it.next();
                if (i != 0) {
                    builder.append("&");
                }
                builder.append("waypoint").append(i).append("=").append(String.valueOf(waypoint.getLongitude()).replace(',', '.')).append(",").append(String.valueOf(waypoint.getLongitude()).replace(',', '.'));
            }

            builder.append("&mode=fastest%3Bcar%3Btraffic%3Aenabled&app_id=").append(appId).append("&app_code=").append(appCode).append("&departure=now");
            //builder.append("&mode=fastest%3Bcar%3Btraffic%3Aenabled&app_id=KcOsDG6cNwwshKhALecH&app_code=K-gS30K9dbNrznv5TonvHQ&departure=now");

            JSONObject json = new JSONObject(readUrl(builder.toString()));
            JSONObject resp = (JSONObject) json.get("response");
            JSONArray routear = (JSONArray) resp.get("route");
            JSONObject routeob = (JSONObject) routear.getJSONObject(0);
            JSONObject summary = (JSONObject) routeob.get("summary");

            int seconds = (int) summary.get("trafficTime");
            int distance = (int) summary.get("distance");

            rd = new RouteData();
            rd.setDistance(distance);
            rd.setDuration(seconds);
            rd.setRoute(route);
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
            throw new URLException("Wrong URL for Here adapter");
        } catch(JSONException e) {
            throw new DataAccessException("Cannot access data for Here adapter");
        }
        return rd;
    }

    private String readUrl(String urlString) throws URLException {
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
            System.out.println(buffer.toString());
            return buffer.toString();
        } catch (IOException e) {
            throw new URLException();
        } finally {
            try {
                if (reader != null) {
                    reader.close();
                }
            } catch (IOException e) {
                throw new URLException("Wrong URL for Here adapter");
            }
        }
    }

    // Add business logic below. (Right-click in editor and choose
    // "Insert Code > Add Business Method")
}
