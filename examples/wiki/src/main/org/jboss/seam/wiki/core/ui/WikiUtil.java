package org.jboss.seam.wiki.core.ui;

import org.jboss.seam.annotations.Name;
import org.jboss.seam.wiki.core.model.*;
import org.jboss.seam.Component;

import javax.faces.component.UIData;
import javax.faces.context.FacesContext;
import java.util.Collection;

/**
 * Adds stuff to and for JSF that should be there but isn't. Also stuff that is exposed
 * as a Facelets function, we can't have that in a Seam component - different classloader
 * for hot redeployment of Seam components.
 */
@Name("wikiUtil")
public class WikiUtil {

    // Used against page names, simply remove everything that is not alphanumeric, should do for most strings
    public static final String WIKINAME_REMOVECHARACTERS = "[^\\p{Alnum}]+";

    // Replacement for missing instaceOf in EL (can't use string comparison, might be proxy)
    public static boolean isDirectory(Node node) {
        return node != null && Directory.class.isAssignableFrom(node.getClass());
    }

    public static boolean isDocument(Node node) {
        return node != null && Document.class.isAssignableFrom(node.getClass());
    }

    public static boolean isFile(Node node) {
        return node != null && File.class.isAssignableFrom(node.getClass());
    }

    // Allow calling this as a Facelets function in pages
    public static String renderURL(Node node) {
        GlobalPreferences globalPrefs = (GlobalPreferences) Component.getInstance("globalPrefs");
        String contextPath = FacesContext.getCurrentInstance().getExternalContext().getRequestContextPath();

        if (isFile(node)) {
            return contextPath + "/files/download?fileId=" + node.getId();
        }

        if (globalPrefs.getDefaultURLRendering().equals(GlobalPreferences.URLRendering.PERMLINK)) {
            return contextPath + "/" + node.getId() + globalPrefs.getPermlinkSuffix();
        } else {
            if (node.getArea().getWikiname().equals(node.getWikiname()))
                return contextPath + "/" + node.getArea().getWikiname();
            return contextPath + "/" + node.getArea().getWikiname()  + "/" + node.getWikiname();
        }
    }

    public static String convertToWikiName(String realName) {
        return realName.replaceAll(WIKINAME_REMOVECHARACTERS, "");
    }

    /**
     * Need to bind UI components to non-conversational backing beans.
     * That this is even needed makes no sense. Why can't I call the UI components
     * in the EL directly? Don't try components['id'], it won't work.
     */
    private UIData datatable;
    public UIData getDatatable() { return datatable; }
    public void setDatatable(UIData datatable) { this.datatable = datatable; }

    /**
     * Can't use col.size() in a value binding. Why can't I call arbitrary methods, even
     * with arguments, in a value binding? Java needs properties badly.
     */
    public static int sizeOf(Collection col) {
        return col.size();
    }
}
