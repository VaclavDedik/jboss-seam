/*
 * JBoss, Home of Professional Open Source
 *
 * Distributable under LGPL license.
 * See terms of license at gnu.org.
 */
package org.jboss.seam.wiki.core.dao;

import org.jboss.seam.annotations.*;
import org.jboss.seam.wiki.core.model.*;
import org.jboss.seam.wiki.core.engine.WikiTextParser;
import org.jboss.seam.wiki.core.engine.WikiTextRenderer;
import org.jboss.seam.wiki.core.engine.WikiLink;
import org.jboss.seam.wiki.core.engine.WikiLinkResolver;
import org.jboss.seam.wiki.core.action.prefs.WikiPreferences;
import org.jboss.seam.ui.validator.FormattedTextValidator;
import org.jboss.seam.wiki.util.WikiUtil;
import org.jboss.seam.Component;
import org.jboss.seam.log.Log;

import javax.persistence.EntityManager;
import javax.persistence.EntityNotFoundException;
import javax.persistence.NoResultException;
import java.util.*;

import antlr.RecognitionException;
import antlr.ANTLRException;

/**
 * DAO for feeds.
 * <p>
 * Uses the <tt>restrictedEntityManager</tt> because it is used in the context of directory editing (same PC).
 * </p>
 * @author Christian Bauer
 *
 */
@Name("feedDAO")
@AutoCreate
public class FeedDAO {

    @Logger static Log log;

    @In protected EntityManager restrictedEntityManager;

    public List<FeedEntry> findLastFeedEntries(Long feedId, int maxResults) {
        return (List<FeedEntry>) restrictedEntityManager
                .createQuery("select fe from Feed f join f.feedEntries fe where f.id = :feedId order by fe.publishedDate desc")
                .setParameter("feedId", feedId)
                .setHint("org.hibernate.cacheable", true)
                .setMaxResults(maxResults)
                .getResultList();
    }

    public void removeFeedEntry(Document document) {
        try {
            FeedEntry fe = (FeedEntry)
                    restrictedEntityManager.createQuery("select fe from FeedEntry fe where not fe.document is null and fe.document = :doc")
                .setParameter("doc", document)
                .getSingleResult();
            if (fe != null) {
                // Unlink feed entry from all feeds
                Set<Feed> feeds = getAvailableFeeds(document, true);
                for (Feed feed : feeds) {
                    log.debug("remove feed entry from feed: " + feed.getId());
                    feed.getFeedEntries().remove(fe);
                }
                log.debug("deleting feed entry");
                restrictedEntityManager.remove(fe);
            }
        } catch (EntityNotFoundException ex) {
        } catch (NoResultException ex) {}
    }

    public Feed findFeed(Long feedId) {
        try {
            return (Feed) restrictedEntityManager
                .createQuery("select f from Feed f where f.id = :id")
                .setParameter("id", feedId)
                .setHint("org.hibernate.cacheable", true)
                .getSingleResult();
        } catch (EntityNotFoundException ex) {
        } catch (NoResultException ex) {}
        return null;
    }

    public FeedEntry findSiteFeedEntry(Document document) {
        try {
            return (FeedEntry) restrictedEntityManager
                    .createQuery("select fe from Feed f join f.feedEntries fe where f = :feed and fe.document = :document")
                    .setParameter("feed", ((Directory)Component.getInstance("wikiRoot")).getFeed() )
                    .setParameter("document", document)
                    .setHint("org.hibernate.cacheable", true)
                    .getSingleResult();
        } catch (EntityNotFoundException ex) {
        } catch (NoResultException ex) {}
        return null;
    }

    public void createFeedEntry(Document document, boolean pushOnSiteFeed) {
        Set<Feed> feeds = getAvailableFeeds(document, pushOnSiteFeed);

        // Now create a feedentry and link it to all the feeds
        if (feeds.size() >0) {
            log.debug("creating new feed entry for document: " + document.getId());
            FeedEntry fe = new FeedEntry();
            fe.setLink(renderFeedURL(document));
            fe.setTitle(document.getName());
            fe.setAuthor(document.getCreatedBy().getFullname());
            fe.setUpdatedDate(fe.getPublishedDate());
            // Do NOT use text/html, the fabulous Sun "Rome" software will
            // render type="HTML" (uppercase!) which kills the Firefox feed renderer!
            fe.setDescriptionType("html");
            fe.setDescriptionValue(renderWikiText(document.getContent()));
            fe.setDocument(document);
            restrictedEntityManager.persist(fe);
            for (Feed feed : feeds) {
                log.debug("linking new feed entry with feed: " + feed.getId());
                feed.getFeedEntries().add(fe);
            }
        }
    }

