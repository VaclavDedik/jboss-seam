package org.jboss.seam.wiki.core.action;

import org.jboss.seam.annotations.*;
import org.jboss.seam.ScopeType;
import org.jboss.seam.Component;
import org.jboss.seam.security.Identity;
import org.jboss.seam.security.AuthorizationException;
import org.jboss.seam.core.FacesMessages;
import org.jboss.seam.wiki.core.model.Document;
import org.jboss.seam.wiki.core.model.User;
import org.jboss.seam.wiki.core.model.Directory;
import org.jboss.seam.wiki.core.model.Comment;
import org.jboss.seam.wiki.core.action.prefs.WikiPreferences;
import org.jboss.seam.wiki.util.WikiUtil;

import javax.persistence.EntityManager;
import javax.faces.application.FacesMessage;
import java.io.Serializable;
import java.util.List;
import java.util.ArrayList;

@Name("commentHome")
@Scope(ScopeType.CONVERSATION)
public class CommentHome implements Serializable {

    @In
    EntityManager entityManager;

    @In
    FacesMessages facesMessages;

    @In
    Document currentDocument;

    @In
    Directory currentDirectory;

    @In
    User currentUser;

    @In
    User guestUser;

    @In("#{commentsPreferences.properties['listAscending']}")
    boolean listCommentsAscending;

    private Comment comment;
    private List<Comment> comments;

    @Create
    public void initialize() {
        refreshComments();
    }

    @Observer("Preferences.commentsPreferences")
    @Transactional
    public void refreshComments() {
        entityManager.joinTransaction();

        comments = new ArrayList<Comment>();
        
        //noinspection unchecked
        comments = entityManager
                .createQuery("select c from Comment c where c.document is :doc" +
                             " order by c.createdOn " + (listCommentsAscending ? "asc" : "desc") )
                .setParameter("doc", currentDocument)
                .getResultList();

        comment = new Comment();
        if (!currentUser.getId().equals(guestUser.getId())) {
            comment.setFromUserName(currentUser.getFullname());
            comment.setFromUserEmail(currentUser.getEmail());
            comment.setFromUserHomepage(
                currentUser.getMemberHome() != null
                    ? ((WikiPreferences)Component.getInstance("wikiPreferences")).getBaseUrl()+WikiUtil.renderHomeURL(currentUser)
                    : null);
        }

        // Default to title of document as subject
        comment.setSubject(currentDocument.getName());
    }

    @Transactional
    public void persist() {

        entityManager.joinTransaction();
        comment.setDocument(entityManager.merge(currentDocument));

        // Null out the property so that the @Email validator doesn't fall over it...
        // I hate JSF and it's "let's set an empty string" behavior
        comment.setFromUserEmail(
            comment.getFromUserEmail()!=null && comment.getFromUserEmail().length()>0
                ? comment.getFromUserEmail()
                : null
        );

        entityManager.persist(comment);


        facesMessages.addFromResourceBundleOrDefault(
            FacesMessage.SEVERITY_INFO,
            "comment.persist",
            "Your comment has been saved."
        );
        refreshComments();
    }

    @Transactional
    public void remove(Long commentId) {
        entityManager.joinTransaction();
        if (!Identity.instance().hasPermission("Comment", "delete", entityManager.merge(currentDocument)) ) {
            throw new AuthorizationException("You don't have permission for this operation");
        }

        Comment foundCommment = entityManager.find(Comment.class, commentId);
        if (foundCommment != null) {
            entityManager.remove(foundCommment);
            facesMessages.addFromResourceBundleOrDefault(
                FacesMessage.SEVERITY_INFO,
                "comment.remove",
                "Comment with subject '" + foundCommment.getSubject() + "' has been removed."
            );
        }

        refreshComments();
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
