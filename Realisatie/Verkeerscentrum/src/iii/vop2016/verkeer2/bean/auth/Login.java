/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package iii.vop2016.verkeer2.bean.auth;
 
import iii.vop2016.verkeer2.ejb.dao.ILoginDAO;
import iii.vop2016.verkeer2.ejb.helper.BeanFactory;
import java.io.IOException;
import java.io.Serializable;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.annotation.Resource;
import javax.ejb.SessionContext;
 
import javax.faces.application.FacesMessage;
import javax.faces.bean.ApplicationScoped;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.RequestScoped;
import javax.faces.bean.SessionScoped;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.servlet.http.HttpSession;
 
@ManagedBean
@SessionScoped
public class Login implements Serializable{
 
    private static final long serialVersionUID = 1094801825228386363L;

    private InitialContext ctx;
    private BeanFactory beanFactory;
    
    @ManagedProperty(value="#{sessionBean}")
    private SessionBean sessionBean;
     
    private String pwd;
    private String msg;
    private String user;
    private String url;

    public Login() {
        System.out.println("Login.java Session started");
        //Initialize bean and its context
        try {
            ctx = new InitialContext();
        } catch (NamingException ex) {
            Logger.getLogger(Login.class.getName()).log(Level.SEVERE, null, ex);
        }
        beanFactory = BeanFactory.getInstance(ctx, null);
    }
 
    //must povide the setter method
    public void setSessionBean(SessionBean sessionBean) {
        this.sessionBean = sessionBean;
        System.out.println("sessionBean setter");
        url = sessionBean.getRequest().getParameter("url");
        if(url == null) url = "/web";
        System.out.println("username = "+sessionBean.getSession().getAttribute("username"));
    }
    
    public String getPwd() {
        return pwd;
    }
 
    public void setPwd(String pwd) {
        this.pwd = pwd;
    }
 
    public String getMsg() {
        return msg;
    }
 
    public void setMsg(String msg) {
        this.msg = msg;
    }
 
    public String getUser() {
        return user;
    }
 
    public void setUser(String user) {
        this.user = user;
        System.out.println("user werd gezet "+user);
    }
 
    //validate login
    public String validateUsernamePassword() {
        ILoginDAO loginDAO = beanFactory.getLoginDAO();
        boolean valid = loginDAO.validate(user, pwd);
        if (valid) {
            sessionBean.getSession().setAttribute("username", user);
            System.out.println("ingelogd als "+sessionBean.getSession().getAttribute("username"));
            ExternalContext ec = FacesContext.getCurrentInstance().getExternalContext();
            try {                
                ec.redirect(url);
            } catch (IOException ex) {
                Logger.getLogger(Login.class.getName()).log(Level.SEVERE, null, ex);
            }
            return "live";
        } else {
            msg = "fout wachtwoord";
            FacesContext.getCurrentInstance().addMessage(
                    null,
                    new FacesMessage(FacesMessage.SEVERITY_WARN,
                            "Incorrect Username and Passowrd",
                            "Please enter correct username and Password"));
            return "login";
        }
    }
 
    //logout event, invalidate session
    public String logout() {
        sessionBean.getSession().invalidate();
        return "login";
    }
}