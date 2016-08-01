/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package iii.vop2016.verkeer2.ejb.helper;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Pattern;
import javax.ejb.AccessTimeout;
import javax.ejb.ConcurrencyManagement;
import javax.ejb.ConcurrencyManagementType;
import javax.ejb.SessionContext;
import javax.naming.InitialContext;
import javax.naming.NamingException;

/**
 *
 * @author Tobias
 */
public class HelperFunctions {

    protected static Pattern timeFormat = Pattern.compile("([0-9]{2})-([0-9]{2})");

    /**
     * Retrieve a properties file located in the JNDI resources. (Heavy
     * operation)
     *
     * @param lookup JNDI name of the Property resource
     * @param ctx InitialContext of the bean
     * @param logger A logger to log error. When in doubt use Logger.getGlobal()
     * @return The requested Property file.
     */
    public static Properties RetrievePropertyFile(String lookup, InitialContext ctx, Logger logger) {
        if (ctx == null) {
            return new Properties();
        }
        try {
            Object obj = ctx.lookup(lookup);
            if (obj != null) {
                return (Properties) obj;
            } else {
                logger.log(Level.WARNING, "Property FILE missing (" + lookup + ")");
            }
        } catch (NamingException ex) {
            logger.log(Level.WARNING, "Property file REFERENCE missing (" + lookup + ")");
        }
        return new Properties();
    }

    public static boolean SavePropertyFile(Properties prop, Logger logger) {
        String location = prop.getProperty("propertyLocation", "");
        if (!location.equals("")) {
            try {
                File f = new File(location);
                if (f.exists()) {
                    OutputStream out = new FileOutputStream(f);
                    prop.remove("propertyLocation");
                    prop.store(out, "Last modified:");
                    prop.setProperty("propertyLocation", location);
                    return true;
                }
            } catch (FileNotFoundException ex) {
                logger.log(Level.SEVERE, null, ex);
            } catch (IOException ex) {
                logger.log(Level.SEVERE, null, ex);
            }
        }
        return false;
    }

    public static boolean SavePropertyFile(String lookup, InitialContext ctx, String key, String value, Logger logger) {
        Properties prop = RetrievePropertyFile(lookup, ctx, logger);
        prop.setProperty(key, value);
        return SavePropertyFile(prop, logger);
    }

    /**
     * Retrieve the beanType according to the provided property file
     *
     * @param beanProperties property file to retrieve JNDI for beans
     * @param bean selection what beantype to get
     * @param ctx SessionContext of the bean
     * @param logger A logger to log error. When in doubt use Logger.getGlobal()
     * @return The requested bean (needs casting)
     */
    public static Object getBean(Properties beanProperties, BeanSelector bean, SessionContext ctx, Logger logger) {
        if (beanProperties == null) {
            return null;
        }

        String jndi = beanProperties.getProperty(bean.toString());
        if (jndi == null || jndi.equals("")) {
            logger.log(Level.SEVERE, "Bean REFERENCE in property file not found (" + jndi + ")");
            return null;
        }

        Object obj = ctx.lookup(jndi);
        if (obj == null) {
            if (jndi.contains("!")) {
                obj = ctx.lookup(jndi.split("!")[0]);
            }
            if (obj == null) {
                logger.log(Level.SEVERE, "BEAN not found (" + jndi + ")");
                return null;
            }
        }

        return obj;
    }

    /**
     * Retrieve the beanType according to the provided property file
     *
     * @param beanProperties property file to retrieve JNDI for beans
     * @param bean selection what beantype to get
     * @param ctx InitialContext of the bean
     * @param logger A logger to log error. When in doubt use Logger.getGlobal()
     * @return The requested bean (needs casting)
     */
    public static Object getBean(Properties beanProperties, BeanSelector bean, InitialContext ctx, Logger logger) {

        if (beanProperties == null) {
            return null;
        }

        String jndi = beanProperties.getProperty(bean.toString());
        if (jndi == null || jndi.equals("")) {
            logger.log(Level.SEVERE, "Bean REFERENCE in property file not found (" + jndi + ")");
            return null;
        }
        try {

            Object obj = ctx.lookup(jndi);
            if (obj == null) {
                if (jndi.contains("!")) {
                    obj = ctx.lookup(jndi.split("!")[0]);
                }
                if (obj == null) {
                    logger.log(Level.SEVERE, "BEAN not found (" + jndi + ")");
                    return null;
                }
            }
            return obj;
        } catch (NamingException ex) {
            logger.log(Level.SEVERE, "BEAN not found (" + jndi + ")");
        }
        return null;
    }

    /**
     * Retrieve the beanType according to the provided JNDI name
     *
     * @param jndi The jndi name that represents the bean.
     * @param ctx SessionContext of the bean
     * @param logger A logger to log error. When in doubt use Logger.getGlobal()
     * @return The requested bean (needs casting)
     */
    public static Object getBean(String jndi, SessionContext ctx, Logger logger) {
        if (jndi == null || jndi.equals("")) {
            logger.log(Level.SEVERE, "Jndi not valid (" + jndi + ")");
            return null;
        }

        Object obj = ctx.lookup(jndi);
        if (obj == null) {
            if (jndi.contains("!")) {
                obj = ctx.lookup(jndi.split("!")[0]);
            }
            if (obj == null) {
                logger.log(Level.SEVERE, "BEAN not found (" + jndi + ")");
                return null;
            }
        }

        return obj;
    }

    /**
     * Retrieve the beanType according to the provided JNDI name
     *
     * @param jndi The jndi name that represents the bean.
     * @param ctx SessionContext of the bean
     * @param logger A logger to log error. When in doubt use Logger.getGlobal()
     * @return The requested bean (needs casting)
     */
    public static Object getBean(String jndi, InitialContext ctx, Logger logger) {
        if (jndi == null || jndi.equals("")) {
            logger.log(Level.SEVERE, "Jndi not valid (" + jndi + ")");
            return null;
        }

        Object obj = null;
        try {
            obj = ctx.lookup(jndi);
            if (obj == null) {
                if (jndi.contains("!")) {
                    obj = ctx.lookup(jndi.split("!")[0]);
                }
                if (obj == null) {
                    logger.log(Level.SEVERE, "BEAN not found (" + jndi + ")");
                    return null;
                }
            }
        } catch (NamingException ex) {
            logger.log(Level.SEVERE, "BEAN not found (" + jndi + ")");
        }
        return obj;
    }
}
