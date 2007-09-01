/*
 * JBoss, Home of Professional Open Source
 *
 * Distributable under LGPL license.
 * See terms of license at gnu.org.
 */
package org.jboss.seam.wiki.core.action;

import org.jboss.seam.annotations.*;
import org.jboss.seam.wiki.core.model.*;
import org.jboss.seam.wiki.core.engine.WikiLinkResolver;
import org.jboss.seam.wiki.core.engine.WikiTextParser;
import org.jboss.seam.wiki.core.engine.WikiTextRenderer;
import org.jboss.seam.wiki.core.engine.WikiLink;
import org.jboss.seam.wiki.core.dao.FeedDAO;
import org.jboss.seam.wiki.core.dao.UserRoleAccessFactory;
import org.jboss.seam.wiki.core.action.prefs.DocumentEditorPreferences;
import org.jboss.seam.Component;
import org.jboss.seam.ScopeType;
import org.jboss.seam.log.Log;
import org.jboss.seam.contexts.Contexts;

import java.util.List;

@Name("documentHome")
@Scope(ScopeType.CONVERSATION)
public class DocumentHome extends NodeHome<Document> {

    @Logger
    static Log log;

    /* -------------------------- Context Wiring ------------------------------ */

    @In(required = false)
    private Node selectedHistoricalNode;
    @In
    private FeedDAO feedDAO;

    /* -------------------------- Request Wiring ------------------------------ */

    @Observer("DocumentHome.init")
    public String init() {
        String result = super.init();
        if (result != null) return result;

        // Preferences
        minorRevision = (Boolean)((DocumentEditorPreferences)Component
                .getInstance("docEditorPreferences")).getProperties().get("minorRevisionEnabled");

        // Rollback to historical revision?
        if (selectedHistoricalNode != null) {
            getLog().debug("rolling back to revision: " + selectedHistoricalNode.getRevision());
            getInstance().rollback(selectedHistoricalNode);
        }

        // Make a copy
        historicalCopy = new Document(getInstance());

        // Wiki text parser and plugins need this
        log.debug("setting current document: " + getInstance());
        Contexts.getPageContext().set("currentDocument", getInstance());
        log.debug("setting current directory: " + getParentDirectory());
        Contexts.getPageContext().set("currentDirectory", getParentDirectory());

        return null;
    }

    /* -------------------------- Internal State ------------------------------ */

    private Document historicalCopy;
    private boolean minorRevision;
    private String formContent;
    private boolean enabledPreview = false;
    private boolean pushOnFeeds = false;
    private boolean pushOnSiteFeed = false;

    /* -------------------------- Basic Overrides ------------------------------ */


    /* -------------------------- Custom CUD ------------------------------ */

    protected boolean beforePersist() {
        // Sync document content
        syncFormToInstance(getParentDirectory());

        // Make a copy
        historicalCopy = new Document(getInstance());

        return true;
    }

    public String persist() {
        String outcome = super.persist();

        // Create feed entries (needs identifiers assigned, so we run after persist())
        if (outcome != null && getInstance().getReadAccessLevel() == UserRoleAccessFactory.GUESTROLE_ACCESSLEVEL && isPushOnFeeds()) {
            feedDAO.createFeedEntry(getInstance(), isPushOnSiteFeed());
            getEntityManager().flush();
            pushOnFeeds = false;
            pushOnSiteFeed = false;
        }

        return outcome;
    }

    protected boolean beforeUpdate() {

        // Sync document content
        syncFormToInstance(getParentDirectory());

        // Update feed entries
        if (getInstance().getReadAccessLevel() == UserRoleAccessFactory.GUESTROLE_ACCESSLEVEL && isPushOnFeeds()) {
            feedDAO.updateFeedEntry(getInstance(), isPushOnSiteFeed());
            pushOnFeeds = false;
            pushOnSiteFeed = false;
        }
        // Feeds should not be removed by a maintenance thread: If there
        // is no activity on the site, feeds shouldn't be empty but show the last updates.
        feedDAO.purgeOldFeedEntries();

        // Write history log and prepare a new copy for further modification
        if (!isMinorRevision()) {

            historicalCopy.setId(getInstance().getId());
            getNodeDAO().persistHistoricalNode(historicalCopy);
            getInstance().incrementRevision();
            // New historical copy in conversation
            historicalCopy = new Document(getInstance());

            // Reset form
            setMinorRevision(
                (Boolean)((DocumentEditorPreferences)Component
                    .getInstance("docEditorPreferences")).getProperties().get("minorRevisionEnabled")
            );
        }

        return true;
    }

