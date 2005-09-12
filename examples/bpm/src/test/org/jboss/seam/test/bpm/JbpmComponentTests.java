package org.jboss.seam.test.bpm;

import java.util.Map;
import java.util.Collection;

import org.jboss.seam.contexts.Contexts;
import org.jboss.seam.util.Strings;
import org.jboss.seam.mock.SeamTest;
import org.jboss.seam.Component;
import org.jboss.seam.example.bpm.JbpmInitializer;
import org.jboss.seam.example.bpm.User;
import org.jboss.seam.example.bpm.ProcessMaintenanceBean;
import org.jboss.seam.example.bpm.ApprovalHandlerBean;
import org.jboss.seam.example.bpm.RegistrationHandlerBean;
import org.jboss.seam.example.bpm.RegistrationHandler;
import org.jboss.seam.example.bpm.ApprovalHandler;
import org.jboss.seam.example.bpm.Group;
import org.jboss.seam.example.bpm.ProcessMaintenance;
import org.jboss.seam.core.Init;
import org.jboss.seam.core.Manager;
import org.jboss.seam.core.Ejb;
import org.jbpm.graph.exe.ProcessInstance;
import org.jbpm.taskmgmt.exe.TaskInstance;
import org.testng.annotations.Test;


public class JbpmComponentTests extends SeamTest
{
   private JbpmInitializer jbpmInitializer;

   @Override
   protected void prepareApplication()
   {
      jbpmInitializer = new JbpmInitializer();
      jbpmInitializer.initialize();
   }

   @Override
   protected void cleanupApplication()
   {
      try
      {
         jbpmInitializer.release();
      }
      catch( Throwable ignore )
      {
         // ignore current issue with "out of order" cleanup
      }
      jbpmInitializer = null;
   }

   @Override
   public void initServletContext(Map initParams)
   {
      initParams.put( Init.MANAGED_PERSISTENCE_CONTEXTS, "bpmUserDatabase" );

      String classNames = Strings.toString(
              Ejb.class,
              User.class,
              Group.class,
              RegistrationHandlerBean.class,
              ApprovalHandlerBean.class,
              ProcessMaintenanceBean.class
      );
      initParams.put( Init.COMPONENT_CLASSES, classNames );

      initParams.put( Init.JBPM_SESSION_FACTORY_NAME, JbpmInitializer.JBPM_SF_NAME );
   }


   @Test
   public void testUserRegistration() throws Exception
   {
      // A user requests registration...
      System.out.println( "***********************************************" );
      System.out.println( "***********************************************" );
      System.out.println( "starting user registration request" );
      System.out.println( "***********************************************" );
      System.out.println( "***********************************************" );
      UserRequestScript userRequest = new UserRequestScript( "steve" );
      userRequest.run();

      // An admin works on the registration request...
      System.out.println( "***********************************************" );
      System.out.println( "***********************************************" );
      System.out.println( "starting admin identity request" );
      System.out.println( "***********************************************" );
      System.out.println( "***********************************************" );
      AdminAssignmentScript adminAssignment = new AdminAssignmentScript( userRequest.taskId );
      String conversationId = adminAssignment.run();

      // admin approves the request...
      System.out.println( "***********************************************" );
      System.out.println( "***********************************************" );
      System.out.println( "starting admin approval request" );
      System.out.println( "***********************************************" );
      System.out.println( "***********************************************" );
      new CompletionScript( conversationId, userRequest.taskId ).run();
   }

