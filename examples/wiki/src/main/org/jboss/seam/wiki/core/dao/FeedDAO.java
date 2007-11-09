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
import org.jboss.seam.ui.validator.FormattedTextValidator;
import org.jboss.seam.wiki.util.WikiUtil;
import org.jboss.seam.Component;
import org.jboss.seam.international.Messages;
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
 * <p>
 * TODO: Needs to use Hibernate security filter! Anybody can access a feed if they know the id!
 * 
 * @author Christian Bauer
 *
 */
@Name("feedDAO")
@AutoCreate
public class FeedDAO {

    @Logger static Log log;

    @In protected EntityManager restrictedEntityManager;

    /* ############################# FINDERS ################################ */

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

    public List<Feed> findFeeds(Document document) {
        return restrictedEntityManager
                .createQuery("select distinct f from Feed f join f.feedEntries fe where fe.document = :document")
                .setParameter("document", document)
                .getResultList();
    }

    public List<FeedEntry> findFeedEntries(Document document) {
        return restrictedEntityManager.createQuery("select fe from FeedEntry fe where fe.document = :doc")
            .setParameter("doc", document)
            .getResultList();
    }

    public FeedEntry findFeedEntry(Document document) {
        try {
            return (FeedEntry)restrictedEntityManager
                .createQuery("select fe from FeedEntry fe where fe.document = :doc and fe.commentIdentifier is null")
                .setParameter("doc", document)
                .getSingleResult();
        } catch (EntityNotFoundException ex) {
        } catch (NoResultException ex) {}
        return null;
    }

    public FeedEntry findFeedEntry(Document document, Comment comment) {
        try {
            return (FeedEntry)restrictedEntityManager
                .createQuery("select fe from FeedEntry fe where fe.document = :doc and fe.commentIdentifier = :cid")
                .setParameter("doc", document)
                .setParameter("cid", comment.getId())
                .getSingleResult();
        } catch (EntityNotFoundException ex) {
        } catch (NoResultException ex) {}
        return null;
    }

    public List<FeedEntry> findLastFeedEntries(Long feedId, int maxResults) {
        return (List<FeedEntry>) restrictedEntityManager
                .createQuery("select fe from Feed f join f.feedEntries fe where f.id = :feedId order by fe.publishedDate desc")
                .setParameter("feedId", feedId)
                .setHint("org.hibernate.cacheable", true)
                .setMaxResults(maxResults)
                .getResultList();
    }

    public boolean isOnSiteFeed(Document document) {
        Long count = (Long)restrictedEntityManager
                .createQuery("select count(fe) from Feed f join f.feedEntries fe where f = :feed and fe.document = :document")
                .setParameter("feed", ((Directory)Component.getInstance("wikiRoot")).getFeed() )
                .setParameter("document", document)
                .setHint("org.hibernate.cacheable", true)
                .getSingleResult();
        return count != 0;
    }

    /* ############################# FEED CUD ################################ */

    public void createFeed(Directory dir) {
        Feed feed = new Feed();
        feed.setDirectory(dir);
        feed.setAuthor(dir.getCreatedBy().getFullname());
        feed.setTitle(dir.getName());
        feed.setDescription(dir.getDescription());
        dir.setFeed(feed);
    }

    public void updateFeed(Directory dir) {
        dir.getFeed().setTitle(dir.getName());
        dir.getFeed().setAuthor(dir.getCreatedBy().getFullname());
        dir.getFeed().setDescription(dir.getDescription());
    }

    public void removeFeed(Directory dir) {
        restrictedEntityManager.remove(dir.getFeed());
        dir.setFeed(null);
    }

    /* ############################# FEEDENTRY CUD ################################ */


    public void createFeedEntry(Document document, boolean pushOnSiteFeed) {
        createFeedEntry(document, null, pushOnSiteFeed, null);
    }

    public void createFeedEntry(Document document, Comment comment, boolean pushOnSiteFeed) {
        createFeedEntry(document, comment, pushOnSiteFeed, null);
    }

    public void createFeedEntry(Document document, boolean pushOnSiteFeed, String overrideTitle) {
        createFeedEntry(document, null, pushOnSiteFeed, overrideTitle);
    }

    public void createFeedEntry(Document document, Comment comment, boolean pushOnSiteFeed, String overrideTitle) {
        Set<Feed> feeds = getAvailableFeeds(document, pushOnSiteFeed, true);

        // Now create a feedentry and link it to all the feeds
        if (feeds.size() >0) {
            log.debug("creating new feed entry for document: " + document.getId());

            FeedEntry fe = new FeedEntry();
            fe.setLink(renderFeedURL(document, comment));
            fe.setTitle(renderTitle(document, comment, overrideTitle));
            fe.setAuthor(renderAuthor(document, comment));
            fe.setUpdatedDate(fe.getPublishedDate());
            // Do NOT use text/html, the fabulous Sun "Rome" software will
            // render type="HTML" (uppercase!) which kills the Firefox feed renderer!
            fe.setDescriptionType("html");
            fe.setDescriptionValue(renderDescription(document, comment));
            fe.setDocument(document);

            if (comment != null) fe.setCommentIdentifier(comment.getId());

            restrictedEntityManager.persist(fe);
            for (Feed feed : feeds) {
                log.debug("linking new feed entry with feed: " + feed.getId());
                feed.getFeedEntries().add(fe);
            }
        }
    }

