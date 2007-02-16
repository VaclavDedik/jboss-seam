package org.jboss.seam.wiki.core.links;

import org.jboss.seam.text.SeamTextParser;
import org.jboss.seam.Component;
import antlr.TokenStream;

import java.util.Map;
import java.util.HashMap;

public class WikiTextParser extends SeamTextParser {

    private String linkClass;
    private String brokenLinkClass;

    private Map<String, WikiLinkResolver.WikiLink> links = new HashMap<String, WikiLinkResolver.WikiLink>();
    private WikiLinkResolver resolver;

    public WikiTextParser(TokenStream tokenStream, String linkClass, String brokenLinkClass) {
        super(tokenStream);
        this.linkClass = linkClass;
        this.brokenLinkClass = brokenLinkClass;
        resolver = (WikiLinkResolver)Component.getInstance(WikiLinkResolver.class);
    }

    // TODO: Not a pretty dependency... this needs to be called first
    protected String linkUrl(String linkText) {
        resolver.resolveWikiLink(links, linkText.trim());
        return links.get(linkText).url;
    }

    // then this needs to be called by the parser
    protected String linkDescription(String descriptionText, String linkText) {
        if (descriptionText != null && descriptionText.length() >0) return descriptionText;
        return links.get(linkText).description;
    }

    protected String linkClass(String linkText) {
        return links.get(linkText).broken ? brokenLinkClass : linkClass;
    }
}
