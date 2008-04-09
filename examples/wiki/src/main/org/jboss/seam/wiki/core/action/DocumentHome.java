/*
 * JBoss, Home of Professional Open Source
 *
 * Distributable under LGPL license.
 * See terms of license at gnu.org.
 */
package org.jboss.seam.wiki.core.action;

import org.jboss.seam.Component;
import org.jboss.seam.ScopeType;
import org.jboss.seam.international.Messages;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.contexts.Contexts;
import org.jboss.seam.wiki.core.action.prefs.CommentsPreferences;
import org.jboss.seam.wiki.core.action.prefs.DocumentEditorPreferences;
import org.jboss.seam.wiki.core.action.prefs.WikiPreferences;
import org.jboss.seam.wiki.core.feeds.FeedDAO;
import org.jboss.seam.wiki.core.feeds.FeedEntryManager;
import org.jboss.seam.wiki.core.engine.WikiLinkResolver;
import org.jboss.seam.wiki.core.renderer.MacroWikiTextRenderer;
import org.jboss.seam.wiki.core.model.*;
import org.jboss.seam.wiki.core.exception.InvalidWikiRequestException;
import org.jboss.seam.wiki.core.template.TemplateRegistry;
import org.jboss.seam.wiki.core.template.WikiDocumentTemplate;
import org.jboss.seam.wiki.core.template.WikiDocumentEditorTemplate;
import org.jboss.seam.wiki.preferences.Preferences;
import org.hibernate.validator.Length;

import static javax.faces.application.FacesMessage.SEVERITY_INFO;
import java.util.*;

@Name("documentHome")
@Scope(ScopeType.CONVERSATION)
public class DocumentHome extends NodeHome<WikiDocument, WikiDirectory> {

    /* -------------------------- Context Wiring ------------------------------ */

    @In(required = false)
    private DocumentHistory documentHistory;
    @In
    private FeedDAO feedDAO;
    @In
    private TagEditor tagEditor;

    /* -------------------------- Internal State ------------------------------ */

    private WikiDocument historicalCopy;
    private Boolean minorRevision;
    private String formContent;
    private Set<WikiFile> linkTargets;
    private boolean enabledPreview = false;
    private boolean pushOnFeeds = false;
    private boolean pushOnSiteFeed = false;
    private boolean isOnSiteFeed = false;
    private List<WikiFile> historicalFiles;
    private Long numOfHistoricalFiles = 0l;
    private String templateType;

    /* -------------------------- Basic Overrides ------------------------------ */

    @Override
    public Class<WikiDocument> getEntityClass() {
        return WikiDocument.class;
    }

    @Override
    public WikiDocument findInstance() {
        return getWikiNodeDAO().findWikiDocument((Long)getId());
    }

    @Override
    protected WikiDirectory findParentNode(Long parentNodeId) {
        return getEntityManager().find(WikiDirectory.class, parentNodeId);
    }

    @Override
    public WikiDocument afterNodeCreated(WikiDocument doc) {
        doc = super.afterNodeCreated(doc);

        tagEditor.setTags(doc.getTags());

        outjectDocumentAndDirectory(doc, getParentNode());

        if (templateType != null && !templateType.equals(WikiDocumentDefaults.class.getName())) {
            getLog().debug("using custom template class for WikiDocument defaults: " + templateType);
            WikiDocumentDefaults defaults;
            try {
                Class<?> tplClass = Class.forName(templateType);

                if (!TemplateRegistry.instance().getTemplateTypes().contains(tplClass)) {
                    throw new InvalidWikiRequestException("Invalid templateType: " + templateType);
                }

                if (tplClass.getAnnotation(WikiDocumentTemplate.class).requiresTemplateInstance()) {
                    getLog().debug("instantiating template " + tplClass.getName() + " with current document instance");
                    defaults = (WikiDocumentDefaults)tplClass.getConstructor(WikiDocument.class).newInstance(doc);
                } else {
                    getLog().debug("instantiating template " + tplClass.getName() + " with no-arg constructor");
                    defaults = (WikiDocumentDefaults)tplClass.newInstance();
                }

                if (WikiDocumentEditorTemplate.class.isAssignableFrom(tplClass)) {
                    getLog().debug("letting template set editor defaults");
                    ((WikiDocumentEditorTemplate)defaults).setEditorDefaults(this);
                }

            } catch (Exception ex) {
                throw new InvalidWikiRequestException("Invalid templateType: " + templateType);
            }
            doc.setDefaults(defaults);
        }

        return doc;
    }

    @Override
    public WikiDocument beforeNodeEditNew(WikiDocument doc) {
        doc = super.beforeNodeEditNew(doc);

        tagEditor.setTags(doc.getTags());

        doc.setEnableComments( Preferences.getInstance(CommentsPreferences.class).getEnableByDefault() );

        return doc;
    }

    @Override
    public WikiDocument afterNodeFound(WikiDocument doc) {
        doc = super.afterNodeFound(doc);

        tagEditor.setTags(doc.getTags());

        findHistoricalFiles(doc);
        syncMacros(doc);
        outjectDocumentAndDirectory(doc, getParentNode());

        return doc;
    }

