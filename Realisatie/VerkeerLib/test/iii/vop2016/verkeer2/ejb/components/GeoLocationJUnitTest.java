/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package iii.vop2016.verkeer2.ejb.components;

import iii.vop2016.verkeer2.ejb.helper.InvalidCoordinateException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author Gebruiker
 */
public class GeoLocationJUnitTest {

    public GeoLocationJUnitTest() {
    }

    @BeforeClass
    public static void setUpClass() {
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

    // TODO add test methods here.
    // The methods must be annotated with annotation @Test. For example:
    //
    // @Test
    // public void hello() {}
    @Test
    public void testInsertGeoLocation() {
        try {
            IGeoLocation gl1 = new GeoLocation(10, 10);
            IGeoLocation gl2 = new GeoLocation(20, 20);
            IGeoLocation gl3 = new GeoLocation(30, 30);
            List<IGeoLocation> l = new ArrayList<>();
            l.add(gl1);
            l.add(gl2);
            l.add(gl3);
            Route route = new Route();
            route.setGeolocations(l);

            IGeoLocation gl4 = new GeoLocation(25, 25);
            route.addGeolocation(gl4, 2);
            List<IGeoLocation> result = route.getGeolocations();
            for (int i = 0; i < result.size(); i++) {
                assertEquals(i + 1, result.get(i).getSortRank());
            }

            IGeoLocation glresult = result.get(2);
            assertEquals(gl4, glresult);
        } catch (InvalidCoordinateException ex) {
            Logger.getLogger(GeoLocationJUnitTest.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    @Test
    public void testInvalidLatitudeException() {
        Throwable e = null;
        try {
            GeoLocation g = new GeoLocation(-86, 0);
        } catch (Throwable ex) {
            e = ex;
        }
        assertTrue(e instanceof InvalidCoordinateException);

        e = null;
        try {
            GeoLocation g = new GeoLocation(86, 0);
        } catch (Throwable ex) {
            e = ex;
        }
        assertTrue(e instanceof InvalidCoordinateException);
    }

    @Test
    public void testInvalidLongitudeException() {
        Throwable e = null;
        try {
            GeoLocation g = new GeoLocation(0, -181);
        } catch (Throwable ex) {
            e = ex;
        }
        assertTrue(e instanceof InvalidCoordinateException);

        e = null;
        try {
            GeoLocation g = new GeoLocation(0, 181);
        } catch (Throwable ex) {
            e = ex;
        }
        assertTrue(e instanceof InvalidCoordinateException);
    }
}
