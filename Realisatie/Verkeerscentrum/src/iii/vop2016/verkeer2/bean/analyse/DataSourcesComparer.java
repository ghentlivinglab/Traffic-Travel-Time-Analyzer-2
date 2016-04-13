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
import java.util.List;
import javafx.util.Pair;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.RequestScoped;

/**
 *
 * @author Mike
 */
@ManagedBean(name = "DataSourcesComparer", eager = true)
@RequestScoped
public class DataSourcesComparer extends AnalysePage implements ITableView, IGraphView {

   
    private Route route = null;
    private Pair<Date,Date> period = null;
    private List<DataProvider> dataproviders = null;

        
    public DataSourcesComparer() {
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
        SimpleDateFormat formatter = new SimpleDateFormat("dd MMMM YYYY (HH:mm)");
        String format1 = formatter.format(period.getKey());
        String format2 = formatter.format(period.getValue());
        return "Periode: "+format1+" - "+format2;
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
    
    public Route getRoute() {
        return route;
    }

    public Pair<Date, Date> getPeriod() {
        return period;
    }

    @Override
    public String getDataURL() {
        List<String> urlParts = new ArrayList<>();
        
        if(period.getKey() != null){
            urlParts.add("start="+period.getKey().getTime());
        }
        if(period.getValue() != null){
            urlParts.add("end="+period.getValue().getTime());
        }
        if(dataproviders != null || dataproviders.size()==0){
            StringBuilder providersURLS = new StringBuilder();
            if(this.dataproviders.size() > 0){
                providersURLS.append("providers=");
                for(DataProvider provider : this.dataproviders){
                    providersURLS.append(provider.getName()).append(",");
                }
                providersURLS.delete(providersURLS.length()-1, providersURLS.length());
                urlParts.add(providersURLS.toString());
            }else{
                System.out.println("geen providers in url beschikbaar");
            }
            
            
        }
        
        StringBuilder sb = new StringBuilder();
        sb.append(super.prop.getProperty("urlProviderComparer"));
        if(urlParts.size()>0){
            sb.append("?").append(urlParts.get(0));
            for(int i=1; i<urlParts.size(); i++){
                sb.append("&").append(urlParts.get(i));
            }
        }
        System.out.println(sb.toString());
                
        return sb.toString();
    }
    
    
}
