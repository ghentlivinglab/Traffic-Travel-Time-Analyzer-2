/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package iii.vop2016.verkeer2.ejb.components;

import java.util.ArrayList;
import java.util.List;
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
    public void testInsertGeoLocation(){
        IGeoLocation gl1= new GeoLocation(10,10);
        IGeoLocation gl2= new GeoLocation(20,20);
        IGeoLocation gl3= new GeoLocation(30,30);
        List<IGeoLocation> l= new ArrayList<>();
        l.add(gl1);
        l.add(gl2);
        l.add(gl3);
        Route route = new Route();
        route.setGeolocations(l);
        
        IGeoLocation gl4= new GeoLocation(25,25);
        route.addGeolocation(gl4, 2);
        List<IGeoLocation> result= route.getGeolocations();
        for (int i=0;i<result.size();i++){
            assertEquals(i+1,result.get(i).getSortRank());
        }
        
        IGeoLocation glresult = result.get(2);
        assertEquals(gl4,glresult);     
    }
}