    @Override
    public WikiDocument beforeNodeEditFound(WikiDocument doc) {
        doc = super.beforeNodeEditFound(doc);

        tagEditor.setTags(doc.getTags());

        // Rollback to historical revision?
        if (documentHistory != null && documentHistory.getSelectedHistoricalFile() != null) {
            getLog().debug("rolling back to revision: " + documentHistory.getSelectedHistoricalFile().getRevision());
            // TODO: Avoid cast, make history polymorphic
            doc.rollback((WikiDocument)documentHistory.getSelectedHistoricalFile());
        }

        isOnSiteFeed = feedDAO.isOnSiteFeed(doc);

        return doc;
    }

    /* -------------------------- Custom CUD ------------------------------ */

    @Override
    protected boolean beforePersist() {
        // Sync document content
        syncFormContentToInstance(getParentNode());
        syncLinks();

        // Make a copy
        historicalCopy = new WikiDocument();
        historicalCopy.flatCopy(getInstance(), true);

        return true;
    }

    @Override
    public String persist() {
        String outcome = super.persist();

        // Create feed entries (needs identifiers assigned, so we run after persist())
        if (outcome != null && isPushOnFeeds()) {
            getLog().debug("creating feed entries on parent dirs - and on site feed: " + isPushOnSiteFeed());
            if (isPushOnSiteFeed()) isOnSiteFeed = true;

            FeedEntry feedEntry =
                    ((FeedEntryManager)Component.getInstance(getFeedEntryManagerName())).createFeedEntry(getInstance());
            feedDAO.createFeedEntry(getParentNode(), getInstance(), feedEntry, isPushOnSiteFeed());

            getEntityManager().flush();
            setPushOnFeeds(false);
            setPushOnSiteFeed(false);
        }

        return outcome;
    }

    @Override
    protected boolean beforeUpdate() {

        // Sync document content
        syncFormContentToInstance(getParentNode());
        syncLinks();

        // Update feed entries
        if (isPushOnFeeds()) {
            if (isPushOnSiteFeed()) isOnSiteFeed = true;

            FeedEntry feedEntry = feedDAO.findFeedEntry(getInstance());
            if (feedEntry == null) {
                getLog().debug("creating feed entries on parent dirs - and on site feed: " + isPushOnSiteFeed());
                feedEntry = ((FeedEntryManager)Component.getInstance(getFeedEntryManagerName())).createFeedEntry(getInstance());
                feedDAO.createFeedEntry(getParentNode(), getInstance(), feedEntry, isPushOnSiteFeed());
            } else {
                getLog().debug("updating feed entries on parent dirs - and on site feed: " + isPushOnSiteFeed());
                ((FeedEntryManager)Component.getInstance(getFeedEntryManagerName())).updateFeedEntry(feedEntry, getInstance());
                feedDAO.updateFeedEntry(getParentNode(), getInstance(), feedEntry, isPushOnSiteFeed());
            }

            setPushOnFeeds(false);
            setPushOnSiteFeed(false);
        }

        // Feeds should not be removed by a maintenance thread: If there
        // is no activity on the site, feeds shouldn't be empty but show the last updates.
        Calendar oldestDate = GregorianCalendar.getInstance();
        oldestDate.add(Calendar.DAY_OF_YEAR, -Preferences.getInstance(WikiPreferences.class).getPurgeFeedEntriesAfterDays().intValue());
        feedDAO.purgeOldFeedEntries(oldestDate.getTime());

        // Write history log and prepare a new copy for further modification
        if (!isMinorRevision()) {
            if (historicalCopy == null)
                throw new IllegalStateException("Call getFormContent() once to create a historical revision");
            getLog().debug("storing the historical copy as a new revision");
            historicalCopy.setId(getInstance().getId());
            historicalCopy.setLastModifiedBy(getCurrentUser());
            getWikiNodeDAO().persistHistoricalFile(historicalCopy);
            getInstance().incrementRevision();
            // New historical copy in conversation
            historicalCopy = new WikiDocument();
            historicalCopy.flatCopy(getInstance(), true);

            // Reset form
            setMinorRevision( Preferences.getInstance(DocumentEditorPreferences.class).getMinorRevisionEnabled() );
        }

        return true;
    }

    @Override
    public String remove() {
        return trash();
    }

    @Override
    protected NodeRemover getNodeRemover() {
        return (DocumentNodeRemover)Component.getInstance(DocumentNodeRemover.class);
    }

    /* -------------------------- Messages ------------------------------ */

    @Override
    protected void createdMessage() {
        getFacesMessages().addFromResourceBundleOrDefault(
                SEVERITY_INFO,
                "lacewiki.msg.Document.Persist",
                "Document '{0}' has been saved.",
                getInstance().getName()
        );
    }

    @Override
    protected void updatedMessage() {
        getFacesMessages().addFromResourceBundleOrDefault(
                SEVERITY_INFO,
                "lacewiki.msg.Document.Update",
                "Document '{0}' has been updated.",
                getInstance().getName()
        );
    }

