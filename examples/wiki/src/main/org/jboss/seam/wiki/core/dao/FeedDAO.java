package org.jboss.seam.wiki.core.dao;

import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.AutoCreate;
import org.jboss.seam.annotations.Transactional;
import org.jboss.seam.annotations.In;
import org.jboss.seam.wiki.core.model.*;
import org.jboss.seam.wiki.core.engine.WikiTextParser;
import org.jboss.seam.wiki.core.engine.WikiTextRenderer;
import org.jboss.seam.wiki.core.engine.WikiLink;
import org.jboss.seam.wiki.core.action.prefs.WikiPreferences;
import org.jboss.seam.Component;

import javax.persistence.EntityManager;
import javax.persistence.EntityNotFoundException;
import javax.persistence.NoResultException;
import java.util.List;
import java.util.Set;
import java.util.HashSet;

/**
 * DAO for feeds.
 *
 * @author Christian Bauer
 *
 */
@Name("feedDAO")
@AutoCreate
@Transactional
public class FeedDAO {

    @In protected EntityManager restrictedEntityManager;

    public Feed findFeed(Long feedId) {
        restrictedEntityManager.joinTransaction();
        try {
            return (Feed) restrictedEntityManager
                .createQuery("select f from Feed f where f.id = :id")
                .setParameter("id", feedId)
                .getSingleResult();
        } catch (EntityNotFoundException ex) {
        } catch (NoResultException ex) {}
        return null;
    }

    public List<FeedEntry> findFeedEntries(Feed feed, Integer limit) {
        restrictedEntityManager.joinTransaction();
        //noinspection unchecked
        return (List<FeedEntry>)restrictedEntityManager
                .createQuery("select fe from FeedEntry fe join fe.feeds f where f = :feed order by fe.updatedDate desc")
                .setParameter("feed", feed)
                .getResultList();
    }

    public void createFeedEntries(boolean pushOnSiteFeed, Document document) {
        restrictedEntityManager.joinTransaction();

        Set<Feed> feeds = new HashSet<Feed>();
        Directory temp = document.getParent();
        while (temp.getParent() != null) {
            if (temp.getFeed() != null) feeds.add(temp.getFeed());
            temp = temp.getParent();
        }

        if (pushOnSiteFeed)
            feeds.add(temp.getFeed()); // Reached wiki root, feed for whole site

        if (feeds.size() >0) {
            FeedEntry feedEntry = new FeedEntry();
            feedEntry.setLink(renderFeedURL(document));
            feedEntry.setTitle(document.getName());
            feedEntry.setAuthor(document.getCreatedBy().getFullname());
            feedEntry.setUpdatedDate(feedEntry.getPublishedDate());
            feedEntry.setDescriptionType("text/html");
            feedEntry.setDescriptionValue(renderWikiText(document.getContent()));
            feedEntry.setDocument(document);
            feedEntry.getFeeds().addAll(feeds);

            restrictedEntityManager.persist(feedEntry);
        }
    }

    public void updateFeedEntries(boolean pushOnSiteFeed, Document document) {
        restrictedEntityManager.joinTransaction();

        int updatedEntries = restrictedEntityManager
            .createQuery("update FeedEntry fe set" +
                         " fe.updatedDate = :date," +
                         " fe.title = :title," +
                         " fe.link = :link," +
                         " fe.author = :author, " +
                         " fe.descriptionValue = :description" +
                         " where fe.document = :document")
            .setParameter("date", document.getLastModifiedOn())
            .setParameter("title", document.getName())
            .setParameter("link", renderFeedURL(document))
            .setParameter("author", document.getLastModifiedBy().getFullname())
            .setParameter("description", renderWikiText(document.getContent()))
            .setParameter("document", document)
            .executeUpdate();

        if (updatedEntries == 0) createFeedEntries(pushOnSiteFeed, document);
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
