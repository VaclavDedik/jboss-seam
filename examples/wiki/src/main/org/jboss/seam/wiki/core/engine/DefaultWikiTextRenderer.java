package org.jboss.seam.wiki.core.engine;

import org.jboss.seam.wiki.util.WikiUtil;
import org.jboss.seam.wiki.core.model.WikiMacro;

import java.util.List;

/**
 * Convenience class that renders some sensible defaults that apply for the wiki.
 *
 * @author Christian Bauer
 */
public class DefaultWikiTextRenderer implements WikiTextRenderer {

    public String renderInlineLink(WikiLink inlineLink) {
        return !inlineLink.isBroken() ?
                "<a href=\""
                + WikiUtil.renderURL(inlineLink.getFile())
                + "\">"
                + inlineLink.getDescription()
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

    public String renderThumbnailImageInlineLink(WikiLink inlineLink) {
        return "[Embedded Image]";
    }

    public String renderMacro(String macroName) {
        return "[Macro]";
    }

    public void addMacro(WikiMacro macro) {}

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

    public String renderHeadline1Opentag() {
        return "<h1 class=\"wikiHeadline1\">";
    }

    public String renderHeadline2OpenTag() {
        return "<h2 class=\"wikiHeadline2\">";
    }

    public String renderHeadline3OpenTag() {
        return "<h3 class=\"wikiHeadline3\">";
    }

    public String renderHeadline4OpenTag() {
        return "<h4 class=\"wikiHeadline4\">";
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
}
