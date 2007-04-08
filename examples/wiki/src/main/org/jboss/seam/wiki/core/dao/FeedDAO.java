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
import org.jboss.seam.wiki.util.WikiUtil;
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

    public List<FeedEntry> findFeedEntries(Feed feed) {
        restrictedEntityManager.joinTransaction();
        //noinspection unchecked
        return (List<FeedEntry>)restrictedEntityManager
                .createQuery("select fe from FeedEntry fe join fe.feeds f where f = :feed")
                .setParameter("feed", feed)
                .getResultList();
    }

    public void createFeedEntries(Document document) {
        restrictedEntityManager.joinTransaction();

        Set<Feed> feeds = new HashSet<Feed>();
        Directory temp = document.getParent();
        while (temp.getParent() != null) {
            if (temp.getFeed() != null) feeds.add(temp.getFeed());
            temp = temp.getParent();
        }
        feeds.add(temp.getFeed()); // Reached wiki root, feed for whole site

        FeedEntry feedEntry = new FeedEntry();
        feedEntry.setDocument(document);
        feedEntry.setLink(renderFeedURL(document));
        feedEntry.setTitle(document.getName());
        feedEntry.setAuthor(document.getCreatedBy().getFullname());
        feedEntry.setDescriptionType("text/html");
        feedEntry.setDescriptionValue(renderWikiText(document.getContent()));
        feedEntry.getFeeds().addAll(feeds);

        restrictedEntityManager.persist(feedEntry);
    }

    public void updateFeedEntries(Document document) {
        restrictedEntityManager.joinTransaction();

        int updatedEntries = restrictedEntityManager
            .createQuery("update FeedEntry fe set" +
                         " fe.updatedDate = :date," +
                         " fe.title = :title," +
                         " fe.author = :author, " +
                         " fe.descriptionValue = :description" +
                         " where fe.document = :document")
            .setParameter("date", document.getLastModifiedOn())
            .setParameter("title", document.getName())
            .setParameter("author", document.getLastModifiedBy().getFullname())
            .setParameter("description", renderWikiText(document.getContent()))
            .setParameter("document", document)
            .executeUpdate();

        if (updatedEntries == 0) createFeedEntries(document);
    }

    public void removeFeedEntries(Document document) {
        restrictedEntityManager.joinTransaction();

        try {
            FeedEntry entry = (FeedEntry) restrictedEntityManager
                .createQuery("select fe from FeedEntry fe where fe.document = :document")
                .setParameter("document", document)
                .getSingleResult();

            restrictedEntityManager.remove(entry);
        } catch (EntityNotFoundException ex) {
        } catch (NoResultException ex) {}
    }

    private String renderFeedURL(Node node) {
        WikiPreferences wikiPrefs = (WikiPreferences) Component.getInstance("wikiPreferences");
        return wikiPrefs.getBaseUrl() + WikiUtil.renderURL(node);
    }

    private String renderWikiText(String wikiText) {
        // Use the WikiTextParser to resolve macros
        WikiTextParser parser = new WikiTextParser(wikiText, false);

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
