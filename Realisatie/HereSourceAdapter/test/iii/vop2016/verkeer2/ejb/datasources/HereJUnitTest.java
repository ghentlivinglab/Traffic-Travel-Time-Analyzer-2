/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package iii.vop2016.verkeer2.ejb.datasources;

import iii.vop2016.verkeer2.ejb.components.*;
import iii.vop2016.verkeer2.ejb.datasources.HereSourceAdapter;
import iii.vop2016.verkeer2.ejb.helper.DataAccessException;
import iii.vop2016.verkeer2.ejb.helper.InvalidCoordinateException;
import iii.vop2016.verkeer2.ejb.helper.URLException;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author Simon
 */
public class HereJUnitTest {

    @Test
    public void testWrongInput() {
        try {
            Throwable e = null;

            Route r = new Route();

            //uit ervaring is gebleken dat van 0,0 tot 1,1 geen antwoord mogelijk is
            GeoLocation g1 = new GeoLocation(0, 0);
            GeoLocation g2 = new GeoLocation(1, 1);

            r.addGeolocation(g1);
            r.addGeolocation(g2);

            HereSourceAdapter h = new HereSourceAdapter();
            h.init();

            try {
                h.parse(r,"test");
            } catch (URLException ex) {
                Logger.getLogger(HereJUnitTest.class.getName()).log(Level.SEVERE, null, ex);
            } catch (Throwable ex) {
                e = ex;
            }
            
            assertTrue(e instanceof DataAccessException);
        } catch (InvalidCoordinateException ex) {
            Logger.getLogger(HereJUnitTest.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
