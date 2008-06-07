package org.jboss.seam.wiki.plugin.forum;

import org.jboss.seam.Component;
import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.*;
import org.jboss.seam.faces.Renderer;
import org.jboss.seam.international.StatusMessages;
import org.jboss.seam.security.Identity;
import org.jboss.seam.wiki.core.action.CommentHome;
import org.jboss.seam.wiki.core.wikitext.editor.WikiTextValidator;
import org.jboss.seam.wiki.core.model.WikiComment;
import org.jboss.seam.wiki.core.model.WikiNode;
import org.jboss.seam.wiki.core.ui.WikiRedirect;
import org.jboss.seam.wiki.core.plugin.PluginRegistry;
import org.jboss.seam.wiki.preferences.Preferences;

import static org.jboss.seam.international.StatusMessage.Severity.INFO;

@Name("replyHome")
@Scope(ScopeType.CONVERSATION)
public class ReplyHome extends CommentHome {

    public static final String REPLY_NOTIFY_TEMPLATE        = "/mailtemplates/forumNotifyReply.xhtml";
    public static final String REPLY_NOTIFY_LIST_TEMPLATE   = "/mailtemplates/forumNotifyReplyToList.xhtml";

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
                Preferences.instance().get(ForumPreferences.class).getNotificationMailingList();
        if (notificationMailingList != null) {
            getLog().debug("sending reply notification e-mail to forum list: " + notificationMailingList);
            renderer.render(PluginRegistry.instance().getPlugin("forum").getPackageThemePath()+REPLY_NOTIFY_LIST_TEMPLATE);
        }

        // Notify original poster
        if (documentHome.getInstance().macroPresent(TopicHome.TOPIC_NOTIFY_ME_MACRO)
            && !documentHome.getInstance().getCreatedBy().getUsername().equals(getInstance().getCreatedBy().getUsername())
           ) {
            getLog().debug("sending reply notification e-mail to original poster");
            renderer.render(PluginRegistry.instance().getPlugin("forum").getPackageThemePath()+REPLY_NOTIFY_TEMPLATE);
        }
    }

    @Begin(flushMode = FlushModeType.MANUAL, join = true)
    public void replyToDocument() {

        getLog().debug("reply to document id: " + getParentNodeId());
        newComment();
        initEditor(false);

        getInstance().setSubject(REPLY_PREFIX + getParentNode().getName());

        WikiRedirect.instance()
                .setWikiDocument(documentHome.getInstance())
                .setPropagateConversation(true)
                .execute();
    }

    @Begin(flushMode = FlushModeType.MANUAL, join = true)
    public void quoteDocument() {

        getLog().debug("quote to document id: " + getParentNodeId());
        newComment();
        initEditor(false);

        getInstance().setSubject(REPLY_PREFIX + getParentNode().getName());

        getInstance().setContent(quote(
            documentHome.getInstance().getContent(),
            documentHome.getInstance().getCreatedOn(),
            documentHome.getInstance().getCreatedBy().getFullname()
        ));

        WikiRedirect.instance()
                .setWikiDocument(documentHome.getInstance())
                .setPropagateConversation(true)
                .execute();
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

    @Override
    protected WikiTextValidator.ValidationCommand[] getPersistValidationCommands() {
        return new WikiTextValidator.ValidationCommand[] {
            new WikiTextValidator.ValidationCommand() {
                public String getKey() {
                    return "reply";
                }

                public String getWikiTextValue() {
                    return getInstance().getContent();
                }

                public boolean getWikiTextRequired() {
                    return true;
                }
            }
        };
    }

    /* -------------------------- Messages ------------------------------ */

    protected void createdMessage() {
        StatusMessages.instance().addFromResourceBundleOrDefault(
                INFO,
                "forum.msg.Reply.Persist",
                "Reply '{0}' has been saved.",
                getInstance().getSubject()
        );
    }

    protected void updatedMessage() {
        StatusMessages.instance().addFromResourceBundleOrDefault(
                INFO,
                "forum.msg.Reply.Update",
                "Reply '{0}' has been updated.",
                getInstance().getSubject()
        );
    }

    protected void deletedMessage() {
        StatusMessages.instance().addFromResourceBundleOrDefault(
                INFO,
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
