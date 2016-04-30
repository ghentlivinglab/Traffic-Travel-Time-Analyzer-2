/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package iii.vop2016.verkeer2.ejb.dao;

import iii.vop2016.verkeer2.bean.APIKey.APIKey;
import iii.vop2016.verkeer2.ejb.helper.BeanFactory;
import java.security.SecureRandom;
import java.util.List;
import java.util.Random;
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

/**
 *
 * @author Gebruiker
 */
@Singleton
public class APIKeyDAO implements APIKeyDAORemote {
    
    @PersistenceContext(name = "GeneralDBPU")
    EntityManager em;
    private InitialContext ctx;
    @Resource
    private SessionContext sctx;
    private BeanFactory beans;
    
    static char[] charset = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789abcdefghijklmnopqrstuvwxyz".toCharArray();

    public APIKeyDAO() {
        System.out.println("APIKeyDAO aangemaakt");
    }

    @PostConstruct
    public void init() {
        try {
            ctx = new InitialContext();
        } catch (NamingException ex) {
            Logger.getLogger(APIKeyDAO.class.getName()).log(Level.SEVERE, null, ex);
        }

        beans = BeanFactory.getInstance(ctx, sctx);
        beans.getLogger().log(Level.INFO, "APIKeyDAO has been initialized.");
        
    }
    
    @Override
    public APIKey insertNewRandomKey(){
        Random random = new SecureRandom();
        char[] result = new char[15];
        for (int i = 0; i < result.length; i++) {
            // picks a random index out of character set > random character
            int randomCharIndex = random.nextInt(charset.length);
            result[i] = charset[randomCharIndex];
        }
        APIKey key = new APIKey();
        key.setActive(0);
        key.setKeyString(new String(result));
        em.persist(key);
        beans.getLogger().log(Level.INFO, "Random key generated");
        return key;
    }
    
    @Override
    public String getKey(){
        //try{
            
            TypedQuery<APIKey> query = em.createQuery("SELECT u FROM APIKey AS u WHERE u.active =0", APIKey.class);
            //query.setParameter("isActive", 0);
            List<APIKey> keys = query.getResultList();
            if(keys.size()>0){
                APIKey key=keys.get(0);
                key.setActive(1);
                beans.getLogger().log(Level.INFO, "A key has been activated");
                return key.getKeyString();
            }else{
                APIKey key= insertNewRandomKey();
                key.setActive(1);
                beans.getLogger().log(Level.INFO, "A key has been activated");
                return key.getKeyString();
            }
        //}catch(Exception ex){
            //TODO
        //}
        
        //return null;
    }
    
    @Override
    public boolean validate(String key) {
        
        beans.getLogger().log(Level.INFO,"Validate APIKey");
        
        TypedQuery<APIKey> query = em.createQuery("SELECT u FROM APIKey AS u WHERE u.keyString = :keyString", APIKey.class);
        query.setParameter("keyString", key);
        List<APIKey> keys = query.getResultList();
        if(keys.size()>0){
            if (keys.get(0).getActive() == 1){
                return true;
            }
            else{
                return false;
            }
        }else{
            return false;
        }
    }
    
}