    @Override
    protected void deletedMessage() {
        getFacesMessages().addFromResourceBundleOrDefault(
                SEVERITY_INFO,
                "lacewiki.msg.Document.Delete",
                "Document '{0}' has been deleted.",
                getInstance().getName()
        );
    }

    protected String getEditorWorkspaceDescription(boolean create) {
        if (create) {
            return Messages.instance().get("lacewiki.label.docEdit.CreateDocument");
        } else {
            return Messages.instance().get("lacewiki.label.docEdit.EditDocument") + ":" + getInstance().getName();
        }
    }

    /* -------------------------- Internal Methods ------------------------------ */

    protected void findHistoricalFiles(WikiDocument doc) {
        getLog().debug("Finding number of historical files for: " + doc);
        numOfHistoricalFiles= getWikiNodeDAO().findNumberOfHistoricalFiles(doc);
        if (isHistoricalFilesPresent()) {
            historicalFiles = getWikiNodeDAO().findHistoricalFiles(doc);
        }
    }

    // Wiki text parser and plugins need this
    protected void outjectDocumentAndDirectory(WikiDocument doc, WikiDirectory dir) {
        if (isPageRootController()) {
            if (doc != null) {
                getLog().debug("setting current document: " + doc);
                Contexts.getConversationContext().set("currentDocument", doc);
            }
            if (dir != null) {
                getLog().debug("setting current directory: " + dir);
                Contexts.getConversationContext().set("currentDirectory", dir);
            }
        }
    }

    private void syncLinks() {
        if (linkTargets != null) getInstance().setOutgoingLinks(linkTargets);
    }

    public void syncMacros(WikiDocument doc) {
        if (doc.getHeader() != null) {
            MacroWikiTextRenderer renderer = MacroWikiTextRenderer.renderMacros(doc.getHeader());
            doc.setHeaderMacros(renderer.getMacros());
        }
        if (doc.getContent() != null) {
            MacroWikiTextRenderer renderer = MacroWikiTextRenderer.renderMacros(doc.getContent());
            doc.setContentMacros(renderer.getMacros());
        }
        if (doc.getFooter() != null) {
            MacroWikiTextRenderer renderer = MacroWikiTextRenderer.renderMacros(doc.getFooter());
            doc.setFooterMacros(renderer.getMacros());
        }
    }

    private void syncFormContentToInstance(WikiDirectory dir) {
        if (formContent != null) {
            getLog().debug("sync form content to instance");
            WikiLinkResolver wikiLinkResolver = (WikiLinkResolver)Component.getInstance("wikiLinkResolver");
            linkTargets = new HashSet<WikiFile>();
            getInstance().setContent(
                wikiLinkResolver.convertToWikiProtocol(linkTargets, dir.getAreaNumber(), formContent)
            );
            syncMacros(getInstance());
        }
    }

    private void syncInstanceToFormContent(WikiDirectory dir) {
        getLog().debug("sync instance to form in area: " + dir.getAreaNumber());
        WikiLinkResolver wikiLinkResolver = (WikiLinkResolver)Component.getInstance("wikiLinkResolver");
        formContent = wikiLinkResolver.convertFromWikiProtocol(dir.getAreaNumber(), getInstance().getContent());
        if (historicalCopy == null) {
            getLog().debug("making a history copy of the document");
            historicalCopy = new WikiDocument();
            historicalCopy.flatCopy(getInstance(), true);
        }
    }

    protected String getFeedEntryManagerName() {
        return "wikiDocumentFeedEntryManager";
    }

    /* -------------------------- Public Features ------------------------------ */

    // TODO: We need to duplicate this here, otherwise it will only validated on persist(): http://jira.jboss.com/jira/browse/JBSEAM-2671
    @Length(min = 0, max = 32767)
    public String getFormContent() {
        // Load the document content and resolve links
        if (formContent == null) syncInstanceToFormContent(getParentNode());
        return formContent;
    }

    public void setFormContent(String formContent) {
        this.formContent = formContent;
        if (formContent != null) {
            syncFormContentToInstance(getParentNode());
        }
    }

    public boolean isMinorRevision() {
        // Lazily initalize preferences
        if (minorRevision == null)
            minorRevision = Preferences.getInstance(DocumentEditorPreferences.class).getMinorRevisionEnabled();
        return minorRevision;
    }
    public void setMinorRevision(boolean minorRevision) { this.minorRevision = minorRevision; }

    public boolean isEnabledPreview() {
        return enabledPreview;
    }

    public void setEnabledPreview(boolean enabledPreview) {
        this.enabledPreview = enabledPreview;
        syncFormContentToInstance(getParentNode());
    }

    public boolean isOnSiteFeed() {
        return isOnSiteFeed;
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
        return showPluginPrefs != null && showPluginPrefs;
    }

    public boolean isHistoricalFilesPresent() {
        return numOfHistoricalFiles != null && numOfHistoricalFiles> 0;
    }

    public List<WikiFile> getHistoricalFiles() {
        return historicalFiles;
    }

    public TagEditor getTagEditor() {
        return tagEditor;
    }

    public String getTemplateType() {
        return templateType;
    }

    public void setTemplateType(String templateType) {
        this.templateType = templateType;
    }
}
