/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package iii.vop2016.verkeer2.ejb.dao;

import iii.vop2016.verkeer2.bean.auth.AuthHelpers;
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
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import javax.persistence.TypedQuery;

@Singleton
public class LoginDAO implements LoginDAORemote, LoginDAOLocal {

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
            query.setParameter("password", AuthHelpers.getMD5(password));
            System.out.println(AuthHelpers.getMD5(password));
            List<AuthUser> users = query.getResultList();

            if (users.size() > 0) {
                return true;
            }
            return false;

        } catch (Exception ex) {
            System.out.println("Login error -->" + ex.getMessage());
            return false;
        }
    }
    
    @Override
    public AuthUser getUser(String user, String password) {
        try {
            TypedQuery<AuthUser> query = em.createQuery("SELECT u FROM AuthUser AS u WHERE u.username = :username AND u.password = :password", AuthUser.class);
            query.setParameter("username", user);
            query.setParameter("password", AuthHelpers.getMD5(password));
            List<AuthUser> users = query.getResultList();

            if (users.size() > 0) {
                return users.get(0);
            }
            return null;

        } catch (Exception ex) {
            System.out.println("Login error -->" + ex.getMessage());
            return null;
        }
    }

    @Override
    public AuthUser getUser(int id) {
        try {
            TypedQuery<AuthUser> q = em.createQuery("SELECT u FROM AuthUser AS u WHERE u.id =:id", AuthUser.class);
            q.setParameter("id", id);
            AuthUser user = q.getSingleResult();
            return user;
        } catch (NoResultException e) {
            return null;
        }
    }

    @Override
    public void updateUser(AuthUser user) {
        Query update = em.createQuery("UPDATE AuthUser AS u SET u.name = :name , u.password =:password, u.username =:username WHERE u.id =:id");
        update.setParameter("name", user.getName());
        update.setParameter("password", user.getPassword());
        update.setParameter("username", user.getUsername());
        update.setParameter("id", user.getId());
        update.executeUpdate();
    }
    
    

}