    public void updateFeedEntry(Document document, boolean pushOnSiteFeed) {
        try {
            FeedEntry fe = (FeedEntry)restrictedEntityManager.createQuery("select fe from FeedEntry fe where fe.document = :doc")
                    .setParameter("doc", document).getSingleResult();

            log.debug("updating feed entry: " + fe.getId());

            // Update the feed entry for this document
            fe.setLink(renderFeedURL(document));
            fe.setUpdatedDate(document.getLastModifiedOn());
            fe.setTitle(document.getName());
            fe.setAuthor(document.getCreatedBy().getFullname());
            fe.setDescriptionValue(renderWikiText(document.getContent()));

            // Link feed entry with all feeds (there might be new feeds since this feed entry was created)
            Set<Feed> feeds = getAvailableFeeds(document, pushOnSiteFeed);
            for (Feed feed : feeds) {
                log.debug("linking feed entry with feed: " + feed.getId());
                feed.getFeedEntries().add(fe);
            }
        } catch (NoResultException ex) {
            // Couldn't find feed entry for this document, create a new one
            log.debug("no feed entry for updating found");
            createFeedEntry(document, pushOnSiteFeed);
        }
    }

    // TODO: Maybe the wiki needs a real maintenance thread at some point... @Observer("Feeds.purgeFeedEntries")
    public void purgeOldFeedEntries() {
        // Clean up _all_ feed entries that are older than N days
        WikiPreferences wikiPrefs = (WikiPreferences) Component.getInstance("wikiPreferences");
        Calendar oldestDate = GregorianCalendar.getInstance();
        oldestDate.roll(Calendar.DAY_OF_YEAR, -wikiPrefs.getPurgeFeedEntriesAfterDays().intValue());
        int result = restrictedEntityManager.createQuery("delete from FeedEntry fe where fe.updatedDate < :oldestDate")
                .setParameter("oldestDate", oldestDate.getTime()).executeUpdate();
        log.debug("cleaned up " + result + " outdated feed entries");
    }

    private Set<Feed> getAvailableFeeds(Document document, boolean includeSiteFeed) {
        // Walk up the directory tree and extract all the feeds from directories
        Set<Feed> feeds = new HashSet<Feed>();
        Node temp = document.getParent();
        while (temp.getParent() != null) {
            if (temp instanceof Directory && ((Directory)temp).getFeed() != null)
                feeds.add( ((Directory)temp).getFeed());
            temp = temp.getParent();
        }

        // If the user wants it on the site feed, that's the wiki root feed which is the top of the dir tree
        if (includeSiteFeed) feeds.add( ((Directory)temp).getFeed());

        return feeds;
    }

    private String renderFeedURL(Node node) {
        /*
        WikiPreferences wikiPrefs = (WikiPreferences) Component.getInstance("wikiPreferences");
        return wikiPrefs.getBaseUrl() + "/" + node.getId() + wikiPrefs.getPermlinkSuffix();
        */
        return WikiUtil.renderURL(node);
    }

    private String renderWikiText(String wikiText) {
        WikiTextParser parser = new WikiTextParser(wikiText, true, true);

        parser.setCurrentDocument((Document)Component.getInstance("currentDocument"));
        parser.setCurrentDirectory((Directory)Component.getInstance("currentDirectory"));
        parser.setResolver((WikiLinkResolver)Component.getInstance("wikiLinkResolver"));

        // Set a customized renderer for parser macro callbacks
        parser.setRenderer(
            new WikiTextRenderer() {

                public String renderInlineLink(WikiLink inlineLink) {
                    return !inlineLink.isBroken() ?
                            "<a href=\""
                            + renderFeedURL(inlineLink.getNode())
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
                    return "[Embedded Plugin]";
                }

                public void setAttachmentLinks(List<WikiLink> attachmentLinks) {}
                public void setExternalLinks(List<WikiLink> externalLinks) {}
            }
        );

        // Run the parser
        try {
            parser.parse(true);

        } catch (RecognitionException rex) {
            // Swallow and log and low debug level
            log.debug( "Ignored parse error generating feed entry text: " + FormattedTextValidator.getErrorMessage(wikiText, rex) );
        } catch (ANTLRException ex) {
            // All other errors are fatal;
            throw new RuntimeException(ex);
        }
        return parser.toString();
    }

}
