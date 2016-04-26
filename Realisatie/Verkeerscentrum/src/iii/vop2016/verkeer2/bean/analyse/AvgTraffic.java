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
@ManagedBean(name = "AvgTraffic", eager = true)
@RequestScoped
public class AvgTraffic extends AnalysePage implements ITableView, IGraphView {

   
    private List<Route> routes = null;
    private Pair<Date,Date> period = null;
    private List<DataProvider> dataproviders = null;

        
    public AvgTraffic() {
        super();
    }

    @Override
    public String getTitle() {
        return "Gemiddelde verkeerssituatie";
    }
   
    @Override
    public String getSubTitle() {
        return "";
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
        this.routes = routeDAO.getSelectedRoutes();
    }
    
    public List<Route> getRoutes() {
        return routes;
    }

    public Pair<Date, Date> getPeriod() {
        return period;
    }

    @Override
    public String getDataURL() {
        List<String> urlParts = new ArrayList<>();
        
        String surl = super.prop.getProperty("urlRoutes");
        
        
       //
        // ROUTE
        //
        if(routes != null || routes.size()==0){
            StringBuilder routesURL = new StringBuilder();
            if(this.routes.size() > 0){
                for(Route route : this.routes){
                    routesURL.append(route.getId()).append(",");
                }
                routesURL.delete(routesURL.length()-1, routesURL.length());
                surl = surl.replaceAll("\\{id\\}", ""+routesURL);
            }
        }
        
        StringBuilder url = new StringBuilder(surl);        
        
        //
        // PERIODS
        //
        if(period.getKey() != null){
            urlParts.add("start="+period.getKey().getTime());
        }
        if(period.getValue() != null){
            urlParts.add("end="+period.getValue().getTime());
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
        
        
        if(urlParts.size()>0){
            url.append("?").append(urlParts.get(0));
            for(int i=1; i<urlParts.size(); i++){
                url.append("&").append(urlParts.get(i));
            }
        }
        
                
        System.out.println(url);
        
        return url.toString();
    }
    
    
}
