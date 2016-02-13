/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ejb;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import javax.ejb.SessionContext;
import javax.ejb.Singleton;
import javax.ejb.Startup;

/**
 *
 * @author tobia
 */
@Singleton
@Startup
public class remote2 implements remote2Remote {
    @Resource
    SessionContext ctx;
    
    public remote2(){
    
    }
    
    @PostConstruct
    public void Init(){
        System.out.println("Startup");
    }

    @Override
    public void lookup1() {
        System.out.println("remote2");
    }
    
    
}
