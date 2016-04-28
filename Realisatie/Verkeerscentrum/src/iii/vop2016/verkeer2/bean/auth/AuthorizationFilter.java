/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package iii.vop2016.verkeer2.bean.auth;

import java.io.IOException;
import javax.faces.bean.RequestScoped;
import javax.faces.bean.SessionScoped;
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
 
@WebFilter(filterName = "AuthFilter")
@RequestScoped
public class AuthorizationFilter implements Filter {
 
    public AuthorizationFilter() {
    }
 
    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
 
    }
 
    @Override
    public void doFilter(ServletRequest request, ServletResponse response,
            FilterChain chain) throws IOException, ServletException {
        try {
 
            HttpServletRequest reqt = (HttpServletRequest) request;
            HttpServletResponse resp = (HttpServletResponse) response;
            HttpSession ses = reqt.getSession(false);
            
            String reqURI = reqt.getRequestURL() + "?" + reqt.getQueryString();
            
            //System.out.println(ses);
            if (reqURI.indexOf("/login") >= 0
                    || (ses != null && ses.getAttribute("username") != null)
                    || reqURI.indexOf("/public/") >= 0
                    || reqURI.contains("javax.faces.resource")){
                
                if(reqURI.indexOf("/login") >= 0 && (ses != null && ses.getAttribute("username") != null)){
                   resp.sendRedirect( reqt.getContextPath() );
                   System.out.println("GA NAAR " + reqt.getContextPath());
                }else{
                    chain.doFilter(request, response);
                }
                // chain.doFilter(request, response);
            }else{
                resp.sendRedirect(reqt.getContextPath() + "/login");
                ses.setAttribute("urlAfterLogin", reqURI);
            }
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }
 
    @Override
    public void destroy() {
 
    }
}