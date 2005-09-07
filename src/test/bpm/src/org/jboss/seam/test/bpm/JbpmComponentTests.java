package org.jboss.seam.test.bpm;

import java.util.Map;
import java.util.Collection;

import org.jboss.seam.contexts.Contexts;
import org.jboss.seam.util.Strings;
import org.jboss.seam.mock.SeamTest;
import org.jboss.seam.Seam;
import org.jboss.seam.core.Init;
import org.jboss.seam.core.Manager;
import org.jboss.logging.Logger;
import org.hibernate.cfg.Configuration;
import org.jbpm.db.JbpmSessionFactory;
import org.jbpm.db.JbpmSession;
import org.jbpm.graph.def.ProcessDefinition;
import org.jbpm.graph.exe.ProcessInstance;
import org.jbpm.taskmgmt.exe.TaskInstance;
import org.testng.annotations.Test;

import javax.naming.InitialContext;

public class JbpmComponentTests extends SeamTest
{

   private static final Logger log = Logger.getLogger( JbpmComponentTests.class );

   private static final String JBPM_SF_NAME = "/JbpmSessionFactory";
   private JbpmSessionFactory factory;

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
      System.out.println( "starting admin assignment request" );
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
            Contexts.getStatelessContext().set( "processId", userRequest.processId );
         }

         protected void invokeApplication() throws Exception
         {
            super.invokeApplication();    //todo: implement overriden method body
         }

         protected void renderResponse() throws Exception
         {
            super.renderResponse();    //todo: implement overriden method body
         }
      }.run();
   }

   @Override
   public void initServletContext(Map initParams)
   {
      initParams.put( Init.PERSISTENCE_UNIT_NAMES, "bpmUserDatabase" );

      String classNames = Strings.toString(
              User.class,
              RegistrationHandlerBean.class,
              ApprovalHandlerBean.class,
              ProcessMaintenanceBean.class
      );
      initParams.put( Init.COMPONENT_CLASS_NAMES, classNames );

      initParams.put( Init.JBPM_SESSION_FACTORY_NAME, JBPM_SF_NAME );
   }


   @Override
   protected void buildJbpm()
   {
      factory = buildJbpmSessionFactory();
      initializeJbpmSessionFactory( factory );

      InitialContext ctx = null;
      try
      {
         new InitialContext().bind( JBPM_SF_NAME, factory );
      }
      catch ( Throwable t )
      {
         throw new RuntimeException( t );
      }
      finally
      {
         release( ctx );
      }
   }

   @Override
   protected void releaseJbpm()
   {
      InitialContext ctx = null;
      try
      {
         ctx = new InitialContext();
         factory.getSessionFactory().close();
         ctx.unbind( JBPM_SF_NAME );
      }
      catch ( Throwable t )
      {
         throw new RuntimeException( t );
      }
      finally
      {
         release( ctx );
      }
   }

   private void release(InitialContext ctx)
   {
      if ( ctx != null )
      {
         try
         {
            ctx.close();
         }
         catch ( Throwable ignore )
         {
         }
      }
   }

   private void initializeJbpmSessionFactory(JbpmSessionFactory factory)
   {
      JbpmSession jbpmSession = factory.openJbpmSessionAndBeginTransaction();
      try
      {
         ProcessDefinition processDefinition = ProcessDefinition.parseXmlString( MY_PROCESS_DEF );
         jbpmSession.getGraphSession().saveProcessDefinition( processDefinition );
      }
      finally
      {
         jbpmSession.commitTransactionAndClose();
      }
      System.out.println( "*************************************************" );
      System.out.println( "ProcessDefinition saved!!!!" );
      System.out.println( "*************************************************" );
   }

   private JbpmSessionFactory buildJbpmSessionFactory()
   {
      try
      {
         Configuration cfg = new Configuration();
         cfg.getProperties().clear();
         cfg.configure( "/org/jboss/seam/test/bpm/hibernate.cfg.xml" );
         return JbpmSessionFactory.buildJbpmSessionFactory( cfg );
      }
      catch ( Throwable t )
      {
         log.error( "Error building jbpm session factory", t );
         throw new RuntimeException( "Error building jbpm session factory" );
      }
   }

   private static final String MY_PROCESS_DEF =
           "<process-definition name=\"UserRegistration\">" +
                   "  <start-state>" +
                   "    <transition to=\"pending\">" +
                   "      <action class='org.jboss.seam.test.bpm.JbpmTransitionListener' />" +
                   "    </transition>" +
                   "  </start-state>" +
                   "  <task-node name=\"pending\">" +
                   "    <task name=\"review\"/>" +
                   "    <transition name=\"approve\" to=\"complete\">" +
                   "      <action class='org.jboss.seam.test.bpm.JbpmTransitionListener' />" +
                   "    </transition>" +
                   "    <transition name=\"deny\" to=\"complete\">" +
                   "      <action class='org.jboss.seam.test.bpm.JbpmTransitionListener' />" +
                   "    </transition>" +
                   "  </task-node>" +
                   "  <end-state name=\"complete\"></end-state>" +
                   "</process-definition>";


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
         String name = Seam.getComponentName( RegistrationHandlerBean.class );
         registrationHandlerBean = ( RegistrationHandler ) Contexts.lookupInAllContexts( name );
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
         String name = Seam.getComponentName( ApprovalHandlerBean.class );
         Contexts.getEventContext().set( "taskId", taskId );
         ApprovalHandler approvalHandler = ( ApprovalHandler ) Contexts.lookupInAllContexts( name );
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
         ///////////////////////////////////////////////////////////////////////
         // For some reason manager as part of conversation not being restored
         // properly here...
//         Manager.instance().setTaskId( taskId );
//         Manager.instance().setTaskName( "task" );
         ///////////////////////////////////////////////////////////////////////
         String name = Seam.getComponentName( ApprovalHandlerBean.class );
         ApprovalHandler approvalHandler = ( ApprovalHandler ) Contexts.lookupInAllContexts( name );
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
