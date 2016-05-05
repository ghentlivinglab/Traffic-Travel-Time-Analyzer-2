/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package iii.vop2016.verkeer2.ejb.threshold;

import iii.vop2016.verkeer2.ejb.components.IRoute;
import iii.vop2016.verkeer2.ejb.components.IThreshold;
import iii.vop2016.verkeer2.ejb.components.Route;
import iii.vop2016.verkeer2.ejb.components.Threshold;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author tobia
 */
public class ThresholdManagerTest {
    
    public ThresholdManagerTest() {
    }
    
    static ThresholdManager instance;
    static IRoute route;
    
    @BeforeClass
    public static void setUpClass() {
        instance = new ThresholdManager();
        route = new Route();
        route.setId(1);
        route.setName("ok");
    }
    
    @AfterClass
    public static void tearDownClass() {
        instance = null;
    }
    
    @Before
    public void setUp() {
        instance.thresholdMap = new HashMap<>();
        List<IThreshold> ths = new ArrayList<>();
        
        for(int i = 2;i<=5;i++){
            IThreshold th = new Threshold(route, i, i*10);
            ths.add(th);
        }
        ths.add(new Threshold(route, 1, 10));
        
        instance.thresholdMap.put(route, ths);
    }
    
    @After
    public void tearDown() {
        instance.thresholdMap = new HashMap<>();
    }

    /**
     * Test of getThresholdLevel method, of class ThresholdManager.
     */
    @Test
    public void testGetThresholdLevel() throws Exception {
        
    }

    /**
     * Test of EvalThresholdLevel method, of class ThresholdManager.
     */
    @Test
    public void testEvalThresholdLevel() throws Exception {
        
    }

    /**
     * Test of addDefaultThresholds method, of class ThresholdManager.
     */
    @Test
    public void testAddDefaultThresholds() throws Exception {
        
    }

    /**
     * Test of getThresholds method, of class ThresholdManager.
     */
    @Test
    public void testGetThresholds() throws Exception {
        System.out.println("testGetThresholds");
        List<IThreshold> list = instance.getThresholds(route);
        for(int i=1;i<=5;i++){
            if(list.get(i-1).getLevel() != i)
                fail();
        }
    }

    /**
     * Test of ModifyThresholds method, of class ThresholdManager.
     */
    @Test
    public void testModifyThresholds() throws Exception {
        
    }
    
}
