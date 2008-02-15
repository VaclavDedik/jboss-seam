/*
 * JBoss, Home of Professional Open Source
 *
 * Distributable under LGPL license.
 * See terms of license at gnu.org.
 */
package org.jboss.seam.wiki.core.action;

import org.jboss.seam.ScopeType;
import org.jboss.seam.Component;
import org.jboss.seam.ui.validator.FormattedTextValidator;
import org.jboss.seam.security.AuthorizationException;
import org.jboss.seam.security.Identity;
import org.jboss.seam.core.Events;
import org.jboss.seam.core.Conversation;
import org.jboss.seam.international.Messages;
import org.jboss.seam.annotations.*;
import org.jboss.seam.annotations.web.RequestParameter;
import org.jboss.seam.wiki.core.feeds.FeedDAO;
import org.jboss.seam.wiki.core.feeds.FeedEntryManager;
import org.jboss.seam.wiki.core.model.*;
import org.jboss.seam.wiki.core.action.prefs.CommentsPreferences;
import org.jboss.seam.wiki.util.WikiUtil;

import static javax.faces.application.FacesMessage.SEVERITY_INFO;
import javax.faces.application.FacesMessage;
import javax.faces.validator.ValidatorException;
import java.util.Date;

@Name("commentHome")
@Scope(ScopeType.CONVERSATION)
public class CommentHome extends NodeHome<WikiComment, WikiNode>{

    public static final String REPLY_PREFIX = "Re: ";

    /* -------------------------- Context Wiring ------------------------------ */

    @In
    protected DocumentHome documentHome;

    @In
    protected FeedDAO feedDAO;

    @In("#{preferences.get('Comments')}")
    protected CommentsPreferences commentsPreferences;

    /* -------------------------- Internal State ------------------------------ */

    @RequestParameter
    private Long parentCommentId;
    private boolean showForm = false;

    /* -------------------------- Basic Overrides ------------------------------ */

    @Override
    protected boolean isPageRootController() {
        return false;
    }

    @Override
    public Class<WikiComment> getEntityClass() {
        return WikiComment.class;
    }

    @Override
    public void create() {
        super.create();
        setParentNodeId(documentHome.getInstance().getId());
    }

    @Override
    public WikiComment findInstance() {
        return getWikiNodeDAO().findWikiComment((Long)getId());
    }

    @Override
    protected WikiNode findParentNode(Long parentNodeId) {
        return getEntityManager().find(WikiNode.class, parentNodeId);
    }

    @Override
    public WikiComment afterNodeCreated(WikiComment comment) {
        comment = super.afterNodeCreated(comment);

        comment.setAreaNumber(documentHome.getInstance().getAreaNumber());
        comment.setDerivedName(documentHome.getInstance());
        comment.setWikiname(WikiUtil.convertToWikiName(comment.getName()));
        comment.setCreatedBy(getCurrentUser());

        // TODO: This means that when a user is deleted, his name will still be preserved. However, it's an old name.
        if (comment.isOwnedByRegularUser()) {
            comment.setFromUserName(getCurrentUser().getFullname());
        }

        // Default to title of document as subject
        comment.setSubject(documentHome.getInstance().getName());

        // Default to help text
        comment.setContent(Messages.instance().get("lacewiki.msg.wikiTextEditor.EditThisText"));

        return comment;
    }

    @Override
    public boolean isPersistAllowed(WikiComment node, WikiNode parent) {
        getLog().trace("checking comment persist permissions");
        return Identity.instance().hasPermission("Comment", "create", documentHome.getInstance());
    }

    /* -------------------------- Custom CUD ------------------------------ */

    @Override
    public String persist() {

        if (!validateContent()) return null;

        String outcome = super.persist();
        if (outcome != null) {

            if (documentHome.getInstance().isEnableCommentsOnFeeds()) {
                FeedEntry feedEntry =
                        ((FeedEntryManager) Component.getInstance(getFeedEntryManagerName())).createFeedEntry(getInstance());
                feedDAO.createFeedEntry(documentHome.getParentNode(), documentHome.getInstance(), feedEntry, false);

                getEntityManager().flush();
            }

            Events.instance().raiseEvent("Comment.persisted");
            endConversation();
            return "redirectToComment";
        }
        return null; // Prevent navigation
    }

    public String remove(Long commentId) {
        setNodeId(commentId);
        initEditor();
        if (isManaged()) {

            // Additional permission required besides NodeHome.remove()
            if (!Identity.instance().hasPermission("Comment", "delete", getInstance().getParent()) ) {
                throw new AuthorizationException("You don't have permission for this operation");
            }

            // Remove feed entry before removing comment
            feedDAO.removeFeedEntry(
                feedDAO.findFeeds(getInstance()),
                feedDAO.findFeedEntry(getInstance())
            );

            remove();
            getEntityManager().clear();
            Events.instance().raiseEvent("Comment.commentListRefresh");
        }

        return null; // Prevent navigation
    }


    @Override
    protected NodeRemover getNodeRemover() {
        return (CommentNodeRemover)Component.getInstance(CommentNodeRemover.class);
    }

/* -------------------------- Messages ------------------------------ */

    @Override
    protected void createdMessage() {
        getFacesMessages().addFromResourceBundleOrDefault(
                SEVERITY_INFO,
                "lacewiki.msg.Comment.Persist",
                "Comment '{0}' has been saved.",
                getInstance().getSubject()
        );
    }

