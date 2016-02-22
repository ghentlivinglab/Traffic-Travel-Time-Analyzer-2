/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package iii.vop2016.verkeer2.ejb.provider;

import iii.vop2016.verkeer2.ejb.components.IRoute;
import iii.vop2016.verkeer2.ejb.components.IRouteData;
import java.util.concurrent.Future;

/**
 *
 * @author Mike
 */
public interface ISourceAdapter {
    
    public IRouteData parse(IRoute route); //Future is needed to download data async
    
}
