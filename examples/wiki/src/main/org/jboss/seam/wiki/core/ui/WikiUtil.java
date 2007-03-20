package org.jboss.seam.wiki.core.ui;

import org.jboss.seam.annotations.Name;
import org.jboss.seam.wiki.core.model.*;
import org.jboss.seam.Component;

import javax.faces.component.UIData;
import javax.faces.context.FacesContext;
import javax.servlet.http.HttpSession;
import java.util.Collection;
import java.util.List;
import java.util.StringTokenizer;

/**
 * Adds stuff to and for JSF that should be there but isn't. Also stuff that is exposed
 * as a Facelets function, we can't have that in a Seam component - different classloader
 * for hot redeployment of Seam components.
 */
@Name("wikiUtil")
public class WikiUtil {

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

    // EL is weak
    public static String truncateString(String string, int length, String appendString) {
        if (string.length() <= length) return string;
        return string.substring(0, length-1) + appendString;
    }

    public static String concat(String a, String b) {
        return a + b;
    }

    // Display all roles for a particular access level
    public static Role.AccessLevel resolveAccessLevel(Integer accessLevel) {
        List<Role.AccessLevel> accessLevels = (List<Role.AccessLevel>)Component.getInstance("accessLevelsList");
        return accessLevels.get(
                accessLevels.indexOf(new Role.AccessLevel(accessLevel, null))
               );
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

    // Creates clean alphanumeric UpperCaseCamelCase
    public static String convertToWikiName(String realName) {
        StringBuilder wikiName = new StringBuilder();
        // Remove everything that is not alphanumeric or whitespace, then split on word boundaries
        String[] tokens = realName.replaceAll("[^\\p{Alnum}|\\s]+", "").split("\\s");
        for (String token : tokens) {
            // Append word, uppercase first letter of word
            if (token.length() > 1) {
                wikiName.append(token.substring(0,1).toUpperCase());
                wikiName.append(token.substring(1));
            } else {
                wikiName.append(token.toUpperCase());
            }
        }
        return wikiName.toString();
    }

    public static String renderHomeURL(User user) {
        String contextPath = FacesContext.getCurrentInstance().getExternalContext().getRequestContextPath();
        return contextPath + "/" + user.getMemberHome().getParent().getWikiname() + "/" + user.getMemberHome().getWikiname();

    }

    public static int getSessionTimeoutSeconds() {
        return ((HttpSession) FacesContext.getCurrentInstance().getExternalContext().getSession(true)).getMaxInactiveInterval();
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
