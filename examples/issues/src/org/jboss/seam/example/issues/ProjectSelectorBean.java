/**
 * 
 */
package org.jboss.seam.example.issues;

import java.util.ResourceBundle;

import javax.ejb.Interceptors;
import javax.ejb.Stateless;

import org.jboss.seam.annotations.Begin;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.core.Conversation;
import org.jboss.seam.ejb.SeamInterceptor;

@Stateless
@Name("projectSelector")
@Interceptors(SeamInterceptor.class)
public class ProjectSelectorBean implements ProjectSelector {

   @In
   private transient ResourceBundle resourceBundle;

   @In(create=true)
   private transient Conversation conversation;

   @In(create=true)
   private transient ProjectEditor projectEditor;
   
   @In(create=true)
   private transient ProjectFinder projectFinder;
   
   @Begin
   public String select() {
      projectEditor.setInstance( projectFinder.getSelection() );
      return conversation.switchableOutcome( "editProject", getDescription() );
   }
   
   private String getDescription() {
      return "Project [" + projectFinder.getSelection().getName() + "]";
   }
   
   public String getButtonLabel() {
      return resourceBundle.getString("View");
   }
   
   public boolean isCreateEnabled() {
      return true;
   }
   
}