    protected boolean prepareRemove() {

        // Remove feed entry before removing document
        feedDAO.removeFeedEntry(getInstance());

        return super.prepareRemove();
    }

    protected void afterNodeMoved(Directory oldParent, Directory newParent) {
        // Update view
        syncFormToInstance(oldParent); // Resolve existing links in old directory
        syncInstanceToForm(newParent); // Now update the form, effectively re-rendering the links
        Contexts.getConversationContext().set("currentDirectory", newParent);
    }

    /* -------------------------- Internal Methods ------------------------------ */


    private void syncFormToInstance(Directory dir) {
        WikiLinkResolver wikiLinkResolver = (WikiLinkResolver)Component.getInstance("wikiLinkResolver");
        getInstance().setContent(
            wikiLinkResolver.convertToWikiProtocol(dir.getAreaNumber(), formContent)
        );
        getInstance().setPluginsUsed( findPluginsUsed() );
    }

    private void syncInstanceToForm(Directory dir) {
        WikiLinkResolver wikiLinkResolver = (WikiLinkResolver)Component.getInstance("wikiLinkResolver");
        formContent = wikiLinkResolver.convertFromWikiProtocol(dir.getAreaNumber(), getInstance().getContent());
    }

    private String findPluginsUsed() {
        final StringBuilder usedPlugins = new StringBuilder();
        WikiTextParser parser = new WikiTextParser(formContent, false, false);
        parser.setCurrentDocument(getInstance());
        parser.setCurrentDirectory(getParentDirectory());
        parser.setResolver((WikiLinkResolver)Component.getInstance("wikiLinkResolver"));
        parser.setRenderer(
            new WikiTextRenderer() {
                public String renderInlineLink(WikiLink inlineLink) { return null; }
                public String renderExternalLink(WikiLink externalLink) { return null; }
                public String renderFileAttachmentLink(int attachmentNumber, WikiLink attachmentLink) { return null; }
                public String renderThumbnailImageInlineLink(WikiLink inlineLink) { return null; }
                public void setAttachmentLinks(List<WikiLink> attachmentLinks) {}
                public void setExternalLinks(List<WikiLink> externalLinks) {}
                public String renderMacro(String macroName) {
                    usedPlugins.append(macroName).append(" ");
                    return null;
                }
            }
        ).parse(false);
        return usedPlugins.toString();
    }

    /* -------------------------- Public Features ------------------------------ */

    public String getFormContent() {
        // Load the document content and resolve links
        if (formContent == null) syncInstanceToForm(getParentDirectory());
        return formContent;
    }

    public void setFormContent(String formContent) {
        this.formContent = formContent;
    }

    public boolean isMinorRevision() { return minorRevision; }
    public void setMinorRevision(boolean minorRevision) { this.minorRevision = minorRevision; }

    public boolean isEnabledPreview() {
        return enabledPreview;
    }

    public void setEnabledPreview(boolean enabledPreview) {
        this.enabledPreview = enabledPreview;
        syncFormToInstance(getParentDirectory());
    }

    public boolean isSiteFeedEntryPresent() {
        return feedDAO.findSiteFeedEntry(getInstance()) != null;
    }

    public boolean isPushOnFeeds() {
        return pushOnFeeds;
    }

    public void setPushOnFeeds(boolean pushOnFeeds) {
        this.pushOnFeeds = pushOnFeeds;
    }

    public boolean isPushOnSiteFeed() {
        return pushOnSiteFeed;
    }

    public void setPushOnSiteFeed(boolean pushOnSiteFeed) {
        this.pushOnSiteFeed = pushOnSiteFeed;
    }

    public void setShowPluginPrefs(boolean showPluginPrefs) {
        Contexts.getPageContext().set("showPluginPreferences", showPluginPrefs);
    }

    public boolean isShowPluginPrefs() {
        Boolean showPluginPrefs = (Boolean)Contexts.getPageContext().get("showPluginPreferences");
        return showPluginPrefs != null ? showPluginPrefs : false;
    }

    public boolean isHistoricalNodesPresent() {
        Long numOfNodes = getNodeDAO().findNumberOfHistoricalNodes(getInstance());
        return numOfNodes != null && numOfNodes > 0;
    }

}
