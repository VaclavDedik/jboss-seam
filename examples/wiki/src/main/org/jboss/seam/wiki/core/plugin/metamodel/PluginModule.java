/*
 * JBoss, Home of Professional Open Source
 *
 * Distributable under LGPL license.
 * See terms of license at gnu.org.
 */
package org.jboss.seam.wiki.core.plugin.metamodel;

import org.jboss.seam.wiki.core.plugin.metamodel.Plugin;
import org.jboss.seam.wiki.core.exception.InvalidWikiConfigurationException;

import java.util.*;
import java.io.Serializable;

/**
 * @author Christian Bauer
 */
public abstract class PluginModule implements Serializable {

    private Plugin plugin;
    private String key;
    private String label;
    private String description;
    private String className;
    private SortedSet<String> fragmentCacheRegions = new TreeSet<String>();

    protected PluginModule(Plugin plugin, String key) {
        if (!key.matches(Plugin.KEY_PATTERN))
            throw new InvalidWikiConfigurationException("Key doesn't match pattern '"+Plugin.KEY_PATTERN+"': " + key);

        this.plugin = plugin;
        this.key = key;
    }

    public Plugin getPlugin() {
        return plugin;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }


    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getClassName() {
        return className;
    }

    public void setClassName(String className) {
        this.className = className;
    }

    public SortedSet<String> getFragmentCacheRegions() {
        return fragmentCacheRegions;
    }

    public List<String> getFragmentCacheRegionsAsList() {
        return Collections.unmodifiableList(new ArrayList<String>(getFragmentCacheRegions()));
    }

    public void setFragmentCacheRegions(SortedSet<String> fragmentCacheRegions) {
        this.fragmentCacheRegions = fragmentCacheRegions;
    }

    public void addFragmentCacheRegion(String name) {
        getFragmentCacheRegions().add(getQualifiedCacheRegionName(name));
    }

    public String getFullyQualifiedKey() {
        return getPlugin().getKey() + "." + getKey();
    }

    public String getQualifiedCacheRegionName(String name) {
        return getFullyQualifiedKey() + "." + name;
    }


}
