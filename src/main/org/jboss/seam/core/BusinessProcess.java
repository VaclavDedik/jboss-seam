package org.jboss.seam.core;

import static org.jboss.seam.InterceptionType.NEVER;

import java.io.Serializable;

import org.jboss.seam.Component;
import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.Intercept;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.contexts.Contexts;
import org.jbpm.JbpmContext;
import org.jbpm.graph.def.ProcessDefinition;
import org.jbpm.graph.exe.ProcessInstance;
import org.jbpm.taskmgmt.exe.TaskInstance;

/**
 * Holds the task and process ids for the current conversation
 * 
 * @author Gavin King
 *
 */
@Scope(ScopeType.CONVERSATION)
@Name("businessProcess")
@Intercept(NEVER)
public class BusinessProcess implements Serializable {
   
   private Long processId;
   private Long taskId;

   public static BusinessProcess instance()
   {
      if ( !Contexts.isConversationContextActive() )
      {
         throw new IllegalStateException("No active conversation context");
      }
      return (BusinessProcess) Component.getInstance(BusinessProcess.class, ScopeType.CONVERSATION, true);
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

   public void createProcess(String processDefinitionName)
   {
      JbpmContext jbpmContext = ManagedJbpmContext.instance();
      
      ProcessDefinition pd = jbpmContext.getGraphSession().findLatestProcessDefinition(processDefinitionName);
      if ( pd == null )
      {
         throw new IllegalArgumentException( "Unknown process definition: " + processDefinitionName );
      }
      
      ProcessInstance process = pd.createProcessInstance();
      jbpmContext.save(process);
      setProcessId( process.getId() );
      // need to set process variables before the signal
      Contexts.getBusinessProcessContext().flush();
      process.signal();
   }

   public void startTask()
   {
      String actorId = Actor.instance().getId();
      TaskInstance task = org.jboss.seam.core.TaskInstance.instance();
      if ( actorId != null )
      {
         task.start(actorId);
      }
      else
      {
         task.start();
      }
   }

   public void endTask(String transitionName)
   {
      TaskInstance task = org.jboss.seam.core.TaskInstance.instance();
      if ( task == null )
      {
         throw new IllegalStateException( "no task instance associated with context" );
      }
      
      if ( "".equals(transitionName) )
      {
         transitionName = Transition.instance().getName();
      }
      
      if ( transitionName == null )
      {
         task.end();
      }
      else
      {
         task.end(transitionName);
      }
      
      setTaskId(null);
   }
   
}
