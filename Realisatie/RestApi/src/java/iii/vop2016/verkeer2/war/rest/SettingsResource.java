/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package iii.vop2016.verkeer2.war.rest;

import iii.vop2016.verkeer2.ejb.components.IThreshold;
import iii.vop2016.verkeer2.ejb.helper.BeanFactory;
import iii.vop2016.verkeer2.ejb.helper.HelperFunctions;
import iii.vop2016.verkeer2.ejb.properties.IProperties;
import iii.vop2016.verkeer2.ejb.threshold.IThresholdManager;
import java.io.StringReader;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.annotation.PostConstruct;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.UriInfo;
import javax.ws.rs.Produces;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PUT;
import javax.enterprise.context.RequestScoped;
import javax.json.Json;
import javax.json.JsonArray;
import javax.json.JsonArrayBuilder;
import javax.json.JsonObject;
import javax.json.JsonObjectBuilder;
import javax.json.JsonReader;
import javax.json.JsonString;
import javax.json.JsonStructure;
import javax.json.JsonValue;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.ws.rs.POST;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

/**
 * REST Web Service
 *
 * @author Gebruiker
 */
@Path("settings")
@RequestScoped
public class SettingsResource {

    @Context
    private UriInfo context;

    private InitialContext ctx;
    private static BeanFactory beans;
    private Helper helper;

    /**
     * Creates a new instance of SettingsResource
     */
    public SettingsResource() {
    }

    @PostConstruct
    private void init() {
        try {
            ctx = new InitialContext();
        } catch (NamingException ex) {
            Logger.getLogger(RoutesResource.class.getName()).log(Level.SEVERE, null, ex);
        }
        beans = BeanFactory.getInstance(ctx, null);
        helper = new Helper();
    }

    /*
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public String getAPIKey() {
        JsonObjectBuilder result = Json.createObjectBuilder();
        String keyString = beans.getAPIKeyDAO().getKey();
        //String keyString = "rootkeytest";
        //String keyString = "keynotactive";
        result.add("key", keyString);
        if (beans.getAPIKeyDAO().validate(keyString)) {
            result.add("klopt", "ja");
        } else {
            result.add("klopt", "nee");
        }
        return result.build().toString();
    }
     */
    @GET
    @Path("buffers/clear")
    @Produces(MediaType.APPLICATION_JSON)
    public Response clearBuffers() {
        if (!helper.validateAPIKey(context, beans)) {
            return Response.status(Response.Status.UNAUTHORIZED).build();
        } else {
            try {
                beans.getDataProvider().invalidateBuffers();
                beans.getTrafficDataDAO().updateBlockList();

                JsonObjectBuilder b = Json.createObjectBuilder();
                b.add("status", "done");
                return Response.status(Response.Status.OK).entity(b.build().toString()).build();
            } catch (Exception e) {
                return Response.status(Response.Status.BAD_REQUEST).build();
            }
        }
    }

    @GET
    @Path("properties")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getSettings() {
        if (!helper.validateAPIKey(context, beans)) {
            return Response.status(Response.Status.UNAUTHORIZED).build();
        } else {
            try {
                IProperties propCol = beans.getPropertiesCollection();
                List<String> propStr = propCol.getProperties();

                JsonArrayBuilder arr = Json.createArrayBuilder();

                for (String jndi : propStr) {
                    JsonObjectBuilder o = Json.createObjectBuilder();
                    o.add("jndi", jndi);

                    JsonObjectBuilder col = Json.createObjectBuilder();
                    Properties prop = HelperFunctions.RetrievePropertyFile(jndi, ctx, Logger.getGlobal());
                    for (Map.Entry<Object, Object> entry : prop.entrySet()) {
                        if (entry.getKey() instanceof String) {
                            if (!((String) entry.getKey()).equals("propertyLocation")) {
                                col.add((String) entry.getKey(), (String) entry.getValue());
                            }
                        }
                    }
                    o.add("content", col);

                    arr.add(o);
                }

                return Response.ok().entity(arr.build().toString()).build();
            } catch (Exception e) {
                return Response.status(Response.Status.SERVICE_UNAVAILABLE).build();
            }
        }
    }

    @POST
    @Path("properties")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response setSettings(String body) {
        if (!helper.validateAPIKey(context, beans)) {
            return Response.status(Response.Status.UNAUTHORIZED).build();
        } else {
            try {
                if (body == null || body.equals("")) {
                    return Response.status(Response.Status.BAD_REQUEST).build();
                }

                JsonReader reader = Json.createReader(new StringReader(body));
                JsonStructure obj = reader.read();
                if (obj.getValueType() == JsonValue.ValueType.ARRAY) {
                    JsonArray arr = (JsonArray) obj;
                    for (JsonValue val : arr) {
                        if (val.getValueType() == JsonValue.ValueType.OBJECT) {
                            if (!readAndApplyPropertiesFromJson((JsonObject) val)) {
                                return Response.status(Response.Status.BAD_REQUEST).build();
                            }
                        }
                    }
                } else if (obj.getValueType() == JsonValue.ValueType.OBJECT) {
                    if (!readAndApplyPropertiesFromJson((JsonObject) obj)) {
                        return Response.status(Response.Status.BAD_REQUEST).build();
                    }
                }

                JsonObjectBuilder o = Json.createObjectBuilder();
                o.add("Status", "Ok");
                return Response.status(Response.Status.ACCEPTED).entity(o.build().toString()).build();
            } catch (Exception e) {
                return Response.status(Response.Status.SERVICE_UNAVAILABLE).build();
            }
        }
    }

    private boolean readAndApplyPropertiesFromJson(JsonObject jsonObject) {
        if (jsonObject.containsKey("jndi") && jsonObject.containsKey("content")) {
            String jndi = jsonObject.getString("jndi");
            JsonObject obj = jsonObject.getJsonObject("content");

            if (jndi != null && obj != null) {
                Properties prop = HelperFunctions.RetrievePropertyFile(jndi, ctx, Logger.getGlobal());
                if (prop != null) {
                    //fill property file with new data
                    for (String o : obj.keySet()) {
                        Object valO = obj.get(o);
                        Object currentValue = prop.getProperty(o, "");
                        if (valO instanceof JsonString) {
                            String val = ((JsonString) valO).getString();
                            if (!currentValue.equals("")) {
                                prop.put(o, val);
                            }
                        }
                    }

                    //persist property file
                    HelperFunctions.SavePropertyFile(prop, Logger.getGlobal());

                    return true;
                }
            }
        }

        return false;
    }

}
