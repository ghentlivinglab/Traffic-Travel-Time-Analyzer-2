/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package iii.vop2016.verkeer2.ejb.properties;

import iii.vop2016.verkeer2.ejb.datasources.ISourceAdapter;
import iii.vop2016.verkeer2.ejb.helper.BeanFactory;
import iii.vop2016.verkeer2.ejb.helper.HelperFunctions;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.annotation.Resource;
import javax.ejb.Lock;
import javax.ejb.LockType;
import javax.ejb.SessionContext;
import javax.ejb.Singleton;
import javax.naming.InitialContext;
import javax.naming.NamingException;

/**
 *
 * @author tobia
 */
@Singleton
@Lock(LockType.WRITE)
public class Properties implements PropertiesRemote, PropertiesLocal {

    @Resource
    protected SessionContext ctxs;

    private List<String> defaultHandlers;
    private List<String> jndi;

    protected InitialContext ctx;
    protected static final String JNDILOOKUP_PROPERTYFILE = "resources/properties/Properties";
    protected BeanFactory beans;

    @PostConstruct
    private void init() {
        //Initialize bean and its context
        try {
            ctx = new InitialContext();
        } catch (NamingException ex) {
            Logger.getLogger(Properties.class.getName()).log(Level.SEVERE, null, ex);
        }
        beans = BeanFactory.getInstance(ctx, ctxs);

        jndi = new ArrayList<>();
        defaultHandlers = new ArrayList<>();

        jndi.add(JNDILOOKUP_PROPERTYFILE);

        java.util.Properties prop = getProjectProperties();
        String defaultProv = prop.getProperty("defaultProviders", "");
        if (!defaultProv.equals("")) {
            String[] arr = defaultProv.split(",");
            for (String a : arr) {
                defaultHandlers.add(a);
            }
        }
    }

    @PreDestroy
    private void destruct() {
        java.util.Properties prop = getProjectProperties();
        StringBuilder s = new StringBuilder();
        for (int i = 0; i < defaultHandlers.size(); i++) {
            if (s.length() != 0) {
                s.append(",");
            }
            s.append(defaultHandlers.get(i));
        }
        prop.put("defaultProviders", s.toString());
        HelperFunctions.SavePropertyFile(prop, Logger.getGlobal());
    }

    private java.util.Properties getProjectProperties() {
        return HelperFunctions.RetrievePropertyFile(JNDILOOKUP_PROPERTYFILE, ctx, Logger.getGlobal());
    }

    @Override
    public void registerProperty(String jndiName) {
        if (!jndi.contains(jndiName)) {
            jndi.add(jndiName);
        }
    }

    @Override
    public List<String> getProperties() {
        return jndi;
    }

    @Override
    public List<String> getDefaultProviders() {
        if (defaultHandlers.isEmpty()) {
            List<ISourceAdapter> adas = beans.getSourceAdaptors();
            for (ISourceAdapter ada : adas) {
                defaultHandlers.add(ada.getProviderName());
            }
        }
        return defaultHandlers;
    }

    @Override
    public void setDefaultProviders(List<String> def) {
        this.defaultHandlers = def;
    }

}
