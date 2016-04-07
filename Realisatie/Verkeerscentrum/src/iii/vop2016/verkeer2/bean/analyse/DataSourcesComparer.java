/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package iii.vop2016.verkeer2.bean.analyse;

import iii.vop2016.verkeer2.bean.components.DataProvider;
import iii.vop2016.verkeer2.bean.components.Route;
import java.text.SimpleDateFormat;
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

    public Route getRoute() {
        return route;
    }

    public Pair<Date, Date> getPeriod() {
        return period;
    }
    
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

    public void setDataproviderDAO(DataproviderDAO dataproviderDAO) {
        super.setDataproviderDAO(dataproviderDAO);
        this.dataproviders = dataproviderDAO.getSelectedProviders();
    }

    public void setRouteDAO(RouteDAO routeDAO) {
        super.setRouteDAO(routeDAO);
        this.route = routeDAO.getSelectedRoutes().get(0);
    }
    
    
}
