/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package iii.vop2016.verkeer2.ejb.timer;

import iii.vop2016.verkeer2.ejb.dao.ITrafficDataDAO;
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
import javax.ejb.Timeout;
import javax.ejb.Timer;
import javax.ejb.TimerConfig;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import iii.vop2016.verkeer2.ejb.datadownloader.ITrafficDataDownloader;
import iii.vop2016.verkeer2.ejb.dataprovider.IDataProvider;
import iii.vop2016.verkeer2.ejb.helper.NoInternetConnectionException;
import iii.vop2016.verkeer2.ejb.logger.ILogger;
import iii.vop2016.verkeer2.ejb.properties.IProperties;
import java.io.IOException;
import java.net.InetAddress;
import java.text.NumberFormat;
import java.time.DateTimeException;
import java.util.GregorianCalendar;
import javax.ejb.Startup;
import org.apache.commons.net.ntp.NTPUDPClient;
import org.apache.commons.net.ntp.NtpV3Packet;
import org.apache.commons.net.ntp.TimeInfo;
import org.apache.commons.net.ntp.TimeStamp;

/**
 *
 * @author Tobias Van der Pulst
 */
@Singleton
@Startup
public class TimerScheduler implements TimerSchedulerRemote, TimerSchedulerLocal {

    @Resource
    protected SessionContext ctxs;

    protected Timer t;
    protected int ticks;
    protected int interval;
    protected SimpleDateFormat sdf = new SimpleDateFormat("HHmm");
    protected Pattern timeFormat = Pattern.compile("([0-9]{2})-([0-9]{2})");
    protected static final int DEFAULTINTERVAL = 5;
    protected boolean isRunning;
    protected boolean isTimeInvalid;
    protected Date currentTime;

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

        IProperties propCol = beans.getPropertiesCollection();
        if (propCol != null) {
            propCol.registerProperty(JNDILOOKUP_PROPERTYFILE);
        }

        beans.getLogger().log(Level.INFO, "TimerScheduler has been initialized.");

        Properties prop = getProperties();

        //Get interval to closest time for timer from properties file
        isTimeInvalid = true;
        Date time;
        try {
            time = getCurrentTime(prop);
        } catch (NoInternetConnectionException ex) {
            beans.getLogger().log(Level.WARNING, "No internet connection");
            time = this.currentTime;
        }
        int currentTime = getIndexedCurrentTime(time);
        interval = getIntervalForClosestTime(currentTime, prop);
        beans.getLogger().log(Level.INFO, "Interval for Timer set to " + interval);

        //Create timer with specified interval
        ticks = 0;
        t = ctxs.getTimerService().createIntervalTimer(1000, 60000, new TimerConfig());
        isRunning = true;

        prop.setProperty("19-00", "30");
        HelperFunctions.SavePropertyFile(prop, Logger.getGlobal());
    }

    private Properties getProperties() {
        return HelperFunctions.RetrievePropertyFile(JNDILOOKUP_PROPERTYFILE, ctx, Logger.getGlobal());
    }

    public void Tick() {

    }

    /**
     * Ticks are driven by the Timers, they start the new download cycle for
     * data
     */
    @Timeout
    public void Tick(Timer timer) {
        if (!t.equals(timer)) {
            return;
        }

        if (!isRunning) {
            return;
        }

        isTimeInvalid = true;

        if (!beans.isBeanActive("TimerScheduler/TimerScheduler")) {
            return;
        }

        Properties prop = getProperties();

        Date time = null;
        int currentTime;
        try {
            time = getCurrentTime(prop);
            currentTime = getIndexedCurrentTime(time);
        } catch (NoInternetConnectionException ex) {
            Logger.getLogger("logger").log(Level.WARNING, "No internet connection", ex);
            currentTime = getIndexedCurrentTime(this.currentTime);
        }

        if (ticks == interval) {
            ticks = 1;
            if (time != null) {
                DoTick(time);
            }
        } else {
            ticks++;
        }

        //get interval for current time, if different set the timer with specified interval
        int i = getIntervalForClosestTime(currentTime, prop);
        if (i != interval) {
            if (ticks != 1) {
                if (time != null) {
                    DoTick(time);
                }
            }

            interval = i;
            ticks = 1;

            ILogger logger = beans.getLogger();
            logger.log(Level.INFO, "Interval for Timer set to " + interval);

        }

        if (isClearBuffersTriggered(currentTime, prop)) {
            ILogger logger = beans.getLogger();
            ITrafficDataDAO dao = beans.getTrafficDataDAO();
            IDataProvider dataProv = beans.getDataProvider();
            dao.updateBlockList();
            dataProv.invalidateBuffers();
            logger.log(Level.INFO, "Buffers cleared and Blocklist for lookup rebuild");
        }

    }

    private int getIndexedCurrentTime(Date time) {
        int currentTime = Integer.parseInt(sdf.format(time));
        return currentTime;
    }

    private Date getCurrentTime(Properties prop) throws NoInternetConnectionException {
        if (isTimeInvalid) {
            Date time = null;
            int currentTime = -1;
            try {
                //retrieve current time from ntp server
                time = getCurrentTime_ntpServer(prop);
                if (time == null) {
                    throw new IOException();
                }
                isTimeInvalid = false;
                this.currentTime = time;
            } catch (Exception ex) {
                //use local server time as backup
                Calendar cal = new GregorianCalendar();
                time = cal.getTime();
                isTimeInvalid = false;
                this.currentTime = time;

                throw new NoInternetConnectionException();
            }

        }
        return this.currentTime;
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
        Date d = null;
        try {
            d = getCurrentTime(prop);
        } catch (NoInternetConnectionException ex) {
            d = this.currentTime;
        }
        if (d != null) {
            return d.getTime();
        }
        return -1;
    }

    private Date getCurrentTime_ntpServer(Properties properties) throws Exception {
        NTPUDPClient client = new NTPUDPClient();
        client.setDefaultTimeout(2000);
        try {
            client.open();
            InetAddress hostAddr = InetAddress.getByName(properties.getProperty("ntpserver", "pool.ntp.org"));
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

    @Override
    public int getPercentDoneToNextInterval() {
        return ((ticks - 1) * 100) / interval;
    }

    private boolean isClearBuffersTriggered(int currentTime, Properties prop) {
        String buf = prop.getProperty("bufferclear", "");
        if (buf.equals("")) {
            return false;
        }

        String[] arr = buf.split(",");
        if (arr == null || arr.length == 0) {
            return false;
        }

        //parse string to int and compare to currentTime
        for (int i = 0; i < arr.length; i++) {
            Matcher m = timeFormat.matcher(arr[i]);
            if (m.matches()) {
                int time = Integer.parseInt(m.group(1) + m.group(2));
                if(time == currentTime)
                    return true;
            }
        }
        
        return false;

    }
}
