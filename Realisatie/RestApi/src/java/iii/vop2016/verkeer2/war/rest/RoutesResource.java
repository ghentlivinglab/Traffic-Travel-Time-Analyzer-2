/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package iii.vop2016.verkeer2.war.rest;

import iii.vop2016.verkeer2.ejb.analyzer.IAnalyzer;
import iii.vop2016.verkeer2.ejb.components.IGeoLocation;
import iii.vop2016.verkeer2.ejb.components.IRoute;
import iii.vop2016.verkeer2.ejb.components.Route;
import iii.vop2016.verkeer2.ejb.helper.BeanFactory;
import java.util.ArrayList;
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

    /**
     * Retrieves representation of an instance of iii.vop2016.verkeer2.war.rest.RoutesResource
     * @return an instance of java.lang.String
     */
    @GET
    @Produces("application/xml")
    public String getXml() {
        IAnalyzer analyzer = beans.getAnalyzer();
        
        IRoute r = new Route();
        r.setName("test");
        r.setGeoLocations(new ArrayList<IGeoLocation>());
        r.setInverseRoute(r);
        
        IRoute r2 = new Route();
        r2.setName("test2");
        r2.setGeoLocations(new ArrayList<IGeoLocation>());
        r2.setInverseRoute(r);
        
        analyzer.addRoute(r);
        analyzer.addRoute(r2);
        //TODO return proper representation object
        return "<test>"+analyzer.getProjectName()+"</test>";
    }

    /**
     * PUT method for updating or creating an instance of RoutesResource
     * @param content representation for the resource
     * @return an HTTP response with content of the updated or created resource.
     */
    @PUT
    @Consumes("application/xml")
    public void putXml(String content) {
    }
}
