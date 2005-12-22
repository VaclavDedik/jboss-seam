package org.jboss.seam.core;

import static org.jboss.seam.InterceptionType.NEVER;

import java.io.Serializable;

import org.jboss.seam.Component;
import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.Intercept;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.contexts.Contexts;

/**
 * Holds the task and process ids for the current conversation
 * 
 * @author Gavin King
 *
 */
@Scope(ScopeType.CONVERSATION)
@Name("org.jboss.seam.core.process")
@Intercept(NEVER)
public class Process implements Serializable {
   
   private Long processId;
   private Long taskId;

   public static Process instance()
   {
      if ( !Contexts.isConversationContextActive() )
      {
         throw new IllegalStateException("No active conversation context");
      }
      return (Process) Component.getInstance(Process.class, true);
   }

   public Long getProcessId() {
      return processId;
   }

   public void setProcessId(Long processId) {
      this.processId = processId;
   }

   public Long getTaskId() {
      return taskId;
   }

   public void setTaskId(Long taskId) {
      this.taskId = taskId;
   }
   
}
