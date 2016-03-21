/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package iii.vop2016.verkeer2.ejb.dao;

import java.util.Arrays;
import java.util.Collections;
import javax.persistence.EntityManager;
import javax.persistence.Query;

/**
 *
 * @author tobia
 */
public class Request {

    private Parameter[] param;
    private boolean ascending;
    private int limitResult;

    public Request(boolean ascending, int limitResult) {
        this.ascending = ascending;
        this.limitResult = limitResult;
        this.param = new Parameter[1];
    }

    public Request addParam(int place, Parameter p) {
        //enlarge array if required
        while(param.length <= place) {
            param = Arrays.copyOf(param, param.length * 2);
        }

        if (param[place] == null) {
            param[place] = p;
        } else {
            throw new ArrayStoreException("A parameter already exists on this slot");
        }

        return this;
    }

    public Query PrepareQuery(EntityManager em) {
        StringBuilder b = new StringBuilder();
        b.append("SELECT r FROM RouteDataEntity r");

        if (param.length > 0) {
            b.append(" WHERE ");
            for (int i = 0; i < param.length; i++) {
                Parameter p = param[i];
                if (p != null) {
                    if (i != 0) {
                        b.append(" AND ");
                    }
                    p.build(b);
                }
            }

        }

        if (!ascending) {
            b.append(" ORDER BY r.id DESC");
        }

        Query q = em.createQuery(b.toString());

        if (param.length > 0) {
            for (int i = 0; i < param.length; i++) {
                Parameter p = param[i];
                if (p != null) {
                    p.setValue(q);
                }
            }

        }

        if (limitResult != 0) {
            q.setMaxResults(limitResult);
        }
        return q;
    }
}
