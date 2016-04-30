/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package iii.vop2016.verkeer2.ejb.logger;

import iii.vop2016.verkeer2.ejb.components.Log;
import iii.vop2016.verkeer2.ejb.helper.BeanFactory;
import iii.vop2016.verkeer2.ejb.helper.HelperFunctions;
import iii.vop2016.verkeer2.ejb.timer.ITimer;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Properties;
import java.util.logging.FileHandler;
import java.util.logging.Handler;
import java.util.logging.Level;
import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.annotation.Resource;
import javax.ejb.SessionContext;
import javax.ejb.Singleton;
import javax.ejb.Startup;
import javax.naming.InitialContext;
import javax.naming.NamingException;

/**
 *
 * @author tobia
 */
@Startup
@Singleton
public class Logger implements LoggerRemote,LoggerLocal {

    protected java.util.logging.Logger l;
    protected List<Log> history;
    protected static final String JNDILOOKUP_PROPERTYFILE = "resources/properties/Logger";

    public Logger() {

    }

    private Properties getProperties() {
        return HelperFunctions.RetrievePropertyFile(JNDILOOKUP_PROPERTYFILE, ctx, java.util.logging.Logger.getGlobal());
    }

    @Resource
    protected SessionContext sctx;
    protected InitialContext ctx;
    protected BeanFactory beans;

    @PostConstruct
    private void init() {
        try {
            ctx = new InitialContext();
        } catch (NamingException ex) {
            java.util.logging.Logger.getLogger(Logger.class.getName()).log(Level.SEVERE, null, ex);
        }
        beans = BeanFactory.getInstance(ctx, null);
        Properties prop = getProperties();

        history = new ArrayList<>();
        try {
            FileHandler h;
            String file = prop.getProperty("filelocation", "");
            if (!(file.endsWith("/") || file.endsWith("\\"))) {
                file += "/";
            }
            file += prop.getProperty("filename", "");

            if (!file.equals("")) {
                h = new FileHandler(file, true);
                l = java.util.logging.Logger.getLogger("logger");
                l.addHandler(h);
                l.setLevel(Level.FINEST);

                l.log(Level.INFO, "Started Logging at " + new Date());
            }
        } catch (IOException ex) {
            java.util.logging.Logger.getLogger(Logger.class.getName()).log(Level.SEVERE, null, ex);
        } catch (SecurityException ex) {
            java.util.logging.Logger.getLogger(Logger.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    @PreDestroy
    private void destroy() {
        if (l != null) {
            l.log(Level.INFO, "Ended Logging at " + new Date());
            for (Handler handler : l.getHandlers()) {
                if (handler instanceof FileHandler) {
                    FileHandler f = (FileHandler) handler;
                    f.close();
                }
            }
        }
    }

    @Override
    public void log(Level l, String message) {
        if (l != null) {
            history.add(new Log(l, message, (new Date()).getTime()));

            if (history.size() > 10000) {
                history = new ArrayList<Log>(history.subList(1000, 10000));
            }
            this.l.log(l, message);
        }
    }

    @Override
    public void entering(String sourceClass, String sourceMethod, Object[] params) {
        if (l != null) {
            this.l.entering(sourceClass, sourceMethod, params);
        }
    }

    @Override
    public void exiting(String sourceClass, String sourceMethod, Object result) {
        if (l != null) {
            this.l.exiting(sourceClass, sourceMethod, result);
        }
    }

    @Override
    public List<Log> getLogs(int amount, int offset, Level filter1, String containing) {
        List<Log> ret = new ArrayList<>();

        int skip = 0;
        int i = history.size() - 1;
        while (ret.size() != amount && i >= 0) {
            Log l = history.get(i);

            if (l.getL().intValue() >= filter1.intValue() && (containing.equals("") || l.getMessage().contains(containing))) {
                if (skip != offset) {
                    skip++;
                } else {
                    ret.add(l);
                }
            }

            i--;
        }

        return ret;
    }
}
