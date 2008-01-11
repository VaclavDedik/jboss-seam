package org.jboss.seam.wiki.core.engine;

import org.jboss.seam.wiki.util.WikiUtil;

import java.util.List;

/**
 * Convenience class that renders some sensible defaults that apply for the wiki.
 *
 * @author Christian Bauer
 */
public class DefaultWikiTextRenderer implements WikiTextRenderer {

    public String renderInternalLink(WikiLink internalLink) {
        return !internalLink.isBroken() ?
                "<a href=\""
                + WikiUtil.renderURL(internalLink.getFile())
                + "\">"
                + internalLink.getDescription()
                + "</a>" : "[Broken Link]";
    }

    public String renderExternalLink(WikiLink externalLink) {
        return "<a href=\""
                + externalLink.getUrl()
                + "\">"
                + externalLink.getDescription()
                + "</a>";
    }

    public String renderFileAttachmentLink(int attachmentNumber, WikiLink attachmentLink) {
        return "[Attachment]";
    }

    public String renderThumbnailImageLink(WikiLink link) {
        return "[Embedded Image]";
    }

    public String renderMacro(WikiMacro macro) {
        return "[Macro]";
    }

    public void setAttachmentLinks(List<WikiLink> attachmentLinks) {}
    public void setExternalLinks(List<WikiLink> externalLinks) {}

    public String renderParagraphOpenTag() {
        return "<p class=\"wikiPara\">\n";
    }

    public String renderPreformattedOpenTag() {
        return "<pre class=\"wikiPreformatted\">\n";
    }

    public String renderBlockquoteOpenTag() {
        return "<blockquote class=\"wikiBlockquote\">\n";
    }

    public String renderHeadline1(String headline) {
        return "<h1 class=\"wikiHeadline1\" id=\""+getHeadlineId(headline)+"\">" + headline + "</h1>";
    }

    public String renderHeadline2(String headline) {
        return "<h2 class=\"wikiHeadline2\" id=\""+getHeadlineId(headline)+"\">" + headline + "</h2>";
    }

    public String renderHeadline3(String headline) {
        return "<h3 class=\"wikiHeadline3\" id=\""+getHeadlineId(headline)+"\">" + headline + "</h3>";
    }

    public String renderHeadline4(String headline) {
        return "<h4 class=\"wikiHeadline4\" id=\""+getHeadlineId(headline)+"\">" + headline + "</h4>";
    }


    public String renderOrderedListOpenTag() {
        return "<ol class=\"wikiOrderedList\">\n";
    }

    public String renderOrderedListItemOpenTag() {
        return "<li class=\"wikiOrderedListItem\">";
    }

    public String renderUnorderedListOpenTag() {
        return "<ul class=\"wikiUnorderedList\">\n";
    }

    public String renderUnorderedListItemOpenTag() {
        return "<li class=\"wikiUnorderedListItem\">";
    }

    public String renderEmphasisOpenTag() {
        return "<i class=\"wikiEmphasis\">";
    }

    public String renderEmphasisCloseTag() {
        return "</i>";
    }

    protected String getHeadlineId(String headline) {
        // HTML id attribute has restrictions on valid values... so the easiest way is to make this a WikiLink
        return HEADLINE_ID_PREFIX+WikiUtil.convertToWikiName(headline);
        // We also need to access it correctly, see WikiLink.java
    }
}
