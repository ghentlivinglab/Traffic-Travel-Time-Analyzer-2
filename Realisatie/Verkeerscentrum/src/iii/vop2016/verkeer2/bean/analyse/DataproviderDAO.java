/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package iii.vop2016.verkeer2.bean.analyse;

import iii.vop2016.verkeer2.bean.components.DataProvider;
import iii.vop2016.verkeer2.bean.components.Route;
import iii.vop2016.verkeer2.bean.helpers.JSONMethods;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.RequestScoped;
import javax.faces.context.FacesContext;
import javax.servlet.http.HttpServletRequest;
import org.json.JSONArray;
import org.json.JSONObject;

/**
 *
 * @author Mike
 */
@ManagedBean
@RequestScoped
public class DataproviderDAO {

    protected List<DataProvider> availableProviders;
    protected List<DataProvider> selectedProviders;
    
    private static String urlAllProviders = "http://localhost:8080/RestApi/v2/providers";

    
    /**
     * Creates a new instance of DataproviderDAO
     */
    public DataproviderDAO() {
        availableProviders = new ArrayList<>();
        selectedProviders = new ArrayList<>();
        
        
        // AVAILABLE ROUTES
        JSONArray providers = JSONMethods.getArrayFromURL(urlAllProviders);
        for(int i=0; i<providers.length(); i++){
            availableProviders.add(new DataProvider(providers.getString(i)));
        }
        
        HttpServletRequest request = (HttpServletRequest) FacesContext.getCurrentInstance().getExternalContext().getRequest();        
        Map<String, String[]> parameterMap = request.getParameterMap();
        String[] providersArray = parameterMap.get("provider");
        
        if(providersArray != null){
            for(String provider : providersArray){
                selectedProviders.add(getDataProvider(provider));
            }
        }
        
    }

    public List<DataProvider> getAvailableProviders() {
        return availableProviders;
    }

    public List<DataProvider> getSelectedProviders() {
        return selectedProviders;
    }

    
    private DataProvider getDataProvider(String s) {
        int i=0;
        while(i < availableProviders.size()){
            if(availableProviders.get(i).getName().equals(s)){
                return availableProviders.get(i);
            }
            i++;
        }
        return null;
    }
    
}
