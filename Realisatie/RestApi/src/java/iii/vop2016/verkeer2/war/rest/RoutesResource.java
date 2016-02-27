/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package iii.vop2016.verkeer2.war.rest;

import iii.vop2016.verkeer2.ejb.analyzer.IAnalyzer;
import iii.vop2016.verkeer2.ejb.components.GeoLocation;
import iii.vop2016.verkeer2.ejb.components.IGeoLocation;
import iii.vop2016.verkeer2.ejb.components.IRoute;
import iii.vop2016.verkeer2.ejb.components.Route;
import iii.vop2016.verkeer2.ejb.dummy.BeanFactoryDummy;
import iii.vop2016.verkeer2.ejb.helper.BeanFactory;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import javax.ejb.SessionContext;
import javax.faces.bean.RequestScoped;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.UriInfo;
import javax.ws.rs.PathParam;
import javax.ws.rs.Consumes;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.GET;
import javax.ws.rs.Produces;

/**
 * REST Web Service
 *
 * @author tobia
 */
@Path("routes")
@RequestScoped
public class RoutesResource {

    @Context
    private UriInfo context;
    
    @Resource
    private SessionContext sctx;
    private InitialContext ctx;
    private static BeanFactory beans;

    /**
     * Creates a new instance of RoutesResource
     */
    public RoutesResource() {
    }
    
    @PostConstruct
    private void init() {
        try {
            ctx = new InitialContext();
        } catch (NamingException ex) {
            Logger.getLogger(RoutesResource.class.getName()).log(Level.SEVERE, null, ex);
        }
        beans = BeanFactory.getInstance(ctx, sctx);
    }

    
    @GET
    @Path("all")
    @Produces("text/html")
    public String getAllRoutes() {
        
        List<IRoute> routes = beans.getGeneralDAO().getRoutes();
        
        StringBuilder sb = new StringBuilder();
        sb.append("<h1>Alle routes</h1> <br>");
        sb.append("<ul>");
        for(int i=0; i<routes.size(); i++){
            sb.append("<ol>"+routes.get(i)+"</ol>");
        }
        sb.append("</ul>");
        return sb.toString();
        
    }
    
    
    
    /**
     * Retrieves representation of an instance of iii.vop2016.verkeer2.war.rest.RoutesResource
     * @return an instance of java.lang.String
     */
    @GET
    @Path("init")
    @Produces("application/xml")
    public String initRoutes() {
                
        IAnalyzer analyzer = beans.getAnalyzer();
        
        /* //** DB DAO **
        IRoute r = new Route();
        r.setName("test1");
        r.setInverseRoute(new Route());
        IGeoLocation geolocation1 = new GeoLocation(50.6565, 51.2566);
        geolocation1.setName("De Brug");
        geolocation1.setRoute(r);
        r.addGeolocation(geolocation1);
        analyzer.addRoute(r);
        
        
        IRoute r2 = new Route();
        r2.setName("test2");
        r2.setInverseRoute(r);
        IGeoLocation geolocation2 = new GeoLocation(50.6, 51.5);
        geolocation1.setName("Home Fabiola");
        geolocation1.setRoute(r2);
        r2.addGeolocation(geolocation2);
        analyzer.addRoute(r2);
        */
        
        IRoute r = new Route("R4 Gent");
        //r.setInverseRoute(r);
        IGeoLocation geolocation1 = new GeoLocation(51.039687, 3.726789);
        IGeoLocation geolocation2 = new GeoLocation(51.022711, 3.686469);
        geolocation1.setName("Home Fabiola");
        geolocation2.setName("Ikea Gent");
        r.addGeolocation(geolocation1);
        r.addGeolocation(geolocation2);
        r = analyzer.addRoute(r);
        
        
        List<IRoute> routes = beans.getGeneralDAO().getRoutes();
        for(IRoute route : routes){
            System.out.println("");
            System.out.println("Ik ben een Route met volgende eigenschappen:");
            System.out.println("ID: "+route.getId());
            System.out.println("Name: "+route.getName());
            System.out.println("Inverse: "+route.getInverseRoute());
            System.out.println("Geolocaties: "+route.getGeolocations());
        }
        
        
        
        //TODO return proper representation object
        return "<test>"+analyzer.getProjectName()+"</test>";
    }


}
