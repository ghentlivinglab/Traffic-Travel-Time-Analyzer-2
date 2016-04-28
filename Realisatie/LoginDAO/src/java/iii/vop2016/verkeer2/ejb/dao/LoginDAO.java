/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package iii.vop2016.verkeer2.ejb.dao;

import iii.vop2016.verkeer2.bean.auth.AuthUser;
import iii.vop2016.verkeer2.ejb.helper.BeanFactory;
import java.sql.PreparedStatement;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import javax.ejb.SessionContext;
import javax.ejb.Singleton;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;


@Singleton
public class LoginDAO implements LoginDAORemote {

    @PersistenceContext(name = "GeneralDBPU")
    EntityManager em;
    private InitialContext ctx;
    @Resource
    private SessionContext sctx;
    private BeanFactory beans;

    public LoginDAO() {
        System.out.println("User aangemaakt");
    }

    @PostConstruct
    public void init() {
        try {
            ctx = new InitialContext();
        } catch (NamingException ex) {
            Logger.getLogger(LoginDAO.class.getName()).log(Level.SEVERE, null, ex);
        }

        beans = BeanFactory.getInstance(ctx, sctx);
        beans.getLogger().log(Level.INFO, "LoginDAO has been initialized.");
        
    }

    @Override
    public boolean validate(String user, String password) {
        try {
            
            TypedQuery<AuthUser> query = em.createQuery("SELECT u FROM AuthUser AS u WHERE u.username = :username AND u.password = :password", AuthUser.class);
            query.setParameter("username", user);
            query.setParameter("password", password);
            List<AuthUser> users = query.getResultList();
            
            if(users.size()>0){
                return true;
            }
            return false;
           
        } catch (Exception ex) {
            System.out.println("Login error -->" + ex.getMessage());
            return false;
        }
        
    }


}
