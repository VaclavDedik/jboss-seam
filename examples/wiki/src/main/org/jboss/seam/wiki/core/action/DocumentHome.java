/*
 * JBoss, Home of Professional Open Source
 *
 * Distributable under LGPL license.
 * See terms of license at gnu.org.
 */
package org.jboss.seam.wiki.core.action;

import static javax.faces.application.FacesMessage.SEVERITY_INFO;

import org.jboss.seam.annotations.*;
import org.jboss.seam.wiki.core.model.*;
import org.jboss.seam.wiki.core.engine.*;
import org.jboss.seam.wiki.core.dao.FeedDAO;
import org.jboss.seam.wiki.core.dao.UserRoleAccessFactory;
import org.jboss.seam.wiki.core.dao.TagDAO;
import org.jboss.seam.wiki.core.action.prefs.DocumentEditorPreferences;
import org.jboss.seam.wiki.core.action.prefs.CommentsPreferences;
import org.jboss.seam.wiki.core.action.prefs.WikiPreferences;
import org.jboss.seam.wiki.util.WikiUtil;
import org.jboss.seam.Component;
import org.jboss.seam.ScopeType;
import org.jboss.seam.log.Log;
import org.jboss.seam.contexts.Contexts;

import java.util.List;
import java.util.Date;
import java.util.Calendar;
import java.util.GregorianCalendar;

@Name("documentHome")
@Scope(ScopeType.CONVERSATION)
public class DocumentHome extends NodeHome<Document> {

    @Logger
    static Log log;

    /* -------------------------- Context Wiring ------------------------------ */

    @In
    private Directory wikiRoot;
    @In(required = false)
    private Node selectedHistoricalNode;
    @In
    private FeedDAO feedDAO;
    @In
    private TagDAO tagDAO;

    /* -------------------------- Request Wiring ------------------------------ */

    @Observer("DocumentHome.init")
    public String init() {
        String result = super.init();
        if (result != null) return result;

        // Rollback to historical revision?
        if (selectedHistoricalNode != null) {
            getLog().debug("rolling back to revision: " + selectedHistoricalNode.getRevision());
            getInstance().rollback(selectedHistoricalNode);
        }

        // Make a copy
        if (historicalCopy == null) {
            historicalCopy = new Document(getInstance(), true);
        }

        // Wiki text parser and plugins need this
        log.debug("setting current document: " + getInstance());
        Contexts.getPageContext().set("currentDocument", getInstance());
        log.debug("setting current directory: " + getParentDirectory());
        Contexts.getPageContext().set("currentDirectory", getParentDirectory());

        return null;
    }

    /* -------------------------- Internal State ------------------------------ */

    private Document historicalCopy;
    private Boolean minorRevision;
    private String formContent;
    private boolean enabledPreview = false;
    private boolean pushOnFeeds = false;
    private boolean pushOnSiteFeed = false;
    private List<Node> historicalNodes;
    private Long numOfHistoricalNodes;

    /* -------------------------- Basic Overrides ------------------------------ */


    /* -------------------------- Custom CUD ------------------------------ */

    protected Document createInstance() {
        Document newDoc = super.createInstance();
        newDoc.setEnableComments( ((CommentsPreferences)Component.getInstance("commentsPreferences")).getEnableByDefault() );
        return newDoc;
    }

    protected boolean beforePersist() {
        // Sync document content
        syncFormToInstance(getParentDirectory());

        // Set createdOn date _now_
        getInstance().setCreatedOn(new Date());

        // Make a copy
        historicalCopy = new Document(getInstance(), true);

        return true;
    }

