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
import org.jboss.seam.international.StatusMessages;
import org.jboss.seam.annotations.*;
import org.jboss.seam.annotations.web.RequestParameter;
import org.jboss.seam.wiki.core.feeds.FeedDAO;
import org.jboss.seam.wiki.core.feeds.FeedEntryManager;
import org.jboss.seam.wiki.core.model.*;
import org.jboss.seam.wiki.core.action.prefs.CommentsPreferences;
import org.jboss.seam.wiki.core.exception.InvalidWikiRequestException;
import org.jboss.seam.wiki.core.ui.WikiRedirect;
import org.jboss.seam.wiki.util.WikiUtil;

import static org.jboss.seam.international.StatusMessage.Severity.INFO;
import static org.jboss.seam.international.StatusMessage.Severity.WARN;

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
            WikiRedirect.instance()
                    .setWikiDocument(documentHome.getInstance())
                    .setPropagateConversation(false)
                    .setFragment("comment" + getInstance().getId())
                    .execute();
        }

        return null; // No navigation
    }

    public String remove(Long commentId) {
        setNodeId(commentId);
        initEditor(false);
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
        StatusMessages.instance().addFromResourceBundleOrDefault(
                INFO,
                "lacewiki.msg.Comment.Persist",
                "Comment '{0}' has been saved.",
                getInstance().getSubject()
        );
    }

    @Override
    protected void updatedMessage() {
        StatusMessages.instance().addFromResourceBundleOrDefault(
                INFO,
                "lacewiki.msg.Comment.Update",
                "Comment '{0}' has been updated.",
                getInstance().getSubject()
        );
    }

    @Override
    protected void deletedMessage() {
        StatusMessages.instance().addFromResourceBundleOrDefault(
                INFO,
                "lacewiki.msg.Comment.Delete",
                "Comment '{0}' has been deleted.",
                getInstance().getSubject()
        );
    }

    protected String getEditorWorkspaceDescription(boolean create) {
        return null;
    }

    /* -------------------------- Internal Methods ------------------------------ */

    // TODO: Why again are we using a different validator here for the text editor?
    protected boolean validateContent() {
        FormattedTextValidator validator = new FormattedTextValidator();
        try {
            validator.validate(null, null, getInstance().getContent());
        } catch (ValidatorException e) {
            // TODO: Needs to use resource bundle, how?
            StatusMessages.instance().addToControl(
                getTextAreaId(),
                WARN,
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
    public void newComment() {
        initEditor(false);
        showForm = true;
    }

    @Begin(flushMode = FlushModeType.MANUAL, join = true)
    public void replyTo() {
        prepareReply();
        WikiRedirect.instance()
                .setWikiDocument(documentHome.getInstance())
                .setPropagateConversation(true)
                .execute();
    }

    @Begin(flushMode = FlushModeType.MANUAL, join = true)
    public void quote() {
        prepareReply();
        setQuotedContent((WikiComment)getParentNode());
        WikiRedirect.instance()
                .setWikiDocument(documentHome.getInstance())
                .setPropagateConversation(true)
                .execute();
    }

    private void prepareReply() {
        if (parentCommentId == null || parentCommentId.equals(0l))
            throw new InvalidWikiRequestException("Missing parentCommentId request parameter");

        getLog().debug("reply to comment id: " + parentCommentId);
        newComment();

        setParentNodeId(parentCommentId);
        getInstance(); // Init the parent
        setReplySubject((WikiComment)getParentNode());
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
            Events.instance().raiseEvent("Comment.rated");
        }
    }

    public void cancel() {
        endConversation();
        WikiRedirect.instance()
                .setWikiDocument(documentHome.getInstance())
                .setPropagateConversation(false)
                .execute();
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
