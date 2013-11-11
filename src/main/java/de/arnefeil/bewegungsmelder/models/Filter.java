package de.arnefeil.bewegungsmelder.models;

import java.util.ArrayList;

/**
 * Created by arne on 10/3/13.
 */
public class Filter {

    private String name;
    private ArrayList<Object> whitelist;

    public Filter(String name) {
        this.name = name;
        this.whitelist = new ArrayList<Object>();
    }

    public ArrayList<Object> getWhitelist() {
        return this.whitelist;
    }

    public void setWhitelist(ArrayList<Object> whitelist) {
        this.whitelist = whitelist;
    }

    public String getName() {
        return this.name;
    }

    public void clearWhitelist() {
        this.whitelist.clear();
    }
    @Override
    public String toString() {
        return this.name;
    }
}
