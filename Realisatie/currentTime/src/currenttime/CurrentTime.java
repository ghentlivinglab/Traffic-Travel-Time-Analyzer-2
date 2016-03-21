/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package currenttime;

import java.util.Date;

/**
 *
 * @author tobia
 */
public class CurrentTime {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        int min = 15;
        System.out.println(new Date().getTime()-min*1000*60);
        
        

    }
    
}
