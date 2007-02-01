package org.jboss.seam.wiki.core.links;

import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Transactional;
import org.jboss.seam.wiki.core.node.Document;
import org.jboss.seam.wiki.core.node.Directory;
import org.jboss.seam.wiki.core.node.Node;

import javax.persistence.EntityManager;
import javax.persistence.EntityNotFoundException;
import javax.persistence.NoResultException;
import java.util.regex.Pattern;
import java.util.regex.Matcher;
import java.util.Map;
import java.net.URLEncoder;
import java.io.UnsupportedEncodingException;

@Name("wikiLinkResolver")
public class WikiLinkResolver {

    // Prepended to primary keys in the database, e.g. [This is a stored link=>wiki://5]
    public static final String WIKI_PROTOCOL = "wiki://";

    // Used against page names, wimply remove everything that is not alphanumeric, should do for most strings
    public static final String WIKINAME_REMOVECHARACTERS = "[^\\p{Alnum}]+";

    // Render this string whenever [=>wiki://123] needs to be resolved but can't
    public static final String BROKENLINK_MARKER = "BROKEN LINK";

    // Match [GROUP1=>GROUP2], used to replace links from user input with wiki:// URLs
    public static final String WIKILINK_REGEX_FORWARD =
            Pattern.quote("[") + "([^" + Pattern.quote("]") + "|" + Pattern.quote("[") + "]*)" +
            "=>([^(?://|@|" + Pattern.quote("]") + "|" + Pattern.quote("[") + ")]+)" + Pattern.quote("]");

    // Match [GROUP1=>wiki://GROUP2], used to replace wiki:// URLs with page names
    public static final String WIKILINK_REGEX_REVERSE =
            Pattern.quote("[") + "([^" + Pattern.quote("]") + "|" + Pattern.quote("[") + "]*)" +
            "=>" + WIKI_PROTOCOL + "([0-9]+)" + Pattern.quote("]");

    @In(create = true)
    protected EntityManager entityManager;

    @In(required = false)
    private Document currentDocument;

    @In(required = false)
    private Directory currentDirectory;

    public static String convertToWikiName(String realName) {
        return realName.replaceAll(WIKINAME_REMOVECHARACTERS, "");
    }

    public String convertToWikiLinks(Directory area, String wikiText) {
        StringBuffer replacedWikiText = new StringBuffer(wikiText.length());

        Pattern pattern = Pattern.compile(WIKILINK_REGEX_FORWARD);
        Matcher matcher = pattern.matcher(wikiText);

        // Replace with [Link Text=>wiki://<node id>] or leave as is if not found
        while (matcher.find()) {
            Node node = findNodeInArea(area, convertToWikiName(matcher.group(2)));
            if (node != null) {
                matcher.appendReplacement(replacedWikiText, "[$1=>wiki://" + node.getId() + "]");
            }
        }
        matcher.appendTail(replacedWikiText);
        return replacedWikiText.toString();
    }

    public String convertFromWikiLinks(String wikiText) {
        if (wikiText == null) return null;
        
        StringBuffer replacedWikiText = new StringBuffer(wikiText.length());

        Pattern pattern = Pattern.compile(WIKILINK_REGEX_REVERSE);
        Matcher matcher = pattern.matcher(wikiText);

        // Replace with [Link Text=>Page Name] or replace with BROKEN LINK marker
        while (matcher.find()) {
            // Find the node by PK
            Node node = findNode(Long.valueOf(matcher.group(2)));
            if (node != null) {
                matcher.appendReplacement(replacedWikiText, "[$1=>" + node.getName() + "]");
            } else {
                matcher.appendReplacement(replacedWikiText, "[$1=>" + BROKENLINK_MARKER + "]");
            }
        }
        matcher.appendTail(replacedWikiText);
        return replacedWikiText.toString();
    }