   @Test
   public void testProcessInjection() throws Exception
   {
      System.out.println( "***********************************************" );
      System.out.println( "***********************************************" );
      System.out.println( "starting user registration request" );
      System.out.println( "***********************************************" );
      System.out.println( "***********************************************" );
      final UserRequestScript userRequest = new UserRequestScript( "steve2" );
      userRequest.run();

      System.out.println( "***********************************************" );
      System.out.println( "***********************************************" );
      System.out.println( "starting process cancellation request" );
      System.out.println( "***********************************************" );
      System.out.println( "***********************************************" );
      new Script()
      {
         protected void applyRequestValues() throws Exception
         {
            Contexts.getEventContext().set( "processId", userRequest.processId );
         }

         protected void invokeApplication() throws Exception
         {
            String name = "processMaintenance";
            ProcessMaintenance processMaintenance = ( ProcessMaintenance ) Component.getInstance( name, true );
            processMaintenance.cancelProcess();
         }

         protected void renderResponse() throws Exception
         {
            ProcessInstance process = ( ProcessInstance ) Contexts.lookupInStatefulContexts( "process" );
            assert process != null;
            assert process.hasEnded();
         }
      }.run();
   }

   private class UserRequestScript extends Script
   {
      public Long taskId;
      public Long processId;
      public final String username;
      RegistrationHandler registrationHandlerBean;

      public UserRequestScript(String username)
      {
         this.username = username;
      }

      @Override
      protected void applyRequestValues()
      {
         User user = new User();
         user.setUsername( username );
         user.setPassword( "steve" );
         Contexts.getEventContext().set( "user", user );
      }

      @Override
      protected void updateModelValues() throws Exception
      {
      }

      @Override
      protected void invokeApplication()
      {
         String name = "registrationHandler"; //Seam.getComponentName( RegistrationHandlerBean.class );
         registrationHandlerBean = ( RegistrationHandler ) Component.getInstance( name, true );
         registrationHandlerBean.register();
      }

      @Override
      protected void renderResponse()
      {
         ProcessInstance process = ( ProcessInstance ) Contexts.lookupInAllContexts( "currentProcess" );
         assert process != null;
         processId = process.getId();
         // force the flush...
         Contexts.getBusinessProcessContext().flush();
         assert process.getContextInstance().hasVariable( "username" );
         Collection tasks = process.getTaskMgmtInstance().getTaskInstances();
         assert tasks != null && tasks.size() == 1;
         TaskInstance task = ( TaskInstance ) tasks.iterator().next();
         taskId = task.getId();
      }
   }

   private class AdminAssignmentScript extends Script
   {
      public Long taskId;

      public AdminAssignmentScript(Long taskId)
      {
         this.taskId = taskId;
      }

      @Override
      protected void applyRequestValues()
      {
      }

      @Override
      protected void invokeApplication()
      {
         Contexts.getEventContext().set( "taskId", taskId );
         String name = "approvalHandler"; //Seam.getComponentName( ApprovalHandlerBean.class );
         ApprovalHandler approvalHandler = ( ApprovalHandler ) Component.getInstance( name, true );
         approvalHandler.beginApproval();
      }

      @Override
      protected void renderResponse()
      {
         TaskInstance task = ( TaskInstance ) Contexts.lookupInAllContexts( "task" );
         assert task != null;
         assert task.getStart() != null;
         Manager manager = Manager.instance();
         assert manager.isLongRunningConversation();
         assert manager.getTaskId() == taskId;
         assert "task".equals( manager.getTaskName() );
         ProcessInstance process = task.getTaskMgmtInstance().getProcessInstance();
         assert process.getContextInstance().hasVariable( "username" );
      }
   }

   private class CompletionScript extends Script
   {
      public Long taskId;

      public CompletionScript(String conversationId, Long taskId)
      {
         super( conversationId );
         this.taskId = taskId;
      }

      @Override
      protected void applyRequestValues()
      {
      }

      @Override
      protected void invokeApplication()
      {
         String name = "approvalHandler"; //Seam.getComponentName( ApprovalHandlerBean.class );
         ApprovalHandler approvalHandler = ( ApprovalHandler ) Component.getInstance( name, true );
         approvalHandler.approve();
      }

      @Override
      protected void renderResponse()
      {
         TaskInstance task = ( TaskInstance ) Contexts.lookupInAllContexts( "task" );
         assert task != null;
         assert task.hasEnded();
         assert !Manager.instance().isLongRunningConversation();
      }
   }
}
