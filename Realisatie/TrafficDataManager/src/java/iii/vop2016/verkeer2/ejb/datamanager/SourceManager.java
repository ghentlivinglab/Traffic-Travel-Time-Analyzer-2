/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package iii.vop2016.verkeer2.ejb.datamanager;

import iii.vop2016.verkeer2.ejb.components.IRoute;
import iii.vop2016.verkeer2.ejb.components.IRouteData;
import java.util.List;
import java.util.concurrent.Future;

/**
 *
 * @author Mike
 */
public class SourceManager implements ISourceManager{

    @Override
    public List<Future<IRouteData>> parse(IRoute route) {
        return null;
    }
    
}
