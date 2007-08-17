/*
 * JBoss, Home of Professional Open Source
 *
 * Distributable under LGPL license.
 * See terms of license at gnu.org.
 */
package org.jboss.seam.wiki.util;

import org.jboss.seam.Component;
import org.jboss.seam.wiki.core.action.prefs.WikiPreferences;
import org.jboss.seam.wiki.core.model.*;

import javax.faces.context.FacesContext;
import javax.imageio.ImageIO;
import javax.servlet.http.HttpSession;
import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

/**
 * Adds stuff to and for JSF that should be there but isn't. Also stuff that is exposed
 * as a Facelets function, and various other useful static methods that are called from
 * everywhere.
 *
 * @author Christian Bauer
 */
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
        if (node == null || node.getId() == null) return "";
        if (isFile(node)) return renderFileLink((File)node);
        WikiPreferences wikiPrefs = (WikiPreferences) Component.getInstance("wikiPreferences");
        String contextPath = FacesContext.getCurrentInstance().getExternalContext().getRequestContextPath();
        return contextPath + "/" + node.getId() + wikiPrefs.getPermlinkSuffix();
    }

    public  static String renderWikiLink(Node node) {
        if (node == null || node.getId() == null) return "";
        String contextPath = FacesContext.getCurrentInstance().getExternalContext().getRequestContextPath();
        if (node.getArea().getWikiname().equals(node.getWikiname()))
            return contextPath + "/" + node.getArea().getWikiname();
        return contextPath + "/" + node.getArea().getWikiname()  + "/" + node.getWikiname();
    }

    private static String renderFileLink(File file) {
        if (file == null || file.getId() == null) return "";
        String contextPath = FacesContext.getCurrentInstance().getExternalContext().getRequestContextPath();
        return contextPath + "/servlets/files/download.seam?fileId=" + file.getId();
    }

    public static String renderHomeURL(User user) {
        if (user == null) return "";
        if (user.getMemberHome() == null) throw new IllegalArgumentException("User does not have a home directory");
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

    public static String escapeEmailAddress(String string) {
        WikiPreferences wikiPrefs = (WikiPreferences) Component.getInstance("wikiPreferences");
        return string.length() >= 7 && string.substring(0, 7).equals("mailto:")
                ? string.replaceAll("@", wikiPrefs.getAtSymbolReplacement()) 
                : string;
    }

    public static String escapeHtml(String string) {
        if (string == null) return null;
        StringBuffer sb = new StringBuffer();
        String htmlEntity;
        char c;
        for (int i = 0; i < string.length(); ++i) {
            htmlEntity = null;
            c = string.charAt(i);
            switch (c) {
                case '<': htmlEntity = "&lt;"; break;
                case '>': htmlEntity = "&gt;"; break;
                case '&': htmlEntity = "&amp;"; break;
                case '"': htmlEntity = "&quot;"; break;
            }
            if (htmlEntity != null) {
                sb.append(htmlEntity);
            } else {
                sb.append(c);
            }
        }
        return sb.toString();
    }

    public static byte[] resizeImage(byte[] imageData, String contentType, int width) {
        ImageIcon icon = new ImageIcon(imageData);

        double ratio = (double) width / icon.getIconWidth();
        int resizedHeight = (int) (icon.getIconHeight() * ratio);

        int imageType = "image/png".equals(contentType)
                        ? BufferedImage.TYPE_INT_ARGB
                        : BufferedImage.TYPE_INT_RGB;
        BufferedImage bImg = new BufferedImage(width, resizedHeight, imageType);
        Graphics2D g2d = bImg.createGraphics();
        g2d.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BICUBIC);
        g2d.drawImage(icon.getImage(), 0, 0, width, resizedHeight, null);
        g2d.dispose();

        String formatName = "";
        if ("image/png".equals(contentType))       formatName = "png";
        else if ("image/jpeg".equals(contentType)) formatName = "jpeg";

        ByteArrayOutputStream baos = new ByteArrayOutputStream(1024);
        try {
            ImageIO.write(bImg, formatName, baos);
            return baos.toByteArray();
        } catch (IOException ex) {
            throw new RuntimeException(ex);
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
     * Moves the element at <tt>oldPosition</tt> to <tt>newPosition</tt>.
     * <p>
     * Correctly handles forward and backward shifting of previous or subsequent elements.
     *
     * @param list the list that is affected
     * @param oldPosition the position of the element to move
     * @param newPosition the new position of the element
     */
    public static void shiftListElement(List list, int oldPosition, int newPosition) {
        if (oldPosition> newPosition) {
            Collections.rotate(list.subList(newPosition, oldPosition+1), +1);
        } else if (oldPosition < newPosition) {
            Collections.rotate(list.subList(oldPosition, newPosition+1), -1);
        }
    }

    /**
     * Can't use col.size() in a value binding. Why can't I call arbitrary methods, even
     * with arguments, in a value binding? Java needs properties badly.
     */
    public static int sizeOf(Collection col) {
        return col == null ? 0 : col.size();
    }

    /**
     * EL doesn't support a String lenth() operator.
     */
    public static int length(String string) {
        return string == null ? 0 : string.length();
    }

    /**
     * Used for conditional rendering of JSF messages, again, inflexible EL can't take value bindings with arguments
     * or support simple String concat...
     */
    public static boolean hasMessage(String namingContainer, String componentId) {
        return FacesContext.getCurrentInstance().getMessages(namingContainer + ":" + componentId).hasNext();
    }

}
