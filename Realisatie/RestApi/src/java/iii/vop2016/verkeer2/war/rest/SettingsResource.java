/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package iii.vop2016.verkeer2.war.rest;

import iii.vop2016.verkeer2.bean.APIKey.APIKey;
import iii.vop2016.verkeer2.bean.auth.AuthUser;
import iii.vop2016.verkeer2.ejb.helper.BeanFactory;
import iii.vop2016.verkeer2.ejb.helper.HelperFunctions;
import iii.vop2016.verkeer2.ejb.helper.VerkeerLibToJson;
import iii.vop2016.verkeer2.ejb.properties.IProperties;
import java.io.StringReader;
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
import javax.enterprise.context.RequestScoped;
import javax.json.Json;
import javax.json.JsonObjectBuilder;
import javax.json.JsonReader;
import javax.json.JsonStructure;
import javax.json.JsonValue;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.ws.rs.POST;
import javax.ws.rs.PathParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import org.json.JSONArray;
import org.json.JSONObject;

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

    @GET
    @Path("keys/generate")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getAPIKey() {
        if (!helper.validateAPIKey(context, beans)) {
            return Response.status(Response.Status.UNAUTHORIZED).build();
        } else {
            APIKey keyString = beans.getAPIKeyDAO().getKey();
            if (!beans.getAPIKeyDAO().validate(keyString.getKeyString())) {
                return Response.status(Response.Status.BAD_REQUEST).build();
            }
            return Response.ok().entity(VerkeerLibToJson.toJson(keyString).toString()).build();
        }
    }

    @POST
    @Path("keys/invalidate")
    @Produces(MediaType.APPLICATION_JSON)
    public Response invalidateAPIKey(String body) {
        if (!helper.validateAPIKey(context, beans)) {
            return Response.status(Response.Status.UNAUTHORIZED).build();
        } else {
            try {
                JSONObject o = VerkeerLibToJson.parseJsonAsObject(body);
                APIKey key = VerkeerLibToJson.fromJson(o, new APIKey());
                beans.getAPIKeyDAO().deactivateKey(key.getKeyString());

                JsonObjectBuilder b = Json.createObjectBuilder();
                b.add("status", "done");
                return Response.status(Response.Status.OK).entity(b.build().toString()).build();
            } catch (Exception e) {
                return Response.status(Response.Status.BAD_REQUEST).build();
            }
        }
    }
    
    @GET
    @Path ("users/{id}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getUser(@PathParam("id") String sid){
        if (!helper.validateAPIKey(context, beans)) {
            return Response.status(Response.Status.UNAUTHORIZED).build();
        } else {
            try {
                int id = Integer.parseInt(sid);
                AuthUser user = beans.getLoginDAO().getUser(id);
                if (user == null){
                    return Response.status(Response.Status.BAD_REQUEST).build();
                }
                JSONObject o = VerkeerLibToJson.toJson(user);
                o.remove("password");
                return Response.status(Response.Status.OK).entity(o.toString()).build();
            } catch (Exception e) {
                Logger.getLogger(SettingsResource.class.getName()).log(Level.SEVERE, null, e);
                return Response.status(Response.Status.BAD_REQUEST).build();
            }
        }
    }
    
    @POST
    @Path("users/update/{id}")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response updateUser(String body,@PathParam("id") String sid) {
        if (!helper.validateAPIKey(context, beans)) {
            return Response.status(Response.Status.UNAUTHORIZED).build();
        } else {
            try {
                JSONObject o = VerkeerLibToJson.parseJsonAsObject(body);
                AuthUser user = VerkeerLibToJson.fromJson(o, new AuthUser());
                beans.getLoginDAO().updateUser(user);

                JsonObjectBuilder b = Json.createObjectBuilder();
                b.add("status", "done");
                return Response.status(Response.Status.OK).entity(b.build().toString()).build();
            } catch (Exception e) {
                return Response.status(Response.Status.BAD_REQUEST).build();
            }
        }
    }

    @GET
    @Path("keys")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getAPIKeys() {
        if (!helper.validateAPIKey(context, beans)) {
            return Response.status(Response.Status.UNAUTHORIZED).build();
        } else {
            try {
                JSONArray arr = new JSONArray();

                List<APIKey> keys = beans.getAPIKeyDAO().getAllKeys();
                for (APIKey key : keys) {
                    JSONObject o = VerkeerLibToJson.toJson(key);
                    arr.put(o);
                }
                return Response.status(Response.Status.OK).entity(arr.toString()).build();
            } catch (Exception e) {
                return Response.status(Response.Status.BAD_REQUEST).build();
            }
        }
    }

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

                JSONArray arr = new JSONArray();
                for (String jndi : propStr) {
                    Properties prop = HelperFunctions.RetrievePropertyFile(jndi, ctx, Logger.getGlobal());
                    JSONObject o = VerkeerLibToJson.toJson(prop, jndi);
                    arr.put(o);
                }

                return Response.ok().entity(arr.toString()).build();
            } catch (Exception e) {
                return Response.status(Response.Status.BAD_REQUEST).build();
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
                    JSONArray arr = VerkeerLibToJson.parseJsonAsArray(body);
                    for (Object p : arr) {
                        String jndi = ((JSONObject) p).getString("jndi");
                        Properties prop = VerkeerLibToJson.fromJson((JSONObject) p, new Properties());
                        applyPropertiesFromJson(prop, jndi);
                    }
                } else if (obj.getValueType() == JsonValue.ValueType.OBJECT) {
                    JSONObject o = VerkeerLibToJson.parseJsonAsObject(body);
                    String jndi = o.getString("jndi");
                    Properties prop = VerkeerLibToJson.fromJson(o, new Properties());
                    applyPropertiesFromJson(prop, jndi);
                }

                JsonObjectBuilder o = Json.createObjectBuilder();
                o.add("Status", "Ok");
                return Response.status(Response.Status.ACCEPTED).entity(o.build().toString()).build();
            } catch (Exception e) {
                return Response.status(Response.Status.BAD_REQUEST).build();
            }
        }
    }

    private boolean applyPropertiesFromJson(Properties newprop, String jndi) {

        Properties prop = HelperFunctions.RetrievePropertyFile(jndi, ctx, Logger.getGlobal());
        if (prop != null) {
            //fill property file with new data
            for (Map.Entry<Object, Object> entry : newprop.entrySet()) {
                prop.setProperty((String) entry.getKey(), (String) entry.getValue());
            }
            //persist property file
            HelperFunctions.SavePropertyFile(prop, Logger.getGlobal());

            return true;
        }

        return false;
    }

}
