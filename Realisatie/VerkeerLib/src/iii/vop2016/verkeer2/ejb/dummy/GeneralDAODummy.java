/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package iii.vop2016.verkeer2.ejb.dummy;

import iii.vop2016.verkeer2.ejb.components.IRoute;
import iii.vop2016.verkeer2.ejb.dao.IGeneralDAO;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author Tobias
 */
public class GeneralDAODummy implements IGeneralDAO{

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
    
}
