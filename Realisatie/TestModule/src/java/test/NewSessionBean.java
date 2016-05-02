/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package test;

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
    private void init(){
        try {
            ctx = new InitialContext();
            t= sctx.getTimerService().createIntervalTimer(1000, 10000, new TimerConfig());
        } catch (NamingException ex) {
            Logger.getLogger(NewSessionBean.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    @Timeout
    public void time(Timer t){
        if(t.equals(this.t)){
            try {
                
                Object local = ctx.lookup("java:global/CoyoteSourceAdapter/CoyoteSourceAdapter!iii.vop2016.verkeer2.ejb.datasources.SourceAdapterLocal");
                Object remote = ctx.lookup("java:global/CoyoteSourceAdapter/CoyoteSourceAdapter!iii.vop2016.verkeer2.ejb.datasources.SourceAdapterRemote");
                Object none = sctx.lookup("java:global/CoyoteSourceAdapter/CoyoteSourceAdapter");
                System.out.println("test");
            } catch (NamingException ex) {
                Logger.getLogger(NewSessionBean.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }
    
}
