/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package iii.vop2016.verkeer2.ejb.dao;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.persistence.Query;
import javax.validation.ConstraintDeclarationException;
import javax.validation.ConstraintViolationException;

/**
 *
 * @author tobia
 */
public class Parameter<T> {

    public String property, name;
    public T value;
    public T value2;
    public Operation operation;

    public Aggregation aggregation;
    private String fields;
    public boolean isAggregation;

    public Parameter(String property, T value, T value2, Operation operation) {
        this.property = property;
        this.name = property;
        this.value = value;
        this.value2 = value2;
        this.operation = operation;
        this.isAggregation = false;
    }

    public Parameter(String property, T value, Operation operation) {
        this.property = property;
        this.name = property;
        this.value = value;
        this.value2 = null;
        this.operation = operation;
        this.isAggregation = false;
    }

    private static Pattern pat = Pattern.compile("([a-zA-Z]+)");
    public Parameter(Aggregation type ,String param) {
        this.aggregation = type;
        this.fields = param;
        this.isAggregation = true;

        if (param == null || param.equals("")) {
            throw new ConstraintDeclarationException("param is empty!");
        }
        
        Matcher matcher = pat.matcher(param);
        StringBuffer b = new StringBuffer();
        while(matcher.find()){
            matcher.appendReplacement(b, "r." + matcher.group(1));
        }
        matcher.appendTail(b);
        
        this.fields = b.toString();
    }

    public void build(StringBuilder b) {
        if (isAggregation) {
            switch (aggregation) {
                case sum:
                    b.append("SUM(");
                    b.append(fields);
                    b.append(")");
                    break;
                case none:
                    b.append("(");
                    b.append(fields);
                    b.append(")");
                    break;
            }
        } else {
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
