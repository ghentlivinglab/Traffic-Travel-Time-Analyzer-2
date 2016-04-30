/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package iii.vop2016.verkeer2.ejb.threshold;

import iii.vop2016.verkeer2.ejb.components.IRoute;

/**
 *
 * @author Tobias
 */
public interface IThresholdHandler {
    void notify(IRoute route, long routeId,int level,int delayTriggerLevel,int difference,int delay);
}
