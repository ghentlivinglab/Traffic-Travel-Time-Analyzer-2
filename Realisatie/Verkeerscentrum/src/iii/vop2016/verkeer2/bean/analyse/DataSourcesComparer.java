/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package iii.vop2016.verkeer2.bean.analyse;

import iii.vop2016.verkeer2.ejb.components.Route;
import java.text.SimpleDateFormat;
import java.util.Date;
import javafx.util.Pair;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.RequestScoped;

/**
 *
 * @author Mike
 */
@ManagedBean(name = "DataSourcesComparer", eager=true)
@RequestScoped
public class DataSourcesComparer extends AnalysePage implements ITableView, IGraphView {

    Route route = null;
    Pair<Date,Date> period = null;

    public Route getRoute() {
        return route;
    }

    public Pair<Date, Date> getPeriod() {
        return period;
    }
    
    public DataSourcesComparer() {
        super();
        try{
            if(super.routes.size()>0)
                route = super.routes.get(0);
            if(super.periods.size()>0)
                period = super.periods.get(0);
        }catch(Exception ex){
            System.out.println(ex);
        }
        
    }

    @Override
    public String getTitle() {
        if(route == null){
            return "Geen route geselecteerd";
        }else{
            return route.getName();
        }
        
    }
   
    @Override
    public String getSubTitle() {
        SimpleDateFormat formatter = new SimpleDateFormat("dd MMMM YYYY (HH:mm)");
        String format1 = formatter.format(period.getKey());
        String format2 = formatter.format(period.getValue());
        return "Periode: "+format1+" - "+format2;
    }
    
    
    
}
