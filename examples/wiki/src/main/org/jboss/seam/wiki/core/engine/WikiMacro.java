package org.jboss.seam.wiki.core.engine;

import java.io.Serializable;
import java.util.SortedMap;
import java.util.TreeMap;

public class WikiMacro implements Serializable {

    /* TODO: Extract metadata and build macro framework
    protected String[] skins = {"d", "m"}
    protected long accessLevel = 0;

    protected boolean documentDiscriminator = false;

    protected boolean reentrant = true;
    protected boolean renderedOnce = false;
    protected boolean affectsRenderingOnly = true;

    protected boolean appliesToHeader = false;
    protected boolean appliesToContent = true;
    protected boolean appliesToFooter = false;

    protected boolean displayedInPreview = true;

    createMethodBuildTime
    createMethodRenderTime
    */

    private Integer position;
    private String clientId;
    private String name;
    private SortedMap<String,String> params = new TreeMap<String,String>();

    public WikiMacro(String name) {
        this.name = name;
    }

    public WikiMacro(Integer position, String name) {
        this.position = position;
        this.name = name;
    }

    public Integer getPosition() {
        return position;
    }

    public void setPosition(Integer position) {
        this.position = position;
    }

    public String getClientId() {
        return clientId;
    }

    public void setClientId(String clientId) {
        this.clientId = clientId;
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

        WikiMacro wikiMacro = (WikiMacro) o;

        if (!name.equals(wikiMacro.name)) return false;
        if (position != null ? !position.equals(wikiMacro.position) : wikiMacro.position != null) return false;

        return true;
    }

    public int hashCode() {
        int result;
        result = (position != null ? position.hashCode() : 0);
        result = 31 * result + name.hashCode();
        return result;
    }

    public String toString() {
        return "WikiMacro (" + getPosition() + "): " + getName() + " Params: " + getParams().size();
    }
}
