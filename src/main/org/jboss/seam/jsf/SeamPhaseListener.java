/*
 * JBoss, Home of Professional Open Source
 *
 * Distributable under LGPL license.
 * See terms of license at gnu.org.
 */
package org.jboss.seam.jsf;

import static javax.faces.event.PhaseId.*;


import java.util.HashMap;
import java.util.Map;

import javax.faces.event.PhaseEvent;
import javax.faces.event.PhaseListener;
import javax.faces.event.PhaseId;
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
   private static final String CONVERSATIONS = "org.jboss.seam.Conversations";
   
   public static final String CONVERSATION_ID = "org.jboss.seam.conversationId";
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
         endWebRequest();
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

   private void beginWebRequest(PhaseEvent event)
   {
      Contexts.beginWebRequest( getRequest(event) );
      Contexts.setProcessing(false);
      log.info("About to restore view");
   }

   private void endWebRequest()
   {
      log.info("After render response, destroying contexts");
      Contexts.destroy( Contexts.getEventContext() );
      if ( !Contexts.isLongRunningConversation() )
      {
         Contexts.destroy( Contexts.getConversationContext() );
      }
      Contexts.endWebRequest();
   }

   private void restoreAnyConversationContext(PhaseEvent event)
   {
      Object conversationId = getAttributes(event).get(CONVERSATION_ID);
      Context conversationContext;
      if (conversationId!=null)
      {
         conversationContext = (Context) getConversations(event).get(conversationId);
         if (conversationContext==null)
         {
            //this can happen after server restart, so do something better
            //(perhaps forward to a special outcome?)
            throw new IllegalStateException("Missing conversation: " + conversationId);
         }
         Contexts.setLongRunningConversation(true);
         log.info("Restored conversation with id: " + conversationId);
      }
      else
      {
         log.info("No stored conversation");
         conversationContext = new ConversationContext();
         Contexts.setLongRunningConversation(false);
      }
      
      log.info("After restore view, conversation context: " + conversationContext);
      Contexts.setConversationContext(conversationContext);
   }

   private void storeAnyConversationContext(PhaseEvent event)
   {      
      Context conversationContext = Contexts.getConversationContext();
      log.info("Before render response, conversation context: " + conversationContext);
      if ( conversationContext==null )
      {
         log.info("No active conversation context");
      }
      else if ( Contexts.isLongRunningConversation() ) 
      {
         Object conversationId = conversationContext.get(CONVERSATION_ID);
         log.info("Storing conversation state: " + conversationId);
         getAttributes(event).put(CONVERSATION_ID, conversationId);
         getConversations(event).put(conversationId, conversationContext);
      }
      else 
      {
         log.info("Discarding conversation state");
         getAttributes(event).remove(CONVERSATION_ID);
      }
   }

   private Map getConversations(PhaseEvent event)
   {
      Map result = (Map) getSession(event).getAttribute(CONVERSATIONS);
      if (result==null) 
      {
         //TODO: minor issue; not threadsafe....
         result = new HashMap();
         getSession(event).setAttribute(CONVERSATIONS, result);
      }
      return result;
   }

   private HttpSession getSession(PhaseEvent event)
   {
      return (HttpSession) event.getFacesContext().getExternalContext().getSession(true);
   }

   private HttpServletRequest getRequest(PhaseEvent event)
   {
      return (HttpServletRequest) event.getFacesContext().getExternalContext().getRequest();
   }

   private Map getAttributes(PhaseEvent event)
   {
      return event.getFacesContext().getViewRoot().getAttributes();
   }

	private void storeAnyBusinessProcessContext() {
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

	private void restoreAnyBusinessProcessContext() {
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
