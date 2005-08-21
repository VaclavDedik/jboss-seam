/*
 * JBoss, Home of Professional Open Source
 *
 * Distributable under LGPL license.
 * See terms of license at gnu.org.
 */
package org.jboss.seam.jsf;

import static javax.faces.event.PhaseId.ANY_PHASE;
import static javax.faces.event.PhaseId.APPLY_REQUEST_VALUES;
import static javax.faces.event.PhaseId.INVOKE_APPLICATION;
import static javax.faces.event.PhaseId.PROCESS_VALIDATIONS;
import static javax.faces.event.PhaseId.RENDER_RESPONSE;
import static javax.faces.event.PhaseId.RESTORE_VIEW;
import static javax.faces.event.PhaseId.UPDATE_MODEL_VALUES;

import java.util.Map;

import javax.faces.event.PhaseEvent;
import javax.faces.event.PhaseId;
import javax.faces.event.PhaseListener;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.jboss.logging.Logger;
import org.jboss.seam.contexts.BusinessProcessContext;
import org.jboss.seam.contexts.Context;
import org.jboss.seam.contexts.Contexts;
import org.jboss.seam.contexts.ConversationContext;

/**
 * Manages the thread/context associations throught the
 * lifecycle of a JSF request.
 *
 * @author Gavin King
 */
public class SeamPhaseListener implements PhaseListener
{
   
   private static final String CONVERSATION_ID = "org.jboss.seam.conversationId";
   private static final String JBPM_TASK_ID = "org.jboss.seam.jbpm.taskId";
   private static final String JBPM_PROCESS_ID = "org.jboss.seam.jbpm.processId";

   private static Logger log = Logger.getLogger(SeamPhaseListener.class);

   public PhaseId getPhaseId()
   {
      return ANY_PHASE;
   }

   public void beforePhase(PhaseEvent event)
   {
      log.trace( "before phase [" + event.getPhaseId() + "]" );

      if (event.getPhaseId() == RESTORE_VIEW)
      {
         beginWebRequest(event);
      }
      else if (event.getPhaseId() == RENDER_RESPONSE)
      {
         storeAnyConversationContext(event);
         storeAnyBusinessProcessContext();
      }
      else if (event.getPhaseId() == INVOKE_APPLICATION)
      {
         Contexts.setProcessing(true);
         log.info("About to invoke application");
      }
      else if (event.getPhaseId() == UPDATE_MODEL_VALUES)
      {
         log.info("About to update model values");
      }
      else if (event.getPhaseId() == PROCESS_VALIDATIONS)
      {
         log.info("About to process validations");
      }
      else if (event.getPhaseId() == APPLY_REQUEST_VALUES)
      {
         log.info("About to apply request values");
      }
   }

   public void afterPhase(PhaseEvent event)
   {
	   log.trace( "after phase [" + event.getPhaseId() + "]" );

      if (event.getPhaseId() == RESTORE_VIEW)
      {
         restoreAnyConversationContext(event);
         restoreAnyBusinessProcessContext();
      }
      else if (event.getPhaseId() == RENDER_RESPONSE) {
         endWebRequest(event);
      }
      else if (event.getPhaseId() == INVOKE_APPLICATION)
      {
         log.info("After invoke application");
         Contexts.setProcessing(false);
      }
      else if (event.getPhaseId() == UPDATE_MODEL_VALUES)
      {
         log.info("After update model values");
      }
      else if (event.getPhaseId() == PROCESS_VALIDATIONS)
      {
         log.info("After process validations");
      }
      else if (event.getPhaseId() == APPLY_REQUEST_VALUES)
      {
         log.info("After apply request values");
      }
   }

   private static void beginWebRequest(PhaseEvent event)
   {
      Contexts.beginWebRequest( getRequest(event) );
      Contexts.setProcessing(false);
      log.info("About to restore view");
   }

   private static void endWebRequest(PhaseEvent event)
   {
      log.info("After render response, destroying contexts");
      HttpServletRequest request = getRequest(event);
      endWebRequest(request);
   }

