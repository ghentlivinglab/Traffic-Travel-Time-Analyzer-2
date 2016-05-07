/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package iii.vop2016.verkeer2.ejb.properties;

import java.util.List;

/**
 *
 * @author tobia
 */
public interface IProperties {
    void registerProperty(String jndiName);
    List<String> getProperties();
    List<String> getDefaultProviders();
    void setDefaultProviders(List<String> def);
}
