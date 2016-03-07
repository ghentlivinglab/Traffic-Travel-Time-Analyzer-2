/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package iii.vop2016.verkeer2.ejb.helper;

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
import iii.vop2016.verkeer2.ejb.downstream.ITrafficDataDownstreamAnalyser;

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

    public ITrafficDataDownstreamAnalyser getTrafficDataDownstreamAnalyser() {
        if (sctx != null) {
            Object obj = HelperFunctions.getBean(beanProperties, BeanSelector.downstreamAnalyser, sctx, Logger.getGlobal());
            if (obj instanceof ITrafficDataDownstreamAnalyser) {
                return (ITrafficDataDownstreamAnalyser) obj;
            }
        } else {
            Object obj = HelperFunctions.getBean(beanProperties, BeanSelector.downstreamAnalyser, ctx, Logger.getGlobal());
            if (obj instanceof ITrafficDataDownstreamAnalyser) {
                return (ITrafficDataDownstreamAnalyser) obj;
            }
        }
        return null;
    }

    public ITrafficDataDownloader getDataManager() {
        if (sctx != null) {
            Object obj = HelperFunctions.getBean(beanProperties, BeanSelector.dataManager, sctx, Logger.getGlobal());
            if (obj instanceof ITrafficDataDownloader) {
                return (ITrafficDataDownloader) obj;
            }
        }
        else{
            Object obj = HelperFunctions.getBean(beanProperties, BeanSelector.dataManager, ctx, Logger.getGlobal());
            if (obj instanceof ITrafficDataDownloader) {
                return (ITrafficDataDownloader) obj;
            }
        }
        return null;
    }

    public ITimer getTimer() {
        if (sctx != null) {
            Object obj = HelperFunctions.getBean(beanProperties, BeanSelector.Timer, sctx, Logger.getGlobal());
            if (obj instanceof ITimer) {
                return (ITimer) obj;
            }
        }
        else{
            Object obj = HelperFunctions.getBean(beanProperties, BeanSelector.Timer, ctx, Logger.getGlobal());
            if (obj instanceof ITimer) {
                return (ITimer) obj;
            }
        }
        return null;
    }

    public IGeneralDAO getGeneralDAO() {
        if (sctx != null) {
            Object obj = HelperFunctions.getBean(beanProperties, BeanSelector.generalDAO, sctx, Logger.getGlobal());
            if (obj instanceof IGeneralDAO) {
                return (IGeneralDAO) obj;
            }
        }
        else{
            Object obj = HelperFunctions.getBean(beanProperties, BeanSelector.generalDAO, ctx, Logger.getGlobal());
            if (obj instanceof IGeneralDAO) {
                return (IGeneralDAO) obj;
            }
        }
        return null;
    }

    public ITrafficDataDAO getTrafficDataDAO() {
        if (sctx != null) {
            Object obj = HelperFunctions.getBean(beanProperties, BeanSelector.trafficDataDAO, sctx, Logger.getGlobal());
            if (obj instanceof ITrafficDataDAO) {
                return (ITrafficDataDAO) obj;
            }
        }
        else{
            Object obj = HelperFunctions.getBean(beanProperties, BeanSelector.trafficDataDAO, ctx, Logger.getGlobal());
            if (obj instanceof ITrafficDataDAO) {
                return (ITrafficDataDAO) obj;
            }
        }
        return null;
    }

    public List<ISourceAdapter> getSourceAdaptors() {
        ArrayList<ISourceAdapter> adapters = new ArrayList<>();
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
        return adapters;
    }

}
