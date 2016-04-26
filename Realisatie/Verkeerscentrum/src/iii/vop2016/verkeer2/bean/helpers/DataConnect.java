/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package iii.vop2016.verkeer2.bean.helpers;


import static iii.vop2016.verkeer2.bean.helpers.UrlDAO.prop;
import iii.vop2016.verkeer2.ejb.helper.HelperFunctions;
import java.sql.Connection;
import java.sql.DriverManager;
import java.util.Properties;
import java.util.logging.Logger;
import javax.naming.InitialContext;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.sql.DataSource;
 
public class DataConnect {
    
    
    protected static final String JNDILOOKUP_PROPERTYFILE = "resources/properties/WebSettings";
    protected static InitialContext ctx;
    protected static DataSource ds;
    
 
    public static Connection getConnection() {
        try {
            ctx = new InitialContext();
            prop = getProperties();
            
            ds = (DataSource)ctx.lookup("");
            
            return null;
        } catch (Exception ex) {
            System.out.println("Database.getConnection() Error -->"
                    + ex.getMessage());
            return null;
        }
    }
    
    
    private static Properties getProperties() {
        return HelperFunctions.RetrievePropertyFile(JNDILOOKUP_PROPERTYFILE, ctx, Logger.getGlobal());
    }
 
    public static void close(Connection con) {
        try {
            con.close();
        } catch (Exception ex) {
        }
    }
}