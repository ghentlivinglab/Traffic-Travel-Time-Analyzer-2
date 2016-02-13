/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ejb;

import java.util.Properties;
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
public class remote1 implements remote1Remote {

    @Resource
    SessionContext ctx;
    
    @Resource(lookup = "resources/prop")
    String pf;
    
    @Resource(lookup = "resources/properties")
    Properties config;
    
    @Resource(lookup = "java:global/remote2-ejb/remote2")
    remote2Remote r2;
    
    public remote1(){
    
    }
    
    @PostConstruct
    public void Init(){
        lookup2();
    }
    
    @Override
    public void lookup2() {
        if(ctx != null){
            System.out.println("Remote ejb:");
            r2.lookup1();
            
            System.out.println(pf);
            
            
            String test = (String) config.get("test");
            System.out.println(test);
       
        }else{
            System.out.println("ctx not injected");
        }
    }

    
}