    @Override
    protected void updatedMessage() {
        getFacesMessages().addFromResourceBundleOrDefault(
                SEVERITY_INFO,
                "lacewiki.msg.Comment.Update",
                "Comment '{0}' has been updated.",
                getInstance().getSubject()
        );
    }

    @Override
    protected void deletedMessage() {
        getFacesMessages().addFromResourceBundleOrDefault(
                SEVERITY_INFO,
                "lacewiki.msg.Comment.Delete",
                "Comment '{0}' has been deleted.",
                getInstance().getSubject()
        );
    }

    protected String getEditorWorkspaceDescription(boolean create) {
        return null;
    }

    /* -------------------------- Internal Methods ------------------------------ */

    protected boolean validateContent() {
        FormattedTextValidator validator = new FormattedTextValidator();
        try {
            validator.validate(null, null, getInstance().getContent());
        } catch (ValidatorException e) {
            // TODO: Needs to use resource bundle, how?
            getFacesMessages().addToControl(
                getTextAreaId(),
                FacesMessage.SEVERITY_WARN,
                e.getFacesMessage().getSummary()
            );
            return false;
        }
        return true;
    }

    protected void endConversation() {
        showForm = false;
        Conversation.instance().end();
        getEntityManager().clear(); // Need to force re-read in the topic list refresh
        Events.instance().raiseEvent("Comment.commentListRefresh");
    }

    protected String getFeedEntryManagerName() {
        return "wikiCommentFeedEntryManager";
    }

    protected void setReplySubject(WikiComment parentComment) {
        if (!parentComment.getSubject().equals(documentHome.getInstance().getName())) {
            if (parentComment.getSubject().startsWith(REPLY_PREFIX)) {
                getInstance().setSubject(parentComment.getSubject());
            } else {
                getInstance().setSubject(REPLY_PREFIX + parentComment.getSubject());
            }
        }
    }

    protected void setQuotedContent(WikiComment parentComment) {
        getInstance().setContent(quote(
            parentComment.getContent(),
            parentComment.getCreatedOn(),
            parentComment.isOwnedByRegularUser() ? parentComment.getCreatedBy().getFullname() : parentComment.getFromUserName()
        ));
    }

    protected String quote(String text, Date date, String authorName) {
        StringBuilder quoted = new StringBuilder();
        quoted.append("<blockquote>").append("\n");
        quoted.append("_").append(authorName);
        quoted.append(" ").append(Messages.instance().get("forum.label.WroteOn")).append(" ");
        quoted.append(WikiUtil.formatDate(date)).append(":").append("_").append("<br/>\n\n");
        quoted.append(text);
        quoted.append("\n").append("</blockquote>").append("\n\n");
        quoted.append(Messages.instance().get("lacewiki.msg.wikiTextEditor.EditThisText"));
        return quoted.toString();
    }

    protected String getTextAreaId() {
        return "commentTextArea";
    }

    /* -------------------------- Public Features ------------------------------ */

    public boolean isShowForm() {
        return showForm;
    }

    public void setShowForm(boolean showForm) {
        this.showForm = showForm;
    }

    @Begin(flushMode = FlushModeType.MANUAL, join = true)
    public String newComment() {
        initEditor();
        showForm = true;
        return "redirectToDocument";
    }

    @Begin(flushMode = FlushModeType.MANUAL, join = true)
    public String replyTo() {
        if (parentCommentId == null || parentCommentId.equals(0l))
            throw new IllegalStateException("Missing parentCommentId request parameter");

        if (commentsPreferences.getThreaded()) {
            // Override parent from @Create
            setParentNodeId(parentCommentId);
        }
        getLog().debug("reply to comment id: " + parentCommentId);
        newComment();

        if (commentsPreferences.getThreaded()) {
            getInstance(); // Init
            setReplySubject((WikiComment)getParentNode());
        } else {
            setReplySubject(getWikiNodeDAO().findWikiComment(parentCommentId));
        }
        return "redirectToDocument";
    }

    @Begin(flushMode = FlushModeType.MANUAL, join = true)
    public String quote() {
        replyTo();

        if (commentsPreferences.getThreaded()) {
            setQuotedContent((WikiComment)getParentNode());
        } else {
            setQuotedContent(getWikiNodeDAO().findWikiComment(parentCommentId));
        }
        return "redirectToDocument";
    }

    public void rate(Long commentId, int rating) {

        getLog().debug("rating comment with id: " + commentId + " as " + rating);

        // Only the owner of the document can rate comments of that document
        if ( !currentUser.getId().equals(documentHome.getInstance().getCreatedBy().getId()) ) {
            throw new AuthorizationException("You don't have permission for this operation");
        }

        // Guest can't rate
        if (currentUser.isGuest()) {
            throw new IllegalStateException("User interface bug, guests can't rate comments");
        }

        setId(commentId);
        if (isManaged()) {

            if (getInstance().getRating() != 0) {
                throw new IllegalStateException("User interface bug, can't rate comment that was already rated");
            }
            if (getInstance().getCreatedBy().getId().equals(currentUser.getId())) {
                throw new IllegalStateException("User interface bug, a user can't rate his/her own comments");
            }

            getInstance().setRating(rating);
        }
    }

    public String cancel() {
        endConversation();
        return "redirectToDocumentNoConversation";
    }

    @RequestParameter("showCommentForm")
    public void showCommentForm(Boolean requestParam) {
        if (requestParam != null && requestParam && !showForm) {
            getLog().debug("request parameter sets comment form visible, starts conversation");
            Conversation.instance().begin(true, false);
            Conversation.instance().changeFlushMode(FlushModeType.MANUAL);
            newComment();
        }
    }

}
