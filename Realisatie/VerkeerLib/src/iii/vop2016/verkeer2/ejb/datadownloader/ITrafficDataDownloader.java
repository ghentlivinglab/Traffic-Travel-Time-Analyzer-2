/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package iii.vop2016.verkeer2.ejb.datadownloader;

import java.util.Date;

/**
 *
 * @author tobia
 */
public interface ITrafficDataDownloader {
    public void downloadNewData(Date timestamp);
}
