/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package iii.vop2016.verkeer2.ejb.components;

import java.util.Observable;

/**
 *
 * @author tobia
 */
public class Threshold extends Observable{
    private int level;
    private int delayTriggerLevel;
    private IRoute route;

    public Threshold(IRoute route, int level, int delayTriggerLevel) {
        this.level = level;
        this.delayTriggerLevel = delayTriggerLevel;
    }

    public int getLevel() {
        return level;
    }

    public void setLevel(int level) {
        this.level = level;
    }

    public int getDelayTriggerLevel() {
        return delayTriggerLevel;
    }

    public void setDelayTriggerLevel(int delayTriggerLevel) {
        this.delayTriggerLevel = delayTriggerLevel;
    }
    
    
}
