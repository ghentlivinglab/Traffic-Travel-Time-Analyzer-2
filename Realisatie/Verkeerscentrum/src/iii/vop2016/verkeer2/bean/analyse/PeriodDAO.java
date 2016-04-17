/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package iii.vop2016.verkeer2.bean.analyse;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import javafx.util.Pair;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.RequestScoped;
import javax.faces.context.FacesContext;
import javax.naming.InitialContext;
import javax.servlet.http.HttpServletRequest;

/**
 *
 * @author Mike
 */
@ManagedBean
@RequestScoped
public class PeriodDAO {
    
    private Pair<Date, Date> period = null;
    private List<Pair<Date, Date>> periods;
    private boolean multiPeriods;

    
   
    
    /**
     * Creates a new instance of PeriodDAO
     */
    public PeriodDAO() {
        periods = new ArrayList<>();
        
         // URL PARSEN
        HttpServletRequest request = (HttpServletRequest) FacesContext.getCurrentInstance().getExternalContext().getRequest();        
        Map<String, String[]> parameterMap = request.getParameterMap();
            
        // PERIOD
        String[] periodStart = parameterMap.get("periodStart");
        String[] periodEnd = parameterMap.get("periodEnd");
        String[] periodsStart = parameterMap.get("periodsStart");
        String[] periodsEnd = parameterMap.get("periodsEnd");       
        
        if(periodStart != null && periodEnd != null 
                && periodStart.length>0 && periodEnd.length>0
                && !periodStart[0].equals("") && !periodEnd[0].equals("")){
            try{
                period = new Pair<>(new Date(Long.parseLong(periodStart[0])),new Date(Long.parseLong(periodEnd[0])));
            }catch(Exception ex){
                System.out.println(ex);
            }            
        }
        
        // PERIODECOMPARER
        
        if(periodsStart != null && periodsEnd != null 
                && periodsStart.length>0 && periodsEnd.length>0
                && !periodsStart[0].equals("") && !periodsEnd[0].equals("")){
            String[] partsBegin = periodsStart[0].split(" ");
            String[] partsEnd = periodsEnd[0].split(" ");
            for(int i=0; i<partsBegin.length; i++){
                try{
                    periods.add(new Pair(new Date(Long.parseLong(partsBegin[i])),new Date(Long.parseLong(partsEnd[i]))));
                }catch(Exception ex){
                    System.out.println(ex);
                }  
            }
        }
        
        // PERIODTYPE
        String[] stype = parameterMap.get("periodtype");
        if(stype != null && stype.length>0 && stype[0].equals("multi")){
            multiPeriods = true;
        }else{
            multiPeriods = false;
        }
        
    }
    
    
    public Pair<Date, Date> getPeriod() {
        return period;
    }

    public List<Pair<Date, Date>> getPeriods() {
        return periods;
    }
    
    public boolean isMultiPeriods() {
        return multiPeriods;
    }
        
    
}
