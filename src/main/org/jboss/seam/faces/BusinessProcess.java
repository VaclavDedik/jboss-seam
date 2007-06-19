package org.jboss.seam.faces;

import static org.jboss.seam.InterceptionType.NEVER;
import static org.jboss.seam.annotations.Install.FRAMEWORK;

import javax.faces.application.FacesMessage;

import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.Install;
import org.jboss.seam.annotations.Intercept;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;

/**
 * Holds the task and process ids for the current conversation,
 * and provides programmatic control over the business process.
 * 
 * @author Gavin King
 *
 */
@Scope(ScopeType.CONVERSATION)
@Name("org.jboss.seam.core.businessProcess")
@Intercept(NEVER)
@Install(dependencies="org.jboss.seam.core.jbpm", precedence=FRAMEWORK, classDependencies="javax.faces.context.FacesContext")
public class BusinessProcess extends org.jboss.seam.bpm.BusinessProcess
{
   
   @Override
   protected void taskNotFound(Long taskId)
   {
      FacesMessages.instance().addFromResourceBundleOrDefault(
            FacesMessage.SEVERITY_WARN, 
            "org.jboss.seam.TaskNotFound", 
            "Task #0 not found", 
            taskId
         );
   }
   
   @Override
   protected void taskEnded(Long taskId)
   {
      FacesMessages.instance().addFromResourceBundleOrDefault(
            FacesMessage.SEVERITY_WARN, 
            "org.jboss.seam.TaskEnded", 
            "Task #0 already ended", 
            taskId
         );
   }
   
   @Override
   protected void processEnded(Long processId)
   {
      FacesMessages.instance().addFromResourceBundleOrDefault(
            FacesMessage.SEVERITY_WARN, 
            "org.jboss.seam.ProcessEnded", 
            "Process #0 already ended", 
            processId
         );
   }
   
   @Override
   protected void processNotFound(Long processId)
   {
      FacesMessages.instance().addFromResourceBundleOrDefault(
            FacesMessage.SEVERITY_WARN, 
            "org.jboss.seam.ProcessNotFound", 
            "Process #0 not found", 
            processId
         );
   }
   
   @Override
   protected void processEnded(String key)
   {
      FacesMessages.instance().addFromResourceBundleOrDefault(
            FacesMessage.SEVERITY_WARN, 
            "org.jboss.seam.ProcessEnded", 
            "Process #0 already ended", 
            key
         );
   }
   
   @Override
   protected void processNotFound(String key)
   {
      FacesMessages.instance().addFromResourceBundleOrDefault(
            FacesMessage.SEVERITY_WARN, 
            "org.jboss.seam.ProcessNotFound", 
            "Process #0 not found", 
            key
         );
   }
   
}
