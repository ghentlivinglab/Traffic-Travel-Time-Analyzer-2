/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package iii.vop2016.verkeer2.ejb.logger;

import iii.vop2016.verkeer2.ejb.components.Log;
import java.util.List;
import java.util.logging.Level;

/**
 *
 * @author tobia
 */
public interface ILogger {

    void log(Level l, String message);

    void entering(String sourceClass, String sourceMethod, Object params[]);

    void exiting(String sourceClass, String sourceMethod, Object result);

    List<Log> getLogs(int amount, int offset, Level filter1, String containing);
}
