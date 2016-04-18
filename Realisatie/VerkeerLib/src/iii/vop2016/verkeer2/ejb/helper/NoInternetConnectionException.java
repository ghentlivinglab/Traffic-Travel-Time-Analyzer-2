/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package iii.vop2016.verkeer2.ejb.helper;

/**
 *
 * @author tobia
 */
public class NoInternetConnectionException extends Exception{
    private static String d = "Date retrieval failed. Or every single ntp server in the world is down, or you lost your internet connection... !Occam's razor! Fix your internet.";
    public NoInternetConnectionException() {
        super(d);
    }
    
}
