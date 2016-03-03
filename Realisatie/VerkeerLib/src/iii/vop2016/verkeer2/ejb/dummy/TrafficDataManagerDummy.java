/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package iii.vop2016.verkeer2.ejb.dummy;

import java.util.Date;
import iii.vop2016.verkeer2.ejb.datadownloader.ITrafficDataDownloader;

/**
 *
 * @author Tobias
 */
public class TrafficDataManagerDummy implements ITrafficDataDownloader{

    @Override
    public void downloadNewData(Date timestamp) {
        
    }
    
}
