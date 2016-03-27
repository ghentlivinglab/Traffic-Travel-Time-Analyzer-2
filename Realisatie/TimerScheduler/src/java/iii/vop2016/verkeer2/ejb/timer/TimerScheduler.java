/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package iii.vop2016.verkeer2.ejb.timer;

import iii.vop2016.verkeer2.ejb.helper.BeanFactory;
import iii.vop2016.verkeer2.ejb.helper.HelperFunctions;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Map;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.annotation.Resource;
import javax.ejb.SessionContext;
import javax.ejb.Singleton;
import javax.ejb.Startup;
import javax.ejb.Timeout;
import javax.ejb.Timer;
import javax.ejb.TimerConfig;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import iii.vop2016.verkeer2.ejb.datadownloader.ITrafficDataDownloader;
import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.text.NumberFormat;
import java.time.DateTimeException;
import org.apache.commons.net.ntp.NTPUDPClient;
import org.apache.commons.net.ntp.NtpUtils;
import org.apache.commons.net.ntp.NtpV3Packet;
import org.apache.commons.net.ntp.TimeInfo;
import org.apache.commons.net.ntp.TimeStamp;
import org.apache.commons.net.time.TimeTCPClient;

/**
 *
 * @author Tobias Van der Pulst
 */
@Singleton
@Startup
public class TimerScheduler implements TimerSchedulerRemote {

    @Resource
    protected SessionContext ctxs;

    protected Timer t;
    protected int ticks;
    protected int interval;
    protected SimpleDateFormat sdf = new SimpleDateFormat("HHmm");
    protected Pattern timeFormat = Pattern.compile("([0-9]{2})-([0-9]{2})");
    protected static final int DEFAULTINTERVAL = 5;
    protected boolean isRunning;

    protected Pattern numberFormat = Pattern.compile("\\d+");

    protected InitialContext ctx;
    protected static final String JNDILOOKUP_PROPERTYFILE = "resources/properties/TimerScheduler";
    //protected Properties properties;

    protected BeanFactory beans;

    /**
     * Constructor
     */
    public TimerScheduler() {
    }

    @PostConstruct
    private void init() {
        //Initialize bean and its context
        try {
            ctx = new InitialContext();
        } catch (NamingException ex) {
            Logger.getLogger(TimerScheduler.class.getName()).log(Level.SEVERE, null, ex);
        }
        beans = BeanFactory.getInstance(ctx, ctxs);

        Logger.getLogger("logger").log(Level.INFO, "TimerScheduler has been initialized.");

        Properties prop = getProperties();

        //Get interval to closest time for timer from properties file
        Date time = getCurrentTime(prop);
        int currentTime = getIndexedCurrentTime(time);
        interval = getIntervalForClosestTime(currentTime, prop);
        Logger.getLogger("logger").log(Level.INFO, "Interval for Timer set to " + interval);

        //Create timer with specified interval
        ticks = 0;
        t = ctxs.getTimerService().createIntervalTimer(1000, 60000, new TimerConfig());
        isRunning = true;
    }

    private Properties getProperties() {
        return HelperFunctions.RetrievePropertyFile(JNDILOOKUP_PROPERTYFILE, ctx, Logger.getGlobal());
    }

    /**
     * Ticks are driven by the Timers, they start the new download cycle for
     * data
     */
    @Override
    @Timeout
    public void Tick() {
        if (!isRunning) {
            return;
        }

        Properties prop = getProperties();

        Date time = getCurrentTime(prop);
        int currentTime = getIndexedCurrentTime(time);

        if (ticks == interval) {
            ticks = 1;
            DoTick(time);
        } else {
            ticks++;
        }

        //get interval for current time, if different set the timer with specified interval
        int i = getIntervalForClosestTime(currentTime, prop);
        if (i != interval) {
            if (ticks != 1) {
                DoTick(time);
            }

            interval = i;
            ticks = 1;
            Logger.getLogger("logger").log(Level.INFO, "Interval for Timer set to " + interval);
        }

    }

    private int getIndexedCurrentTime(Date time) {
        int currentTime = Integer.parseInt(sdf.format(time));
        return currentTime;
    }

