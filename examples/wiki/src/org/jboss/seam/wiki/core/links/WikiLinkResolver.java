package org.jboss.seam.wiki.core.links;

import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Transactional;
import org.jboss.seam.annotations.AutoCreate;
import org.jboss.seam.wiki.core.node.Document;
import org.jboss.seam.wiki.core.node.Directory;
import org.jboss.seam.wiki.core.node.Node;
import org.jboss.seam.wiki.core.node.File;
import org.jboss.seam.wiki.core.prefs.GlobalPreferences;
import org.jboss.seam.wiki.core.dao.NodeDAO;
import org.jboss.seam.Component;

import javax.faces.context.FacesContext;
import java.util.regex.Pattern;
import java.util.regex.Matcher;
import java.util.Map;

@Name("wikiLinkResolver")
@AutoCreate
public class WikiLinkResolver {

    // Prepended to primary keys in the database, e.g. [This is a stored link=>wiki://5]
    public static final String WIKI_PROTOCOL = "wiki://([0-9]+)";

    // Known protocols are rendered as is
    public static final String KNOWN_PROTOCOLS = "(http://)|(https://)|(ftp://)|(mailto:)";

    // Used against page names, simply remove everything that is not alphanumeric, should do for most strings
    public static final String WIKINAME_REMOVECHARACTERS = "[^\\p{Alnum}]+";

    // Render these strings whenever [=>wiki://123] needs to be resolved but can't
    public static final String BROKENLINK_URL = "PageDoesNotExist";
    public static final String BROKENLINK_DESCRIPTION = "?BROKEN LINK?";

    // Match [GROUP1=>GROUP2], used to replace links from user input with wiki:// URLs
    public static final String WIKILINK_REGEX_FORWARD =
            Pattern.quote("[") + "([^" + Pattern.quote("]") + "|" + Pattern.quote("[") + "]*)" +
            "=>([^(?://)@" + Pattern.quote("]") + Pattern.quote("[") + "]+)" + Pattern.quote("]");

    // Match "Foo Bar|Baz Brrr" as two groups
    public static final String WIKILINK_REGEX_CROSSAREA = "^(.*)" + Pattern.quote("|") + "(.*)$";


    // Match [GROUP1=>wiki://GROUP2], used to replace wiki:// URLs with page names
    public static final String WIKILINK_REGEX_REVERSE =
            Pattern.quote("[") + "([^" + Pattern.quote("]") + "|" + Pattern.quote("[") + "]*)" +
            "=>" + WIKI_PROTOCOL + Pattern.quote("]");

    @In
    private NodeDAO nodeDAO;

    @In
    protected Directory wikiRoot;

    @In(required = false)
    private Document currentDocument;

    @In(required = false)
    private Directory currentDirectory;


    public static String convertToWikiName(String realName) {
        return realName.replaceAll(WIKINAME_REMOVECHARACTERS, "");
    }

    public String convertToWikiLinks(Directory area, String wikiText) {
        if (wikiText == null) return null;

        StringBuffer replacedWikiText = new StringBuffer(wikiText.length());
        Matcher matcher = Pattern.compile(WIKILINK_REGEX_FORWARD).matcher(wikiText);

        // Replace with [Link Text=>wiki://<node id>] or leave as is if not found
        while (matcher.find()) {
            String linkText = matcher.group(2);
            Node node = resolveCrossAreaLinkText(area, linkText);
            if (node != null) matcher.appendReplacement(replacedWikiText, "[$1=>wiki://" + node.getId() + "]");
        }
        matcher.appendTail(replacedWikiText);
        return replacedWikiText.toString();
    }

    public String convertFromWikiLinks(Directory area, String wikiText) {
        if (wikiText == null) return null;
        
        StringBuffer replacedWikiText = new StringBuffer(wikiText.length());
        Matcher matcher = Pattern.compile(WIKILINK_REGEX_REVERSE).matcher(wikiText);

        // Replace with [Link Text=>Page Name] or replace with BROKENLINK "page name"
        while (matcher.find()) {

            // Find the node by PK
            Node node = nodeDAO.findNode(Long.valueOf(matcher.group(2)));

            // Node is in current area, just use its name
            if (node != null && node.getAreaNumber().equals(area.getAreaNumber())) {
                matcher.appendReplacement(replacedWikiText, "[$1=>" + node.getName() + "]");

            // Node is in different area, prepend the area name
            } else if (node != null && !node.getAreaNumber().equals(area.getAreaNumber())) {
                matcher.appendReplacement(replacedWikiText, "[$1=>" + node.getArea().getName() + "|" + node.getName() + "]");

            // Couldn't find it anymore, its a broken link
            } else {
                matcher.appendReplacement(replacedWikiText, "[$1=>" + BROKENLINK_DESCRIPTION + "]");
            }
        }
        matcher.appendTail(replacedWikiText);
        return replacedWikiText.toString();
    }

