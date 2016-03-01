/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package iii.vop2016.verkeer2.ejb.components;

import java.util.Comparator;

/**
 *
 * @author Mike
 */
public class GeoLocationComparator implements Comparator<IGeoLocation>{

    @Override
    public int compare(IGeoLocation o1, IGeoLocation o2) {
        return o1.getSortRank()-o2.getSortRank();
    }
    
}
