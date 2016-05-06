/*
* To change this license header, choose License Headers in Project Properties.
* To change this template file, choose Tools | Templates
* and open the template in the editor.
*/
package iii.vop2016.verkeer2.bean.analyse;

import iii.vop2016.verkeer2.bean.analyse.*;
import iii.vop2016.verkeer2.bean.helpers.JSONMethods;
import iii.vop2016.verkeer2.ejb.components.IRoute;
import iii.vop2016.verkeer2.ejb.components.Route;
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
@ManagedBean(name = "precisionDAO", eager = true)
@RequestScoped
public class PrecisionDAO {
    
    protected int precision;
    protected boolean withPrecision;
    
    public PrecisionDAO() {
     
        HttpServletRequest request = (HttpServletRequest) FacesContext.getCurrentInstance().getExternalContext().getRequest();
        Map<String, String[]> parameterMap = request.getParameterMap();
        // SELECTED ROUTES
        String[] precisionArray = parameterMap.get("precision");
        if(precisionArray != null && precisionArray.length > 0){
            precision = Integer.parseInt(precisionArray[0]);
        }else{
            precision = 100;
        }
        
        withPrecision = true;
        
       // withPrecision = true;

    }

    public int getPrecision() {
        return precision;
    }

    public void setPrecision(int precision) {
        this.precision = precision;
    }

    public boolean isWithPrecision() {
        return withPrecision;
    }

    public void setWithPrecision(boolean withPrecision) {
        this.withPrecision = withPrecision;
    }
    

    
    
    
}
