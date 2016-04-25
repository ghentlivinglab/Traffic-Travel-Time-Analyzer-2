/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package iii.vop2016.verkeer2.ejb.threshold;

import iii.vop2016.verkeer2.ejb.components.IRoute;
import iii.vop2016.verkeer2.ejb.components.IThreshold;
import java.util.List;

/**
 *
 * @author Tobias
 */
public interface IThresholdManager {

    int getThresholdLevel(IRoute route, int delay);

    int EvalThresholdLevel(IRoute route, int delay);

    void addDefaultThresholds(IRoute route);
    
    List<IThreshold> getThresholds(IRoute r);

    boolean ModifyThresholds(List<IThreshold> list);
}
