/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package iii.vop2016.verkeer2.ejb.datamanager;

import iii.vop2016.verkeer2.ejb.components.IRoute;
import iii.vop2016.verkeer2.ejb.components.IRouteData;
import iii.vop2016.verkeer2.ejb.helper.BeanFactory;
import iii.vop2016.verkeer2.ejb.helper.DataAccessException;
import iii.vop2016.verkeer2.ejb.helper.URLException;
import iii.vop2016.verkeer2.ejb.provider.*;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import javax.ejb.SessionContext;
import javax.ejb.Startup;
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


    
    public SourceManager() {
        
        //Initialize bean and its context
        try {
            ctx = new InitialContext();
        } catch (NamingException ex) {
            Logger.getLogger(SourceManager.class.getName()).log(Level.SEVERE, null, ex);
        }
        beanFactory = BeanFactory.getInstance(ctx, ctxs);
        
        adapters = new ArrayList<ISourceAdapter>();
        adapters.addAll(beanFactory.getSourceAdaptors());
        
        
        for(ISourceAdapter adapter : adapters){
            Logger.getGlobal().log(Level.INFO, "DataSourceAdapter added to system: " + adapter.getClass().getName());
        }
        
        
    }
    
    
    
    
    @Override
    public List<IRouteData> parse(final IRoute route) {
        
        List<IRouteData> result = new ArrayList<>();
        
        ExecutorService executor = Executors.newFixedThreadPool(1);
        List<Future<IRouteData>> futures = new ArrayList<>();
                
        for(final ISourceAdapter adapter : adapters){
            futures.add(executor.submit(new Callable() {
                @Override
                public IRouteData call() throws URLException, DataAccessException {
                    System.out.println(adapter.getClass().getName()+"is uitgevoerd");
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
                
                if(futures.isEmpty()){
                    System.out.println("Done");
                    //shut down executor service
                }
                //System.out.println("Waiting for other tasks to complete");
        }
        
        executor.shutdown();
        return result;
        
        
        /*
        List<Future<String>> futures = new ArrayList<>();
       
        executorService.submit(new Runnable() {
            @Override
            public void run() {
                
                for(Future<String> future : futures){
                    try {
                        future.get();
                        if (future.isDone()) {
                            System.out.println("true");
                        }
                        else{
                            System.out.println("false");
                        }
                    } catch (InterruptedException ex) {
                        Logger.getLogger(TrafficDataManager.class.getName()).log(Level.SEVERE, null, ex);
                    } catch (ExecutionException ex) {
                        Logger.getLogger(TrafficDataManager.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }
                
                
                
                for (int i = 0; i < 100; ++i) {
                    try {
                        final Future<String> myValue = completionService.take();
                        //do stuff with the Future
                        final String result = myValue.get();
                        System.out.println(result);
                    } catch (InterruptedException ex) {
                        return;
                    } catch (ExecutionException ex) {
                        System.err.println("TASK FAILED");
                    }
                }
            }
        });
        
        
        
        for (int i = 0; i < 100; ++i) {
            futures.add(completionService.submit(new Callable<String>() {
                @Override
                public String call() throws Exception {
                    if (Math.random() > 0.5) {
                        throw new RuntimeException("FAILED");
                    }
                    return "SUCCESS";
                }
            }));
        }
        
        
        
        
        
        executorService.shutdown();
        
        */
    }
    
}
