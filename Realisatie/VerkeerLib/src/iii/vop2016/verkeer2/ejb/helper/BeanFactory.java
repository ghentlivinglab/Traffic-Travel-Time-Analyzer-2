/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package iii.vop2016.verkeer2.ejb.helper;

import iii.vop2016.verkeer2.ejb.dao.IGeneralDAO;
import iii.vop2016.verkeer2.ejb.dao.ILoginDAO;
import iii.vop2016.verkeer2.ejb.dao.ITrafficDataDAO;
import iii.vop2016.verkeer2.ejb.datasources.ISourceAdapter;
import iii.vop2016.verkeer2.ejb.timer.ITimer;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.logging.Logger;
import javax.ejb.SessionContext;
import javax.naming.InitialContext;
import iii.vop2016.verkeer2.ejb.datadownloader.ITrafficDataDownloader;
import iii.vop2016.verkeer2.ejb.downstream.ITrafficDataDownstreamAnalyser;
import iii.vop2016.verkeer2.ejb.geojson.GeoJsonRemote;
import iii.vop2016.verkeer2.ejb.dataprovider.IDataProvider;
import iii.vop2016.verkeer2.ejb.logger.LoggerRemote;
import iii.vop2016.verkeer2.ejb.threshold.IThresholdHandler;
import iii.vop2016.verkeer2.ejb.threshold.IThresholdManager;
import java.util.Map;

/**
 *
 * @author Tobias
 */
public class BeanFactory {

    private static final String JNDILOOKUP_BEANFILE = "resources/properties/Beans";
    private static final String JNDILOOKUP_SOURCEADAPORTSFILE = "resources/properties/SourceAdaptors";
    private static final String JNDILOOKUP_THRESHOLDHANDLERSFILE = "resources/properties/ThresholdHandlers";

    private static BeanFactory instance;

    public static BeanFactory getInstance(InitialContext ctx, SessionContext sctx) {
        if (instance == null) {
            instance = new BeanFactory(ctx, sctx);
        }
        return instance;
    }

    private InitialContext ctx;
    private SessionContext sctx;

    private Properties getBeanProperties() throws ResourceFileMissingException {
        Properties prop = HelperFunctions.RetrievePropertyFile(JNDILOOKUP_BEANFILE, ctx, Logger.getGlobal());
        if (prop == null) {
            throw new ResourceFileMissingException(JNDILOOKUP_BEANFILE);
        }
        return prop;
    }

    private Properties getThresholdHandlerProperties() throws ResourceFileMissingException {
        Properties prop = HelperFunctions.RetrievePropertyFile(JNDILOOKUP_THRESHOLDHANDLERSFILE, ctx, Logger.getGlobal());
        if (prop == null) {
            throw new ResourceFileMissingException(JNDILOOKUP_BEANFILE);
        }
        return prop;
    }

    private Properties getSourceAdaptorsProperties() throws ResourceFileMissingException {
        Properties prop = HelperFunctions.RetrievePropertyFile(JNDILOOKUP_SOURCEADAPORTSFILE, ctx, Logger.getGlobal());
        if (prop == null) {
            throw new ResourceFileMissingException(JNDILOOKUP_SOURCEADAPORTSFILE);
        }
        return prop;
    }

    protected BeanFactory(InitialContext ctx, SessionContext sctx) {
        this.ctx = ctx;
        this.sctx = sctx;
    }

    public LoggerRemote getLogger() throws ResourceFileMissingException {
        if (sctx != null) {
            Object obj = HelperFunctions.getBean(getBeanProperties(), BeanSelector.Logger, sctx, Logger.getGlobal());
            if (obj instanceof LoggerRemote) {
                return (LoggerRemote) obj;
            }
        } else {
            Object obj = HelperFunctions.getBean(getBeanProperties(), BeanSelector.Logger, ctx, Logger.getGlobal());
            if (obj instanceof LoggerRemote) {
                return (LoggerRemote) obj;
            }
        }
        return null;
    }

    public ITrafficDataDownstreamAnalyser getTrafficDataDownstreamAnalyser() throws ResourceFileMissingException {
        if (sctx != null) {
            Object obj = HelperFunctions.getBean(getBeanProperties(), BeanSelector.downstreamAnalyser, sctx, Logger.getGlobal());
            if (obj instanceof ITrafficDataDownstreamAnalyser) {
                return (ITrafficDataDownstreamAnalyser) obj;
            }
        } else {
            Object obj = HelperFunctions.getBean(getBeanProperties(), BeanSelector.downstreamAnalyser, ctx, Logger.getGlobal());
            if (obj instanceof ITrafficDataDownstreamAnalyser) {
                return (ITrafficDataDownstreamAnalyser) obj;
            }
        }
        return null;
    }

    public GeoJsonRemote getGeoJsonProvider() throws ResourceFileMissingException {
        if (sctx != null) {
            Object obj = HelperFunctions.getBean(getBeanProperties(), BeanSelector.GeoJsonProvider, sctx, Logger.getGlobal());
            if (obj instanceof GeoJsonRemote) {
                return (GeoJsonRemote) obj;
            }
        } else {
            Object obj = HelperFunctions.getBean(getBeanProperties(), BeanSelector.GeoJsonProvider, ctx, Logger.getGlobal());
            if (obj instanceof GeoJsonRemote) {
                return (GeoJsonRemote) obj;
            }
        }
        return null;
    }

    public ITrafficDataDownloader getDataManager() throws ResourceFileMissingException {
        if (sctx != null) {
            Object obj = HelperFunctions.getBean(getBeanProperties(), BeanSelector.dataManager, sctx, Logger.getGlobal());
            if (obj instanceof ITrafficDataDownloader) {
                return (ITrafficDataDownloader) obj;
            }
        } else {
            Object obj = HelperFunctions.getBean(getBeanProperties(), BeanSelector.dataManager, ctx, Logger.getGlobal());
            if (obj instanceof ITrafficDataDownloader) {
                return (ITrafficDataDownloader) obj;
            }
        }
        return null;
    }

