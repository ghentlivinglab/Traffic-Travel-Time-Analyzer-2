/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package iii.vop2016.verkeer2.ejb.dao;

/**
 *
 * @author tobia
 */
public class AggregationContainer {
    public Aggregation aggregation;
    public String attr;

    public AggregationContainer(Aggregation aggregation, String attr) {
        this.aggregation = aggregation;
        this.attr = attr;
    }
    
    
}
