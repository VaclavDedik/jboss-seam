package org.jboss.seam.wiki.core.links;

import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Transactional;
import org.jboss.seam.annotations.AutoCreate;
import org.jboss.seam.wiki.core.model.Directory;
import org.jboss.seam.wiki.core.dao.NodeDAO;
import org.jboss.seam.wiki.core.model.*;
import org.jboss.seam.wiki.util.WikiUtil;


import java.util.regex.Pattern;
import java.util.regex.Matcher;
import java.util.Map;

/**
 * A default implementation of <tt>WikiLinkResolver</tt>.
 *
 * @author Christian Bauer
 */
@Name("wikiLinkResolver")
@AutoCreate
public class DefaultWikiLinkResolver implements WikiLinkResolver {

    // Render these strings whenever [=>wiki://123] needs to be resolved but can't
    public static final String BROKENLINK_URL = "PageDoesNotExist";
    public static final String BROKENLINK_DESCRIPTION = "?BROKEN LINK?";

    @In
    private NodeDAO nodeDAO;

    public String convertToWikiProtocol(Long currentAreaNumber, String wikiText) {
        if (wikiText == null) return null;

        StringBuffer replacedWikiText = new StringBuffer(wikiText.length());
        Matcher matcher = Pattern.compile(REGEX_WIKILINK_FORWARD).matcher(wikiText);

        // Replace with [Link Text=>wiki://<node id>] or leave as is if not found
        while (matcher.find()) {
            String linkText = matcher.group(2);
            Node node = resolveCrossAreaLinkText(currentAreaNumber, linkText);
            if (node != null) matcher.appendReplacement(replacedWikiText, "[$1=>wiki://" + node.getId() + "]");
        }
        matcher.appendTail(replacedWikiText);
        return replacedWikiText.toString();
    }

    public String convertFromWikiProtocol(Long currentAreaNumber, String wikiText) {
        if (wikiText == null) return null;
        
        StringBuffer replacedWikiText = new StringBuffer(wikiText.length());
        Matcher matcher = Pattern.compile(REGEX_WIKILINK_REVERSE).matcher(wikiText);

        // Replace with [Link Text=>Page Name] or replace with BROKENLINK "page name"
        while (matcher.find()) {

            // Find the node by PK
            Node node = nodeDAO.findNode(Long.valueOf(matcher.group(2)));

            // Node is in current area, just use its name
            if (node != null && node.getAreaNumber().equals(currentAreaNumber)) {
                matcher.appendReplacement(replacedWikiText, "[$1=>" + node.getName() + "]");

            // Node is in different area, prepend the area name
            } else if (node != null && !node.getAreaNumber().equals(currentAreaNumber)) {
                Directory area = nodeDAO.findArea(node.getAreaNumber());
                matcher.appendReplacement(replacedWikiText, "[$1=>" + area.getName() + "|" + node.getName() + "]");

            // Couldn't find it anymore, its a broken link
            } else {
                matcher.appendReplacement(replacedWikiText, "[$1=>" + BROKENLINK_DESCRIPTION + "]");
            }
        }
        matcher.appendTail(replacedWikiText);
        return replacedWikiText.toString();
    }

    @Transactional
    public void resolveLinkText(Long currentAreaNumber, Map<String, WikiLink> links, String linkText) {

        // Don't resolve twice
        if (links.containsKey(linkText)) return;

        Matcher wikiProtocolMatcher = Pattern.compile(REGEX_WIKI_PROTOCOL).matcher(linkText);
        Matcher knownProtocolMatcher = Pattern.compile(REGEX_KNOWN_PROTOCOL).matcher(linkText);

        WikiLink wikiLink;

        // Check if its a common protocol
        if (knownProtocolMatcher.find()) {
            wikiLink = new WikiLink(false, true);
            wikiLink.setUrl(linkText);
            wikiLink.setDescription(linkText);

        // Check if it is a wiki protocol
        } else if (wikiProtocolMatcher.find()) {

            // Find the node by PK
            Node node = nodeDAO.findNode(Long.valueOf(wikiProtocolMatcher.group(1)));
            if (node != null) {
                wikiLink = new WikiLink(false, false);
                wikiLink.setNode(node);
                wikiLink.setDescription(node.getName());
            } else {
                // Can't do anything, [=>wiki://123] no longer exists
                wikiLink = new WikiLink(true, false);
                wikiLink.setUrl(BROKENLINK_URL);
                wikiLink.setDescription(BROKENLINK_DESCRIPTION);
            }

        // It must be a stored clear text link, such as [=>Target Name] or [=>Area Name|Target Name]
        // (This can happen if the string [foo=>bar] or [foo=>bar|baz] was stored in the database because the
        //  targets didn't exist at the time of saving)
        } else {

            // Try a WikiWord search in the current or named area
            Node node = resolveCrossAreaLinkText(currentAreaNumber, linkText);
            if (node!=null) {

                wikiLink = new WikiLink(false, false);
                wikiLink.setNode(node);
                wikiLink.setDescription(node.getName());
                // Indicate that caller should update the wiki text that contains this link
                wikiLink.setRequiresUpdating(true);

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
                wikiLink = new WikiLink(true, false);
                wikiLink.setUrl(BROKENLINK_URL);
                wikiLink.setDescription(BROKENLINK_DESCRIPTION);
            }
        }
        links.put(linkText, wikiLink);
    }

    private Node resolveCrossAreaLinkText(Long currentAreaNumber, String linkText) {
        Matcher crossLinkMatcher = Pattern.compile(REGEX_WIKILINK_CROSSAREA).matcher(linkText);
        if (crossLinkMatcher.find()) {
            // Try to find the node in the referenced area
            String areaName = crossLinkMatcher.group(1);
            String nodeName = crossLinkMatcher.group(2);
            Node crossLinkArea = nodeDAO.findArea(WikiUtil.convertToWikiName(areaName));
            if ( crossLinkArea != null && (nodeName == null || nodeName.length() == 0) )
                return crossLinkArea; // Support [=>This is an Area Link|] syntax
            else if (crossLinkArea != null)
                return nodeDAO.findNodeInArea(crossLinkArea.getAreaNumber(), WikiUtil.convertToWikiName(nodeName));
        } else {
            // Try the current area
            return nodeDAO.findNodeInArea(currentAreaNumber, WikiUtil.convertToWikiName(linkText));
        }
        return null;
    }

}
