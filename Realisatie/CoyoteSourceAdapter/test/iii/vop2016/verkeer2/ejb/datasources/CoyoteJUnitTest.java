/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package iii.vop2016.verkeer2.ejb.datasources;

import iii.vop2016.verkeer2.ejb.components.GeoLocation;
import iii.vop2016.verkeer2.ejb.components.Route;
import iii.vop2016.verkeer2.ejb.helper.DataAccessException;
import iii.vop2016.verkeer2.ejb.helper.InvalidCoordinateException;
import iii.vop2016.verkeer2.ejb.helper.URLException;
import java.util.logging.Level;
import java.util.logging.Logger;
import static org.junit.Assert.assertTrue;
import org.junit.Test;

/**
 *
 * @author Simon
 */
public class CoyoteJUnitTest {

    @Test
    public void testWrongInput() {
        try {
            Throwable e = null;

            Route r = new Route("Random naam"); //deze naam staat niet op Coyote dus levert regex-mismatch op

            GeoLocation g1 = new GeoLocation(0, 0);
            GeoLocation g2 = new GeoLocation(1, 1);

            r.addGeolocation(g1);
            r.addGeolocation(g2);

            CoyoteSourceAdapter c = new CoyoteSourceAdapter();
            c.init();

            try {
                c.parse(r,"test");
            } catch (URLException ex) {
                Logger.getLogger(CoyoteJUnitTest.class.getName()).log(Level.SEVERE, null, ex);
            } catch (Throwable ex) {
                e = ex;
            }

            assertTrue(e instanceof DataAccessException);
        } catch (InvalidCoordinateException ex) {
            Logger.getLogger(CoyoteJUnitTest.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

}
