/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package iii.vop2016.verkeer2.bean.settings;


import iii.vop2016.verkeer2.bean.auth.Login;
import iii.vop2016.verkeer2.bean.helpers.JSONMethods;
import static iii.vop2016.verkeer2.bean.settings.RouteSettings.prop;
import iii.vop2016.verkeer2.ejb.components.GeoLocation;
import iii.vop2016.verkeer2.ejb.components.IGeoLocation;
import iii.vop2016.verkeer2.ejb.components.IRoute;
import iii.vop2016.verkeer2.ejb.helper.BeanFactory;
import iii.vop2016.verkeer2.ejb.components.IThreshold;
import iii.vop2016.verkeer2.ejb.helper.VerkeerLibToJson;
import iii.vop2016.verkeer2.ejb.threshold.IThresholdManager;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.RequestScoped;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import org.json.JSONArray;
import org.json.JSONObject;


/**
 *
 * @author Mike
 */
@ManagedBean(name = "editRouteBean", eager = true)
@RequestScoped
public class EditRouteBean {
    
    @ManagedProperty(value="#{routeSettings}")
    protected RouteSettings routeSettings;
    
    protected List<String> availableObservers;
    protected String selectedObserver;
    protected String selectedDelayLevel;
    protected String newLocationName;
    protected String newLocationGeo;
    protected String addAfterGeoLocation;
    @ManagedProperty(value="#{param.geoLocationToRemove}")
    protected int geoLocationToRemove;
    
    /*
    protected long id;
    protected IRoute route;
    protected List<IThreshold> thresholds;
    */
      
    private static Map<String,Object> delayLevels;
    private static Map<String,Object> geolocations;
    
    private InitialContext ctx;
    private BeanFactory beanFactory;


    public EditRouteBean() {
        try {
            ctx = new InitialContext();
        } catch (NamingException ex) {
            Logger.getLogger(Login.class.getName()).log(Level.SEVERE, null, ex);
        }
        beanFactory = BeanFactory.getInstance(ctx, null);
        this.availableObservers = beanFactory.getThresholdHandlers();
    }
   
   /*
    ID
    */ 
    public long getId() {
        return routeSettings.id;
    }
   
    public void setId(long id) {
        routeSettings.id = id;
    }
    
    /*
    THRESHOLDS
    */
    public void setThresholds(List<IThreshold> thresholds) {
        routeSettings.thresholds = thresholds;
    }
    
    public List<IThreshold> getThresholds() {
        return routeSettings.thresholds;
    }

    /*
    ROUTES
    */
    public IRoute getRoute() {
        return routeSettings.route;
    }

    public void setRoute(IRoute route) {
        routeSettings.route = route;
    }
    
    /*
    ROUTESETTINGS (SUPER)
    */
    public RouteSettings getRouteSettings() {
        return routeSettings;
    }

    public void setRouteSettings(RouteSettings routeSettings) {
        this.routeSettings = routeSettings;
    }
    
    
    
    
    /*
    EDIT
    */
    public String submit(){
        
        //
        // ROUTE
        //
        JSONObject obj = VerkeerLibToJson.toJson(getRoute());
        String url = prop.getProperty("urlUpdateRoute");
        url = url.replaceAll("\\{apikey\\}", ""+prop.getProperty("apiKey"));
        url = url.replaceAll("\\{id\\}", ""+getRoute().getId());
        JSONMethods.postObjectToURL(url, obj, prop);
        
        //
        // THRESHOLDS
        //
        List<IThreshold> list = getThresholds();
        JSONArray obj1 = new JSONArray();
        for(int i=0; i<list.size(); i++){
            obj1.put(VerkeerLibToJson.toJson(list.get(i)));
        }
        String url1 = prop.getProperty("urlUpdateThresholds");
        url1 = url1.replaceAll("\\{apikey\\}", ""+prop.getProperty("apiKey"));
        url1 = url1.replaceAll("\\{id\\}", ""+getRoute().getId());
        JSONMethods.postArrayToURL(url1, obj1, prop);
        
        
        
        return "pretty:settings-routes-details";
    }
    
    
    
    /*
    ADD OBSERVER
    */
    
    public Map<String,Object> getDelayLevels() {
        delayLevels = new LinkedHashMap<String,Object>();
        for(int i=0; i<getThresholds().size(); i++){
            delayLevels.put(getThresholds().get(i).getDelayTriggerLevel()+" s", getThresholds().get(i).getDelayTriggerLevel()); 
        }
        return delayLevels;
    }
    
    
    public List<String> getAvailableObservers() {
        return availableObservers;
    }

    public void setAvailableObservers(List<String> availableObservers) {
        this.availableObservers = availableObservers;
    }

    public String getSelectedObserver() {
        return selectedObserver;
    }

    public void setSelectedObserver(String selectedObserver) {
        this.selectedObserver = selectedObserver;
    }

    public String getSelectedDelayLevel() {
        return selectedDelayLevel;
    }