    public String persist() {
        String outcome = super.persist();

        // Create feed entries (needs identifiers assigned, so we run after persist())
        if (outcome != null && isPushOnFeeds()) {
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
        WikiPreferences wikiPrefs = (WikiPreferences) Component.getInstance("wikiPreferences");
        Calendar oldestDate = GregorianCalendar.getInstance();
        oldestDate.roll(Calendar.DAY_OF_YEAR, -wikiPrefs.getPurgeFeedEntriesAfterDays().intValue());
        feedDAO.purgeOldFeedEntries(oldestDate.getTime());

        // Write history log and prepare a new copy for further modification
        if (!isMinorRevision()) {

            historicalCopy.setId(getInstance().getId());
            getNodeDAO().persistHistoricalNode(historicalCopy);
            getInstance().incrementRevision();
            // New historical copy in conversation
            historicalCopy = new Document(getInstance(), true);

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
        feedDAO.removeFeedEntries(getInstance());

        return super.prepareRemove();
    }

    protected void afterNodeMoved(Directory oldParent, Directory newParent) {
        // Update view
        syncFormToInstance(oldParent); // Resolve existing links in old directory
        syncInstanceToForm(newParent); // Now update the form, effectively re-rendering the links
        Contexts.getConversationContext().set("currentDirectory", newParent);
    }

    /* -------------------------- Messages ------------------------------ */

    protected void createdMessage() {
        getFacesMessages().addFromResourceBundleOrDefault(
                SEVERITY_INFO,
                "lacewiki.msg.Document.Persist",
                "Document '{0}' has been saved.",
                getInstance().getName()
        );
    }

    protected void updatedMessage() {
        getFacesMessages().addFromResourceBundleOrDefault(
                SEVERITY_INFO,
                "lacewiki.msg.Document.Update",
                "Document '{0}' has been updated.",
                getInstance().getName()
        );
    }

    protected void deletedMessage() {
        getFacesMessages().addFromResourceBundleOrDefault(
                SEVERITY_INFO,
                "lacewiki.msg.Document.Delete",
                "Document '{0}' has been deleted.",
                getInstance().getName()
        );
    }

    /* -------------------------- Internal Methods ------------------------------ */


    private void syncFormToInstance(Directory dir) {
        WikiLinkResolver wikiLinkResolver = (WikiLinkResolver)Component.getInstance("wikiLinkResolver");
        getInstance().setContent(
            wikiLinkResolver.convertToWikiProtocol(dir.getAreaNumber(), formContent)
        );
        getInstance().setMacros( WikiUtil.findMacros(getInstance(), getParentDirectory(), formContent) );
    }

    private void syncInstanceToForm(Directory dir) {
        WikiLinkResolver wikiLinkResolver = (WikiLinkResolver)Component.getInstance("wikiLinkResolver");
        formContent = wikiLinkResolver.convertFromWikiProtocol(dir.getAreaNumber(), getInstance().getContent());
    }

    /* -------------------------- Public Features ------------------------------ */

    public String getFormContent() {
        // Load the document content and resolve links
        if (formContent == null) syncInstanceToForm(getParentDirectory());
        return formContent;
    }

    public void setFormContent(String formContent) {
        this.formContent = formContent;
        if (formContent != null) syncFormToInstance(getParentDirectory());
    }

    public boolean isMinorRevision() {
        // Lazily initalize preferences
        if (minorRevision == null)
            minorRevision = (Boolean)((DocumentEditorPreferences)Component
                    .getInstance("docEditorPreferences")).getProperties().get("minorRevisionEnabled");
        return minorRevision;
    }
    public void setMinorRevision(boolean minorRevision) { this.minorRevision = minorRevision; }

    public boolean isEnabledPreview() {
        return enabledPreview;
    }

    public void setEnabledPreview(boolean enabledPreview) {
        this.enabledPreview = enabledPreview;
        syncFormToInstance(getParentDirectory());
    }

    public boolean isSiteFeedEntryPresent() {
        return feedDAO.isOnSiteFeed(getInstance());
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

    // TODO: Move this into WikiTextEditor.java
    public boolean isShowPluginPrefs() {
        Boolean showPluginPrefs = (Boolean)Contexts.getPageContext().get("showPluginPreferences");
        return showPluginPrefs != null ? showPluginPrefs : false;
    }

    public boolean isHistoricalNodesPresent() {
        if (numOfHistoricalNodes == null) {
            getLog().debug("Finding number of historical nodes for: " + getInstance());
            numOfHistoricalNodes = getNodeDAO().findNumberOfHistoricalNodes(getInstance());
        }
        return numOfHistoricalNodes != null && numOfHistoricalNodes > 0;
    }

    public List<Node> getHistoricalNodes() {
        if (historicalNodes == null)
            historicalNodes = getNodeDAO().findHistoricalNodes(getInstance());
        return historicalNodes;
    }

    public List<TagDAO.TagCount> getPopularTags() {
        List list = tagDAO.findTagsAggregatedSorted(wikiRoot, null, 0);
        return list;
    }

}
