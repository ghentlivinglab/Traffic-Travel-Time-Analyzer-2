/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package iii.vop2016.verkeer2.ejb.components;

import java.util.logging.Level;

/**
 *
 * @author Tobias
 */
public class Log {
    private Level l;
    private String message;
    private long date;

    public Log(Level l, String message, long date) {
        this.l = l;
        this.message = message;
    }

    public Level getL() {
        return l;
    }

    public String getMessage() {
        return message;
    }

    public long getDate() {
        return date;
    }
    
    
}
