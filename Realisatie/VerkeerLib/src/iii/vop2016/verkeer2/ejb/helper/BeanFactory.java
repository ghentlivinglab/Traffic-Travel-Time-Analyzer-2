/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package iii.vop2016.verkeer2.ejb.helper;

import iii.vop2016.verkeer2.ejb.downstream.IAnalyzer;
import iii.vop2016.verkeer2.ejb.dao.IGeneralDAO;
import iii.vop2016.verkeer2.ejb.dao.ITrafficDataDAO;
import iii.vop2016.verkeer2.ejb.datasources.ISourceAdapter;
import iii.vop2016.verkeer2.ejb.timer.ITimer;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.ejb.SessionContext;
import javax.naming.InitialContext;
import iii.vop2016.verkeer2.ejb.datadownloader.ITrafficDataDownloader;

/**
 *
 * @author Tobias
 */
public class BeanFactory {

    private static final String JNDILOOKUP_BEANFILE = "resources/properties/Beans";
    private static final String JNDILOOKUP_SOURCEADAPORTSFILE = "resources/properties/SourceAdaptors";

    private static BeanFactory instance;

    public static BeanFactory getInstance(InitialContext ctx, SessionContext sctx) {
        if (instance == null) {
            try {
                instance = new BeanFactory(ctx, sctx);
            } catch (ResourceFileMissingException ex) {
                Logger.getLogger(BeanFactory.class.getName()).log(Level.SEVERE, null, ex);
                ex.printSolution();
            }
        }
        return instance;
    }

    private InitialContext ctx;
    private SessionContext sctx;
    private Properties beanProperties;
    private Properties sourceAdaptorsProperties;

    private IAnalyzer analyzer = null;
    private ITrafficDataDownloader dataManager = null;
    private IGeneralDAO generalDAO = null;
    private ITrafficDataDAO trafficDataDAO = null;
    private ITimer timer = null;
    private List<ISourceAdapter> adapters = null;

    protected BeanFactory(InitialContext ctx, SessionContext sctx) throws ResourceFileMissingException {
        this.ctx = ctx;
        this.sctx = sctx;
        if (ctx != null) {
            beanProperties = HelperFunctions.RetrievePropertyFile(JNDILOOKUP_BEANFILE, ctx, Logger.getGlobal());
            sourceAdaptorsProperties = HelperFunctions.RetrievePropertyFile(JNDILOOKUP_SOURCEADAPORTSFILE, ctx, Logger.getGlobal());
        }

        if (beanProperties == null) {
            throw new ResourceFileMissingException(JNDILOOKUP_BEANFILE);
        }
        if (sourceAdaptorsProperties == null) {
            throw new ResourceFileMissingException(JNDILOOKUP_SOURCEADAPORTSFILE);
        }
    }

    public IAnalyzer getAnalyzer() {
        if (analyzer == null && sctx != null) {
            Object obj = HelperFunctions.getBean(beanProperties, BeanSelector.downstreamAnalyser, sctx, Logger.getGlobal());
            if (obj instanceof IAnalyzer) {
                analyzer = (IAnalyzer) obj;
            }
        }
        if (analyzer == null && sctx == null) {
            Object obj = HelperFunctions.getBean(beanProperties, BeanSelector.downstreamAnalyser, ctx, Logger.getGlobal());
            if (obj instanceof IAnalyzer) {
                analyzer = (IAnalyzer) obj;
            }
        }
        return analyzer;
    }

    public ITrafficDataDownloader getDataManager() {
        if (dataManager == null && sctx != null) {
            Object obj = HelperFunctions.getBean(beanProperties, BeanSelector.dataManager, sctx, Logger.getGlobal());
            if (obj instanceof ITrafficDataDownloader) {
                dataManager = (ITrafficDataDownloader) obj;
            }
        }
        if (dataManager == null && sctx == null) {
            Object obj = HelperFunctions.getBean(beanProperties, BeanSelector.dataManager, ctx, Logger.getGlobal());
            if (obj instanceof ITrafficDataDownloader) {
                dataManager = (ITrafficDataDownloader) obj;
            }
        }
        return dataManager;
    }

    public ITimer getTimer() {
        if (timer == null && sctx != null) {
            Object obj = HelperFunctions.getBean(beanProperties, BeanSelector.Timer, sctx, Logger.getGlobal());
            if (obj instanceof ITimer) {
                timer = (ITimer) obj;
            }
        }
        if (timer == null && sctx == null) {
            Object obj = HelperFunctions.getBean(beanProperties, BeanSelector.Timer, ctx, Logger.getGlobal());
            if (obj instanceof ITimer) {
                timer = (ITimer) obj;
            }
        }
        return timer;
    }

    public IGeneralDAO getGeneralDAO() {
        if (generalDAO == null && sctx != null) {
            Object obj = HelperFunctions.getBean(beanProperties, BeanSelector.generalDAO, sctx, Logger.getGlobal());
            if (obj instanceof IGeneralDAO) {
                generalDAO = (IGeneralDAO) obj;
            }
        }
        if (generalDAO == null && sctx == null) {
            Object obj = HelperFunctions.getBean(beanProperties, BeanSelector.generalDAO, ctx, Logger.getGlobal());
            if (obj instanceof IGeneralDAO) {
                generalDAO = (IGeneralDAO) obj;
            }
        }
        return generalDAO;
    }
    
    public ITrafficDataDAO getTrafficDataDAO() {
        if (trafficDataDAO == null && sctx != null) {
            Object obj = HelperFunctions.getBean(beanProperties, BeanSelector.trafficDataDAO, sctx, Logger.getGlobal());
            if (obj instanceof ITrafficDataDAO) {
                trafficDataDAO = (ITrafficDataDAO) obj;
            }
        }
        if (generalDAO == null && sctx == null) {
            Object obj = HelperFunctions.getBean(beanProperties, BeanSelector.trafficDataDAO, ctx, Logger.getGlobal());
            if (obj instanceof ITrafficDataDAO) {
                trafficDataDAO = (ITrafficDataDAO) obj;
            }
        }
        return trafficDataDAO;
    }

    public List<ISourceAdapter> getSourceAdaptors() {
        if (adapters == null) {
            adapters = new ArrayList<>();
            for (Object jndi : sourceAdaptorsProperties.values()) {
                if (jndi instanceof String) {
                    Object bean = null;
                    if (sctx != null) {
                        bean = HelperFunctions.getBean((String) jndi, sctx, Logger.getGlobal());
                    } else {
                        bean = HelperFunctions.getBean((String) jndi, ctx, Logger.getGlobal());
                    }
                    if ((bean != null) && (bean instanceof ISourceAdapter)) {
                        adapters.add((ISourceAdapter) bean);
                    }
                }
            }
        }
        return adapters;
    }

}
