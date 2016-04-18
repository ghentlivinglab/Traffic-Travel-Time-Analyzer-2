/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package iii.vop2016.verkeer2.ejb.logger;

import java.util.logging.Level;
import javax.ejb.Remote;

/**
 *
 * @author tobia
 */
@Remote
public interface LoggerRemote {
    void log(Level l,String message);
    void entering(String sourceClass, String sourceMethod, Object params[]);
    void exiting(String sourceClass, String sourceMethod, Object result);
}
