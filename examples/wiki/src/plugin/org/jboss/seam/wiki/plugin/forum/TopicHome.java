package org.jboss.seam.wiki.plugin.forum;

import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.*;
import org.jboss.seam.annotations.web.RequestParameter;
import org.jboss.seam.core.Conversation;
import org.jboss.seam.core.Events;
import org.jboss.seam.faces.Renderer;
import org.jboss.seam.ui.validator.FormattedTextValidator;
import org.jboss.seam.wiki.core.action.DocumentHome;
import org.jboss.seam.wiki.core.action.prefs.WikiPreferences;
import org.jboss.seam.wiki.core.model.WikiDirectory;
import org.jboss.seam.wiki.core.model.WikiDocument;
import org.jboss.seam.wiki.core.model.WikiTextMacro;
import org.jboss.seam.wiki.core.ui.WikiRedirect;
import org.jboss.seam.wiki.preferences.Preferences;

import javax.faces.application.FacesMessage;
import static javax.faces.application.FacesMessage.SEVERITY_INFO;
import javax.faces.validator.ValidatorException;

@Name("topicHome")
@Scope(ScopeType.CONVERSATION)
public class TopicHome extends DocumentHome {

    public static final String TOPIC_NOTIFY_ME_MACRO = "forumNotifyReplies";

    @In
    WikiDirectory currentDirectory;

    @In
    WikiDocument currentDocument;

    @In(create = true)
    private Renderer renderer;

    private boolean showForm = false;
    private boolean sticky = false;
    private boolean notifyReplies = false;

    /* -------------------------- Basic Overrides ------------------------------ */

    @Override
    protected boolean isPageRootController() {
        return false;
    }

    @Override
    public Class<WikiDocument> getEntityClass() {
        return WikiDocument.class;
    }

    @Override
    public void create() {
        super.create();
        setParentNodeId(currentDirectory.getId());

        Boolean preferencesNotifyReplies = Preferences.instance().get(ForumPreferences.class).getNotifyMeOfReplies();
        notifyReplies = preferencesNotifyReplies != null && preferencesNotifyReplies;
    }

    @Override
    public WikiDocument afterNodeCreated(WikiDocument doc) {
        WikiDocument newTopic = super.afterNodeCreated(doc);
        newTopic.setDefaults(new TopicDefaults());
        setPushOnFeeds(true);
        return newTopic;
    }

    @Override
    protected boolean preparePersist() {
        FormattedTextValidator validator = new FormattedTextValidator();
        try {
            validator.validate(null, null, getInstance().getContent());
        } catch (ValidatorException e) {
            // TODO: Needs to use resource bundle, how?
            getFacesMessages().addToControl(
                "topicTextArea",
                FacesMessage.SEVERITY_WARN,
                e.getFacesMessage().getSummary()
            );
            return false;
        }
        return super.preparePersist();
    }

    @Override
    protected boolean beforePersist() {
        // TODO: Use macro parameters for "sticky" and "notify" options instead of additional macros
        if (isSticky()) {
            getInstance().removeHeaderMacros("forumPosting");
            getInstance().addHeaderMacro(new WikiTextMacro("forumStickyPosting"));
        }
        if (isNotifyReplies()) {
            getInstance().addHeaderMacro(new WikiTextMacro(TOPIC_NOTIFY_ME_MACRO));
        }
        return super.beforePersist();
    }

    @Override
    public String persist() {
        // Only owners or admins can edit topics
        getInstance().setWriteAccessLevel(org.jboss.seam.wiki.core.model.Role.ADMINROLE_ACCESSLEVEL);

        String outcome = super.persist();
        if (outcome != null) {

            // Notify forum mailing list
            String notificationMailingList =
                    Preferences.instance().get(ForumPreferences.class).getNotificationMailingList();
            if (notificationMailingList != null) {
                getLog().debug("sending topic notification e-mail to forum list");
                renderer.render("/themes/"
                        + Preferences.instance().get(WikiPreferences.class).getThemeName()
                        + "/mailtemplates/forumNotifyTopicToList.xhtml");
            }

            endConversation();

            // TODO: We should redirect here to the posted topic with WikiRedirect, see cancel()
        }
        return null; // Prevent navigation
    }

    @Override
    public String update() {
        String outcome = super.update();
        if (outcome != null) endConversation();
        return null; // Prevent navigation
    }

    @Override
    public String remove() {
        String outcome = super.remove();
        if (outcome != null) endConversation();
        return null; // Prevent navigation
    }

    /* -------------------------- Messages ------------------------------ */

    protected void createdMessage() {
        getFacesMessages().addFromResourceBundleOrDefault(
                SEVERITY_INFO,
                "forum.msg.Topic.Persist",
                "Topic '{0}' has been saved.",
                getInstance().getName()
        );
    }

    protected void updatedMessage() {
        getFacesMessages().addFromResourceBundleOrDefault(
                SEVERITY_INFO,
                "forum.msg.Topic.Update",
                "Topic '{0}' has been updated.",
                getInstance().getName()
        );
    }

    protected void deletedMessage() {
        getFacesMessages().addFromResourceBundleOrDefault(
                SEVERITY_INFO,
                "forum.msg.Topic.Delete",
                "Topic '{0}' has been deleted.",
                getInstance().getName()
        );
    }

    protected String getEditorWorkspaceDescription(boolean create) {
        return null;
    }

    /* -------------------------- Internal Methods ------------------------------ */

    private void endConversation() {
        getLog().debug("ending conversation and hiding topic form");
        showForm = false;
        Conversation.instance().end();
        getEntityManager().clear(); // Need to force re-read in the topic list refresh
        Events.instance().raiseEvent("Forum.topicListRefresh");
    }

    protected String getFeedEntryManagerName() {
        return "forumTopicFeedEntryManager";
    }

    /* -------------------------- Public Features ------------------------------ */

    public boolean isShowForm() {
        return showForm;
    }

    public void setShowForm(boolean showForm) {
        this.showForm = showForm;
    }

    public boolean isSticky() {
        return sticky;
    }

    public void setSticky(boolean sticky) {
        this.sticky = sticky;
    }

    public boolean isNotifyReplies() {
        return notifyReplies;
    }

    public void setNotifyReplies(boolean notifyReplies) {
        this.notifyReplies = notifyReplies;
    }

    @Begin(flushMode = FlushModeType.MANUAL, join = true)
    public void newTopic() {
        initEditor(false);
        showForm = true;
    }

    public void cancel() {
        endConversation();

        // Redirect to topic list
        WikiRedirect.instance().setWikiDocument(currentDocument).execute();
    }

    @RequestParameter("showTopicForm")
    public void showTopicForm(Boolean requestParam) {
        if (requestParam != null && requestParam && !showForm) {
            getLog().debug("request parameter sets topic form visible, starts conversation");
            Conversation.instance().begin(true, false);
            Conversation.instance().changeFlushMode(FlushModeType.MANUAL);
            newTopic();
        }
    }

}
