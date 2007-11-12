package org.jboss.seam.wiki.core.engine;

import java.util.List;

/**
 * Renders nothing for links and macros and unstyled HTML for anything else.
 *
 * @author Christian Bauer
 */
public class NullWikiTextRenderer implements WikiTextRenderer {

    public String renderInlineLink(WikiLink inlineLink) { return null; }
    public String renderExternalLink(WikiLink externalLink) { return null; }
    public String renderFileAttachmentLink(int attachmentNumber, WikiLink attachmentLink) { return null; }
    public String renderThumbnailImageInlineLink(WikiLink inlineLink) { return null; }
    public void setAttachmentLinks(List<WikiLink> attachmentLinks) {}
    public void setExternalLinks(List<WikiLink> externalLinks) {}
    public String renderMacro(String macroName) { return null; }
    public String renderParagraphOpenTag() { return "<p>\n"; }
    public String renderPreformattedOpenTag() { return "<pre>\n"; }
    public String renderBlockquoteOpenTag() { return "<blockquote>\n"; }
    public String renderHeadline1Opentag() { return "<h1>"; }
    public String renderHeadline2OpenTag() { return "<h2>"; }
    public String renderHeadline3OpenTag() { return "<h3>"; }
    public String renderHeadline4OpenTag() { return "<h4>"; }
    public String renderOrderedListOpenTag() { return "<ol>\n"; }
    public String renderOrderedListItemOpenTag() { return "<li>"; }
    public String renderUnorderedListOpenTag() { return "<ul>\n"; }
    public String renderUnorderedListItemOpenTag() { return "<li>"; }
}
