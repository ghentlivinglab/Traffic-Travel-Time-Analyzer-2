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
import java.util.ArrayList;
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
        System.out.println("Setup");

        instance = new GeoJsonProvider();
        instance.init();

        System.out.println("Setup done");

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
     * Test of getRoutePlotGeoLocations method, of class GeoJsonProvider.
     */
    @Test
    public void testGetRoutePlotGeoLocations() throws Exception {
        System.out.println("getRoutePlotGeoLocations");

        IRoute r = CreateTestRoute();
        List<IGeoLocation> list = instance.getRoutePlotGeoLocations(r);

        assertEquals(84, list.size());
    }

    @Test
    public void testGetGeoJson() throws Exception {
        System.out.println("getGeoJson");
        IRoute r = CreateTestRoute();
        IRoute r2 = CreateTestRoute2();

        List<IGeoLocation> list = instance.getRoutePlotGeoLocations(r);
        List<IGeoLocation> list2 = instance.getRoutePlotGeoLocations(r2);

        Map<IRoute, List<IGeoLocation>> map = new HashMap<>();
        map.put(r, list);
        map.put(r2, list2);

        Map<IRoute, Integer> del = new HashMap<>();
        del.put(r, 0);
        del.put(r2, 1);

        String json = instance.getGeoJson(map, del);
        System.out.println("dq");
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

    private IRoute CreateTestRoute2() {
        try {
            IRoute r = new Route("R4 Zelzate andere");

            //r.setInverseRoute(r);
            IGeoLocation geolocation1 = new GeoLocation(51.195338, 3.828952);
            IGeoLocation geolocation2 = new GeoLocation(51.085447, 3.755755);
            geolocation1.setName("Zelzate");
            geolocation2.setName("Gent");
            r.addGeolocation(geolocation1);
            r.addGeolocation(geolocation2);
            r.setId(1);
            return r;
        } catch (InvalidCoordinateException ex) {
            Logger.getLogger(GeoJsonProviderTest.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }

}
