/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package iii.vop2016.verkeer2.ejb.dao;

import iii.vop2016.verkeer2.ejb.components.IRoute;
import java.util.ArrayList;
import java.util.List;
import javax.ejb.Singleton;

/**
 *
 * @author Tobias
 */
@Singleton
public class GeneralDAO implements GeneralDAORemote {

    @Override
    public List<IRoute> getRoutes() {
        return new ArrayList<>();
    }

    @Override
    public IRoute getRoute(String name) {
        return null;
    }

    @Override
    public void addRoute(IRoute route) {
        
    }

    @Override
    public void removeRoute(IRoute route) {
        
    }

    // Add business logic below. (Right-click in editor and choose
    // "Insert Code > Add Business Method")
}
