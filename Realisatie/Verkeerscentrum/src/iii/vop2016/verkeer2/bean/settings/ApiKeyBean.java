/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package iii.vop2016.verkeer2.bean.settings;

import iii.vop2016.verkeer2.bean.settings.*;
import iii.vop2016.verkeer2.bean.helpers.JSONMethods;
import iii.vop2016.verkeer2.ejb.helper.BeanFactory;
import iii.vop2016.verkeer2.ejb.helper.HelperFunctions;
import iii.vop2016.verkeer2.ejb.helper.VerkeerLibToJson;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.RequestScoped;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import org.json.JSONArray;

/**
 *
 * @author Mike
 */

@ManagedBean(name = "apikeyBean", eager = true)
@RequestScoped
public class ApiKeyBean implements Serializable{

    protected static Properties prop;
    protected InitialContext ctx;
    protected BeanFactory beanFactory;
    protected static final String JNDILOOKUP_PROPERTYFILE = "resources/properties/WebSettings";
    
    protected List<String> apikeys;


    public ApiKeyBean() {
        try {
            beanFactory = BeanFactory.getInstance(ctx, null);
            ctx = new InitialContext();
            prop = getProperties();
            
            

        } catch (NamingException ex) {
            Logger.getLogger(ApiKeyBean.class.getName()).log(Level.SEVERE, null, ex);
        }  
    }
    
    
    private Properties getProperties() {
        return HelperFunctions.RetrievePropertyFile(JNDILOOKUP_PROPERTYFILE, ctx, Logger.getGlobal());
    }
    
       

    
    
    
  
    
}
