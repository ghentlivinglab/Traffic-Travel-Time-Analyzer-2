/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package iii.vop2016.verkeer2.ejb.datasources;

import iii.vop2016.verkeer2.ejb.components.IRoute;
import iii.vop2016.verkeer2.ejb.components.IRouteData;
import iii.vop2016.verkeer2.ejb.components.RouteData;
import iii.vop2016.verkeer2.ejb.helper.DataAccessException;
import iii.vop2016.verkeer2.ejb.helper.URLException;
import java.sql.Timestamp;
import java.util.Date;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.annotation.PostConstruct;
import javax.ejb.Singleton;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.firefox.FirefoxDriver;

/**
 *
 * @author Simon
 */
@Singleton
public class CoyoteSourceAdapter implements CoyoteSourceAdapterRemote {

    private static final String providerName = "Coyote";
    private static final String login = "110971610";
    private static final String password = "50c20b94";

    private static long lastDownload = 0;
    private static long timeDifference = 240000; // 4 min = 4*60*1000
    private static String downloadedString = null;

    @PostConstruct
    public void init() {
        Logger.getLogger("logger").log(Level.INFO, providerName + "SourceAdapter has been initialized.");
    }

    @Override
    public IRouteData parse(IRoute route) throws URLException, DataAccessException {
        Date current = new Date();
        long currentLong = current.getTime();

        //initalisatie nodige objecten
        RouteData rd = null;
        int distance = 0;
        int seconds = 0;

        //ik kijk na of er al een keer gedownload is
        //of of de huidige timeStamp minstens 4 minuten groter is dan lastDownload
        if (lastDownload == 0 || (currentLong - timeDifference) > lastDownload) {
            try {
                download();
            } catch (Exception e) {
                throw new URLException("Website " + providerName + " doesn't work");
            }
        }

        //onderstaande code zal ik nog opkuisen & aanvullen met excepties en dergelijke
        try {
            rd = new RouteData();
            String baseString = route.getName();

            String baseToRegexString = baseString.replaceAll("\\(", "\\\\(").replaceAll("\\)", "\\\\)");
            //  System.out.println(test);

            String pattern = "(?m)((" + baseToRegexString + "(.*))\\+.*)";
            //  System.out.println(pattern);
            // Create a Pattern object
            Pattern p1 = Pattern.compile(pattern, Pattern.CASE_INSENSITIVE);

            // Now create matcher object.
            Matcher m1 = p1.matcher(downloadedString);
            String coyoteString = null;
            if (m1.find()) {
                coyoteString = m1.group(0).trim();
                //  System.out.println(coyoteString);
                //    String s2 = m1.group(2).trim();
                //     System.out.println("Traject: " + s2);
                //   System.out.println("Found value: " + m.group(1) );
                // System.out.println("Found value: " + m.group(4) );
            } else {
                System.out.println("NO MATCH");
            }

            String pattern2 = "\\((\\d*)m\\)";

            // Create a Pattern object
            Pattern p2 = Pattern.compile(pattern2);
            // Now create matcher object.
            Matcher m2 = p2.matcher(coyoteString);
            if (m2.find()) {
                seconds = Integer.parseInt(m2.group(1)) * 60;
                //System.out.println("Reistijd: " + seconds);
                //   System.out.println("Found value: " + m.group(1) );
                //   System.out.println("Found value: " + m.group(2) );
            } else {
                System.out.println("NO MATCH");
            }

            String pattern3 = "(\\d*\\.\\d*) km";

            // Create a Pattern object
            Pattern r3 = Pattern.compile(pattern3);
            // Now create matcher object.
            Matcher m3 = r3.matcher(coyoteString);
            if (m3.find()) {
                distance = (int) (Double.parseDouble(m3.group(1)) * 1000);
                //  int s = j;
                //System.out.println("Afstand: " + distance);
                //   System.out.println("Found value: " + m.group(1) );
                //   System.out.println("Found value: " + m.group(2) );
            } else {
                System.out.println("NO MATCH");
            }

            rd.setProvider(getProviderName());
            rd.setDistance(distance);
            rd.setDuration(seconds);
            rd.setRouteId(route.getId());
            rd.setTimestamp(new Date());
        } catch (Exception e) {
            throw new DataAccessException("Can't find Regex-match in " + providerName + "for this route: " + route.getName());
        }
        return rd;
    }

    private WebDriver startDriver() throws URLException {
        Date current = new Date();
        lastDownload = current.getTime(); //laatste download = nu
        WebDriver driver = null;
        try {
            driver = new FirefoxDriver();
            driver.get("https://maps.coyotesystems.com/traffic/");
            //  driver.manage().window().maximize();
            driver.findElement(By.name("login")).sendKeys(login);
            driver.findElement(By.name("password")).sendKeys(password);
            driver.manage().timeouts().implicitlyWait(60, TimeUnit.SECONDS);
            driver.findElement(By.xpath("//input[@type='submit']")).click();
        } catch (Exception e) {
            throw new URLException("Website " + providerName + " doesn't work");
        }
        return driver;
    }

    private void download() throws URLException {
        try {
            WebDriver driver = startDriver();
            driver.findElement(By.id("location_list")).click(); //p1
            downloadedString = driver.findElement(By.id("path_list")).findElement(By.xpath("table/tbody")).getText(); //overschrijf oude downloadedString

            driver.findElement(By.id("path_list")).findElement(By.className("next")).click(); //p2
            downloadedString += driver.findElement(By.id("path_list")).findElement(By.xpath("table/tbody")).getText();

            driver.findElement(By.id("path_list")).findElement(By.className("next")).click(); //p3
            downloadedString += driver.findElement(By.id("path_list")).findElement(By.xpath("table/tbody")).getText();

            driver.findElement(By.id("path_list")).findElement(By.className("next")).click(); //p4
            downloadedString += driver.findElement(By.id("path_list")).findElement(By.xpath("table/tbody")).getText();

            driver.close();
        } catch (Exception e) {
            throw new URLException("Website " + providerName + " doesn't work");
        }
        //ofwel laten we de driver altijd openstaan?
        //voordeel: hij moet Firefox niet altijd opnieuw starten
        //nadeel: misschien geraakt hij na een tijdje zijn sessie kwijt / wat als Firefox vanzelf crasht na een uur of 3?
    }

    @Override
    public String getProviderName() {
        return providerName;
    }

    // Add business logic below. (Right-click in editor and choose
    // "Insert Code > Add Business Method")
}
