/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package iii.vop2016.verkeer2.ejb.dao;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import javax.persistence.Query;

/**
 *
 * @author tobia
 */
public class Parameter<T> {

    public String property, name;
    public T value;
    public T value2;
    public Operation operation;

    public Parameter(String property, T value, T value2, Operation operation) {
        this.property = property;
        this.name = property;
        this.value = value;
        this.value2 = value2;
        this.operation = operation;
    }

    public Parameter(String property, T value, Operation operation) {
        this.property = property;
        this.name = property;
        this.value = value;
        this.value2 = null;
        this.operation = operation;
    }

    public void build(StringBuilder b) {
        b.append("(");

        if (value instanceof List) {
            List val = (List) value;
            for (int x = 0; x < val.size(); x++) {
                if (x != 0) {
                    b.append(" OR ");
                }
                buildComparator(b, property, name + "Entry" + x);
            }
        } else {
            buildComparator(b, property, name);
        }

        b.append(")");

    }

    private void buildComparator(StringBuilder b, String property, String name) {
        switch (operation) {
            case eq:
                b.append("r." + property);
                b.append(" = ");
                b.append(":" + name);
                break;
            case gt:
                b.append("r." + property);
                b.append(" > ");
                b.append(":" + name);
                break;
            case lt:
                b.append("r." + property);
                b.append(" < ");
                b.append(":" + name);
                break;
            case between:
                b.append("( r." + property);
                b.append(" > ");
                b.append(":" + name + "1");

                b.append(" AND ");

                b.append("r." + property);
                b.append(" < ");
                b.append(":" + name + "2 )");
                break;
        }
    }

    void setValue(Query q) {

        if (value instanceof List) {
            List val = (List) value;
            for (int x = 0; x < val.size(); x++) {
                if (value2 != null) {
                    List val2 = (List) value2;
                    setValueSingle(q, name + "Entry" + x, val.get(x), val2.get(x));
                } else {
                    setValueSingle(q, name + "Entry" + x, val.get(x), null);
                }
            }
        } else {
            setValueSingle(q, name, value, value2);
        }

    }

    private void setValueSingle(Query q, String name, Object value, Object value2) {
        switch (operation) {
            case eq:
            case gt:
            case lt:
                q.setParameter(name, value);
                break;
            case between:
                q.setParameter(name + "1", value);
                q.setParameter(name + "2", value2);
                break;
        }
    }
}
