/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package iii.vop2016.verkeer2.ejb.components;

import java.io.Serializable;
import java.util.Date;

/**
 *
 * @author Mike
 */
public interface IPeriod extends Serializable {
    
    public Date getStart();
    public Date getEnd();
    public String getName();
    
    public void setStart(Date date);
    public void setEnd(Date date);
    public void setName(String name);
    
}
