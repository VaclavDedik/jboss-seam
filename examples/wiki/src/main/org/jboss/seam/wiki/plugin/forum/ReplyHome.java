package org.jboss.seam.wiki.plugin.forum;

import org.jboss.seam.wiki.core.action.CommentHome;
import org.jboss.seam.wiki.core.model.Comment;
import org.jboss.seam.wiki.core.model.Document;
import org.jboss.seam.wiki.core.model.User;
import org.jboss.seam.wiki.util.WikiUtil;
import org.jboss.seam.annotations.*;
import org.jboss.seam.ScopeType;
import org.jboss.seam.Component;
import org.jboss.seam.international.Messages;

import java.util.Date;

@Name("replyHome")
@Scope(ScopeType.CONVERSATION)
public class ReplyHome extends CommentHome {


    @Create
    public void initialize() {
        super.initialize();

        // Add this document to the "read" list in the forum cookie
        // TODO: At some point, this adds up to the 4 kb cookie value limit, maybe we should only store unread ids?
        ForumCookie forumCookie = (ForumCookie) Component.getInstance("forumCookie");
        forumCookie.addCookieValue(currentDocument.getId().toString(), "r");
    }

    private boolean showForm = false;

    public boolean isShowForm() {
        return showForm;
    }

    public void setShowForm(boolean showForm) {
        this.showForm = showForm;
    }

    public void createComment() {
        comment = new Comment();
        comment.setFromUser(currentUser);
    }

    public void createComment(Comment replyTo, boolean quote) {
        createComment();
        comment.setSubject(replyTo.getSubject());
        if (quote) comment.setText(quote(replyTo.getText(), replyTo.getCreatedOn(), replyTo.getFromUser()));
    }

    public void createComment(ForumTopic replyTo, boolean quote) {
        createComment();
        comment.setSubject(replyTo.getName());
        if (quote) comment.setText(quote(replyTo.getContentWithoutMacros(), replyTo.getCreatedOn(), replyTo.getCreatedBy()));
    }

    @Begin(flushMode = FlushModeType.MANUAL)
    public void newReplyToComment(Long commentId, boolean quote) {
        showForm = true;

        // Take content from other comment
        Comment foundCommment = restrictedEntityManager.find(Comment.class, commentId);
        createComment(foundCommment, quote);
    }

    @Begin(flushMode = FlushModeType.MANUAL)
    public void newReplyToTopic(Long topicId, boolean quote) {
        showForm = true;

        // Take content from topic
        ForumTopic topic = restrictedEntityManager.find(ForumTopic.class, topicId);
        createComment(topic, quote);
    }

    @End
    public void cancel() {
        showForm = false;
    }

    @End
    @RaiseEvent("Forum.replyPersisted")
    public void persist() {
        Document doc = restrictedEntityManager.find(Document.class, currentDocument.getId());
        comment.setDocument(doc);
        // TODO: Break this, for performance reasons... doc.getComments().add(comment);

        restrictedEntityManager.persist(comment);

        StringBuilder feedEntryTitle = new StringBuilder();
        if (doc.getName().equals(comment.getSubject())) {
            feedEntryTitle.append("[").append(doc.getParent().getName()).append("] ");
            feedEntryTitle.append( Messages.instance().get("forum.label.reply.FeedEntryTitlePrefix") );
            feedEntryTitle.append(" ").append(comment.getSubject());
        } else {
            feedEntryTitle.append("[").append(doc.getParent().getName()).append("] ");
            feedEntryTitle.append("(");
            feedEntryTitle.append( Messages.instance().get("forum.label.reply.FeedEntryTitlePrefix") );
            feedEntryTitle.append(" ").append(WikiUtil.truncateString(doc.getName(), 20, "...")).append(") ");
            feedEntryTitle.append(comment.getSubject());
        }

        pushOnFeeds(doc, feedEntryTitle.toString());

        restrictedEntityManager.flush();
        restrictedEntityManager.clear();

        refreshComments();

        showForm = false;
    }

    private String quote(String text, Date date, User author) {
        StringBuilder quoted = new StringBuilder();
        quoted.append("<blockquote>").append("\n");
        quoted.append("_").append(author.getFullname());
        quoted.append(" ").append(Messages.instance().get("forum.label.WroteOn")).append(" ");
        quoted.append(WikiUtil.formatDate(date)).append(":").append("_").append("<br/>\n");
        quoted.append(text);
        quoted.append("\n").append("</blockquote>").append("\n\n");
        return quoted.toString();
    }

}
