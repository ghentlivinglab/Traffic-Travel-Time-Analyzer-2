/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package iii.vop2016.verkeer2.ejb.timer;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import javax.ejb.SessionContext;
import javax.ejb.Singleton;
import javax.ejb.Startup;
import javax.ejb.Timeout;
import javax.ejb.Timer;
import javax.ejb.TimerConfig;
import javax.naming.InitialContext;
import javax.naming.NamingException;

/**
 *
 * @author tobia
 */
@Singleton
@Startup
public class TimerSchedular implements TimerSchedularRemote {

    @Resource
    private SessionContext ctxs;
    private Timer t;
    private int interval;
    private SimpleDateFormat sdf = new SimpleDateFormat("HHmm");

    private InitialContext ctx;
    private Properties properties;

    public TimerSchedular() {

    }

    @PostConstruct
    public void init() {
        //get initialcontext to retrieve resources
        try {
            ctx = new InitialContext();
        } catch (NamingException ex) {
            Logger.getLogger(TimerSchedular.class.getName()).log(Level.SEVERE, null, ex);
        }

        //retrieve properties file
        RetrievePropertiesFromFile();
        
        //get interval to closest time for timer from properties file
        int currentTime = getIndexedCurrentTime();
        
        interval = Integer.parseInt((String) properties.get("0000"));
        t = ctxs.getTimerService().createIntervalTimer(0,interval*60000 , new TimerConfig());
    }

    @Override
    @Timeout
    public void Tick() {
        int currentTime = getIndexedCurrentTime();
        
        for(int i = currentTime-interval+1;i <=currentTime;i++){
            String s = (String) properties.getProperty(i+"");
            if(s != null){
                interval = Integer.parseInt(s);
                t.cancel();
                t = ctxs.getTimerService().createIntervalTimer(interval*60000,interval*60000 , new TimerConfig());
            }
        }
        System.out.println("");
        

    }

    private void RetrievePropertiesFromFile() {
        try {
            properties = (Properties) ctx.lookup("resources/properties/TimerScheduler");
        } catch (NamingException ex) {
            Logger.getLogger(TimerSchedular.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private int getIndexedCurrentTime() {
        Calendar cal = Calendar.getInstance();
        int currentTime = Integer.parseInt(sdf.format(cal.getTime()));
        return currentTime;
    }

}
