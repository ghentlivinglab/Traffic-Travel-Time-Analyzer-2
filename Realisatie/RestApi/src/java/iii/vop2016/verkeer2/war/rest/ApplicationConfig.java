/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package iii.vop2016.verkeer2.war.rest;

import java.util.Set;
import javax.ws.rs.core.Application;

/**
 *
 * @author tobia
 */
@javax.ws.rs.ApplicationPath("v2")
public class ApplicationConfig extends Application {

    @Override
    public Set<Class<?>> getClasses() {
        Set<Class<?>> resources = new java.util.HashSet<>();
        addRestResourceClasses(resources);
        return resources;
    }

    /**
     * Do not modify addRestResourceClasses() method.
     * It is automatically populated with
     * all resources defined in the project.
     * If required, comment out calling this method in getClasses().
     */
    private void addRestResourceClasses(Set<Class<?>> resources) {
        resources.add(iii.vop2016.verkeer2.war.rest.GeojsonResource.class);
        resources.add(iii.vop2016.verkeer2.war.rest.LoggingsResource.class);
        resources.add(iii.vop2016.verkeer2.war.rest.ProviderResource.class);
        resources.add(iii.vop2016.verkeer2.war.rest.RoutesResource.class);
        resources.add(iii.vop2016.verkeer2.war.rest.SettingsResource.class);
        resources.add(iii.vop2016.verkeer2.war.rest.ThresholdsResource.class);
        resources.add(iii.vop2016.verkeer2.war.rest.TimersResource.class);
    }
    
}
