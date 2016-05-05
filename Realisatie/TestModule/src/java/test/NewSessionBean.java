/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package test;

import iii.vop2016.verkeer2.ejb.components.GeoLocation;
import iii.vop2016.verkeer2.ejb.components.IGeoLocation;
import iii.vop2016.verkeer2.ejb.components.IRoute;
import iii.vop2016.verkeer2.ejb.dao.IGeneralDAO;
import iii.vop2016.verkeer2.ejb.helper.BeanFactory;
import java.util.function.Predicate;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import javax.ejb.SessionContext;
import javax.ejb.Singleton;
import javax.ejb.Startup;
import javax.ejb.Timeout;
import javax.ejb.Timer;
import javax.ejb.TimerConfig;
import javax.naming.InitialContext;
import javax.naming.NamingException;

/**
 *
 * @author tobia
 */
@Singleton
@Startup
public class NewSessionBean implements NewSessionBeanLocal {

    @Resource
    SessionContext sctx;
    InitialContext ctx;
    private Timer t;

    @PostConstruct
    private void init() {
        try {
            ctx = new InitialContext();
            t = sctx.getTimerService().createIntervalTimer(1000, 10000, new TimerConfig());
        } catch (NamingException ex) {
            Logger.getLogger(NewSessionBean.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    @Timeout
    public void time(Timer t) {
        if (t.equals(this.t)) {
            try {
                BeanFactory beans = BeanFactory.getInstance(ctx, sctx);

                IGeneralDAO dao = beans.getGeneralDAO();
                IRoute r = dao.getRoute(1);
                r.setName("try me");
                r.removeGeolocation(2);
                
                beans.getGeneralDAO().updateRoute(r);

                System.out.println("test");
            } catch (Exception ex) {
                Logger.getLogger(NewSessionBean.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }

}
