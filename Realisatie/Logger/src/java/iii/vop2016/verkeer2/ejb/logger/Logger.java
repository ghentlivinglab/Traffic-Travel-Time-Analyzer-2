/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package iii.vop2016.verkeer2.ejb.logger;

import iii.vop2016.verkeer2.ejb.helper.BeanFactory;
import java.io.IOException;
import java.util.Date;
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
public class Logger implements LoggerRemote {

    protected java.util.logging.Logger l;

    public Logger() {
        try {
            FileHandler h;
            h = new FileHandler("/root/verkeer2/log.txt", true);
            l = java.util.logging.Logger.getLogger("logger");
            l.addHandler(h);
            l.setLevel(Level.FINEST);

            l.log(Level.INFO, "Started Logging at " + new Date());
        } catch (IOException ex) {
            java.util.logging.Logger.getLogger(Logger.class.getName()).log(Level.SEVERE, null, ex);
        } catch (SecurityException ex) {
            java.util.logging.Logger.getLogger(Logger.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    @PostConstruct
    private void init() {

    }

    @PreDestroy
    private void destroy() {
        l.log(Level.INFO, "Ended Logging at " + new Date());
        for (Handler handler : l.getHandlers()) {
            if (handler instanceof FileHandler) {
                FileHandler f = (FileHandler) handler;
                f.close();
            }
        }
    }

    @Override
    public void log(Level l, String message) {
        this.l.log(l, message);
    }

    @Override
    public void entering(String sourceClass, String sourceMethod, Object[] params) {
        this.l.entering(sourceClass, sourceMethod,params);
    }

    @Override
    public void exiting(String sourceClass, String sourceMethod, Object result) {
        this.l.exiting(sourceClass, sourceMethod, result);
    }
}
