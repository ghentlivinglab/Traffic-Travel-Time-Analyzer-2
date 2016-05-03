/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package iii.vop2016.verkeer2.ejb.datasources;

import iii.vop2016.verkeer2.ejb.components.IRoute;
import iii.vop2016.verkeer2.ejb.components.IRouteData;
import iii.vop2016.verkeer2.ejb.components.RouteData;
import iii.vop2016.verkeer2.ejb.helper.BeanFactory;
import iii.vop2016.verkeer2.ejb.helper.DataAccessException;
import iii.vop2016.verkeer2.ejb.helper.HelperFunctions;
import iii.vop2016.verkeer2.ejb.helper.URLException;
import iii.vop2016.verkeer2.ejb.properties.IProperties;
import java.sql.Timestamp;
import java.util.Date;
import java.util.Properties;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.annotation.PostConstruct;
import javax.ejb.Singleton;
import javax.naming.InitialContext;
import javax.naming.NamingException;
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
public class CoyoteSourceAdapter implements SourceAdapterLocal, SourceAdapterRemote {

    private static final String providerName = "Coyote";
    private static String login;
    private static String password;

    private String sessionId = "";
    private static long lastDownload = 0;
    private static long timeDifference = 240000; // 4 min = 4*60*1000
    private static JSONObject downloadedJSON = null;

    private InitialContext ctx;
    protected static final String JNDILOOKUP_PROPERTYFILE = "resources/properties/SourceAdaptersKeys";
    protected static final String JNDILOOKUP_PROPERTYFILE2 = "resources/properties/CoyoteMapping";

    @PostConstruct
    public void init() {
        try {
            ctx = new InitialContext();
        } catch (NamingException ex) {
            Logger.getLogger(CoyoteSourceAdapter.class.getName()).log(Level.SEVERE, null, ex);
        }

        IProperties p = BeanFactory.getInstance(ctx, null).getPropertiesCollection();
        if (p != null) {
            p.registerProperty(JNDILOOKUP_PROPERTYFILE);
            p.registerProperty(JNDILOOKUP_PROPERTYFILE2);
        }

        Logger.getLogger("logger").log(Level.INFO, providerName + "SourceAdapter has been initialized.");
    }

    private Properties getProperties() {
        return HelperFunctions.RetrievePropertyFile(JNDILOOKUP_PROPERTYFILE, ctx, Logger.getGlobal());
    }

    private Properties getMapping() {
        return HelperFunctions.RetrievePropertyFile(JNDILOOKUP_PROPERTYFILE2, ctx, Logger.getGlobal());
    }

    @Override
    public IRouteData parse(IRoute route, String sessionId) throws URLException, DataAccessException {
        Properties prop = getProperties();
        login = prop.getProperty("CoyoteUsername");
        password = prop.getProperty("CoyotePassword");

        Date current = new Date();
        long currentLong = current.getTime();

        //initalisatie nodige objecten
        RouteData rd = null;
        int distance = 0;
        int seconds = 0;

        //ik kijk na of er al een keer gedownload is
        //of of de huidige timeStamp minstens 4 minuten groter is dan lastDownload
        //anders gaat hij elke keer het volledige JSON-object ophalen wat ook mag maar dit is beter denk ik
        if(!this.sessionId.equals(sessionId)){
            try {
                download();
                this.sessionId = sessionId;
            } catch (Exception e) {
                throw new URLException("Can't connect to URL for " + providerName + " adapter");
            }
        }

        rd = new RouteData();
        String routeName = route.getName();

        //get mapped name for coyote
        Properties mapping = getMapping();
        if (mapping != null) {
            routeName = routeName.replace(" ", "_");
            routeName  = mapping.getProperty(routeName, routeName);
            routeName = routeName.replace("_", " ");
        }

        //System.out.println(routeName + " - " + geoStart + " - " + geoEnd);
        try {

            JSONObject traject = downloadedJSON.getJSONObject(routeName);

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
            throw new URLException("Can't connect to URL for " + providerName + " adapter");
        }
    }

    @Override
    public String getProviderName() {
        return providerName;
    }

    // Add business logic below. (Right-click in editor and choose
    // "Insert Code > Add Business Method")
}