    public ITimer getTimer() throws ResourceFileMissingException {
        if (sctx != null) {
            Object obj = HelperFunctions.getBean(getBeanProperties(), BeanSelector.Timer, sctx, Logger.getGlobal());
            if (obj instanceof ITimer) {
                return (ITimer) obj;
            }
        } else {
            Object obj = HelperFunctions.getBean(getBeanProperties(), BeanSelector.Timer, ctx, Logger.getGlobal());
            if (obj instanceof ITimer) {
                return (ITimer) obj;
            }
        }
        return null;
    }

    public IGeneralDAO getGeneralDAO() throws ResourceFileMissingException {
        if (sctx != null) {
            Object obj = HelperFunctions.getBean(getBeanProperties(), BeanSelector.generalDAO, sctx, Logger.getGlobal());
            if (obj instanceof IGeneralDAO) {
                return (IGeneralDAO) obj;
            }
        } else {
            Object obj = HelperFunctions.getBean(getBeanProperties(), BeanSelector.generalDAO, ctx, Logger.getGlobal());
            if (obj instanceof IGeneralDAO) {
                return (IGeneralDAO) obj;
            }
        }
        return null;
    }

    public ITrafficDataDAO getTrafficDataDAO() throws ResourceFileMissingException {
        if (sctx != null) {
            Object obj = HelperFunctions.getBean(getBeanProperties(), BeanSelector.trafficDataDAO, sctx, Logger.getGlobal());
            if (obj instanceof ITrafficDataDAO) {
                return (ITrafficDataDAO) obj;
            }
        } else {
            Object obj = HelperFunctions.getBean(getBeanProperties(), BeanSelector.trafficDataDAO, ctx, Logger.getGlobal());
            if (obj instanceof ITrafficDataDAO) {
                return (ITrafficDataDAO) obj;
            }
        }
        return null;
    }

    public List<ISourceAdapter> getSourceAdaptors() throws ResourceFileMissingException {
        ArrayList<ISourceAdapter> adapters = new ArrayList<>();
        for (Object jndi : getSourceAdaptorsProperties().values()) {
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

    public IDataProvider getDataProvider() throws ResourceFileMissingException {
        if (sctx != null) {
            Object obj = HelperFunctions.getBean(getBeanProperties(), BeanSelector.DataProvider, sctx, Logger.getGlobal());
            if (obj instanceof IDataProvider) {
                return (IDataProvider) obj;
            }
        } else {
            Object obj = HelperFunctions.getBean(getBeanProperties(), BeanSelector.DataProvider, ctx, Logger.getGlobal());
            if (obj instanceof IDataProvider) {
                return (IDataProvider) obj;
            }
        }
        return null;
    }

    public IThresholdManager getThresholdManager() throws ResourceFileMissingException {
        if (sctx != null) {
            Object obj = HelperFunctions.getBean(getBeanProperties(), BeanSelector.ThresholdManager, sctx, Logger.getGlobal());
            if (obj instanceof IThresholdManager) {
                return (IThresholdManager) obj;
            }
        } else {
            Object obj = HelperFunctions.getBean(getBeanProperties(), BeanSelector.ThresholdManager, ctx, Logger.getGlobal());
            if (obj instanceof IThresholdManager) {
                return (IThresholdManager) obj;
            }
        }
        return null;
    }

    public boolean isBeanActive(String bean) {
        for (Object val : getBeanProperties().values()) {
            if (val instanceof String) {
                String value = (String) val;
                if (value.endsWith(bean)) {
                    return true;
                }
            }
        }
        return false;
    }

    public List<IThresholdHandler> getThresholdHandlers(List<String> observers) {
        List<IThresholdHandler> ret = new ArrayList<>();
        if (observers == null || observers.size() == 0) {
            return ret;
        }

        Properties prop = getThresholdHandlerProperties();
        for (String obs : observers) {
            String lookup = prop.getProperty(obs, "");
            if (!lookup.equals("")) {
                if (sctx != null) {
                    Object obj = HelperFunctions.getBean(lookup, sctx, Logger.getGlobal());
                    if (obj instanceof IThresholdHandler) {
                        ret.add((IThresholdHandler) obj);
                    }
                } else {
                    Object obj = HelperFunctions.getBean(lookup, ctx, Logger.getGlobal());
                    if (obj instanceof IThresholdHandler) {
                        ret.add((IThresholdHandler) obj);
                    }
                }
            }
        }
        return ret;
    }
    
    public List<String> getThresholdHandlers() {
        List<String> ret = new ArrayList<>();

        Properties prop = getThresholdHandlerProperties();
        for(Map.Entry<Object,Object> entry :  prop.entrySet()){
            if(entry.getKey() instanceof String){
                ret.add((String) entry.getKey());
            }
        }
        return ret;
    }
    
    public ILoginDAO getLoginDAO() throws ResourceFileMissingException {
        if (sctx != null) {
            Object obj = HelperFunctions.getBean(getBeanProperties(), BeanSelector.LoginDAO, sctx, Logger.getGlobal());
            if (obj instanceof ILoginDAO) {
                return (ILoginDAO) obj;
            }
        } else {
            Object obj = HelperFunctions.getBean(getBeanProperties(), BeanSelector.LoginDAO, ctx, Logger.getGlobal());
            if (obj instanceof ILoginDAO) {
                return (ILoginDAO) obj;
            }
        }
        return null;
    }

}
