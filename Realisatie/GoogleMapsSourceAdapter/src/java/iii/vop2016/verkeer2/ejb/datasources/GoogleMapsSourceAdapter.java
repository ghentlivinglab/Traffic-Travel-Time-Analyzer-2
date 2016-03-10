/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package iii.vop2016.verkeer2.ejb.datasources;

import iii.vop2016.verkeer2.ejb.datasources.GoogleMapsSourceAdapterRemote;
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
import java.net.URL;
import java.util.Date;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.annotation.PostConstruct;
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
    private static final String providerName = "GoogleMaps";

    @PostConstruct
    public void init(){
        Logger.getLogger("logger").log(Level.INFO, "GoogleMapsSourceAdaptor has been initialized.");  
    }

    @Override
    public IRouteData parse(IRoute route) throws URLException, DataAccessException{

        RouteData rd = null;

        int duration=0;
        int distance=0;
        
        String URL;
        
        List<IGeoLocation> geoLocations= route.getGeolocations();
        
        //every call is divided in several parts with each just one start and one endpoint to reduce the number of call to the API
        for (int i=0;i<geoLocations.size()-1;i++){
            
            URL =createURL(geoLocations.get(i),geoLocations.get(i+1));
            try{
                URL obj = new URL(URL);
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
                        for (int j = 0; j < rows.length(); j++) {
                        JSONArray elements = rows.getJSONObject(j).getJSONArray("elements"); //Gets an array with all the 'elements' of the JSON, one 'element' for each destination
                    
                        //indien er geen resultaten zijn zal je als resultaat status: ZERO RESULTS krijgen in de elements array
                        JSONObject status1 = elements.getJSONObject(0);
                        String status = (String) status1.getString("status");
                        if(status.equalsIgnoreCase("ZERO_RESULTS")){
                            throw new DataAccessException("Cannot access data from Google Maps adapter");
                        }
                    
                        for (int k = 0; k < elements.length(); k++) {

                            //The API returns the distances and durations in a matrix (of origins and distinations)
                            //because of the way the URL is constructed, the only interesting data for this app are on the diagonal of the matrix
                            //the durations and distances are cumulated to get the total distance and duration from start- to endpoint
                            if (j == k) {
                                duration = duration + elements.getJSONObject(k).getJSONObject("duration_in_traffic").getInt("value");
                                distance = distance + elements.getJSONObject(k).getJSONObject("distance").getInt("value");
                            }
                        }
                    }
                } else {
                    throw new DataAccessException("Cannot access data from Google Maps adapter");
            }
            } 
            catch (IOException ex) {
                throw new URLException("Wrong URL for Google Maps adapter");
            }
            
        }

        rd = new RouteData();
        rd.setProvider(getProviderName());
        rd.setDistance(distance);
        rd.setDuration(duration);
        rd.setRouteId(route.getId());
        rd.setTimestamp(new Date());
        
        return rd;


    }
   
    //Method to create the correct Google API URL, based on a start an end location
    private String createURL(IGeoLocation start, IGeoLocation end){
        StringBuilder sb= new StringBuilder(basicURL);
        sb.append("origins=");
        sb.append(start.getLatitude());
        sb.append(",");
        sb.append(start.getLongitude());
       
        sb.append("&destinations=");
        sb.append(end.getLatitude());
        sb.append(",");
        sb.append(end.getLongitude());
        
        sb.append("&traffic_model&departure_time=now");
        sb.append("&key=");
        
        sb.append(key);

        return sb.toString();
    }

    // Add business logic below. (Right-click in editor and choose
    // "Insert Code > Add Business Method")

    @Override
    public String getProviderName() {
        return providerName;
    }
}
