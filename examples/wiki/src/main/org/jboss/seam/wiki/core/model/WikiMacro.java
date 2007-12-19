package org.jboss.seam.wiki.core.model;

import java.io.Serializable;
import java.util.SortedMap;
import java.util.TreeMap;

public class WikiMacro implements Serializable {

    private String name;
    private SortedMap<String,String> params = new TreeMap<String,String>();

    public WikiMacro(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public SortedMap<String, String> getParams() {
        return params;
    }

    public void setParams(SortedMap<String, String> params) {
        this.params = params;
    }

    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        WikiMacro macro = (WikiMacro) o;

        if (!name.equals(macro.name)) return false;

        return true;
    }

    public int hashCode() {
        return name.hashCode();
    }

    public String toString() {
        return "WikiMacro: " + getName() + " Params: " + getParams().size();
    }
}
