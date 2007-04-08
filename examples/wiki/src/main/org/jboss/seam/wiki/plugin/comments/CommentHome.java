package org.jboss.seam.wiki.plugin.comments;

import org.jboss.seam.annotations.*;
import org.jboss.seam.ScopeType;
import org.jboss.seam.Component;
import org.jboss.seam.core.FacesMessages;
import org.jboss.seam.wiki.core.model.Document;
import org.jboss.seam.wiki.core.model.User;
import org.jboss.seam.wiki.core.model.Directory;
import org.jboss.seam.wiki.core.action.prefs.WikiPreferences;
import org.jboss.seam.wiki.core.engine.WikiLinkResolver;
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
    Document currentDocument;

    @In
    Directory currentDirectory;

    @In
    User currentUser;

    @In
    User guestUser;

    private Comment comment;
    private List<Comment> comments;

    private String formContent;
    private boolean enabledPreview = false;

    @Create
    public void initialize() {
        System.out.println("########################### CREATE COMMENT HOME ##################################");

        refreshComments();
    }

    //@Observer("Preferences.blogDirectoryPreferences")
    @Transactional
    public void refreshComments() {
        System.out.println("########################### REFRESH COMMENTS ##################################");
        entityManager.joinTransaction();

        comments = new ArrayList<Comment>();
        //noinspection unchecked
        comments = entityManager
                .createQuery("select c from Comment c where c.document is :doc order by c.createdOn asc")
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
        formContent = null;
    }

    @Transactional
    public void persist() {
        System.out.println("###################################### PERSIST #####################################");

        syncFormToInstance(currentDirectory);

        entityManager.joinTransaction();
        comment.setDocument(entityManager.merge(currentDocument));
        if (!currentUser.getId().equals(guestUser.getId()))
            comment.setFromUser(entityManager.merge(currentUser));

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

    /* Wiki text editing */

    public String getFormContent() {
        // Load the text content and resolve links
        if (formContent == null) syncInstanceToForm(currentDirectory);
        return formContent;
    }

    public void setFormContent(String formContent) {
        this.formContent = formContent;
    }

    private void syncFormToInstance(Directory dir) {
        WikiLinkResolver wikiLinkResolver = (WikiLinkResolver)Component.getInstance("wikiLinkResolver");
        comment.setText(
            wikiLinkResolver.convertToWikiProtocol(dir.getAreaNumber(), formContent)
        );
    }

    private void syncInstanceToForm(Directory dir) {
        WikiLinkResolver wikiLinkResolver = (WikiLinkResolver)Component.getInstance("wikiLinkResolver");
        formContent = wikiLinkResolver.convertFromWikiProtocol(dir.getAreaNumber(), comment.getText());
    }

    public boolean isEnabledPreview() {
        return enabledPreview;
    }

    public void setEnabledPreview(boolean enabledPreview) {
        this.enabledPreview = enabledPreview;
        syncFormToInstance(currentDirectory);
    }

}
