package org.jboss.seam.wiki.core.ui;

import org.jboss.seam.text.SeamTextParser;
import org.jboss.seam.core.Expressions;
import org.jboss.seam.contexts.Contexts;
import org.jboss.seam.wiki.core.model.File;
import antlr.TokenStream;

import java.util.Map;
import java.util.ArrayList;
import java.util.List;
import java.util.HashMap;

public class WikiTextParser extends SeamTextParser {

    private String linkClass;
    private String brokenLinkClass;
    private String attachmentLinkClass;
    private String inlineLinkClass;

    private UIWikiFormattedText textComponent;

    private List<WikiLink> attachments = new ArrayList<WikiLink>();
    private List<WikiLink> externalLinks = new ArrayList<WikiLink>();
    private Map<String, WikiLink> links = new HashMap<String, WikiLink>();

    // TODO: Refactor to avoid callback to the UI component
    public WikiTextParser(TokenStream tokenStream,
                          String linkClass, String brokenLinkClass, String attachmentLinkClass, String inlineLinkClass,
                          UIWikiFormattedText textComponent) {
        super(tokenStream);
        this.linkClass = linkClass;
        this.brokenLinkClass = brokenLinkClass;
        this.attachmentLinkClass = attachmentLinkClass;
        this.inlineLinkClass = inlineLinkClass;
        this.textComponent = textComponent;
    }

    protected String linkTag(String descriptionText, String linkText) {

        // Resolve the link with loosely coupled calls (resolver is in different classloader during hot deploy)
        Contexts.getEventContext().set("linkMap", links);
        Contexts.getEventContext().set("linkText", linkText.trim());
        Expressions.MethodBinding method = Expressions.instance()
                .createMethodBinding("#{wikiLinkResolver.resolveWikiLink(linkMap,linkText)}");
        method.invoke();

        WikiLink link = links.get((linkText));
        if (link == null) return "";

        String finalDescriptionText =
                (descriptionText!=null && descriptionText.length() > 0 ? descriptionText : link.getDescription());

        // Link to file (inline or attached)
        if (WikiUtil.isFile(link.getNode())) {
            File file = (File)link.getNode();

            if (file.getImageMetaInfo() == null || 'A' == file.getImageMetaInfo().getThumbnail()) {
                // It's an attachment
                if (!attachments.contains(link)) attachments.add(link);
                return "<a href=\"#attachment"
                        + (attachments.indexOf(link)+1)
                        + "\" class=\""
                        + attachmentLinkClass
                        + "\">"
                        + finalDescriptionText
                        + "[" + (attachments.indexOf(link)+1) + "]"
                        + "</a>";
            } else {
                // It's an image and we need to show it inline
                int thumbnailWidth;
                switch(file.getImageMetaInfo().getThumbnail()) {
                    case 'S': thumbnailWidth = 80; break;
                    case 'M': thumbnailWidth = 160; break;
                    case 'L': thumbnailWidth = 320; break;
                    default: thumbnailWidth = file.getImageMetaInfo().getSizeX();
                }
                String thumbnailUrl = link.getUrl() + "&width=" + thumbnailWidth;

                return "<a href=\""
                        + link.getUrl()
                        + "\" class=\""
                        + inlineLinkClass
                        + "\"><img src=\""
                        + thumbnailUrl
                        + "\"/></a>";
            }
        }

        // External link
        if (link.isExternal() && !externalLinks.contains(link)) externalLinks.add(link);

        // Regular link
        return "<a href=\""
                + link.getUrl()
                + "\" class=\""
                + (link.isBroken() ? brokenLinkClass : linkClass)
                + "\">"
                + finalDescriptionText
                + "</a>";
    }

    protected String macroInclude(String macroName) {
        // Filter out any dangerous characters
        return textComponent.renderMacro(macroName.replaceAll("[^\\p{Alnum}]+", ""));
    }

    public List<WikiLink> getAttachments() {
        return attachments;
    }

    public List<WikiLink> getExternalLinks() {
        return externalLinks;
    }


}
