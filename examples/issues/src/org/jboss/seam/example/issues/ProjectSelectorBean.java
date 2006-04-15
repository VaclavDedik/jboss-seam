/**
 * 
 */
package org.jboss.seam.example.issues;

import java.util.ResourceBundle;

import javax.ejb.Stateless;

import org.jboss.seam.annotations.Begin;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;

@Stateless
@Name("projectSelector")
public class ProjectSelectorBean implements ProjectSelector {

   @In(create=true)
   private transient ResourceBundle resourceBundle;

   @In(create=true)
   private transient ProjectEditor projectEditor;
   
   @In(create=true)
   private transient ProjectFinder projectFinder;
   
   @Begin
   public String select() {
      projectEditor.setInstance( projectFinder.getSelection() );
      return "editProject";
   }
   
   public String getButtonLabel() {
      return resourceBundle.getString("View");
   }
   
   public boolean isCreateEnabled() {
      return true;
   }
   
}