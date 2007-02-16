package org.jboss.seam.wiki.core.prefs;

import javax.faces.context.FacesContext;

public class GlobalPreferences {

    private URLRendering defaultURLRendering;
    private String permlinkSuffix;
    private String themeName;

    public URLRendering getDefaultURLRendering() {
        return defaultURLRendering;
    }

    public void setDefaultURLRendering(String enumName) {
        defaultURLRendering = URLRendering.valueOf(enumName);
    }

    public String getPermlinkSuffix() {
        return permlinkSuffix;
    }

    public void setPermlinkSuffix(String permlinkSuffix) {
        this.permlinkSuffix = permlinkSuffix;
    }

    public String getThemeName() {
        return themeName;
    }

    public void setThemeName(String themeName) {
        this.themeName = themeName;
    }

    public enum URLRendering {
        PERMLINK, WIKILINK
    }

}
