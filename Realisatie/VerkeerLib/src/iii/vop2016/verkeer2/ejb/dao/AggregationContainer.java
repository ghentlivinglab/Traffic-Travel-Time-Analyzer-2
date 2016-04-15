/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package iii.vop2016.verkeer2.ejb.dao;

import java.io.Serializable;

/**
 *
 * @author tobia
 */
public class AggregationContainer implements Serializable{
    public Aggregation aggregation;
    public String attr;

    public AggregationContainer(Aggregation aggregation, String attr) {
        this.aggregation = aggregation;
        this.attr = attr;
    }
    
    
}
