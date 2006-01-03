package org.jboss.seam.core;

import static org.jboss.seam.InterceptionType.NEVER;

import org.jboss.seam.Component;
import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.Intercept;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.contexts.Contexts;
import org.jbpm.graph.exe.ProcessInstance;

/**
 * A Seam component that manages the current
 * jBPM ProcessInstance used for pageflow.
 * 
 * @author Gavin King
 */
@Scope(ScopeType.CONVERSATION)
@Name("pageflow")
@Intercept(NEVER)
public class Pageflow {
   
   private ProcessInstance processInstance;

   public ProcessInstance getProcessInstance() {
      return processInstance;
   }

   public void setProcessInstance(ProcessInstance processInstance) {
      this.processInstance = processInstance;
   }
   
   public static Pageflow instance()
   {
      if ( !Contexts.isConversationContextActive() )
      {
         throw new IllegalStateException("No active conversation context");
      }
      return (Pageflow) Component.getInstance(Pageflow.class, ScopeType.CONVERSATION, true);
   }

}
