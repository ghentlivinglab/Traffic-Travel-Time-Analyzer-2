/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package iii.vop2016.verkeer2.ejb.components;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.function.Predicate;

/**
 *
 * @author Mike
 */
public class Route implements IRoute {

    protected long id;
    protected String name;
    protected List<IGeoLocation> geolocations;

    public Route() {
        geolocations = new ArrayList<>();
        this.name = null;
        this.id = 0;
    }

    public Route(String name) {
        this.name = name;
        geolocations = new ArrayList<>();
        this.id = 0;
    }

    public Route(IRoute component) {
        this.id = component.getId();
        this.name = component.getName();
        this.geolocations = component.getGeolocations();
        List<IGeoLocation> l = new ArrayList<>();
        for (IGeoLocation loc : getGeolocations()) {
            l.add(new GeoLocation(loc));
        }
        Collections.sort(l, new GeoLocationComparator());
        setGeolocations(l);
    }

    @Override
    public long getId() {
        return this.id;
    }

    @Override
    public void setId(long id) {
        this.id = id;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public List<IGeoLocation> getGeolocations() {
        return geolocations;
    }

    @Override
    public IGeoLocation getStartLocation() {
        if (geolocations.size() > 0) {
            return geolocations.get(0);
        } else {
            return null;
        }
    }

    @Override
    public IGeoLocation getEndLocation() {
        if (geolocations.size() > 0) {
            return geolocations.get(geolocations.size() - 1);
        } else {
            return null;
        }
    }

    @Override
    public void setName(String name) {
        this.name = name;
    }

    @Override
    public void setGeolocations(List<IGeoLocation> locations) {
        geolocations = locations;
    }

    @Override
    public void addGeolocation(IGeoLocation location) {
        geolocations.add(location);
        location.setSortRank(geolocations.size());
    }

    @Override
    public void addGeolocation(IGeoLocation location, int i) {
        for (IGeoLocation loc : geolocations) {
            if (loc.getSortRank() >= i) {
                loc.setSortRank(loc.getSortRank() + 1);
            }
        }
        location.setSortRank(i);
        geolocations.add(location);

    }

    @Override
    public void removeGeoLocation(IGeoLocation location) {
        geolocations.remove(location);
    }

    @Override
    public String toString() {
        return "Route (" + id + ", " + name + ")" + hashCode();
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 97 * hash + Objects.hashCode(this.name) + Objects.hashCode(this.id);
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (!(obj instanceof IRoute)) {
            return false;
        }
        final IRoute other = (IRoute) obj;
        if (!other.getName().equals(getName())) {
            return false;
        }
        if (other.getId() != getId()) {
            return false;
        }
        return true;
    }

    @Override
    public void removeGeolocation(final int sortRank) {
        geolocations.removeIf(new Predicate<IGeoLocation>() {
            @Override
            public boolean test(IGeoLocation t) {
                if (t.getSortRank() == sortRank) {
                    return true;
                }
                return false;
            }
        });

        for (IGeoLocation geo : geolocations) {
            if(geo.getSortRank() > sortRank){
                geo.setSortRank(geo.getSortRank()-1);
            }
        }
    }

}
