/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package iii.vop2016.verkeer2.bean.components;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;

/**
 *
 * @author Mike
 */
public class Route{

    protected long id;
    protected String name;

    
    public Route(long id, String name) {
        this.id = id;
        this.name = name;
    }
    

    public long getId() {
        return this.id;
    }
    
    public void setId(long id) {
        this.id = id;
    }
    

    public String getName() {
        return name;
    }


    public void setName(String name) {
        this.name = name;
    }

    @Override
    public String toString(){
        return "Route ("+id+", "+name+")";
    }

    
}
