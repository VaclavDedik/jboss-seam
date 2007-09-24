/*
 * JBoss, Home of Professional Open Source
 *
 * Distributable under LGPL license.
 * See terms of license at gnu.org.
 */
package org.jboss.seam.wiki.core.action;

import org.jboss.seam.annotations.*;
import org.jboss.seam.annotations.remoting.WebRemote;
import org.jboss.seam.ScopeType;
import org.jboss.seam.Component;
import org.jboss.seam.security.Identity;
import org.jboss.seam.security.AuthorizationException;
import org.jboss.seam.faces.FacesMessages;
import org.jboss.seam.wiki.core.model.*;
import org.jboss.seam.wiki.core.action.prefs.WikiPreferences;
import org.jboss.seam.wiki.util.WikiUtil;

import javax.persistence.EntityManager;
import javax.faces.application.FacesMessage;
import java.io.Serializable;
import java.util.List;
import java.util.ArrayList;

@Name("commentHome")
@Scope(ScopeType.PAGE)
public class CommentHome implements Serializable {

    @In
    EntityManager entityManager;

    @In
    FacesMessages facesMessages;

    @In
    DocumentHome documentHome;

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

    @Observer(value = {"org.jboss.seam.postAuthenticate", "PreferenceComponent.refresh.commentsPreferences"}, create = false)
    public void refreshComments() {
        entityManager.joinTransaction();

        comments = new ArrayList<Comment>();
        
        //noinspection unchecked
        comments = entityManager
                .createQuery("select c from Comment c where c.document is :doc" +
                             " order by c.createdOn " + (listCommentsAscending ? "asc" : "desc") )
                .setParameter("doc", documentHome.getInstance())
                .setHint("org.hibernate.cacheable", true)
                .getResultList();

        comment = new Comment();
        if (!currentUser.getId().equals(guestUser.getId())) {
            comment.setFromUserName(currentUser.getFullname());
            comment.setFromUserEmail(currentUser.getEmail());
            comment.setFromUserHomepage(
                currentUser.getMemberHome() != null
                    ? WikiUtil.renderHomeURL(currentUser)
                    : null);
        }

        // Default to title of document as subject
        comment.setSubject(documentHome.getInstance().getName());
    }

    public void persist() {

        entityManager.joinTransaction();
        Document currentDocument = entityManager.merge(documentHome.getInstance());
        comment.setDocument(currentDocument);
        currentDocument.getComments().add(comment);

        // Null out the property so that the @Email validator doesn't fall over it...
        // I hate JSF and its "let's set an empty string" behavior
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

    public void remove(Long commentId) {
        entityManager.joinTransaction();

        Comment foundCommment = entityManager.find(Comment.class, commentId);
        if (foundCommment != null) {
            if (!Identity.instance().hasPermission("Comment", "delete", foundCommment.getDocument()) ) {
                throw new AuthorizationException("You don't have permission for this operation");
            }

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
