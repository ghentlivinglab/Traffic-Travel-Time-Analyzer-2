/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package iii.vop2016.verkeer2.bean.analyse;

import iii.vop2016.verkeer2.bean.components.DataProvider;
import iii.vop2016.verkeer2.bean.components.Route;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javafx.util.Pair;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.RequestScoped;

/**
 *
 * @author Mike
 */
@ManagedBean(name = "PeriodComparer", eager = true)
@RequestScoped
public class PeriodComparer extends AnalysePage implements ITableView, IGraphView {

   
    private Route route = null;
    private List<Pair<Date,Date>> periods = null;
    private List<DataProvider> dataproviders = null;

        
    public PeriodComparer() {
        super();
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
        return this.periods.size()+" perioden geselecteerd voor vergelijking";
    }
    
    @Override
    public void setPeriodDAO(PeriodDAO periodDAO) {
        super.setPeriodDAO(periodDAO);
        this.periods = periodDAO.getPeriods();
    }

    @Override
    public void setDataproviderDAO(DataproviderDAO dataproviderDAO) {
        super.setDataproviderDAO(dataproviderDAO);
        this.dataproviders = dataproviderDAO.getSelectedProviders();
    }

    @Override
    public void setRouteDAO(RouteDAO routeDAO) {
        super.setRouteDAO(routeDAO);
        this.route = routeDAO.getSelectedRoutes().get(0);
    }
    
    public Route getRoute() {
        return route;
    }

    
    @Override
    public String getDataURL() {
        
        StringBuilder url = new StringBuilder();
        url.append(super.prop.getProperty("urlPeriodComparer"));
        
        //
        // ROUTE
        //
        
        
        //
        // PERIODS
        //
        StringBuilder urlPartStart = new StringBuilder();
        StringBuilder urlPartEnd = new StringBuilder();
        if(periods != null || periods.isEmpty()){
            if(this.periods.size() > 0){
                for(Pair<Date,Date> period : this.periods){
                    urlPartStart.append(period.getKey().getTime()).append(",");
                    urlPartEnd.append(period.getValue().getTime()).append(",");
                }
                urlPartStart.delete(urlPartStart.length()-1, urlPartStart.length());
                urlPartEnd.delete(urlPartEnd.length()-1, urlPartEnd.length());
            }
        }
        
        //
        // FILTERS
        //
        List<String> urlParts = new ArrayList<>();
        
        if(dataproviders != null || dataproviders.size()==0){
            StringBuilder providersURLS = new StringBuilder();
            if(this.dataproviders.size() > 0){
                providersURLS.append("providers=");
                for(DataProvider provider : this.dataproviders){
                    providersURLS.append(provider.getName()).append(",");
                }
                providersURLS.delete(providersURLS.length()-1, providersURLS.length());
                urlParts.add(providersURLS.toString());
            }
        }
        
        if(urlParts.size()>0){
            url.append("?").append(urlParts.get(0));
            for(int i=1; i<urlParts.size(); i++){
                url.append("&").append(urlParts.get(i));
            }
        }
        
                
        String surl = url.toString();
          
        surl = surl.replaceAll("\\{id\\}", ""+route.getId());
        surl = surl.replaceAll("\\{starts\\}", urlPartStart.toString());
        surl = surl.replaceAll("\\{ends\\}", urlPartEnd.toString());
        
        
        System.out.println("URL = "+surl);
                
        return surl;
    }
    
    public Map<String,String> getPeriodNames() {
        Map<String,String> res = new HashMap<>();
        for(int i=0; i<periods.size(); i++){
            String date1 = new SimpleDateFormat("dd/MM/yyyy").format(periods.get(i).getKey());
            String date2 = new SimpleDateFormat("dd/MM/yyyy").format(periods.get(i).getValue());
            if(date1.equals(date2)){
                res.put("period"+(i+1), date1);
            }else{
                res.put("period"+(i+1), date1 +" - "+ date2);
            }
        }
        return res;
    }
    
    
}
