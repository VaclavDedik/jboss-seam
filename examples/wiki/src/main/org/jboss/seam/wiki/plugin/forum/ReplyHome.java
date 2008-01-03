package org.jboss.seam.wiki.plugin.forum;

import org.jboss.seam.Component;
import org.jboss.seam.ScopeType;
import org.jboss.seam.security.Identity;
import org.jboss.seam.security.AuthorizationException;
import org.jboss.seam.annotations.Begin;
import org.jboss.seam.annotations.FlushModeType;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.wiki.core.action.CommentHome;

import static javax.faces.application.FacesMessage.SEVERITY_INFO;

@Name("replyHome")
@Scope(ScopeType.CONVERSATION)
public class ReplyHome extends CommentHome {

    @Override
    public void create() {
        super.create();
        markTopicRead();
    }

    @Begin(flushMode = FlushModeType.MANUAL, join = true)
    public String replyToDocument() {

        getLog().debug("reply to document id: " + getParentNodeId());
        newComment();
        setEdit(true);

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

    /* Forum replies require write permissions on the forum directory */
    public boolean hasReplyPermission() {
        Integer currentAccessLevel = (Integer)Component.getInstance("currentAccessLevel");
        return Identity.instance().hasPermission("Comment", "create", documentHome.getInstance())
                && (documentHome.getParentNode().getWriteAccessLevel() <= currentAccessLevel);
    }

    protected void checkPersistPermissions() {
        if (!hasReplyPermission()) {
            throw new AuthorizationException("You don't have permission for this operation");
        }
    }

    protected String getFeedEntryManagerName() {
        return "forumReplyFeedEntryManager";
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
            ForumTopicReadManager forumTopicReadManager = (ForumTopicReadManager)Component.getInstance("forumTopicReadManager");
            forumTopicReadManager.addTopicId(documentHome.getParentNode().getId(), documentHome.getInstance().getId());
        }
    }

}
