package org.jboss.seam.wiki.core.action;

import org.jboss.seam.annotations.*;
import org.jboss.seam.wiki.core.links.WikiLinkResolver;
import org.jboss.seam.wiki.core.model.*;
import org.jboss.seam.core.Events;
import org.jboss.seam.Component;
import org.jboss.seam.ScopeType;
import org.jboss.seam.security.AuthorizationException;
import org.jboss.seam.contexts.Contexts;

@Name("documentHome")
@Scope(ScopeType.CONVERSATION)
public class DocumentHome extends NodeHome<Document> {

    private String formContent;
    private boolean enabledPreview = false;

    @In(required = false)
    Node selectedHistoricalNode;
    private Document historicalCopy;
    private boolean minorRevision = true;

    @Override
    public void create() {
        super.create();

        // Settings
        GlobalPreferences globalPrefs = (GlobalPreferences) Component.getInstance("globalPrefs");
        minorRevision = !globalPrefs.isDefaultNewRevisionForEditedDocument();

        // Rollback to historical revision?
        if (selectedHistoricalNode != null) getInstance().rollback(selectedHistoricalNode);

        // Make a copy
        historicalCopy = new Document(getInstance());
    }

    @Override
    public String persist() {
        checkNodeAccessLevelChangePermission();

        syncFormText();
        return super.persist();
    }

    @Override
    @Transactional
    public String update() {
        checkNodeAccessLevelChangePermission();

        syncFormText();
        
        Events.instance().raiseEvent("Nodes.menuStructureModified");

        // Write history log and prepare a new copy for further modification
        if (!isMinorRevision()) {
            getNodeDAO().persistHistoricalNode(historicalCopy);
            getInstance().incrementRevision();
            // New historical copy in conversation
            historicalCopy = new Document(getInstance());
        }

        return super.update();
    }

    @Override
    @Transactional
    public String remove() {

        // Delete all history nodes
        getNodeDAO().removeHistoricalNodes(getInstance());

        // Unset the default document id
        if (getParentDirectory().getDefaultDocument().equals(getInstance()))
            getParentDirectory().setDefaultDocument(null);

        return super.remove();
    }

    public String getFormContent() {
        // Load the document content and resolve links
        if (formContent == null) {

            // Outject instances required for WikiLinkResolver
            Contexts.getEventContext().set("currentDocument", getInstance());
            Contexts.getEventContext().set("currentDirectory", getParentDirectory());

            WikiLinkResolver wikiLinkResolver = (WikiLinkResolver)Component.getInstance("wikiLinkResolver");
            formContent = wikiLinkResolver.convertFromWikiLinks(getParentDirectory(), getInstance().getContent());
        }
        return formContent;
    }

    public void setFormContent(String formContent) {
        this.formContent = formContent;
    }

    public boolean isEnabledPreview() {
        return enabledPreview;
    }

    public void setEnabledPreview(boolean enabledPreview) {
        this.enabledPreview = enabledPreview;
        syncFormText();
        Events.instance().raiseEvent("Nodes.menuStructureModified");
    }

    public boolean isMinorRevision() {
        return minorRevision;
    }

    public void setMinorRevision(boolean minorRevision) {
        this.minorRevision = minorRevision;
    }

    private void syncFormText() {

        // Outject instances required for WikiLinkResolver
        Contexts.getEventContext().set("currentDocument", getInstance());
        Contexts.getEventContext().set("currentDirectory", getParentDirectory());

        // Convert and set form content onto entity instance
        WikiLinkResolver wikiLinkResolver = (WikiLinkResolver)Component.getInstance("wikiLinkResolver");
        getInstance().setContent(
            wikiLinkResolver.convertToWikiLinks(getParentDirectory(), getFormContent())
        );
    }

}
