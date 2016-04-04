/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package iii.vop2016.verkeer2.dummy.timer;

import iii.vop2016.verkeer2.ejb.helper.BeanFactory;
import iii.vop2016.verkeer2.ejb.helper.HelperFunctions;
import iii.vop2016.verkeer2.ejb.timer.TimerSchedulerRemote;
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import javax.ejb.SessionContext;
import javax.ejb.Singleton;
import javax.ejb.Startup;
import javax.ejb.Timeout;
import javax.ejb.TimerConfig;
import javax.naming.InitialContext;
import javax.naming.NamingException;

/**
 *
 * @author tobia
 */
@Singleton
@Startup
public class DummyTimerScheduler implements TimerSchedulerRemote {

    boolean running = true;
    long interval = 60000;
    long timePerInterval = 60000;
    long time = 0;

    @Resource
    private SessionContext sctx;
    private InitialContext ctx;
    private BeanFactory beans;

    @PostConstruct
    public void init() {
        try {
            ctx = new InitialContext();
            sctx.getTimerService().createIntervalTimer(interval, interval, new TimerConfig());
            beans = BeanFactory.getInstance(ctx, sctx);
        } catch (NamingException ex) {
            Logger.getLogger(DummyTimerScheduler.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    @Override
    @Timeout
    public void Tick() {
        if (!beans.isBeanActive("DummyTimerScheduler/DummyTimerScheduler")) {
            return;
        }

        if (running) {
            time += timePerInterval;
            beans.getDataManager().downloadNewData(new Date(time));
        }
    }

    @Override
    public void StopTimer() {
        running = false;
    }

    @Override
    public boolean isTimerRunning() {
        return running;
    }

    @Override
    public void StartTimer() {
        running = true;
    }

    @Override
    public int getCurrentInterval() {
        return Math.toIntExact(interval/1000);
    }

    @Override
    public long getCurrentTime() {
        return time;
    }

}
