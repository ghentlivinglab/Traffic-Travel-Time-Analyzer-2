/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package iii.vop2016.verkeer2.ejb.helper;

import java.io.PrintStream;
import java.io.PrintWriter;
import java.io.StringWriter;

/**
 *
 * @author tobia
 */
public class ResourceFileMissingException extends RuntimeException{
    String jndi;
    public ResourceFileMissingException(String jndi){
        super();
        this.jndi = jndi;
    }  
    
    public void printSolution(){
        System.err.println("Solution: ");
        System.err.println("JNDI ("+jndi+") could not be found in the glassfish server. Are you sure it is added to the custom resources?");
        System.err.println("Make sure the filename points to the ABSOLUTE path of the properties file.");
    }
}

