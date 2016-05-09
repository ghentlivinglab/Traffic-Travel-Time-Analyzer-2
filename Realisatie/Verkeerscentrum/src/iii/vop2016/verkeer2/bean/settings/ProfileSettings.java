/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package iii.vop2016.verkeer2.bean.settings;

import iii.vop2016.verkeer2.bean.analyse.RouteDAO;
import iii.vop2016.verkeer2.bean.auth.AuthHelpers;
import iii.vop2016.verkeer2.bean.auth.AuthUser;
import iii.vop2016.verkeer2.bean.auth.SessionBean;
import iii.vop2016.verkeer2.ejb.components.IRoute;
import iii.vop2016.verkeer2.ejb.dao.LoginDAOLocal;
import iii.vop2016.verkeer2.ejb.helper.BeanFactory;
import iii.vop2016.verkeer2.ejb.helper.HelperFunctions;
import java.io.Serializable;
import java.util.List;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.RequestScoped;
import javax.naming.InitialContext;
import javax.naming.NamingException;

/**
 *
 * @author Mike
 */
@ManagedBean(name = "profileSettings", eager = true)
@RequestScoped
public class ProfileSettings implements Serializable{

    protected static Properties prop;
    protected InitialContext ctx;
    protected BeanFactory beanFactory;
    protected static final String JNDILOOKUP_PROPERTYFILE = "resources/properties/WebSettings";
    
    @ManagedProperty(value="#{sessionBean}")
    private SessionBean session;
    
    protected AuthUser user;
    protected String newPassword;
  

    public ProfileSettings() {
        try {
            beanFactory = BeanFactory.getInstance(ctx, null);
            ctx = new InitialContext();
            prop = getProperties();
                        
        } catch (NamingException ex) {
            Logger.getLogger(ProfileSettings.class.getName()).log(Level.SEVERE, null, ex);
        }  
    }
    
    
    
    private Properties getProperties() {
        return HelperFunctions.RetrievePropertyFile(JNDILOOKUP_PROPERTYFILE, ctx, Logger.getGlobal());
    }
    
    public int getId(){
        return session.getUserId();
    }
    
    public String getUsername(){
        return ""+session.getUserName();
    }

    public SessionBean getSession() {
        return session;
    }

    public void setSession(SessionBean session) {
        this.session = session;
    }
    
    public AuthUser getUser(){
        if(user == null) user = beanFactory.getLoginDAO().getUser(session.getUserId());
        return user;
    }

    public String getNewPassword() {
        return newPassword;
    }

    public void setNewPassword(String newPassword) {
        this.newPassword = newPassword;
    }
    
    public String submit(){
        if(newPassword != null || newPassword.length() > 1){
            user.setPassword(AuthHelpers.getMD5(newPassword));
        }
        beanFactory.getLoginDAO().updateUser(user);
        return "pretty:settings-profile";
    }
    
    
    
}
