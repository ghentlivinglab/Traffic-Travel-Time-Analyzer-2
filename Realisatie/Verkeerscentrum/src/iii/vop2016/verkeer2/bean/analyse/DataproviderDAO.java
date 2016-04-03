/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package iii.vop2016.verkeer2.bean.analyse;

import iii.vop2016.verkeer2.ejb.components.DataProvider;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.RequestScoped;
import javax.faces.context.FacesContext;
import javax.servlet.http.HttpServletRequest;

/**
 *
 * @author Mike
 */
@ManagedBean
@RequestScoped
public class DataproviderDAO {

    protected List<DataProvider> availableProviders;
    protected List<DataProvider> selectedProviders;
    
    /**
     * Creates a new instance of DataproviderDAO
     */
    public DataproviderDAO() {
        availableProviders = new ArrayList<>();
        selectedProviders = new ArrayList<>();
        
        //
        // AJAX CALL OM ROUTES OP TE HALEN
        //
         
        // AVAILABLE ROUTES
        availableProviders.add(new DataProvider("Google Maps"));
        
        HttpServletRequest request = (HttpServletRequest) FacesContext.getCurrentInstance().getExternalContext().getRequest();        
        Map<String, String[]> parameterMap = request.getParameterMap();
        String[] providers = parameterMap.get("provider");
        
        if(providers != null){
            for(String provider : providers){
                selectedProviders.add(new DataProvider(provider));
            }
        }
        
    }

    public List<DataProvider> getAvailableProviders() {
        return availableProviders;
    }

    public List<DataProvider> getSelectedProviders() {
        return selectedProviders;
    }
    
}
