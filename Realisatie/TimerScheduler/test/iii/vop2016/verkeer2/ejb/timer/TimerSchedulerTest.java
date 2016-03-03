/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package iii.vop2016.verkeer2.ejb.timer;

import iii.vop2016.verkeer2.ejb.dummy.BeanFactoryDummy;
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
public class TimerSchedulerTest {
    TimerScheduler instance;
    public TimerSchedulerTest() { 
       instance = new TimerScheduler();
    }
    
    @BeforeClass
    public static void setUpClass() {
    }
    
    @AfterClass
    public static void tearDownClass() {
    }
    
    @Before
    public void setUp() {
        instance.beans = BeanFactoryDummy.getInstance();
    }
    
    @After
    public void tearDown() {
    }

    /**
     * Test of Tick method, of class TimerScheduler.
     */
    @Test
    public void testTick() throws Exception {
        System.out.println("Tick");
        instance.Tick();
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }
    
}
