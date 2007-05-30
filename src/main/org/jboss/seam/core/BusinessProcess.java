package org.jboss.seam.core;

import static org.jboss.seam.InterceptionType.NEVER;
import static org.jboss.seam.annotations.Install.BUILT_IN;
import java.io.Serializable;
import javax.faces.application.FacesMessage;
import org.jboss.seam.Component;
import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.Install;
import org.jboss.seam.annotations.Intercept;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.contexts.Contexts;
import org.jbpm.graph.exe.ProcessInstance;
import org.jbpm.taskmgmt.exe.TaskInstance;

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
@Install(dependencies="org.jboss.seam.core.jbpm", precedence=BUILT_IN)
public class BusinessProcess extends AbstractMutable implements Serializable 
{

   private static final long serialVersionUID = 4722350870845851070L;
   private Long processId;
   private Long taskId;
   
   public static BusinessProcess instance()
   {
      if ( !Contexts.isConversationContextActive() )
      {
         throw new IllegalStateException("No active conversation context");
      }
      return (BusinessProcess) Component.getInstance(BusinessProcess.class, ScopeType.CONVERSATION);
   }
   
   /**
    * Is there a process instance associated with 
    * the current conversation?
    */
   public boolean hasCurrentProcess()
   {
      return processId!=null;
   }
   
   /**
    * Is there a process instance that has not ended 
    * associated with the current conversation?
    */
   public boolean hasActiveProcess()
   {
      return hasCurrentProcess() && 
            !org.jboss.seam.core.ProcessInstance.instance().hasEnded();
   }
   /**
    * Is there a task instance associated with 
    * the current conversation?
    */
   public boolean hasCurrentTask()
   {
      return taskId!=null;
   }
   /**
    * The jBPM process instance id associated with
    * the current conversation.
    */
   public Long getProcessId() 
   {
      return processId;
   }
   /**
    * Set the process instance id, without validating
    * that the process instance actually exists.
    */
   public void setProcessId(Long processId) 
   {
      setDirty(this.processId, processId);
      this.processId = processId;
   }
   /**
    * The jBPM task instance id associated with
    * the current conversation.
    */
   public Long getTaskId() 
   {
      return taskId;
   }
   /**
    * Set the task instance id, without validating
    * that the task instance actually exists.
    */
   public void setTaskId(Long taskId) 
   {
      setDirty(this.taskId, taskId);
      this.taskId = taskId;
   }
   /**
    * Create a process instance and associate it with the
    * current conversation.
    * 
    * @param processDefinitionName the jBPM process definition name
    */
   public void createProcess(String processDefinitionName)
   {
      ProcessInstance process = ManagedJbpmContext.instance().newProcessInstanceForUpdate(processDefinitionName);
      setProcessId( process.getId() );
      // need to set process variables before the signal
      Contexts.getBusinessProcessContext().flush();
      process.signal();
      
      Events.instance().raiseEvent("org.jboss.seam.createProcess." + processDefinitionName);
   }
   /**
    * Start the current task, using the current actor id
    * 
    * @see Actor
    */
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
   /**
    * End the current task, via the given transition. If no transition name 
    * is given, check the Transition component for a transition, or use the 
    * default transition.
    * 
    * @param transitionName the jBPM transition name, or null
    */
   public void endTask(String transitionName)
   {
      TaskInstance task = org.jboss.seam.core.TaskInstance.instance();
      if (task==null)
      {
         throw new IllegalStateException( "no task instance associated with context" );
      }
      
      if ( transitionName==null || "".equals(transitionName) )
      {
         transitionName = Transition.instance().getName();
      }
      
      if (transitionName==null)
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
   
   /**
    * Signal the given transition for the current process instance.
    * 
    * @param transitionName the jBPM transition name 
    */
   public void transition(String transitionName)
   {
      ProcessInstance process = org.jboss.seam.core.ProcessInstance.instance();
      process.signal(transitionName);
      if ( process.hasEnded() )
      {
         Events.instance().raiseEvent("org.jboss.seam.endProcess." + process.getProcessDefinition().getName());
      }
   }
   
   /**
    * Associate the task instance with the given id with the current
    * conversation.
    * 
    * @param taskId the jBPM task instance id
    * @return true if the task was found and was not ended
    */
   public boolean resumeTask(Long taskId)
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
   
   /**
    * Associate the process instance with the given id with the 
    * current conversation.
    * 
    * @param processId the jBPM process instance id
    * @return true if the process was found and was not ended
    */
   public boolean resumeProcess(Long processId)
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
   /**
    * Check that the task currently associated with the conversation
    * exists and has not ended.
    * 
    * @return true if the task exists and was not ended
    */
   public boolean validateTask()
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
   protected void taskNotFound(Long taskId)
   {
      FacesMessages.instance().addFromResourceBundleOrDefault(
            FacesMessage.SEVERITY_WARN, 
            "org.jboss.seam.TaskNotFound", 
            "Task #0 not found", 
            taskId
         );
   }
   protected void taskEnded(Long taskId)
   {
      FacesMessages.instance().addFromResourceBundleOrDefault(
            FacesMessage.SEVERITY_WARN, 
            "org.jboss.seam.TaskEnded", 
            "Task #0 already ended", 
            taskId
         );
   }
   protected void processEnded(Long processId)
   {
      FacesMessages.instance().addFromResourceBundleOrDefault(
            FacesMessage.SEVERITY_WARN, 
            "org.jboss.seam.ProcessEnded", 
            "Process #0 already ended", 
            processId
         );
   }
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
   public String toString()
   {
      return "BusinessProcess(processId=" + processId + ",taskId=" + taskId + ")";
   }
}
