/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package iii.vop2016.verkeer2.ejb.helper;

import iii.vop2016.verkeer2.ejb.provider.ISourceAdapter;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.logging.Logger;
import javax.ejb.SessionContext;
import javax.naming.InitialContext;

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
            instance = new BeanFactory(ctx, sctx);
        }
        return instance;
    }

    private InitialContext ctx;
    private SessionContext sctx;
    private Properties beanProperties;
    private Properties sourceAdaptorsProperties;
    private Object analyzer = null, dataManager = null, dataDAO = null, generalDAO = null, timer = null;
    private List<ISourceAdapter> adapters = null;

    private BeanFactory(InitialContext ctx, SessionContext sctx) {
        this.ctx = ctx;
        this.sctx = sctx;
        beanProperties = HelperFunctions.RetrievePropertyFile(JNDILOOKUP_BEANFILE, ctx, Logger.getGlobal());
        sourceAdaptorsProperties = HelperFunctions.RetrievePropertyFile(JNDILOOKUP_SOURCEADAPORTSFILE, ctx, Logger.getGlobal());
    }

    public Object getAnalyzer() {
        if (analyzer == null) {
            analyzer = HelperFunctions.getBean(beanProperties, BeanSelector.analyzer, sctx, Logger.getGlobal());
        }
        return analyzer;
    }

    public Object getDataManager() {
        if (dataManager == null) {
            dataManager = HelperFunctions.getBean(beanProperties, BeanSelector.dataManager, sctx, Logger.getGlobal());
        }
        return dataManager;
    }

    public Object getDataDAO() {
        if (dataDAO == null) {
            dataDAO = HelperFunctions.getBean(beanProperties, BeanSelector.dataDAO, sctx, Logger.getGlobal());
        }
        return dataDAO;
    }

    public Object getTimer() {
        if (timer == null) {
            timer = HelperFunctions.getBean(beanProperties, BeanSelector.Timer, sctx, Logger.getGlobal());
        }
        return timer;
    }

    public Object getGeneralDAO() {
        if (generalDAO == null) {
            generalDAO = HelperFunctions.getBean(beanProperties, BeanSelector.generalDAO, sctx, Logger.getGlobal());
        }
        return generalDAO;
    }
    
    public List<ISourceAdapter> getSourceAdaptors(){
        if(adapters == null){
            adapters = new ArrayList<>();
            for(Object jndi:sourceAdaptorsProperties.values()){
                if(jndi instanceof String){
                    Object bean = HelperFunctions.getBean((String)jndi, sctx, Logger.getGlobal());
                    if((bean != null) && (bean instanceof ISourceAdapter)){
                        adapters.add((ISourceAdapter) bean);
                    }
                }
            }
        }
        return adapters;
    }

}
