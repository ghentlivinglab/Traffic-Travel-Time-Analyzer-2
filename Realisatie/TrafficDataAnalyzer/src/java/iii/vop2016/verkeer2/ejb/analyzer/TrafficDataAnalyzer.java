/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package iii.vop2016.verkeer2.ejb.analyzer;

import iii.vop2016.verkeer2.ejb.helper.BeanFactory;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import javax.ejb.SessionContext;
import javax.ejb.Singleton;
import javax.naming.InitialContext;
import javax.naming.NamingException;

/**
 *
 * @author tobia
 */

@Singleton
public class TrafficDataAnalyzer implements TrafficDataAnalyzerRemote {
    
    @Resource
    private SessionContext sctx;
    private InitialContext ctx;
    
    private BeanFactory beans;
    
    @PostConstruct
    private void init(){
        try {
            ctx = new InitialContext();
        } catch (NamingException ex) {
            Logger.getLogger(TrafficDataAnalyzer.class.getName()).log(Level.SEVERE, null, ex);
        }
        beans = BeanFactory.getInstance(ctx, sctx);
    }

    // Add business logic below. (Right-click in editor and choose
    // "Insert Code > Add Business Method")

    @Override
    public String getProjectName() {
        return "Verkeer-2";
    }
}
