/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package iii.vop2016.verkeer2.ejb.datamanager;

import java.util.Date;

/**
 *
 * @author tobia
 */
public interface ITrafficDataManager {
    public void downloadNewData(Date timestamp);
    public void initRoutes();
}
