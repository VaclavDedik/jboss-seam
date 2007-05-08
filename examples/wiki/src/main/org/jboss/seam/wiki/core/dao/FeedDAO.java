package org.jboss.seam.wiki.core.dao;

import org.jboss.seam.annotations.*;
import org.jboss.seam.annotations.Observer;
import org.jboss.seam.wiki.core.model.*;
import org.jboss.seam.wiki.core.engine.WikiTextParser;
import org.jboss.seam.wiki.core.engine.WikiTextRenderer;
import org.jboss.seam.wiki.core.engine.WikiLink;
import org.jboss.seam.wiki.core.action.prefs.WikiPreferences;
import org.jboss.seam.Component;
import org.jboss.seam.log.Log;

import javax.persistence.EntityManager;
import javax.persistence.EntityNotFoundException;
import javax.persistence.NoResultException;
import java.util.*;

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
@Transactional
public class FeedDAO {

    @Logger static Log log;

    @In protected EntityManager restrictedEntityManager;

    public Feed findFeed(Long feedId) {
        restrictedEntityManager.joinTransaction();
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

    public void createFeedEntry(boolean pushOnSiteFeed, Document document) {
        restrictedEntityManager.joinTransaction();

        Set<Feed> feeds = getAvailableFeeds(pushOnSiteFeed, document);

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

    public void updateFeedEntry(boolean pushOnSiteFeed, Document document) {
        restrictedEntityManager.joinTransaction();

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
            Set<Feed> feeds = getAvailableFeeds(pushOnSiteFeed, document);
            for (Feed feed : feeds) {
                log.debug("linking feed entry with feed: " + feed.getId());
                feed.getFeedEntries().add(fe);
            }
        } catch (NoResultException ex) {
            // Couldn't find feed entry for this document, create a new one
            log.debug("no feed entry for updating found");
            createFeedEntry(pushOnSiteFeed, document);
        }
    }

    // TODO: Maybe the wiki needs a real maintenance thread at some point... @Observer("Feeds.purgeFeedEntries")
    public void purgeOldFeedEntries() {
        restrictedEntityManager.joinTransaction();

        // Clean up _all_ feed entries that are older than N days
        WikiPreferences wikiPrefs = (WikiPreferences) Component.getInstance("wikiPreferences");
        Calendar oldestDate = GregorianCalendar.getInstance();
        oldestDate.roll(Calendar.DAY_OF_YEAR, -wikiPrefs.getPurgeFeedEntriesAfterDays().intValue());
        int result = restrictedEntityManager.createQuery("delete from FeedEntry fe where fe.updatedDate < :oldestDate")
                .setParameter("oldestDate", oldestDate.getTime()).executeUpdate();
        log.debug("cleaned up " + result + " outdated feed entries");
    }

    private Set<Feed> getAvailableFeeds(boolean includeSiteFeed, Document document) {
        // Walk up the directory tree and extract all the feeds from directories
        Set<Feed> feeds = new HashSet<Feed>();
        Directory temp = document.getParent();
        while (temp.getParent() != null) {
            if (temp.getFeed() != null) feeds.add(temp.getFeed());
            temp = temp.getParent();
        }

        // If the user wants it on the site feed, that's the wiki root feed which is the top of the dir tree
        if (includeSiteFeed) feeds.add(temp.getFeed());

        return feeds;
    }

    private String renderFeedURL(Node node) {
        WikiPreferences wikiPrefs = (WikiPreferences) Component.getInstance("wikiPreferences");
        return wikiPrefs.getBaseUrl() + "/" + node.getId() + wikiPrefs.getPermlinkSuffix();
    }

    private String renderWikiText(String wikiText) {
        // Use the WikiTextParser to resolve macros
        WikiTextParser parser = new WikiTextParser(wikiText, true, true);

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
        parser.parse(true);
        return parser.toString();
    }

}