    public void updateFeedEntry(Document document, boolean pushOnSiteFeed) {
        updateFeedEntry(document, null, pushOnSiteFeed, null);
    }

    public void updateFeedEntry(Document document, Comment comment, boolean pushOnSiteFeed) {
        updateFeedEntry(document, comment, pushOnSiteFeed, null);
    }

    public void updateFeedEntry(Document document, boolean pushOnSiteFeed, String overrideTitle) {
        updateFeedEntry(document, null, pushOnSiteFeed, overrideTitle);
    }

    // This absolutely needs to be called in beforeUpdate(), so that the lastModifiedOn() timestamp of
    // the document is still the old one, not the new Date()!
    public void updateFeedEntry(Document document, Comment comment, boolean pushOnSiteFeed, String overrideTitle) {
        FeedEntry feedEntry;
        if (comment == null) {
            feedEntry = findFeedEntry(document);
        } else {
            feedEntry = findFeedEntry(document, comment);
        }

        if (feedEntry == null) {
            log.debug("no feed entry for updating found");
            createFeedEntry(document, comment, pushOnSiteFeed, overrideTitle);
            return;
        }

        log.debug("updating feed entry: " + feedEntry.getId());

        feedEntry.setLink(renderFeedURL(document, comment));
        feedEntry.setUpdatedDate(comment == null ? document.getLastModifiedOn() : comment.getCreatedOn());
        feedEntry.setTitle(renderTitle(document, comment, overrideTitle));
        feedEntry.setAuthor(renderAuthor(document, comment));
        feedEntry.setDescriptionValue(renderDescription(document, comment));

        // Link feed entry with all feeds (there might be new feeds since this feed entry was created)
        Set<Feed> feeds = getAvailableFeeds(document, pushOnSiteFeed, true);
        for (Feed feed : feeds) {
            log.debug("linking feed entry with feed: " + feed.getId());
            feed.getFeedEntries().add(feedEntry);
        }
    }

    public void removeFeedEntry(Document document, Comment comment) {
        removeFeedEntry(document, findFeedEntry(document, comment) );
    }

    public void removeFeedEntries(Document document) {
        List<FeedEntry> entries = findFeedEntries(document);
        if (entries.size() != 0) for (FeedEntry fe : entries) removeFeedEntry(document, fe);
    }

    private void removeFeedEntry(Document document, FeedEntry feedEntry) {
        if (feedEntry != null) {
            // Unlink feed entry from all feeds
            List<Feed> feeds = findFeeds(document);
            for (Feed feed : feeds) {
                log.debug("remove feed entry from feed: " + feed.getId());
                feed.getFeedEntries().remove(feedEntry);
            }
            log.debug("deleting feed entry");
            restrictedEntityManager.remove(feedEntry);
        }
    }

    public void purgeOldFeedEntries(Date olderThan) {
        // Clean up _all_ feed entries that are older than N days
        int result = restrictedEntityManager.createQuery("delete from FeedEntry fe where fe.updatedDate < :oldestDate")
                .setParameter("oldestDate", olderThan).executeUpdate();
        log.debug("cleaned up " + result + " outdated feed entries");
    }

    /* ############################# INTERNAL ################################ */

    private Set<Feed> getAvailableFeeds(Document document, boolean includeSiteFeed, boolean restrictAccess) {
        // Walk up the directory tree and extract all the feeds from directories
        Set<Feed> feeds = new HashSet<Feed>();
        Node dir = document.getParent();
        while (dir.getParent() != null) {
            // Only include feeds if the directory (owner of feed) has lower or equal read access level as the doc
            if (dir instanceof Directory && ((Directory)dir).getFeed() != null &&
                (!restrictAccess || dir.getReadAccessLevel() <= document.getReadAccessLevel()) ) {
                feeds.add( ((Directory)dir).getFeed());
            }
            dir = dir.getParent();
        }

        // If the user wants it on the site feed, that's the wiki root feed which is the top of the dir tree
        if (includeSiteFeed && (!restrictAccess || dir.getReadAccessLevel() <= document.getReadAccessLevel()) )
            feeds.add( ((Directory)dir).getFeed());

        return feeds;
    }

    private String renderTitle(Document document, Comment comment, String overrideTitle) {
        if (overrideTitle != null) {
            return overrideTitle;
        } else if (comment != null) {
            return comment.getSubject();
        } else {
            return document.getName();
        }
    }

    private String renderAuthor(Document document, Comment comment) {
        if (comment != null && comment.getFromUser() != null) {
            return comment.getFromUser().getFullname();
        } else if (comment != null) {
            return comment.getFromUserName();
        } else {
            return document.getCreatedBy().getFullname();
        }
    }

    private String renderDescription(Document document, Comment comment) {
        if (comment != null) {
            StringBuilder desc = new StringBuilder();
            desc.append(Messages.instance().get("lacewiki.msg.comment.FeedIntro"));
            desc.append("&#160;");
            desc.append("<a href=\"").append(WikiUtil.renderPermLink(document)).append("\">");
            desc.append("'").append(document.getName()).append("'");
            desc.append("</a>.");
            desc.append("<hr/>");
            desc.append(renderWikiText(comment.getText()));
            return desc.toString();
        }
        return renderWikiText(document.getContent());
    }

    private String renderFeedURL(Node node, Comment comment) {
        return WikiUtil.renderURL(node, comment);
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
                            + renderFeedURL(inlineLink.getNode(), null)
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
                    return "";
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
