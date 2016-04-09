/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package iii.vop2016.verkeer2.ejb.datasources;

import iii.vop2016.verkeer2.ejb.components.IRoute;
import iii.vop2016.verkeer2.ejb.components.IRouteData;
import iii.vop2016.verkeer2.ejb.components.RouteData;
import iii.vop2016.verkeer2.ejb.helper.DataAccessException;
import iii.vop2016.verkeer2.ejb.helper.URLException;
import java.sql.Timestamp;
import java.util.Date;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.annotation.PostConstruct;
import javax.ejb.Singleton;
import org.json.JSONObject;
import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

/**
 *
 * @author Simon
 */
@Singleton
public class CoyoteSourceAdapter implements CoyoteSourceAdapterRemote {

    private static final String providerName = "Coyote";
    private static final String login = "110971610";
    private static final String password = "50c20b94";

    private static long lastDownload = 0;
    private static long timeDifference = 240000; // 4 min = 4*60*1000
    private static JSONObject downloadedJSON = null;

    @PostConstruct
    public void init() {
        Logger.getLogger("logger").log(Level.INFO, providerName + "SourceAdapter has been initialized.");
    }

    @Override
    public IRouteData parse(IRoute route) throws URLException, DataAccessException {
        Date current = new Date();
        long currentLong = current.getTime();

        //initalisatie nodige objecten
        RouteData rd = null;
        int distance = 0;
        int seconds = 0;

        //ik kijk na of er al een keer gedownload is
        //of of de huidige timeStamp minstens 4 minuten groter is dan lastDownload
        //anders gaat hij elke keer het volledige JSON-object ophalen wat ook mag maar dit is beter denk ik
        if (lastDownload == 0 || (currentLong - timeDifference) > lastDownload) {
            try {
                download();
            } catch (Exception e) {
                throw new URLException("Website " + providerName + " doesn't work");
            }
        }

        rd = new RouteData();
        String routeName = route.getName();

        //hoofdletter Northbound/northbound of Southbound/
        int index = routeName.indexOf(')');
        routeName = routeName.substring(0, index + 1) + " " + routeName.substring(index + 2, index + 3).toUpperCase() + routeName.substring(index + 3);

        String geoStart = route.getStartLocation().getName();
        String geoEnd = route.getEndLocation().getName();
        
        //System.out.println(routeName + " - " + geoStart + " - " + geoEnd);
        
        try {

            JSONObject traject = downloadedJSON.getJSONObject(routeName + " - " + geoStart + " - " + geoEnd);

            seconds = traject.getBigDecimal("real_time").intValue();
            //System.out.println(seconds);

            distance = traject.getInt("length");
            //System.out.println(distance);

            rd.setProvider(getProviderName());
            rd.setDistance(distance);
            rd.setDuration(seconds);
            rd.setRouteId(route.getId());
            rd.setTimestamp(new Date());
            
        } catch (Exception e) {
            throw new DataAccessException("Can't find JSON-match in " + providerName + " for this route: " + route.getName());
        }
        return rd;
    }

    private void download() throws URLException {
        try {
            Connection.Response loginForm = Jsoup.connect("https://maps.coyotesystems.com/traffic/")
                    .method(Connection.Method.GET)
                    .execute();

            Document document = Jsoup.connect("https://maps.coyotesystems.com/traffic/")
                    .data("cookieexists", "false")
                    .data("login", login)
                    .data("password", password)
                    .cookies(loginForm.cookies())
                    .post();

            Document document2 = Jsoup.connect("https://maps.coyotesystems.com/traffic/ajax/get_perturbation_list.ajax.php")
                    .method(Connection.Method.GET)
                    .cookies(loginForm.cookies())
                    .ignoreContentType(true)
                    .get();
            Element body = document2.body();
            JSONObject obj = new JSONObject(body.text());
            downloadedJSON = obj.getJSONObject("Gand");
        } catch (Exception e) {
            throw new URLException("Website " + providerName + " doesn't work");
        }
    }

    @Override
    public String getProviderName() {
        return providerName;
    }

    // Add business logic below. (Right-click in editor and choose
    // "Insert Code > Add Business Method")
}
