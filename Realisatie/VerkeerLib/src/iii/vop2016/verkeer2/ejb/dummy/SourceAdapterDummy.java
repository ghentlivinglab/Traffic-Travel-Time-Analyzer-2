/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package iii.vop2016.verkeer2.ejb.dummy;

import iii.vop2016.verkeer2.ejb.components.IRoute;
import iii.vop2016.verkeer2.ejb.components.IRouteData;
import iii.vop2016.verkeer2.ejb.datasources.ISourceAdapter;
import java.util.concurrent.Future;

/**
 *
 * @author Tobias
 */
public class SourceAdapterDummy implements ISourceAdapter{

    @Override
    public IRouteData parse(IRoute route) {
        return null;
    }

    @Override
    public String getProviderName() {
        return "SourceAdapterDummy";
    }
    
}