   static void endWebRequest(HttpServletRequest request)
   {
      if ( Contexts.isEventContextActive() )
      {
         Contexts.destroy( Contexts.getEventContext() );
      }
      if ( !Contexts.isLongRunningConversation() && Contexts.isConversationContextActive() )
      {
         Contexts.destroy( Contexts.getConversationContext() );
      }
      Contexts.endWebRequest( request );
   }

   private static void restoreAnyConversationContext(PhaseEvent event)
   {
      String conversationId = (String) getAttributes(event).get(CONVERSATION_ID);
      Context conversationContext;
      if (conversationId!=null)
      {
         //TODO: how can we detect a missing conversation?
         //      (happens after a server restart)
         Contexts.setLongRunningConversation(true);
         conversationContext = new ConversationContext( getSession(event), conversationId );
         log.info("Restored conversation with id: " + conversationId);
      }
      else
      {
         log.info("No stored conversation");
         conversationContext = new ConversationContext( getSession(event) );
         Contexts.setLongRunningConversation(false);
      }
      
      Contexts.setConversationContext(conversationContext);
      log.info("After restore view, conversation context: " + Contexts.getConversationContext());
   }

   private static void storeAnyConversationContext(PhaseEvent event)
   {      
      Context conversationContext = Contexts.getConversationContext();
      log.info("Before render response, conversation context: " + conversationContext);
      if ( conversationContext==null )
      {
         log.info("No active conversation context");
      }
      else if ( Contexts.isLongRunningConversation() ) 
      {
         Object conversationId = ConversationContext.getId(conversationContext);
         log.info("Storing conversation state: " + conversationId);
         getAttributes(event).put(CONVERSATION_ID, conversationId);
      }
      else 
      {
         log.info("Discarding conversation state");
         getAttributes(event).remove(CONVERSATION_ID);
      }
   }

   private static HttpServletRequest getRequest(PhaseEvent event)
   {
      return (HttpServletRequest) event.getFacesContext().getExternalContext().getRequest();
   }

   private static HttpSession getSession(PhaseEvent event)
   {
      return (HttpSession) event.getFacesContext().getExternalContext().getSession(true);
   }

   private static Map getAttributes(PhaseEvent event)
   {
      return event.getFacesContext().getViewRoot().getAttributes();
   }

	private static void storeAnyBusinessProcessContext() {
		Context conversation = Contexts.getConversationContext();

		if ( !Contexts.isBusinessProcessContextActive() ) {
			conversation.remove( JBPM_TASK_ID );
			conversation.remove( JBPM_PROCESS_ID );
			return;
		}

		BusinessProcessContext jbpmContext = ( BusinessProcessContext ) Contexts.getBusinessProcessContext();

		if ( jbpmContext.getProcessInstance().hasEnded() ||
		        jbpmContext.getProcessInstance().isTerminatedImplicitly() ) {
			conversation.remove( JBPM_TASK_ID );
			conversation.remove( JBPM_PROCESS_ID );
			return;
		}

		if ( jbpmContext.getTaskInstance() != null ) {
			conversation.set( JBPM_TASK_ID, jbpmContext.getTaskInstance().getId() );
		}

		if ( jbpmContext.getProcessInstance() != null ) {
			conversation.set( JBPM_PROCESS_ID, jbpmContext.getProcessInstance().getId() );
		}
	}

	private static void restoreAnyBusinessProcessContext() {
		Context conversation = Contexts.getConversationContext();
		Long taskId = ( Long ) conversation.get( JBPM_TASK_ID );
		Long processId = ( Long ) conversation.get( JBPM_PROCESS_ID );

		// task is the more specific, so try that first...
		if ( taskId != null ) {
			Contexts.beginBusinessProcessContextViaTask( taskId );
		}
		else if ( processId != null ) {
			Contexts.beginBusinessProcessContextViaProcess( processId );
		}
	}

}
