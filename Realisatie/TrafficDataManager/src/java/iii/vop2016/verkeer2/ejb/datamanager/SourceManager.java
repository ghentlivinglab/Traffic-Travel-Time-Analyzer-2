/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package iii.vop2016.verkeer2.ejb.datamanager;

import iii.vop2016.verkeer2.ejb.components.IRoute;
import iii.vop2016.verkeer2.ejb.components.IRouteData;
import iii.vop2016.verkeer2.ejb.provider.*;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Future;

/**
 *
 * @author Mike
 */
public class SourceManager implements ISourceManager{

    private List<ISourceAdapter> adapters;

    public SourceManager() {
        adapters = new ArrayList<ISourceAdapter>();
    }
    
    
    
    
    @Override
    public List<Future<IRouteData>> parse(IRoute route) {
        return null;
    }
    
}