    @Transactional
    public void resolveWikiLink(Map<String, WikiLink> links, String linkText) {

        // Don't resolve twice
        if (links.containsKey(linkText)) return;

        Pattern pattern = Pattern.compile(WIKI_PROTOCOL + "([0-9]+)");
        Matcher matcher = pattern.matcher(linkText);

        WikiLink wikiLink = null;

        // Check if its a common protocol
        if ("http://".equals(linkText.substring(0, 6)) ||
            "https://".equals(linkText.substring(0,7)) ||
            "mailto://".equals(linkText.substring(0,8)) ||
            "ftp://".equals(linkText.substring(0,5))
           ) {
            wikiLink = new WikiLink(null, false, linkText, linkText);

        // Check if it is a wiki protocol
        } else if (matcher.find()) {

            // Find the node by PK
            Node node = findNode(Long.valueOf(matcher.group(1)));
            if (node != null) {
                wikiLink = new WikiLink(node.getId(), false, node.getId() + ".html", node.getName());
            } else {
                wikiLink = new WikiLink(Long.valueOf(matcher.group(1)), true, BROKENLINK_MARKER, BROKENLINK_MARKER);
            }

        // Try a WikiWord search in the current area
        } else {

            Node node = findNodeInArea(currentDirectory, convertToWikiName(linkText));
            if (node!=null) {
                wikiLink = new WikiLink(node.getId(), false, node.getId() + ".html", node.getName());
                // Run the converter again and UPDATE the currentDocument (yes, not the best solution)
                currentDocument.setContent(convertToWikiLinks(currentDirectory, currentDocument.getContent()));
                // This should be updated in the database during the next flush()
            }
        }

        // Let's assume its a page name and render a real /Area/WikiLink (but encoded, so it gets transported fully)
        if (wikiLink == null) {
            try {
                String encodedPagename = currentDirectory.getWikiname() + "/" + URLEncoder.encode(linkText, "UTF-8");
                wikiLink = new WikiLink(null, true, encodedPagename, linkText);
            } catch (UnsupportedEncodingException e) {
                throw new RuntimeException(e); // Java is so great...
            }
        }
        links.put(linkText, wikiLink);
    }

    public class WikiLink {
        Long nodeId;
        boolean broken = false;
        String url;
        String description;

        public WikiLink(Long nodeId, boolean broken, String url, String description) {
            this.nodeId = nodeId;
            this.broken = broken;
            this.url = url;
            this.description = description;
        }

        public String toString() {
            return "Description: " + description + " URL: " + url;
        }
    }

    // Convenience methods

    @Transactional
    public Node findNode(Long nodeId) {
        entityManager.joinTransaction();
        try {
            return entityManager.find(Node.class, nodeId);
        } catch (EntityNotFoundException ex) {
        }
        return null;
    }

    @Transactional
    public Node findNodeInArea(Directory area, String wikiname) {
        entityManager.joinTransaction();

        try {
            return (Node) entityManager
                    .createQuery("select n from Node n where n.areaNumber = :areaNumber and n.wikiname = :wikiname")
                    .setParameter("areaNumber", area.getAreaNumber())
                    .setParameter("wikiname", wikiname)
                    .getSingleResult();
        } catch (EntityNotFoundException ex) {
        } catch (NoResultException ex) {
        }
        return null;
    }

    @Transactional
    public Document findDocumentInArea(Directory area, String wikiname) {
        entityManager.joinTransaction();

        try {
            return (Document) entityManager
                    .createQuery("select d from Document d where d.areaNumber = :areaNumber and d.wikiname = :wikiname")
                    .setParameter("areaNumber", area.getAreaNumber())
                    .setParameter("wikiname", wikiname)
                    .getSingleResult();
        } catch (EntityNotFoundException ex) {
        } catch (NoResultException ex) {
        }
        return null;
    }

    @Transactional
    public Directory findDirectoryInArea(Directory area, String wikiname) {
        entityManager.joinTransaction();

        try {
            return (Directory) entityManager
                    .createQuery("select d from Directory d where d.areaNumber = :areaNumber and d.wikiname = :wikiname")
                    .setParameter("areaNumber", area.getAreaNumber())
                    .setParameter("wikiname", wikiname)
                    .getSingleResult();
        } catch (EntityNotFoundException ex) {
        } catch (NoResultException ex) {
        }
        return null;
    }

}
