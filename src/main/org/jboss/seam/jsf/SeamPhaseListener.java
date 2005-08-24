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
import javax.servlet.http.HttpSession;

import org.jboss.logging.Logger;
import org.jboss.seam.Components;
import org.jboss.seam.Seam;
import org.jboss.seam.components.ConversationManager;
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
   private static final String JBPM_STATE_MAP = "org.jboss.seam.bpm.recoverableState";

   private static Logger log = Logger.getLogger(SeamPhaseListener.class);

   public PhaseId getPhaseId()
   {
      return ANY_PHASE;
   }
   
   public void beforePhase(PhaseEvent event)
   {
      log.trace( "before phase: " + event.getPhaseId() );

      if (event.getPhaseId() == RESTORE_VIEW)
      {
         Contexts.beginRequest( getSession(event) );
         Contexts.setProcessing(false);
         log.info("About to restore view");
      }
      else if (event.getPhaseId() == RENDER_RESPONSE)
      {
         storeAnyConversationContext(event);
         storeAnyBusinessProcessContext();
         getConversationManager().conversationTimeout( getSession(event) );
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
      log.trace( "after phase: " + event.getPhaseId() );

      if (event.getPhaseId() == RESTORE_VIEW)
      {
         restoreAnyConversationContext(event);
         restoreAnyBusinessProcessContext();
      }
      else if (event.getPhaseId() == RENDER_RESPONSE) {
         Contexts.endRequest( getSession(event) );
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

   private static void restoreAnyConversationContext(PhaseEvent event)
   {
      String conversationId = (String) getAttributes(event).get(CONVERSATION_ID);
      Context conversationContext;
      boolean isStoredConversation = conversationId!=null && 
            getConversationManager().getConversationIds().contains(conversationId);
      if ( isStoredConversation )
      {
         
         Contexts.setLongRunningConversation(true);
         getConversationManager().setCurrentConversationId(conversationId);
         conversationContext = new ConversationContext( getSession(event), conversationId );
         log.info("Restored conversation with id: " + conversationId);
      }
      else
      {
         log.info("No stored conversation");
         conversationId = getConversationManager().createConversationId();
         conversationContext = new ConversationContext( getSession(event), conversationId );
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
         getAttributes(event).remove(CONVERSATION_ID); //TODO: do we really need it in this case?
      }
      else if ( Contexts.isLongRunningConversation() ) 
      {
         String conversationId = getConversationManager().getCurrentConversationId();
         log.info("Storing conversation state: " + conversationId);
         if ( !Contexts.isSessionInvalid() ) 
         {
            //if the session is invalid, don't put the conversation id
            //in the view, 'cos we are expecting the conversation to
            //be destroyed by the servlet session listener
            getAttributes(event).put(CONVERSATION_ID, conversationId);
         }
         //even if the session is invalid, still put the id in the map,
         //so it can be cleaned up along with all the other conversations
         getConversationManager().addConversationId(conversationId);
      }
      else 
      {
         String conversationId = getConversationManager().getCurrentConversationId();
         log.info("Discarding conversation state: " + conversationId);
         getAttributes(event).remove(CONVERSATION_ID);
         getConversationManager().removeConversationId(conversationId);
      }
   }
   
   private static ConversationManager getConversationManager()
   {
      return (ConversationManager) Components.getComponentInstance( Seam.getComponentName(ConversationManager.class), true );
   }

   /*private static HttpServletRequest getRequest(PhaseEvent event)
   {
      return (HttpServletRequest) event.getFacesContext().getExternalContext().getRequest();
   }*/

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
		BusinessProcessContext jbpmContext = ( BusinessProcessContext ) Contexts.getBusinessProcessContext();

		log.trace( "storing bpm recoverable state" );
		conversation.set( JBPM_STATE_MAP, jbpmContext.getRecoverableState() );
	}

	private static void restoreAnyBusinessProcessContext() {
		Context conversation = Contexts.getConversationContext();
		Map state = ( Map ) conversation.get( JBPM_STATE_MAP );
		log.trace( "restoring bpm state from : " + state );
		if ( state != null ) {
			Contexts.recoverBusinessProcessContext( state );
		}
		else {
			Contexts.beginBusinessProcessContext();
		}
	}

}
