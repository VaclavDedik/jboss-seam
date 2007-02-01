package org.jboss.seam.wiki.core.links;

import org.jboss.seam.text.SeamTextParser;
import org.jboss.seam.Component;
import antlr.TokenStream;

import java.util.Map;
import java.util.HashMap;

public class WikiTextParser extends SeamTextParser {

    private Map<String, WikiLinkResolver.WikiLink> links = new HashMap<String, WikiLinkResolver.WikiLink>();

    private WikiLinkResolver resolver;

    public WikiTextParser(TokenStream tokenStream, String linkClass, String brokenLinkClass) {
        super(tokenStream);
        resolver = (WikiLinkResolver)Component.getInstance(WikiLinkResolver.class);
    }

    protected String linkUrl(String linkText) {
        resolver.resolveWikiLink(links, linkText.trim());
        return links.get(linkText).url;
    }

    protected String linkDescription(String descriptionText, String linkText) {
        return links.get(linkText).description;
    }

    protected String linkClass(String linkText) {
        return links.get(linkText).broken ? "foo" : "bar";
    }
}
