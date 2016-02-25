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
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Date;
import java.util.List;
import java.util.concurrent.Future;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.ejb.Singleton;
import org.json.JSONArray;
import org.json.JSONObject;

/**
 *
 * @author tobia
 */
@Singleton
public class GoogleMapsSourceAdapter implements GoogleMapsSourceAdapterRemote {

    //This final variables may be better in resourcefile?
    //Free key for the Google API, connected to the project. Limited usage.
    private final String key = "AIzaSyDCx8SzAp2pjZHacrgJ9DDcC45UdGR_yQw";
    private final String basicURL = "https://maps.googleapis.com/maps/api/distancematrix/json?";

    @Override
    public IRouteData parse(IRoute route) throws URLException, DataAccessException{

        int duration = 0;
        int distance = 0;

        List<IGeoLocation> geoLocations = route.getGeolocations();
        String URL = createURL(geoLocations);

        RouteData rd = null;

        try {
            URL obj = new URL(URL);
            //try {
            HttpURLConnection con = (HttpURLConnection) obj.openConnection();
            //Optional, GET is default
            con.setRequestMethod("GET");

            //This sections puts the HttpAnswer in a String
            BufferedReader in = new BufferedReader(
                    new InputStreamReader(con.getInputStream()));
            String inputLine;
            StringBuffer response = new StringBuffer();

            while ((inputLine = in.readLine()) != null) {
                response.append(inputLine);
            }
            in.close();

            //This section interpretes the JSON
            JSONObject jsonobj = new JSONObject(response.toString());
            //First check if statuscode of JSON is OK
            if (jsonobj.getString("status").equalsIgnoreCase("Ok")) {
                JSONArray rows = jsonobj.getJSONArray("rows"); //Gets an array with all the 'rows' of the JSON,a row has one or more 'elements', one row for each origin
                for (int i = 0; i < rows.length(); i++) {
                    JSONArray elements = rows.getJSONObject(i).getJSONArray("elements"); //Gets an array with all the 'elements' of the JSON, one 'element' for each destination
                    for (int j = 0; j < elements.length(); j++) {

                        //The API returns the distances and durations in a matrix (of origins and distinations)
                        //because of the way the URL is constructed, the only interesting data for this app are on the diagonal of the matrix
                        //the durations and distances are cumulated to get the total distance and duration from start- to endpoint
                        if (i == j) {
                            duration = duration + elements.getJSONObject(j).getJSONObject("duration").getInt("value");
                            distance = distance + elements.getJSONObject(j).getJSONObject("distance").getInt("value");
                        }
                    }
                }
            } else {
                throw new DataAccessException("Cannot access data from Google Maps adapter");
            }
            rd = new RouteData();
            rd.setDistance(distance);
            rd.setDuration(duration);
            rd.setRoute(route);
            rd.setTimestamp(new Date());

            /*  } catch (IOException ex) {
                Logger.getLogger(GoogleMapsSourceAdapter.class.getName()).log(Level.SEVERE, null, ex);
            }*/
        } catch (IOException ex) {
            throw new URLException("Wrong URL for Google Maps adapter");
        }

        //TODO
        return rd;

        //make IRouteDataobject and fill with distance, duration, timestamp and route
        //where must this class be instantiated
        //what to do when status is not ok?
        //return null;
    }

    //Method to create the correct Google API URL, based on all the geoLocations
    private String createURL(List<IGeoLocation> geoLocations) {
        StringBuilder sb = new StringBuilder(basicURL);
        sb.append("origins=");
        //Loop to put all the origins in the URL = all the geoLocations, except for the last one
        for (int i = 0; i < geoLocations.size() - 1; i++) {
            sb.append(geoLocations.get(i).getLatitude());
            sb.append(",");
            sb.append(geoLocations.get(i).getLongitude());
            sb.append("|");
        }
        sb.deleteCharAt(sb.length() - 1); //Delete the last '|' sign
        sb.append("&destinations=");
        //Loop to put all the destinations in the URL = all the geoLocations, except for the first one
        for (int i = 1; i < geoLocations.size(); i++) {
            sb.append(geoLocations.get(i).getLatitude());
            sb.append(",");
            sb.append(geoLocations.get(i).getLongitude());
            sb.append("|");
        }
        sb.deleteCharAt(sb.length() - 1); //Delete the last '|' sign
        sb.append("&key=");
        sb.append(key);

        return sb.toString();
    }

    // Add business logic below. (Right-click in editor and choose
    // "Insert Code > Add Business Method")
}
