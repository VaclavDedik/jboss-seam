package org.jboss.seam.example.issues;
// Generated Dec 27, 2005 10:28:30 AM by Hibernate Tools 3.1.0 beta3

import static javax.ejb.TransactionAttributeType.NOT_SUPPORTED;

import java.util.Date;

import javax.ejb.Interceptors;
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
import org.jboss.seam.core.Conversation;
import org.jboss.seam.ejb.SeamInterceptor;


@Name("commentEditor")
@Stateful
@Interceptors(SeamInterceptor.class)
public class CommentEditorBean implements CommentEditor {

    @In(create=true)
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
    
    @In(create=true)
    private transient Conversation conversation;
    
    @In
    private Login login;
        
    @In 
    private IssueEditor issueEditor;

    @Begin(nested=true)
    public String createComment() {
       isNew = true;
       comment = new Comment();
       comment.setIssue( issueEditor.getInstance() );
       comment.setUser( login.getInstance() );
       comment.setSubmitted( new Date() );
       return conversation.switchableOutcome( "editComment", getCreateDescription() );
    }
    
    @Begin(nested=true)
    public String selectComment() {
       isNew = false;
       comment = issueEditor.getSelectedComment();
       return conversation.switchableOutcome( "editComment", getDescription() );
    }

    @End
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
    @IfInvalid(outcome=Outcome.REDISPLAY)
    public String update() {
       return "editIssue";
    }

    @End
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
    
    private String getDescription() {
       return "Comment [" + comment.getId() + "]";
    }
    
    public String getCreateDescription() {
       return "Create new Comment for " + getIssueDescription();
    }
    
    private String getIssueDescription()
    {
       return "Issue [" + issueEditor.getInstance().getId() + "]";
    }

    @Destroy @Remove
    public void destroy() {} 

}