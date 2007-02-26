package org.jboss.seam.example.issues;
// Generated Dec 27, 2005 10:28:30 AM by Hibernate Tools 3.1.0 beta3

import static javax.ejb.TransactionAttributeType.NOT_SUPPORTED;

import java.util.Date;

import javax.ejb.Remove;
import javax.ejb.Stateful;
import javax.ejb.TransactionAttribute;
import javax.persistence.EntityManager;

import org.hibernate.validator.Valid;
import org.jboss.seam.annotations.Begin;
import org.jboss.seam.annotations.Destroy;
import org.jboss.seam.annotations.End;
import org.jboss.seam.annotations.IfInvalid;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Outcome;
import org.jboss.seam.annotations.security.Restrict;


@Name("commentEditor")
@Stateful
public class CommentEditorBean implements CommentEditor {

    @In
    private EntityManager entityManager;

    @Valid
    private Comment comment;
    
    @TransactionAttribute(NOT_SUPPORTED)
    public Comment getInstance() {
       return comment;
    }

    private boolean isNew;
    
    @TransactionAttribute(NOT_SUPPORTED)
    public boolean isNew() {
       return isNew;
    }
        
    @In 
    private IssueEditor issueEditor;
    
    @In(required = false)
    private User authenticatedUser;

    @Begin(nested=true)
    @Restrict("#{identity.loggedIn}")
    public String createComment() {
       isNew = true;
       comment = new Comment();
       comment.setIssue( issueEditor.getInstance() );
       comment.setUser( authenticatedUser );
       comment.setSubmitted( new Date() );
       return "editComment";
    }
    
    @Begin(nested=true)
    public String selectComment() {
       isNew = false;
       comment = issueEditor.getSelectedComment();
       return "editComment";
    }

    @End
    @Restrict("#{identity.loggedIn}")
    @IfInvalid(outcome=Outcome.REDISPLAY)
    public String create() {
       entityManager.persist(comment);
       isNew = false;
       if (comment.getIssue()!=null) {
          comment.getIssue().getComments().add(comment);
       }
       return "editIssue";
    }

    @End
    @Restrict("#{identity.loggedIn}")
    @IfInvalid(outcome=Outcome.REDISPLAY, refreshEntities=true)
    public String update() {
       return "editIssue";
    }

    @End
    @Restrict("#{identity.loggedIn}")
    public String delete() {
       entityManager.remove(comment);
       comment.getIssue().getComments().remove(comment);
       return "editIssue";
    }

    @End
    public String done() {
       if (!isNew) entityManager.refresh(comment);
       return "editIssue";
    }
    
    @TransactionAttribute(NOT_SUPPORTED)
    public String getDescription() {
       return comment==null || comment.getId()==null ?
             "Comment on " + issueEditor.getDescription() :
             "Comment [" + comment.getId() + "] for " + issueEditor.getDescription();
    }
    
    @Destroy @Remove
    public void destroy() {} 

}