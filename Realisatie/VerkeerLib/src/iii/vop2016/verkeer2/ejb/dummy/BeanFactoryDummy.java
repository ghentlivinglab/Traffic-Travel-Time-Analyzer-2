/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package iii.vop2016.verkeer2.ejb.dummy;

import iii.vop2016.verkeer2.ejb.analyzer.IAnalyzer;
import iii.vop2016.verkeer2.ejb.dao.IGeneralDAO;
import iii.vop2016.verkeer2.ejb.dao.ITrafficDataDAO;
import iii.vop2016.verkeer2.ejb.datamanager.ITrafficDataManager;
import iii.vop2016.verkeer2.ejb.helper.BeanFactory;
import iii.vop2016.verkeer2.ejb.provider.ISourceAdapter;
import iii.vop2016.verkeer2.ejb.timer.ITimer;
import java.util.List;

/**
 *
 * @author Tobias
 */
public class BeanFactoryDummy extends BeanFactory{
    
    private BeanFactoryDummy() {
        super(null,null);
    }
    
    private static BeanFactoryDummy INSTANCE;
    
    public static BeanFactoryDummy getInstance() {
        if(INSTANCE== null)
            INSTANCE = new BeanFactoryDummy();
        return INSTANCE;
    }

    @Override
    public IAnalyzer getAnalyzer() {
        return super.getAnalyzer(); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public ITrafficDataDAO getDataDAO() {
        return super.getDataDAO(); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public ITrafficDataManager getDataManager() {
        return super.getDataManager(); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public IGeneralDAO getGeneralDAO() {
        return super.getGeneralDAO(); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public List<ISourceAdapter> getSourceAdaptors() {
        return super.getSourceAdaptors(); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public TimerSchedulerDummy getTimer() {
        return new TimerSchedulerDummy(); //To change body of generated methods, choose Tools | Templates.
    }
    
    
}
