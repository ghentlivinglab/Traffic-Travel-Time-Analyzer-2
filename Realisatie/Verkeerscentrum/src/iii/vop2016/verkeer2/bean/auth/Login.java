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
@RequestScoped
public class Login implements Serializable{
 
    private static final long serialVersionUID = 1094801825228386363L;

    private InitialContext ctx;
    private BeanFactory beanFactory;
    
    @ManagedProperty(value="#{sessionBean}")
    private SessionBean sessionBean;
     
    private String pwd;
    private String msg;
    private String username;
    private String url;

    public Login() {
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
        url = (String) sessionBean.getSession().getAttribute("urlAfterLogin");
        System.out.println("urlAfterLogin = " + url);
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
 
    public String getUsername() {
        return username;
    }
 
    public void setUsername(String username) {
        this.username = username;
    }
 
    //validate login
    public String validateUsernamePassword() {
        ILoginDAO loginDAO = beanFactory.getLoginDAO();
        boolean valid = loginDAO.validate(username, pwd);
        if (valid) {
            AuthUser user = loginDAO.getUser(username,pwd);
            sessionBean.getSession().setAttribute("username", user.getUsername());
            sessionBean.getSession().setAttribute("userid", user.getId());
            sessionBean.getSession().setAttribute("user", user);
            ExternalContext ec = FacesContext.getCurrentInstance().getExternalContext();
            try {                
                ec.redirect(url);
            } catch (Exception ex) {
                Logger.getLogger(Login.class.getName()).log(Level.SEVERE, null, ex);
            }
            return "dashboard";
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