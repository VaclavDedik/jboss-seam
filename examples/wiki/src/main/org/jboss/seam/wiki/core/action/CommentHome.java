/*
 * JBoss, Home of Professional Open Source
 *
 * Distributable under LGPL license.
 * See terms of license at gnu.org.
 */
package org.jboss.seam.wiki.core.action;

import org.jboss.seam.annotations.*;
import org.jboss.seam.ScopeType;
import org.jboss.seam.international.Messages;
import org.jboss.seam.security.Identity;
import org.jboss.seam.security.AuthorizationException;
import org.jboss.seam.wiki.core.model.*;
import org.jboss.seam.wiki.core.dao.FeedDAO;
import org.jboss.seam.wiki.util.WikiUtil;

import javax.persistence.EntityManager;
import java.io.Serializable;
import java.util.List;
import java.util.ArrayList;

@Name("commentHome")
@Scope(ScopeType.PAGE)
public class CommentHome implements Serializable {

    @In
    FeedDAO feedDAO;

    @In
    protected EntityManager restrictedEntityManager;

    @In
    protected Document currentDocument;

    @In
    protected User currentUser;

    @In
    protected User guestUser;

    @In("#{commentsPreferences.properties['listAscending']}")
    protected boolean listCommentsAscending;

    protected Comment comment;
    protected List<Comment> comments;

    @Create
    public void initialize() {
        refreshComments();
    }

    @Observer(value = {"org.jboss.seam.postAuthenticate", "PreferenceComponent.refresh.commentsPreferences"}, create = false)
    public void refreshComments() {

        comments = new ArrayList<Comment>();
        
        //noinspection unchecked
        comments = restrictedEntityManager
                .createQuery("select c from Comment c left join fetch c.fromUser u left join fetch u.profile fetch all properties where c.document is :doc" +
                             " order by c.createdOn " + (listCommentsAscending ? "asc" : "desc") )
                .setParameter("doc", currentDocument)
                .setHint("org.hibernate.cacheable", true)
                .getResultList();

        createComment(); // Stay inside the same persistence context
    }

    public void createComment() {

        User user = restrictedEntityManager.find(User.class, currentUser.getId());

        comment = new Comment();
        if (!user.getId().equals(guestUser.getId())) {
            comment.setFromUserName(user.getFullname());
            comment.setFromUserEmail(user.getEmail());
            // Profile website overrides member home website
            comment.setFromUserHomepage(
                user.getProfile() != null && user.getProfile().getWebsite() != null
                    ? user.getProfile().getWebsite()
                    : user.getMemberHome() != null ? WikiUtil.renderHomeURL(user) : null);
        }

        // Default to title of document as subject
        comment.setSubject(currentDocument.getName());

        // Default to help text
        comment.setText(Messages.instance().get("lacewiki.msg.commentForm.EditThisTextPreviewUpdatesAutomatically"));
    }

    public void persist() {

        Document doc = restrictedEntityManager.find(Document.class, currentDocument.getId());
        comment.setDocument(doc);
        doc.getComments().add(comment);

        // Null out the property so that the @Email validator doesn't fall over it...
        // I hate JSF and its "let's set an empty string" behavior
        comment.setFromUserEmail(
            comment.getFromUserEmail()!=null && comment.getFromUserEmail().length()>0
                ? comment.getFromUserEmail()
                : null
        );

        restrictedEntityManager.persist(comment);

        pushOnFeeds(doc, null);

        refreshComments();
        createComment();
    }

    public void remove(Long commentId) {

        Comment foundCommment = restrictedEntityManager.find(Comment.class, commentId);
        if (foundCommment != null) {
            if (!Identity.instance().hasPermission("Comment", "delete", foundCommment.getDocument()) ) {
                throw new AuthorizationException("You don't have permission for this operation");
            }

            restrictedEntityManager.remove(foundCommment);

            Document doc = restrictedEntityManager.find(Document.class, currentDocument.getId());
            feedDAO.removeFeedEntry(doc, foundCommment);
        }

        refreshComments();
        createComment();
    }

    protected void pushOnFeeds(Document document, String title) {

        String feedEntryTitle =
                title == null
                ? Messages.instance().get("lacewiki.label.comment.FeedEntryTitlePrefix") + " " + comment.getSubject()
                : title;
        if (currentDocument.getEnableComments() && document.getEnableCommentsOnFeeds()) {
            feedDAO.createFeedEntry(document, comment, false, feedEntryTitle);
        }
    }

    public Comment getComment() {
        return comment;
    }

    public void setComment(Comment comment) {
        this.comment = comment;
    }

    public List<Comment> getComments() {
        return comments;
    }

}
