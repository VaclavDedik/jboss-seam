package org.jboss.seam.wiki.plugin.forum;

import org.jboss.seam.Component;
import org.jboss.seam.ScopeType;
import org.jboss.seam.faces.Renderer;
import org.jboss.seam.security.Identity;
import org.jboss.seam.annotations.*;
import org.jboss.seam.wiki.core.action.CommentHome;
import org.jboss.seam.wiki.core.action.prefs.WikiPreferences;
import org.jboss.seam.wiki.core.model.WikiComment;
import org.jboss.seam.wiki.core.model.WikiNode;
import org.jboss.seam.wiki.preferences.Preferences;

import static javax.faces.application.FacesMessage.SEVERITY_INFO;

@Name("replyHome")
@Scope(ScopeType.CONVERSATION)
public class ReplyHome extends CommentHome {

    @Override
    public void create() {
        super.create();
        markTopicRead();
    }

    @In(create = true)
    private Renderer renderer;

    @Observer(value = "Comment.persisted", create = false)
    public void sendNotificationMails() {
        // Triggered by superclass after reply was persisted

        // Notify forum mailing list
        String notificationMailingList =
                Preferences.getInstance(ForumPreferences.class).getNotificationMailingList();
        if (notificationMailingList != null) {
            getLog().debug("sending reply notification e-mail to forum list");
            renderer.render("/themes/"
                    + Preferences.getInstance(WikiPreferences.class).getThemeName()
                    + "/mailtemplates/forumNotifyReplyToList.xhtml");
        }

        // Notify original poster
        if (documentHome.getInstance().macroPresent(TopicHome.TOPIC_NOTIFY_ME_MACRO)
            && !documentHome.getInstance().getCreatedBy().getUsername().equals(
                    getInstance().getCreatedBy().getUsername()
                )) {
            getLog().debug("sending reply notification e-mail to original poster");
            renderer.render("/themes/"
                    + Preferences.getInstance(WikiPreferences.class).getThemeName()
                    + "/mailtemplates/forumNotifyReply.xhtml");
        }
    }

    @Begin(flushMode = FlushModeType.MANUAL, join = true)
    public String replyToDocument() {

        getLog().debug("reply to document id: " + getParentNodeId());
        newComment();
        initEditor();

        getInstance().setSubject(REPLY_PREFIX + getParentNode().getName());

        return "redirectToDocument";
    }

    @Begin(flushMode = FlushModeType.MANUAL, join = true)
    public String quoteDocument() {
        replyToDocument();

        getInstance().setContent(quote(
            documentHome.getInstance().getContent(),
            documentHome.getInstance().getCreatedOn(),
            documentHome.getInstance().getCreatedBy().getFullname()
        ));

        return "redirectToDocument";
    }

    @Override
    public boolean isPersistAllowed(WikiComment node, WikiNode parent) {
        /* Forum replies require write permissions on the forum directory */
        Integer currentAccessLevel = (Integer)Component.getInstance("currentAccessLevel");
        return Identity.instance().hasPermission("Comment", "create", documentHome.getInstance())
                && (documentHome.getParentNode().getWriteAccessLevel() <= currentAccessLevel);
    }

    protected String getFeedEntryManagerName() {
        return "forumReplyFeedEntryManager";
    }

    protected String getTextAreaId() {
        return "replyTextArea";
    }

    /* -------------------------- Messages ------------------------------ */

    protected void createdMessage() {
        getFacesMessages().addFromResourceBundleOrDefault(
                SEVERITY_INFO,
                "forum.msg.Reply.Persist",
                "Reply '{0}' has been saved.",
                getInstance().getSubject()
        );
    }

    protected void updatedMessage() {
        getFacesMessages().addFromResourceBundleOrDefault(
                SEVERITY_INFO,
                "forum.msg.Reply.Update",
                "Reply '{0}' has been updated.",
                getInstance().getSubject()
        );
    }

    protected void deletedMessage() {
        getFacesMessages().addFromResourceBundleOrDefault(
                SEVERITY_INFO,
                "forum.msg.Reply.Delete",
                "Reply '{0}' has been deleted.",
                getInstance().getSubject()
        );
    }

    private void markTopicRead() {
        if (!getCurrentUser().isAdmin() && !getCurrentUser().isGuest()) {
            getLog().debug("adding to read topics, forum id: "
                            + documentHome.getParentNode().getId() + " topic id: " + documentHome.getInstance().getId());
            ForumTopicReadManager forumTopicReadManager = (ForumTopicReadManager)Component.getInstance(ForumTopicReadManager.class);
            forumTopicReadManager.addTopicId(documentHome.getParentNode().getId(), documentHome.getInstance().getId());
        }
    }

}
