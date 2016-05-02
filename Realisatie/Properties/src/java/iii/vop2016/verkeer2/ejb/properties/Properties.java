/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package iii.vop2016.verkeer2.ejb.properties;

import java.util.ArrayList;
import java.util.List;
import javax.annotation.PostConstruct;
import javax.ejb.Lock;
import javax.ejb.LockType;
import javax.ejb.Singleton;

/**
 *
 * @author tobia
 */
@Singleton
@Lock(LockType.WRITE)
public class Properties implements PropertiesRemote, PropertiesLocal {

    private List<String> jndi;

    @PostConstruct
    private void init() {
        jndi = new ArrayList<>();
    }

    @Override
    public void registerProperty(String jndiName) {
        if (!jndi.contains(jndiName)) {
            jndi.add(jndiName);
        }
    }

    @Override
    public List<String> getProperties() {
        return jndi;
    }

}
