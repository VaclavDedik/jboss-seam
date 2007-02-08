/**
 * 
 */
package org.jboss.seam.example.issues;

import static org.jboss.seam.ScopeType.CONVERSATION;

import java.util.ResourceBundle;

import javax.ejb.Stateless;

import org.jboss.seam.Component;
import org.jboss.seam.annotations.Begin;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;


@Stateless
@Name("issueProjectSelector")
public class IssueProjectSelectorBean implements IssueProjectSelector {
   
   @In(create=true)
   private transient ResourceBundle resourceBundle;

   @In(create=true)
   private transient ProjectFinder projectFinder;

   @In(required=false)
   private transient IssueEditor issueEditor;
   
   @Begin(join=true)
   public String selectProject() {
      CONVERSATION.getContext().set("projectSelector",
            Component.getInstance("issueProjectSelector", true) );
       return "selectProject";
   }
   
   public String getDescription() {
       return "Select Project for " + getIssueDescription();
   }
   
   private String getIssueDescription() {
      Integer issueId = issueEditor==null ? null : issueEditor.getInstance().getId();
      return issueId==null ? "New Issue" : "Issue [" + issueId + "]";
   }

   private String completed() {
      CONVERSATION.getContext().remove("projectSelector");
      return "editIssue";
   }
   
   public String select() {
      Issue issue = issueEditor.getInstance();
      issue.setProject( projectFinder.getSelection() );
      projectFinder.getSelection().getIssues().add(issue);
      return completed();
   }
   
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