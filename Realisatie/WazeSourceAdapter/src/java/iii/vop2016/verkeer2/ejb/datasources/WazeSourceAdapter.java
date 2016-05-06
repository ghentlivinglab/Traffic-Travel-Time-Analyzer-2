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
import java.io.IOException;
import java.io.StringReader;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.annotation.PostConstruct;
import javax.ejb.Singleton;
import javax.json.Json;
import javax.json.JsonArray;
import javax.json.JsonObject;
import javax.json.JsonValue;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import org.json.JSONObject;
import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

/**
 *
 * @author tobia
 */
@Singleton
public class WazeSourceAdapter implements SourceAdapterRemote, SourceAdapterLocal {

    private InitialContext ctx;
    private String sessionID = "";
    private JsonObject data;
    protected static final String JNDILOOKUP_PROPERTYFILE = "resources/properties/SourceAdaptersKeys";
    protected static final String JNDILOOKUP_PROPERTYFILE2 = "resources/properties/WazeMapping";
    private String providerName = "Waze";
    private String url = "https://www.waze.com/trafficview";
    private String jsonUrl = "https://www.waze.com/row-rtserver/broadcast/BroadcastRSS?bid=147&format=JSON";

    @PostConstruct
    public void init() {
        try {
            ctx = new InitialContext();
        } catch (NamingException ex) {
            Logger.getLogger(WazeSourceAdapter.class.getName()).log(Level.SEVERE, null, ex);
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

    private Properties getMappings() {
        return HelperFunctions.RetrievePropertyFile(JNDILOOKUP_PROPERTYFILE2, ctx, Logger.getGlobal());
    }

    @Override
    public IRouteData parse(IRoute route, String sessionID) throws URLException, DataAccessException {
        Properties prop = getProperties();
        //download new data only in new session
        if (!this.sessionID.equals(sessionID)) {
            try {
                data = downloadNewData(prop);
                this.sessionID = sessionID;
            } catch (Exception ex) {
                throw new DataAccessException("Can't find JSON-match in " + providerName + " for this route: " + route.getName());
            }
        }

        Properties mapping = getMappings();
        String routeName = route.getName().replace(" ", "_");
        routeName = mapping.getProperty(routeName, routeName).replace("_", " ");

        if (!routeName.equals("")) {
            return parseJson(data, route, routeName);
        }
        throw new DataAccessException("Can't find mapping to JSON-match in " + providerName + " for this route: " + route.getName());

    }

    @Override
    public String getProviderName() {
        return providerName;
    }

    private JsonObject downloadNewData(Properties prop) throws IOException {
        String login = prop.getProperty("WazeUsername");
        String password = prop.getProperty("WazePassword");

        Connection.Response loginForm = Jsoup.connect(url)
                .method(Connection.Method.GET)
                .execute();

        /*Document document0 = Jsoup.connect(" https://www.waze.com/login/create")
                .cookies(loginForm.cookies())
                .data("{\"reply\":{\"error\":0,\"message\":\"VerkeerGent\",\"user_id\":132039670,\"rank\":0,\"full_name\":\"\",\"login\":true}}")
                .post();

        Document document = Jsoup.connect(url)
                .data("username", login)
                .data("password", password)
                .cookies(loginForm.cookies())
                .post();*/
        Document document2 = Jsoup.connect(jsonUrl)
                .method(Connection.Method.GET)
                .cookies(loginForm.cookies())
                .ignoreContentType(true)
                .get();
        Element body = document2.body();
        return Json.createReader(new StringReader(body.text())).readObject();

    }

    private IRouteData parseJson(JsonObject data, IRoute route, String routeName) throws DataAccessException {
        JsonArray arr = data.getJsonArray("routes");
        for (JsonValue val : arr) {
            if (val.getValueType() == JsonValue.ValueType.OBJECT) {
                JsonObject o = (JsonObject) val;
                if (o.getString("name", "").equals(routeName)) {
                    IRouteData d = new RouteData();
                    d.setDistance(o.getInt("length"));
                    d.setDuration(o.getInt("time"));
                    d.setProvider(providerName);
                    d.setRouteId(route.getId());
                    return d;
                }
            }
        }
        throw new DataAccessException("Can't find mapping to JSON-match in " + providerName + " for this route: " + route.getName());
    }

}
