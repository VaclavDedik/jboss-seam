package org.jboss.seam.wiki.core.links;

import org.jboss.seam.text.SeamTextParser;
import org.jboss.seam.Component;
import org.jboss.seam.wiki.core.node.File;
import antlr.TokenStream;

import java.util.Map;
import java.util.HashMap;
import java.util.List;
import java.util.ArrayList;

public class WikiTextParser extends SeamTextParser {

    private String linkClass;
    private String brokenLinkClass;
    private String attachmentLinkClass;
    private String inlineLinkClass;

    private WikiLinkResolver resolver;

    private List<WikiLinkResolver.WikiLink> attachments = new ArrayList<WikiLinkResolver.WikiLink>();
    private Map<String, WikiLinkResolver.WikiLink> links = new HashMap<String, WikiLinkResolver.WikiLink>();

    public WikiTextParser(TokenStream tokenStream,
                          String linkClass, String brokenLinkClass, String attachmentLinkClass, String inlineLinkClass) {
        super(tokenStream);
        this.linkClass = linkClass;
        this.brokenLinkClass = brokenLinkClass;
        this.attachmentLinkClass = attachmentLinkClass;
        this.inlineLinkClass = inlineLinkClass;
        resolver = (WikiLinkResolver)Component.getInstance(WikiLinkResolver.class);
    }

    protected String linkTag(String descriptionText, String linkText) {

        // Resolve the link
        resolver.resolveWikiLink(links, linkText.trim());
        WikiLinkResolver.WikiLink link = links.get((linkText));

        String finalDescriptionText =
                (descriptionText!=null && descriptionText.length() > 0 ? descriptionText : link.description);

        // Link to file (inline or attached)
        if (WikiLinkResolver.isFile(link.node)) {
            File file = (File)link.node;

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
                String thumbnailUrl = link.url + "&width=" + thumbnailWidth;

                return "<a href=\""
                        + link.url
                        + "\" class=\""
                        + inlineLinkClass
                        + "\"><img src=\""
                        + thumbnailUrl
                        + "\"/></a>";
            }
        }

        // Regular link
        return "<a href=\""
                + link.url
                + "\" class=\""
                + (link.broken ? brokenLinkClass : linkClass)
                + "\">"
                + finalDescriptionText
                + "</a>";
    }


    public List<WikiLinkResolver.WikiLink> getAttachments() {
        return attachments;
    }

}
