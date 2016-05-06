/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package iii.vop2016.verkeer2.ejb.helper;

import iii.vop2016.verkeer2.bean.APIKey.APIKey;
import iii.vop2016.verkeer2.bean.auth.AuthUser;
import iii.vop2016.verkeer2.ejb.components.GeoLocation;
import iii.vop2016.verkeer2.ejb.components.IGeoLocation;
import iii.vop2016.verkeer2.ejb.components.IPeriod;
import iii.vop2016.verkeer2.ejb.components.IRoute;
import iii.vop2016.verkeer2.ejb.components.IRouteData;
import iii.vop2016.verkeer2.ejb.components.IThreshold;
import iii.vop2016.verkeer2.ejb.components.Log;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.logging.Level;
import org.json.JSONArray;
import org.json.JSONObject;

/**
 *
 * @author tobia
 */
public class VerkeerLibToJson {

    public static JSONObject parseJsonAsObject(String source) {
        try {
            return new JSONObject(source);
        } catch (Exception e) {
            throw new ParserException();
        }
    }

    public static JSONArray parseJsonAsArray(String source) {
        try {
            return new JSONArray(source);
        } catch (Exception e) {
            throw new ParserException();
        }
    }

    public static JSONObject toJson(APIKey obj) {
        try {
            JSONObject o = new JSONObject();
            o.put("id", obj.getId());
            o.put("active", obj.getActive());
            o.put("key", obj.getKeyString());

            return o;
        } catch (Exception e) {

        }

        return new JSONObject();
    }

    public static APIKey fromJson(JSONObject o, APIKey obj) {
        try {
            obj.setId(o.getInt("id"));
            obj.setActive(o.getInt("active"));
            obj.setKeyString(o.getString("key"));
            return obj;
        } catch (Exception e) {
            throw new ParserException();
        }
    }

    public static JSONObject toJson(AuthUser obj) {
        try {
            JSONObject o = new JSONObject();
            o.put("id", obj.getId());
            o.put("name", obj.getName());
            o.put("password", obj.getPassword());
            o.put("username", obj.getUsername());

            return o;
        } catch (Exception e) {

        }

        return new JSONObject();
    }

    public static AuthUser fromJson(JSONObject o, AuthUser obj) {
        try {
            obj.setId(o.getInt("id"));
            obj.setName(o.getString("name"));
            obj.setPassword(o.getString("password"));
            obj.setUsername(o.getString("username"));
            return obj;
        } catch (Exception e) {
            throw new ParserException();
        }
    }

    public static JSONObject toJson(IGeoLocation obj) {
        try {
            JSONObject o = new JSONObject();
            o.put("id", obj.getId());
            o.put("latitude", obj.getLatitude());
            o.put("longitude", obj.getLongitude());
            o.put("name", obj.getName());
            o.put("sortRank", obj.getSortRank());

            return o;
        } catch (Exception e) {

        }

        return new JSONObject();
    }

    public static IGeoLocation fromJson(JSONObject o, IGeoLocation obj) {
        try {
            obj.setId(o.getLong("id"));
            obj.setLatitude(o.getDouble("latitude"));
            obj.setLongitude(o.getDouble("longitude"));
            obj.setName(o.getString("name"));
            obj.setSortRank(o.getInt("sortRank"));
            return obj;
        } catch (Exception e) {
            throw new ParserException();
        }
    }

    public static JSONObject toJson(IPeriod obj) {
        try {
            JSONObject o = new JSONObject();
            o.put("start", obj.getStart().getTime());
            o.put("end", obj.getEnd().getTime());
            o.put("name", obj.getName());

            return o;
        } catch (Exception e) {

        }

        return new JSONObject();
    }

    public static IPeriod fromJson(JSONObject o, IPeriod obj) {
        try {
            obj.setStart(new Date(o.getLong("start")));
            obj.setEnd(new Date(o.getLong("end")));
            obj.setName(o.getString("name"));
            return obj;
        } catch (Exception e) {
            throw new ParserException();
        }
    }

    public static JSONObject toJson(IRoute obj) {
        try {
            JSONObject o = new JSONObject();
            o.put("id", obj.getId());
            o.put("name", obj.getName());

            JSONArray arr = new JSONArray();
            for (IGeoLocation geo : obj.getGeolocations()) {
                arr.put(toJson(geo));
            }
            o.put("geolocations", arr);

            return o;
        } catch (Exception e) {

        }

        return new JSONObject();
    }

    public static IRoute fromJson(JSONObject o, IRoute obj) {
        try {
            obj.setId(o.getLong("id"));
            obj.setName(o.getString("name"));

            List<IGeoLocation> s = new ArrayList<>();
            JSONArray arr = o.getJSONArray("geolocations");
            for (int i = 0; i < arr.length(); i++) {
                s.add(fromJson(arr.optJSONObject(i), new GeoLocation()));
            }
            obj.setGeolocations(s);

            return obj;
        } catch (Exception e) {
            throw new ParserException();
        }
    }

    public static JSONObject toJson(IRouteData obj) {
        try {
            JSONObject o = new JSONObject();
            o.put("id", obj.getId());
            o.put("distance", obj.getDistance());
            o.put("duration", obj.getDuration());
            o.put("provider", obj.getProvider());
            o.put("routeId", obj.getRouteId());
            o.put("timestamp", obj.getTimestamp().getTime());

            return o;
        } catch (Exception e) {

        }

        return new JSONObject();
    }

    public static IRouteData fromJson(JSONObject o, IRouteData obj) {
        try {
            obj.setId(o.getLong("id"));
            obj.setDistance(o.getInt("distance"));
            obj.setDuration(o.getInt("duration"));
            obj.setProvider(o.getString("provider"));
            obj.setRouteId(o.getLong("routeId"));
            obj.setTimestamp(new Date(o.getLong("timestamp")));
            return obj;
        } catch (Exception e) {
            throw new ParserException();
        }
    }

    public static JSONObject toJson(IThreshold obj) {
        try {
            JSONObject o = new JSONObject();
            o.put("id", obj.getId());
            o.put("delayTrigger", obj.getDelayTriggerLevel());
            o.put("level", obj.getLevel());
            o.put("routeId", obj.getRouteId());

            JSONArray arr = new JSONArray();
            for (String s : obj.getObservers()) {
                arr.put(s);
            }
            o.put("handlers", arr);

            return o;
        } catch (Exception e) {

        }

        return new JSONObject();
    }

    public static IThreshold fromJson(JSONObject o, IThreshold obj) {
        try {
            obj.setId(o.getLong("id"));
            obj.setDelayTriggerLevel(o.getInt("delayTrigger"));
            obj.setLevel(o.getInt("level"));
            obj.setRouteId(o.getLong("routeId"));

            List<String> s = new ArrayList<>();
            JSONArray arr = o.getJSONArray("handlers");
            for (int i = 0; i < arr.length(); i++) {
                s.add(arr.getString(i));
            }
            obj.setObservers(s);

            return obj;
        } catch (Exception e) {
            throw new ParserException();
        }
    }

    public static JSONObject toJson(Log obj) {
        try {
            JSONObject o = new JSONObject();
            o.put("date", obj.getDate());
            o.put("level", obj.getL().getName());
            o.put("message", obj.getMessage());

            return o;
        } catch (Exception e) {

        }

        return new JSONObject();
    }

    public static Log fromJson(JSONObject o, Log obj) {
        try {
            return new Log(Level.parse(o.getString("level")), o.getString("message"), o.getLong("date"));
        } catch (Exception e) {
            throw new ParserException();
        }
    }
}
