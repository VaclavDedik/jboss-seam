package org.jboss.seam.wiki.util;

import org.jboss.seam.annotations.Name;
import org.jboss.seam.wiki.core.model.*;
import org.jboss.seam.wiki.core.action.prefs.WikiPreferences;
import org.jboss.seam.Component;

import javax.faces.component.UIData;
import javax.faces.context.FacesContext;
import javax.servlet.http.HttpSession;
import java.util.Collection;
import java.util.List;
import java.math.BigDecimal;

/**
 * Adds stuff to and for JSF that should be there but isn't. Also stuff that is exposed
 * as a Facelets function, and various other useful static methods that are called from
 * everywhere.
 *
 * @author Christian Bauer
 */
@Name("wikiUtil")
public class WikiUtil {

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

    // Rendering made easy
    public static String renderURL(Node node) {
        if (isFile(node)) return renderFileLink((File)node);
        WikiPreferences wikiPrefs = (WikiPreferences) Component.getInstance("wikiPreferences");
        if (wikiPrefs.isRenderPermlinks()) {
            return renderPermLink(node);
        } else {
            return renderWikiLink(node);
        }
    }

    public static String renderPermLink(Node node) {
        if (isFile(node)) return renderFileLink((File)node);
        WikiPreferences wikiPrefs = (WikiPreferences) Component.getInstance("wikiPreferences");
        String contextPath = FacesContext.getCurrentInstance().getExternalContext().getRequestContextPath();
        return contextPath + "/" + node.getId() + wikiPrefs.getPermlinkSuffix();
    }

    public  static String renderWikiLink(Node node) {
        String contextPath = FacesContext.getCurrentInstance().getExternalContext().getRequestContextPath();
        if (node.getArea().getWikiname().equals(node.getWikiname()))
            return contextPath + "/" + node.getArea().getWikiname();
        return contextPath + "/" + node.getArea().getWikiname()  + "/" + node.getWikiname();
    }

    private static String renderFileLink(File file) {
        String contextPath = FacesContext.getCurrentInstance().getExternalContext().getRequestContextPath();
        return contextPath + "/servlets/files/download.seam?fileId=" + file.getId();
    }

    public static String renderHomeURL(User user) {
        String contextPath = FacesContext.getCurrentInstance().getExternalContext().getRequestContextPath();
        return contextPath + "/" + user.getMemberHome().getParent().getWikiname() + "/" + user.getMemberHome().getWikiname();

    }

    public static String displayFilesize(int fileSizeInBytes) {
        // TODO: Yeah, that could be done smarter..
        if (fileSizeInBytes >= 1073741824) {
            return new BigDecimal(fileSizeInBytes / 1024 / 1024 / 1024) + " GiB";
        }else if (fileSizeInBytes >= 1048576) {
            return new BigDecimal(fileSizeInBytes / 1024 / 1024) + " MiB";
        } else if (fileSizeInBytes >= 1024) {
            return new BigDecimal(fileSizeInBytes / 1024) + " KiB";
        } else {
            return new BigDecimal(fileSizeInBytes) + " Bytes";
        }
    }


    public static Throwable unwrap(Throwable throwable) throws IllegalArgumentException {
        if (throwable == null) {
            throw new IllegalArgumentException("Cannot unwrap null throwable");
        }
        for (Throwable current = throwable; current != null; current = current.getCause()) {
            throwable = current;
        }
        return throwable;
    }

    public static int getSessionTimeoutSeconds() {
        return ((HttpSession) FacesContext.getCurrentInstance().getExternalContext().getSession(true)).getMaxInactiveInterval();
    }

    /**
     * Need to bind UI components to non-conversational backing beans.
     * That this is even needed makes no sense. Why can't I call the UI components
     * in the EL directly? Don't try #{components['id']}, it won't work.
     */
    private UIData datatable;
    public UIData getDatatable() { return datatable; }
    public void setDatatable(UIData datatable) { this.datatable = datatable; }

    /**
     * Can't use col.size() in a value binding. Why can't I call arbitrary methods, even
     * with arguments, in a value binding? Java needs properties badly.
     */
    public static int sizeOf(Collection col) {
        return col == null ? 0 : col.size();
    }

    public static int lenth(String string) {
        return string.length();
    }
}
