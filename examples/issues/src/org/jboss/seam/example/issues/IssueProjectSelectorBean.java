/**
 * 
 */
package org.jboss.seam.example.issues;

import static org.jboss.seam.ScopeType.CONVERSATION;

import java.util.ResourceBundle;

import javax.ejb.Interceptors;
import javax.ejb.Stateless;

import org.jboss.seam.Component;
import org.jboss.seam.annotations.Begin;
import org.jboss.seam.annotations.End;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.core.Conversation;
import org.jboss.seam.ejb.SeamInterceptor;


@Stateless
@Name("issueProjectSelector")
@Interceptors(SeamInterceptor.class)
public class IssueProjectSelectorBean implements IssueProjectSelector {
   
   @In
   private transient ResourceBundle resourceBundle;

   @In(create=true)
   private transient Conversation conversation;

   @In(create=true)
   private transient ProjectFinder projectFinder;

   @In
   private transient IssueEditor issueEditor;
   
   @Begin(nested=true)
   public String selectProject() {
      CONVERSATION.getContext().set("projectSelector",
            Component.getInstance("issueProjectSelector", true) );
       return conversation.switchableOutcome("selectProject", getSelectProjectDescription() );
   }
   
   private String getSelectProjectDescription() {
       return "Select Project for " + getIssueDescription();
   }
   
   private String getIssueDescription() {
      Integer issueId = issueEditor.getInstance().getId();
      return issueId==null ? "New Issue" : "Issue [" + issueId + "]";
   }

   private String completed() {
      CONVERSATION.getContext().remove("projectSelector");
      return conversation.switchableOutcome("editIssue", getIssueDescription());
   }
   
   @End
   public String select() {
      Issue issue = issueEditor.getInstance();
      issue.setProject( projectFinder.getSelection() );
      projectFinder.getSelection().getIssues().add(issue);
      return completed();
   }
   
   @End
   public String cancel() {
      return completed();
   }

   public String getButtonLabel() {
      return resourceBundle.getString("Select");
   }

   public boolean isCreateEnabled() {
      return false;
   }

}