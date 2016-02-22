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
import iii.vop2016.verkeer2.ejb.helper.ResourceFileMissingExcepion;
import iii.vop2016.verkeer2.ejb.provider.ISourceAdapter;
import iii.vop2016.verkeer2.ejb.timer.ITimer;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Tobias
 */
public class BeanFactoryDummy extends BeanFactory{
    
    private BeanFactoryDummy() throws ResourceFileMissingExcepion {
        super(null,null);
    }
    
    private static BeanFactoryDummy INSTANCE;
    
    public static BeanFactoryDummy getInstance() {
        if(INSTANCE== null)
            try {
                INSTANCE = new BeanFactoryDummy();
        } catch (ResourceFileMissingExcepion ex) {
            Logger.getLogger(BeanFactoryDummy.class.getName()).log(Level.SEVERE, null, ex);
        }
        return INSTANCE;
    }

    @Override
    public IAnalyzer getAnalyzer() {
        return new TrafficDataAnalyzerDummy();
    }

    @Override
    public ITrafficDataDAO getDataDAO() {
        return new TrafficDataDAODummy();
    }

    @Override
    public ITrafficDataManager getDataManager() {
        return new TrafficDataManagerDummy();
    }

    @Override
    public IGeneralDAO getGeneralDAO() {
        return new GeneralDAODummy();
    }

    @Override
    public List<ISourceAdapter> getSourceAdaptors() {
        List<ISourceAdapter> l = new ArrayList<>();
        l.add(new SourceAdapterDummy());
        return l;
    }

    @Override
    public TimerSchedulerDummy getTimer() {
        return new TimerSchedulerDummy(); //To change body of generated methods, choose Tools | Templates.
    }
    
    
}
