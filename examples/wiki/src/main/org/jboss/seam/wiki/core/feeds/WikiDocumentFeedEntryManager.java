/*
 * JBoss, Home of Professional Open Source
 *
 * Distributable under LGPL license.
 * See terms of license at gnu.org.
 */
package org.jboss.seam.wiki.core.feeds;

import org.jboss.seam.annotations.Name;
import org.jboss.seam.wiki.core.model.WikiDocument;
import org.jboss.seam.wiki.core.model.WikiDocumentFeedEntry;

@Name("wikiDocumentFeedEntryManager")
public class WikiDocumentFeedEntryManager extends FeedEntryManager<WikiDocument, WikiDocumentFeedEntry> {

    public WikiDocumentFeedEntry createFeedEntry(WikiDocument document) {

        WikiDocumentFeedEntry fe = new WikiDocumentFeedEntry();

        fe.setLink(wikiURLRenderer.renderURL(document));
        fe.setTitle(getFeedEntryTitle(document));
        fe.setAuthor(document.getCreatedBy().getFullname());
        fe.setUpdatedDate(fe.getPublishedDate());

        // Do NOT use text/html, the fabulous Sun "Rome" software will
        // render type="HTML" (uppercase!) which kills the Firefox feed renderer!
        fe.setDescriptionType("html");
        fe.setDescriptionValue(renderWikiText(document.getAreaNumber(), document.getFeedDescription()));

        fe.setDocument(document);
        return fe;
    }

    public void updateFeedEntry(WikiDocumentFeedEntry fe, WikiDocument document) {

        fe.setLink(wikiURLRenderer.renderURL(document));
        fe.setTitle(getFeedEntryTitle(document));
        fe.setAuthor(document.getCreatedBy().getFullname());
        fe.setUpdatedDate(document.getLastModifiedOn());

        fe.setDescriptionValue(renderWikiText(document.getAreaNumber(), document.getFeedDescription()));
    }

    public String getFeedEntryTitle(WikiDocument document) {
        return document.getName();
    }
}
