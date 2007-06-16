package org.jboss.seam.jbpm;

import org.jbpm.context.exe.ContextInstance;
import org.jbpm.graph.def.Action;
import org.jbpm.graph.exe.ExecutionContext;
import org.jbpm.graph.exe.Token;
import org.jbpm.instantiation.UserCodeInterceptor;
import org.jbpm.taskmgmt.def.AssignmentHandler;
import org.jbpm.taskmgmt.def.TaskControllerHandler;
import org.jbpm.taskmgmt.exe.Assignable;
import org.jbpm.taskmgmt.exe.TaskInstance;

public class SeamUserCodeInterceptor implements UserCodeInterceptor
{

   public void executeAction(Action action, ExecutionContext context) throws Exception
   {
      action.execute(context);
   }

   public void executeAssignment(AssignmentHandler handler, Assignable task, ExecutionContext context)
            throws Exception
   {
      handler.assign(task, context);
   }

   public void executeTaskControllerInitialization(TaskControllerHandler handler, TaskInstance task,
            ContextInstance context, Token token)
   {
      handler.initializeTaskVariables(task, context, token);
   }

   public void executeTaskControllerSubmission(TaskControllerHandler handler, TaskInstance task,
            ContextInstance context, Token token)
   {
      handler.submitTaskVariables(task, context, token);
   }

}
