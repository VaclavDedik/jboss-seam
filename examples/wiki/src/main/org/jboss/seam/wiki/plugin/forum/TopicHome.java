package org.jboss.seam.wiki.plugin.forum;

import org.jboss.seam.Component;
import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.*;
import org.jboss.seam.wiki.core.action.NodeHome;
import org.jboss.seam.wiki.core.engine.WikiLinkResolver;
import org.jboss.seam.wiki.core.model.Directory;
import org.jboss.seam.wiki.core.dao.FeedDAO;

import static javax.faces.application.FacesMessage.SEVERITY_INFO;
import java.util.Date;

@Name("topicHome")
@Scope(ScopeType.CONVERSATION)
public class TopicHome extends NodeHome<ForumTopic> {

    @In
    Directory currentDirectory;

    @In
    private FeedDAO feedDAO;

    private boolean showForm = false;
    private String formContent;

    /* -------------------------- Basic Overrides ------------------------------ */

    public void create() {
        super.create();
        super.setParentDirectory(currentDirectory);
    }

    protected boolean beforePersist() {
        // Sync topic content
        syncFormToInstance(getParentDirectory());

        // Macros
        getInstance().setDefaultMacros();

        // Set createdOn date _now_
        getInstance().setCreatedOn(new Date());

        return true;
    }

    /* -------------------------- Internal Methods ------------------------------ */


    private void syncFormToInstance(Directory dir) {
        WikiLinkResolver wikiLinkResolver = (WikiLinkResolver) Component.getInstance("wikiLinkResolver");
        getInstance().setContentWithoutMacros(
            wikiLinkResolver.convertToWikiProtocol(dir.getAreaNumber(), formContent)
        );
    }

    private void syncInstanceToForm(Directory dir) {
        WikiLinkResolver wikiLinkResolver = (WikiLinkResolver)Component.getInstance("wikiLinkResolver");
        formContent = wikiLinkResolver.convertFromWikiProtocol(dir.getAreaNumber(), getInstance().getContentWithoutMacros());
    }

    /* -------------------------- Messages ------------------------------ */

    protected void createdMessage() {
        getFacesMessages().addFromResourceBundleOrDefault(
                SEVERITY_INFO,
                "lacewiki.msg.Topic.Persist",
                "Topic '{0}' has been saved.",
                getInstance().getName()
        );
    }

    protected void updatedMessage() {
        getFacesMessages().addFromResourceBundleOrDefault(
                SEVERITY_INFO,
                "lacewiki.msg.Topic.Update",
                "Topic '{0}' has been updated.",
                getInstance().getName()
        );
    }

    protected void deletedMessage() {
        getFacesMessages().addFromResourceBundleOrDefault(
                SEVERITY_INFO,
                "lacewiki.msg.Topic.Delete",
                "Topic '{0}' has been deleted.",
                getInstance().getName()
        );
    }

    /* -------------------------- Public Features ------------------------------ */

    @Begin(flushMode = FlushModeType.MANUAL)
    public void newTopic() {

        showForm = true;

        // Start with a fresh instance
        setInstance(createInstance());

        // Get a fresh parent directory instance into the current persistence context
        setParentDirectory(loadParentDirectory(getParentDirectory().getId()));
    }

    @End
    public void cancel() {
        showForm = false;
    }

    @End
    @RaiseEvent("Forum.topicPersisted")
    public String persist() {
        String outcome = super.persist();
        showForm = outcome == null; // Keep showing the form if there was a validation error

        // Create feed entries (needs identifiers assigned, so we run after persist())
        if (outcome != null) {
            String feedEntryTitle = "[" + getParentDirectory().getName() + "] " + getInstance().getName();
            feedDAO.createFeedEntry(getInstance(), false, feedEntryTitle);
            getEntityManager().flush();
        }

        return outcome;
    }

    public boolean isShowForm() {
        return showForm;
    }

    public void setShowForm(boolean showForm) {
        this.showForm = showForm;
    }

    public String getFormContent() {
        // Load the topic content and resolve links
        if (formContent == null) syncInstanceToForm(getParentDirectory());
        return formContent;
    }

    public void setFormContent(String formContent) {
        this.formContent = formContent;
        if (formContent != null) syncFormToInstance(getParentDirectory());
    }

}
