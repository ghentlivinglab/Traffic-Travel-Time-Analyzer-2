/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package iii.vop2016.verkeer2.ejb.datadownloader;

import iii.vop2016.verkeer2.ejb.datasources.ISourceAdapter;
import iii.vop2016.verkeer2.ejb.components.IRoute;
import iii.vop2016.verkeer2.ejb.components.IRouteData;
import iii.vop2016.verkeer2.ejb.datadownloader.ISourceManager;
import iii.vop2016.verkeer2.ejb.helper.BeanFactory;
import iii.vop2016.verkeer2.ejb.helper.DataAccessException;
import iii.vop2016.verkeer2.ejb.helper.URLException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.annotation.Resource;
import javax.ejb.SessionContext;
import javax.naming.InitialContext;
import javax.naming.NamingException;

/**
 *
 * @author Mike
 */
public class SourceManager implements ISourceManager{

    @Resource
    private SessionContext ctxs;
    private InitialContext ctx;
    private BeanFactory beanFactory;
    private List<ISourceAdapter> adapters;

    ExecutorService executor;
    
    public SourceManager() {
        
        //Initialize bean and its context
        try {
            ctx = new InitialContext();
        } catch (NamingException ex) {
            Logger.getLogger(SourceManager.class.getName()).log(Level.SEVERE, null, ex);
        }
        beanFactory = BeanFactory.getInstance(ctx, ctxs);
        
        adapters = new ArrayList<ISourceAdapter>();
        
        executor = Executors.newFixedThreadPool(10);
        
        Logger.getLogger("logger").log(Level.INFO, "SourceManager has been initialized.");        
        
    }
    
    
    
    
    @Override
    public List<IRouteData> parse(final IRoute route) {
        
        if(adapters.size() == 0)
            adapters.addAll(beanFactory.getSourceAdaptors());
        
        List<IRouteData> result = new ArrayList<>();
        
        
        List<Future<IRouteData>> futures = new ArrayList<>();
                
        for(final ISourceAdapter adapter : adapters){
            futures.add(executor.submit(new Callable() {
                @Override
                public IRouteData call() throws URLException, DataAccessException {
                    return adapter.parse(route);
                }
            }));
        }
        
        List<Future<IRouteData>> toRemove = new ArrayList<>();
        while (!futures.isEmpty()) {
                for(Future<IRouteData> future : futures){
                    if(future.isDone()){
                        try{
                            IRouteData data = future.get();
                            //wait indefinitely for future task to complete
                            System.out.println("Future output = "+data);
                            if(data != null)
                                result.add(future.get());
                            toRemove.add(future);
                        }catch(Exception ex){
                            ex.getCause().printStackTrace();
                            toRemove.add(future);
                        }
                        
                    }
                }
                if(!toRemove.isEmpty())
                    futures.removeAll(toRemove);
        }
        
        
        return result;
    }

    @Override
    public void destroy() {
        executor.shutdown();
    }
    
}
