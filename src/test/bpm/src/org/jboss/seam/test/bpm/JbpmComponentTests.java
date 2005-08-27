package org.jboss.seam.test.bpm;

import java.util.Map;
import java.util.Collection;

import org.jboss.seam.contexts.Contexts;
import org.jboss.seam.util.Strings;
import org.jboss.seam.mock.SeamTest;
import org.jboss.seam.Seam;
import org.jboss.seam.Component;
import org.jboss.seam.core.Init;
import org.jboss.seam.core.JbpmProcess;
import org.jboss.seam.core.Manager;
import org.jboss.seam.core.JbpmTask;
import org.jboss.seam.interceptors.BusinessProcessInterceptor;
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
      UserRequestScript userRequest = new UserRequestScript();
      userRequest.run();

      // An admin works on the registration request...
      System.out.println( "***********************************************" );
      System.out.println( "***********************************************" );
      System.out.println( "starting admin assignment request" );
      System.out.println( "***********************************************" );
      System.out.println( "***********************************************" );
      AdminAssignmentScript adminAssignment = new AdminAssignmentScript( userRequest.taskId );
      adminAssignment.run();

      // admin approves the request...
      System.out.println( "***********************************************" );
      System.out.println( "***********************************************" );
      System.out.println( "starting admin approval request" );
      System.out.println( "***********************************************" );
      System.out.println( "***********************************************" );
      new CompletionScript( userRequest.taskId ).run();
   }


   @Override
   public void initServletContext(Map initParams)
   {
      initParams.put( Init.PERSISTENCE_UNIT_NAMES, "bpmUserDatabase" );

      String classNames = Strings.toString( User.class, RegistrationHandlerBean.class, ApprovalHandlerBean.class );
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
      RegistrationHandler registrationHandlerBean;

      @Override
      protected void applyRequestValues()
      {
         User user = new User();
         user.setUsername( "steve" );
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
//         ProcessInstance process = ( ProcessInstance ) Contexts.getEventContext().get( Seam.getComponentName( JbpmProcess.class ) );
         ProcessInstance process = ( ProcessInstance ) Component.getInstance( JbpmProcess.class, false );
         assert process != null;
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
         // TODO : need to add this logic back into the interceptor b4 interception
      }

      @Override
      protected void invokeApplication()
      {
         String name = Seam.getComponentName( ApprovalHandlerBean.class );
         Contexts.getEventContext().set( "taskId", taskId );
//         Manager.instance().setTaskId( taskId );
         ApprovalHandler approvalHandler = ( ApprovalHandler ) Contexts.lookupInAllContexts( name );
         approvalHandler.beginApproval();
      }

      @Override
      protected void renderResponse()
      {
//         TaskInstance task = ( TaskInstance ) Contexts.getEventContext().get( Seam.getComponentName( JbpmTask.class ) );
         TaskInstance task = ( TaskInstance ) Component.getInstance( JbpmTask.class, false );
         assert task != null;
         assert task.getStart() != null;
         assert Manager.instance().isLongRunningConversation();
      }
   }

   private class CompletionScript extends Script
   {
      public Long taskId;

      public CompletionScript(Long taskId)
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
         ///////////////////////////////////////////////////////////////////////
         // For some reason manager as part of conversation not being restored
         // properly here...
         Manager.instance().setTaskId( taskId );
         ///////////////////////////////////////////////////////////////////////
         String name = Seam.getComponentName( ApprovalHandlerBean.class );
         ApprovalHandler approvalHandler = ( ApprovalHandler ) Contexts.lookupInAllContexts( name );
         approvalHandler.approve();
      }

      @Override
      protected void renderResponse()
      {
         TaskInstance task = ( TaskInstance ) Component.getInstance( JbpmTask.class, false );
         assert task != null;
         assert task.hasEnded();
         assert !Manager.instance().isLongRunningConversation();
      }
   }
}
