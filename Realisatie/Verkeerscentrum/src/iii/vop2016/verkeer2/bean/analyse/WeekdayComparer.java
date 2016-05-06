/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package iii.vop2016.verkeer2.bean.analyse;

import iii.vop2016.verkeer2.bean.components.DataProvider;
import iii.vop2016.verkeer2.ejb.components.IRoute;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import javafx.util.Pair;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.RequestScoped;

/**
 *
 * @author Mike
 */
@ManagedBean(name = "WeekdayComparer", eager = true)
@RequestScoped
public class WeekdayComparer extends AnalysePage implements ITableView, IGraphView {

   
    private IRoute route = null;
    private Pair<Date,Date> period = null;
    private List<DataProvider> dataproviders = null;

        
    public WeekdayComparer() {
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
        if(period != null){
            SimpleDateFormat formatter = new SimpleDateFormat("dd MMMM YYYY (HH:mm)");
            String format1 = formatter.format(period.getKey());
            String format2 = formatter.format(period.getValue());
            return "Periode: "+format1+" - "+format2;
        }else{
            return null;
        }
    }
    
    @Override
    public void setPeriodDAO(PeriodDAO periodDAO) {
        super.setPeriodDAO(periodDAO);
        this.period = periodDAO.getPeriod();
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
    
    public IRoute getRoute() {
        return route;
    }

    public Pair<Date, Date> getPeriod() {
        return period;
    }

    @Override
    public String getDataURL() {
        List<String> urlParts = new ArrayList<>();
        
        StringBuilder url = new StringBuilder();
        url.append(super.prop.getProperty("urlWeekdayComparer"));
        
        //
        // ROUTE
        //
        
        
        //
        // PERIODS
        //
        if(period != null){
            if(period.getKey() != null){
                urlParts.add("start="+period.getKey().getTime());
            }
            if(period.getValue() != null){
                urlParts.add("end="+period.getValue().getTime());
            }
        }
        //
        // PROVIDERS
        //
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
        
        //
        // PRECISION
        //
        urlParts.add("precision="+getPrecisionDAO().getPrecision());
        
        
        if(urlParts.size()>0){
            url.append("?").append(urlParts.get(0));
            for(int i=1; i<urlParts.size(); i++){
                url.append("&").append(urlParts.get(i));
            }
        }
        
        
        String surl = url.toString();
        surl = surl.replaceAll("\\{id\\}", ""+route.getId());
                
        System.out.println(surl);
        
        return surl;
    }    
}