    public void setSelectedDelayLevel(String selectedDelayLevel) {
        this.selectedDelayLevel = selectedDelayLevel;
    }
    
    public String addNewObserver(){
        IThresholdManager thmanager = beanFactory.getThresholdManager();
        List<IThreshold> thresholds = getThresholds();
        /*
        GET SELECTED THRESHOLD
        */
        IThreshold threshold = null;
        int j=0;
        while(j<thresholds.size()){
            if(thresholds.get(j).getDelayTriggerLevel() == Integer.parseInt(selectedDelayLevel)){
                threshold = thresholds.get(j);
                j = thresholds.size();
            }
            j++;
        }
        /*
        ADD OBSERVER TO THRESHOLD
        */
        List<String> observers = threshold.getObservers();
        if(observers == null)  observers = new ArrayList<>();
        observers.add(selectedObserver);
        threshold.setObservers(observers);
        /*
        UPDATE THRESHOLD
        */
        List<IThreshold> list = getThresholds();
        JSONArray obj1 = new JSONArray();
        for(int i=0; i<list.size(); i++){
            obj1.put(VerkeerLibToJson.toJson(list.get(i)));
        }
        String url1 = prop.getProperty("urlUpdateThresholds");
        url1 = url1.replaceAll("\\{apikey\\}", ""+prop.getProperty("apiKey"));
        url1 = url1.replaceAll("\\{id\\}", ""+getRoute().getId());
        JSONMethods.postArrayToURL(url1, obj1, prop);
        
        
        return "pretty:settings-routes-details";
    }
    
  

    
    /*
    ADD GEOLOCATION
    */
    
    public Map<String,Object> getGeoLocations() {
        geolocations = new LinkedHashMap<String,Object>();
        for(int i=0; i<getRoute().getGeolocations().size(); i++){
            geolocations.put(getRoute().getGeolocations().get(i).getName(), getRoute().getGeolocations().get(i).getId()); 
        }     
        return geolocations;
    }
        
    public String getNewLocationName() {
        return newLocationName;
    }

    public String getNewLocationGeo() {
        return newLocationGeo;
    }

    public void setNewLocationName(String newLocationName) {
        this.newLocationName = newLocationName;
    }

    public void setNewLocationGeo(String newLocationGeo) {
        this.newLocationGeo = newLocationGeo;
    }
    
    public String addNewGeoLocation(){
               
        String[] partsGeo = newLocationGeo.split(",");
        
        IGeoLocation location = new GeoLocation();
        location.setName(newLocationName);
        location.setLatitude(Double.parseDouble(partsGeo[0]));
        location.setLongitude(Double.parseDouble(partsGeo[1]));
        
        int k=0;
        int afterID = getRoute().getGeolocations().size()-1;
        while(k<getRoute().getGeolocations().size()){
            if(getRoute().getGeolocations().get(k).getId() == Long.parseLong(addAfterGeoLocation)){
                k = getRoute().getGeolocations().size();
                afterID = k;
            }
        }
        
        getRoute().addGeolocation(location,afterID);
        
        JSONObject obj = VerkeerLibToJson.toJson(getRoute());
        String url = prop.getProperty("urlUpdateRoute");
        url = url.replaceAll("\\{apikey\\}", ""+prop.getProperty("apiKey"));
        url = url.replaceAll("\\{id\\}", ""+getRoute().getId());
        JSONMethods.postObjectToURL(url, obj, prop);

        return "pretty:settings-routes-details";
    }

    public String getAddAfterGeoLocation() {
        return addAfterGeoLocation;
    }

    public void setAddAfterGeoLocation(String addAfterGeoLocation) {
        this.addAfterGeoLocation = addAfterGeoLocation;
    }
    
    public String removeGeoLocation() {
        IGeoLocation location = null;
        int i=0;
        while(i<getRoute().getGeolocations().size()){
            if(getRoute().getGeolocations().get(i).getId() == geoLocationToRemove){
                location = getRoute().getGeolocations().get(i);
                i = getRoute().getGeolocations().size();
            }
            i++;
        }
        getRoute().removeGeoLocation(location);
        return "pretty:index";
    }

    public void setGeoLocationToRemove(int geoLocationToRemove) {
        this.geoLocationToRemove = geoLocationToRemove;
    }
    
    public String resetObservers(){
        for(int i=0; i<getThresholds().size(); i++){
            getThresholds().get(i).setObservers(null);
        }
        List<IThreshold> list = getThresholds();
        JSONArray obj1 = new JSONArray();
        for(int i=0; i<list.size(); i++){
            obj1.put(VerkeerLibToJson.toJson(list.get(i)));
        }
        String url1 = prop.getProperty("urlUpdateThresholds");
        url1 = url1.replaceAll("\\{apikey\\}", ""+prop.getProperty("apiKey"));
        url1 = url1.replaceAll("\\{id\\}", ""+getRoute().getId());
        JSONMethods.postArrayToURL(url1, obj1, prop);
        
        return "pretty:settings-routes-details";
    }

    
    
}