    @Transactional
    public void resolveWikiLink(Map<String, WikiLink> links, String linkText) {

        // Don't resolve twice
        if (links.containsKey(linkText)) return;

        Matcher wikiUrlMatcher = Pattern.compile(WIKI_PROTOCOL).matcher(linkText);
        Matcher knownProtocolMatcher = Pattern.compile(KNOWN_PROTOCOLS).matcher(linkText);

        WikiLink wikiLink;

        // Check if its a common protocol
        if (knownProtocolMatcher.find()) {
            wikiLink = new WikiLink(null, false, linkText, linkText);

        // Check if it is a wiki protocol
        } else if (wikiUrlMatcher.find()) {

            // Find the node by PK
            Node node = nodeDAO.findNode(Long.valueOf(wikiUrlMatcher.group(1)));
            if (node != null) {
                wikiLink = new WikiLink(node, false, renderURL(node), node.getName());
            } else {
                wikiLink = new WikiLink(null, true, BROKENLINK_URL, BROKENLINK_DESCRIPTION);
            }

        // Try a WikiWord search in the current or named area
        // (This can happen if the string [foo=>bar] or [foo=>bar|baz] was stored in the database because bar or baz
        // didn't exist at the time of saving, so we need to resolve it now and replace it with wiki://123)
        } else {

            Node node = resolveCrossAreaLinkText(currentDirectory, linkText);
            if (node!=null) {
                wikiLink = new WikiLink(node, false, renderURL(node), node.getName());
                // Run the converter again and UPDATE the currentDocument (yes, this happens during rendering!)
                currentDocument.setContent(convertToWikiLinks(currentDirectory, currentDocument.getContent()));
                // This should be updated in the database during the next flush()

            } else {
                /* TODO: Not sure we should actually implement this..., one of these things that the wiki "designers" got wrong
                // OK, so it's not any recognized URL and we can't find a node with that wikiname
                // Let's assume its a page name and render /Area/WikiLink (but encoded, so it gets transported fully)
                // into the edit page when the user clicks on the link to create the document
                try {
                    String encodedPagename = currentDirectory.getWikiname() + "/" + URLEncoder.encode(linkText, "UTF-8");
                    wikiLink = new WikiLink(null, true, encodedPagename, linkText);
                } catch (UnsupportedEncodingException e) {
                    throw new RuntimeException(e); // Java is so great...
                }
                */
                wikiLink = new WikiLink(null, true, BROKENLINK_URL, BROKENLINK_DESCRIPTION);
            }
        }
        links.put(linkText, wikiLink);
    }

    private Node resolveCrossAreaLinkText(Node currentArea, String linkText) {
        Matcher crossLinkMatcher = Pattern.compile(WIKILINK_REGEX_CROSSAREA).matcher(linkText);
        if (crossLinkMatcher.find()) {
            // Try to find the node in the referenced area
            String areaName = crossLinkMatcher.group(1);
            String nodeName = crossLinkMatcher.group(2);
            Node crossLinkArea = nodeDAO.findArea(convertToWikiName(areaName));
            if (crossLinkArea != null)
                return nodeDAO.findNodeInArea(crossLinkArea.getAreaNumber(), convertToWikiName(nodeName));
        } else {
            // Try the current area
            return nodeDAO.findNodeInArea(currentArea.getAreaNumber(), convertToWikiName(linkText));
        }
        return null;
    }

    public class WikiLink {
        Node node;
        boolean broken = false;
        String url;
        String description;

        public WikiLink(Node node, boolean broken, String url, String description) {
            this.node = node;
            this.url = url;
            this.broken = broken;
            this.description = description;
        }
        public Node getNode() { return node; }
        public boolean isBroken() { return broken; }
        public String getUrl() { return url; }
        public String getDescription() { return description; }
        public String toString() {
            return "Description: " + description + " URL: " + url;
        }
    }

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

}
