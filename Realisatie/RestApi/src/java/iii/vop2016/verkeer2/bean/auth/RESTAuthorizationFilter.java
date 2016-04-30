/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package iii.vop2016.verkeer2.bean.auth;

import iii.vop2016.verkeer2.ejb.helper.BeanFactory;
import iii.vop2016.verkeer2.war.rest.RoutesResource;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.faces.bean.RequestScoped;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

@WebFilter(filterName = "RESTAuthFilter")
//@RequestScoped
public class RESTAuthorizationFilter implements Filter {

    private InitialContext ctx;
    private static BeanFactory beans;

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        try {
            ctx = new InitialContext();
        } catch (NamingException ex) {
            Logger.getLogger(RoutesResource.class.getName()).log(Level.SEVERE, null, ex);
        }
        beans = BeanFactory.getInstance(ctx, null);
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        HttpServletRequest reqt = (HttpServletRequest) request;
        HttpServletResponse resp = (HttpServletResponse) response;
        //HttpSession ses = reqt.getSession(false);
        String keyString = reqt.getParameter("key");
        if (keyString == null) {
            resp.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Provide activated key.");
        } else if (beans.getAPIKeyDAO().validate(keyString)) {
            chain.doFilter(request, response);
        } else {
            resp.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Provide activated key.");
        }
        //resp.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Provide activated key.");
    }

    @Override
    public void destroy() {

    }

}
