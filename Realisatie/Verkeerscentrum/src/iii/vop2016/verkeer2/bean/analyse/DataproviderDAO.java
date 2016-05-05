/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package iii.vop2016.verkeer2.bean.analyse;

import static iii.vop2016.verkeer2.bean.analyse.RouteDAO.prop;
import iii.vop2016.verkeer2.bean.components.DataProvider;
import iii.vop2016.verkeer2.bean.helpers.JSONMethods;
import iii.vop2016.verkeer2.ejb.helper.HelperFunctions;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.RequestScoped;
import javax.faces.context.FacesContext;
import javax.naming.InitialContext;
import javax.naming.NamingException;
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
    
    protected static Properties prop;
    protected InitialContext ctx;
   
    protected static final String JNDILOOKUP_PROPERTYFILE = "resources/properties/WebSettings";

    
    /**
     * Creates a new instance of DataproviderDAO
     */
    public DataproviderDAO() {
        availableProviders = new ArrayList<>();
        selectedProviders = new ArrayList<>();
        
        try {
            ctx = new InitialContext();
            prop = getProperties();
        
            // AVAILABLE ROUTES
            JSONArray providers = JSONMethods.getArrayFromURL(prop.getProperty("urlAllProviders"), prop);
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
        } catch (NamingException ex) {
            Logger.getLogger(AnalysePage.class.getName()).log(Level.SEVERE, null, ex);
        }  
        
    }
    
    private Properties getProperties() {
        return HelperFunctions.RetrievePropertyFile(JNDILOOKUP_PROPERTYFILE, ctx, Logger.getGlobal());
    }

    public List<DataProvider> getAvailableProviders() {
        return availableProviders;
    }

    public List<DataProvider> getSelectedProviders() {
        return selectedProviders;
    }
    
    public boolean isSelected(DataProvider provider) {
        int i=0;
        while(i<selectedProviders.size()){
            if(selectedProviders.get(i).equals(provider)) return true;
            i++;
        }
        return false;
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
