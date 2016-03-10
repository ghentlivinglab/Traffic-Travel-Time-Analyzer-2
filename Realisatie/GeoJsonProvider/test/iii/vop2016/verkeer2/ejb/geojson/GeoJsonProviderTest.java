/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package iii.vop2016.verkeer2.ejb.geojson;

import iii.vop2016.verkeer2.ejb.components.GeoLocation;
import iii.vop2016.verkeer2.ejb.components.IGeoLocation;
import iii.vop2016.verkeer2.ejb.components.IRoute;
import iii.vop2016.verkeer2.ejb.components.Route;
import iii.vop2016.verkeer2.ejb.helper.InvalidCoordinateException;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.ejb.embeddable.EJBContainer;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author Tobias
 */
public class GeoJsonProviderTest {


    public GeoJsonProviderTest() {
    }

    static GeoJsonProvider instance;
    
    @BeforeClass
    public static void setUpClass() {
        try {
            instance = new GeoJsonProvider();
            instance.properties = new Properties();
            instance.properties.load(new FileInputStream("C:/Users/tobia/Documents/baproef/verkeer-2/Realisatie/GeoJsonProvider.properties"));
            instance.init();
        } catch (IOException ex) {
            Logger.getLogger(GeoJsonProviderTest.class.getName()).log(Level.SEVERE, null, ex);
        }
        
    }

    @AfterClass
    public static void tearDownClass() {

    }

    @Before
    public void setUp() {
    }

    @After
    public void tearDown() {
    }

    /**
     * Test of getGeoJson method, of class GeoJsonProvider.
     */
    @Test
    public void testGetGeoJson() throws Exception {
        System.out.println("getGeoJson");

        IRoute r = CreateTestRoute();
        List<IGeoLocation> list = instance.getGeoJson(r);

        assertEquals(84,list.size());
    }

    private IRoute CreateTestRoute() {
        try {
            IRoute r = new Route("R4 Zelzate");

            //r.setInverseRoute(r);
            IGeoLocation geolocation1 = new GeoLocation(51.192226, 3.776342);
            IGeoLocation geolocation2 = new GeoLocation(51.086447, 3.672188);
            geolocation1.setName("Zelzate");
            geolocation2.setName("Gent");
            r.addGeolocation(geolocation1);
            r.addGeolocation(geolocation2);
            return r;
        } catch (InvalidCoordinateException ex) {
            Logger.getLogger(GeoJsonProviderTest.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }

}