    private Date getCurrentTime(Properties prop) {
        Date time = null;
        int currentTime = -1;
        try {
            //retrieve current time from ntp server
            time = getCurrentTime_ntpServer(prop);
            if (time == null) {
                throw new IOException();
            }
        } catch (Exception ex) {
            //use local server time as backup
            Calendar cal = Calendar.getInstance();
            time = cal.getTime();
        }
        return time;
    }

    private int getIntervalForClosestTime(int currentTime, Properties properties) {
        if (properties == null) {
            return DEFAULTINTERVAL;
        }

        int closestTime = Integer.MAX_VALUE;
        int interval = DEFAULTINTERVAL;

        for (Map.Entry<Object, Object> entry : properties.entrySet()) {
            //check if key is string
            if ((entry.getKey() != null) && (entry.getKey() instanceof String)) {
                String key = (String) entry.getKey();
                //check if key matches format
                Matcher m = timeFormat.matcher(key);
                if (m.matches()) {
                    int time = Integer.parseInt(m.group(1) + m.group(2));
                    int diff = currentTime - time;
                    //only check when time is earlier then current time
                    if (diff >= 0) {
                        //if time is closer then prev time, get its interval
                        if (diff < closestTime) {
                            if ((entry.getValue() != null) && (entry.getValue() instanceof String) && (numberFormat.matcher((String) entry.getValue()).matches())) {
                                closestTime = diff;
                                interval = Integer.parseInt((String) entry.getValue());
                            } else {
                                Logger.getGlobal().log(Level.WARNING, "Config file error: <" + entry.getKey() + " - " + entry.getValue() + "> expected a valid object (int).");
                            }
                        }
                    }
                }
            }
        }

        return interval;
    }

    private void DoTick(Date currentTime) {
        //lookup datamanager bean and trigger timed function
        ITrafficDataDownloader managementBean = beans.getDataManager();
        if (managementBean != null) {
            managementBean.downloadNewData(currentTime);
        } else {
            Logger.getLogger("logger").log(Level.SEVERE, "Could not access dataManagement bean to trigger Timed function");
        }

    }

    @PreDestroy
    private void destroy() {
        t.cancel();
    }

    @Override
    public void StopTimer() {
        isRunning = false;
    }

    @Override
    public boolean isTimerRunning() {
        return isRunning;
    }

    @Override
    public void StartTimer() {
        isRunning = true;
    }

    @Override
    public int getCurrentInterval() {
        // interval in seconds
        return interval * 60;
    }

    @Override
    public long getCurrentTime() {
        Properties prop = getProperties();
        Date d = getCurrentTime(prop);
        if (d != null) {
            return d.getTime();
        }
        return -1;
    }

    private Date getCurrentTime_ntpServer(Properties properties) throws Exception {
        NTPUDPClient client = new NTPUDPClient();
        client.setDefaultTimeout(10000);
        try {
            client.open();
            InetAddress hostAddr = InetAddress.getByName(properties.getProperty("ntpserver", ""));
            TimeInfo info = client.getTime(hostAddr);
            return processResponse(info);
        } catch (IOException ioe) {
            return null;
        } finally {
            client.close();
        }
    }

    private static final NumberFormat nFormat = new java.text.DecimalFormat("0.00");

    public Date processResponse(TimeInfo info) throws DateTimeException {
        NtpV3Packet message = info.getMessage();
        int stratum = message.getStratum();
        String refType;
        if (stratum <= 0) {
            refType = "";
            throw new DateTimeException("stratum: Unspecified or Unavailable");
        } else {
            double disp = message.getRootDispersionInMillisDouble();
            System.out.println(" rootdelay=" + nFormat.format(message.getRootDelayInMillisDouble()) + ", rootdispersion(ms): " + nFormat.format(disp));

            long destTime = info.getReturnTime();
            
            // Transmit time is time reply sent by server (t3)
            TimeStamp xmitNtpTime = message.getTransmitTimeStamp();
            info.computeDetails();
            Long delayValue = info.getDelay();
            
            Date date = xmitNtpTime.getDate();
            date = new Date(date.getTime() + delayValue);
            
            return date;
        }
    }
}
