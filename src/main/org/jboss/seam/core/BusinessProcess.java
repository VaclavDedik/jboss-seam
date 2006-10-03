package org.jboss.seam.core;

import static org.jboss.seam.InterceptionType.NEVER;

import java.io.Serializable;

import javax.faces.application.FacesMessage;

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
   
   public boolean hasCurrentProcess()
   {
      return processId!=null;
   }

   public boolean hasCurrentTask()
   {
      return taskId!=null;
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
      
      Events.instance().raiseEvent("org.jboss.seam.createProcess." + processDefinitionName);
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
      
      Events.instance().raiseEvent("org.jboss.seam.startTask." + task.getTask().getName());
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
      
      setTaskId(null); //TODO: do I really need this???!
      
      Events.instance().raiseEvent("org.jboss.seam.endTask." + task.getTask().getName());

      ProcessInstance process = org.jboss.seam.core.ProcessInstance.instance();
      if ( process.hasEnded() )
      {
         Events.instance().raiseEvent("org.jboss.seam.endProcess." + process.getProcessDefinition().getName());
      }
   }
   
   public void transition(String transitionName)
   {
      ProcessInstance process = org.jboss.seam.core.ProcessInstance.instance();
      process.signal(transitionName);
      if ( process.hasEnded() )
      {
         Events.instance().raiseEvent("org.jboss.seam.endProcess." + process.getProcessDefinition().getName());
      }
   }
   
   public boolean initTask(Long taskId)
   {
      setTaskId(taskId);
      TaskInstance task = org.jboss.seam.core.TaskInstance.instance();
      if (task==null)
      {
         taskNotFound(taskId);
         return false;
      }
      else if ( task.hasEnded() )
      {
         taskEnded(taskId);
         return false;
      }
      else
      {
         setProcessId( task.getTaskMgmtInstance().getProcessInstance().getId() );
         Events.instance().raiseEvent("org.jboss.seam.initTask." + task.getTask().getName());
         return true;
      }
      
   }
   
   public boolean initProcess(Long processId)
   {
      setProcessId(processId);
      ProcessInstance process = org.jboss.seam.core.ProcessInstance.instance();
      if ( process==null )
      {
         processNotFound(processId);
         return false;
      }
      else if ( process.hasEnded() )
      {
         processEnded(processId);
         return false;
      }
      else
      {
         Events.instance().raiseEvent("org.jboss.seam.initProcess." + process.getProcessDefinition().getName());
         return true;
      }
   }

   public boolean checkTask()
   {
      if ( !hasCurrentTask() )
      {
         taskNotFound(taskId);
         return false;
      }
      else if ( org.jboss.seam.core.TaskInstance.instance().hasEnded() )
      {
         taskEnded(taskId);
         return false;
      }
      else
      {
         return true;
      }
   }

   private void taskNotFound(Long taskId)
   {
      FacesMessages.instance().addFromResourceBundle(
            FacesMessage.SEVERITY_WARN, 
            "org.jboss.seam.TaskNotFound", 
            "Task #0 not found", 
            taskId
         );
   }

   private void taskEnded(Long taskId)
   {
      FacesMessages.instance().addFromResourceBundle(
            FacesMessage.SEVERITY_WARN, 
            "org.jboss.seam.TaskEnded", 
            "Task #0 already ended", 
            taskId
         );
   }

   private void processEnded(Long processId)
   {
      FacesMessages.instance().addFromResourceBundle(
            FacesMessage.SEVERITY_WARN, 
            "org.jboss.seam.ProcessEnded", 
            "Process #0 already ended", 
            processId
         );
   }

   private void processNotFound(Long processId)
   {
      FacesMessages.instance().addFromResourceBundle(
            FacesMessage.SEVERITY_WARN, 
            "org.jboss.seam.ProcessNotFound", 
            "Process #0 not found", 
            processId
         );
   }

}
