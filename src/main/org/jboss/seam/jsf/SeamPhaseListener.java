/*
 * JBoss, Home of Professional Open Source
 *
 * Distributable under LGPL license.
 * See terms of license at gnu.org.
 */
package org.jboss.seam.jsf;

import java.util.Map;

import javax.faces.event.PhaseEvent;
import javax.faces.event.PhaseId;
import javax.faces.event.PhaseListener;
import javax.servlet.http.HttpServletRequest;

import org.jboss.logging.Logger;
import org.jboss.seam.contexts.Context;
import org.jboss.seam.contexts.Contexts;
import org.jboss.seam.contexts.ConversationContext;
import org.jboss.seam.contexts.BusinessProcessContext;

/**
 * Manages the thread/context associations throught the
 * lifecycle of a JSF request.
 *
 * @author Gavin King
 */
public class SeamPhaseListener implements PhaseListener
{
   private static final String CONVERSATION = "org.jboss.seam.Conversation";
   private static final String JBPM_TASK_ID = "org.jboss.seam.jbpm.taskId";
   private static final String JBPM_PROCESS_ID = "org.jboss.seam.jbpm.processId";

   private static Logger log = Logger.getLogger(SeamPhaseListener.class);

   public PhaseId getPhaseId()
   {
      return PhaseId.ANY_PHASE;
   }

   public void afterPhase(PhaseEvent event)
   {
	   log.trace( "after phase [" + event.getPhaseId() + "]" );

      if (event.getPhaseId() == PhaseId.RESTORE_VIEW)
      {
         Context context = (Context) getAttributes(event).get(CONVERSATION);
         log.info("After restore view, conversation context: " + context);
         Contexts.setLongRunningConversation(context!=null);
         if (context==null)
         {
            log.info("No stored conversation state");
            context = new ConversationContext();
         }
         else
         {
            log.info("Retrieved conversation state");
         }
         Contexts.setConversationContext(context);

         restoreAnyBusinessProcessContext( event );
      }
      else if (event.getPhaseId() == PhaseId.RENDER_RESPONSE) {
         log.info("After render response, destroying contexts");
         Contexts.destroy( Contexts.getEventContext() );
         if ( !Contexts.isLongRunningConversation() )
         {
            Contexts.destroy( Contexts.getConversationContext() );
         }
         Contexts.endWebRequest();
      }
      else if (event.getPhaseId() == PhaseId.INVOKE_APPLICATION)
      {
         log.info("After invoke application");
         Contexts.setProcessing(false);
      }
   }

   public void beforePhase(PhaseEvent event)
   {
	   log.trace( "before phase [" + event.getPhaseId() + "]" );

      if (event.getPhaseId() == PhaseId.RESTORE_VIEW)
      {
         log.info("Before restore view");
         Contexts.beginWebRequest( (HttpServletRequest) event.getFacesContext().getExternalContext().getRequest() );
         Contexts.setProcessing(false);
      }
      else if (event.getPhaseId() == PhaseId.RENDER_RESPONSE)
      {
         Context context = Contexts.getConversationContext();
         log.info("Before render response, conversation context: " + context);
         if ( Contexts.isLongRunningConversation() )
         {
            log.info("Storing conversation state");
            getAttributes(event).put(CONVERSATION, context);
         }
         else
         {
            log.info("Discarding conversation state");
            getAttributes(event).put(CONVERSATION, null);
         }
         storeAnyBusinessProcessContext( event );
      }
      else if (event.getPhaseId() == PhaseId.INVOKE_APPLICATION)
      {
         log.info("Before invoke application");
         Contexts.setProcessing(true);
      }
   }

   private Map getAttributes(PhaseEvent event)
   {
      return event.getFacesContext().getViewRoot().getAttributes();
   }

	private void storeAnyBusinessProcessContext(PhaseEvent event) {
		Map attributes = getAttributes( event );

		if ( !Contexts.isBusinessProcessContextActive() ) {
			attributes.remove( JBPM_TASK_ID );
			attributes.remove( JBPM_PROCESS_ID );
			return;
		}

		BusinessProcessContext jbpmContext = ( BusinessProcessContext ) Contexts.getBusinessProcessContext();

		if ( jbpmContext.getProcessInstance().hasEnded() ||
		        jbpmContext.getProcessInstance().isTerminatedImplicitly() ) {
			attributes.remove( JBPM_TASK_ID );
			attributes.remove( JBPM_PROCESS_ID );
			return;
		}

		if ( jbpmContext.getTaskInstance() != null ) {
			attributes.put( JBPM_TASK_ID, jbpmContext.getTaskInstance().getId() );
		}

		if ( jbpmContext.getProcessInstance() != null ) {
			attributes.put( JBPM_PROCESS_ID, jbpmContext.getProcessInstance().getId() );
		}
	}

	private void restoreAnyBusinessProcessContext(PhaseEvent event) {
		Map attributes = getAttributes( event );
		Long taskId = ( Long ) attributes.get( JBPM_TASK_ID );
		Long processId = ( Long ) attributes.get( JBPM_PROCESS_ID );

		// task is the more specific, so try that first...
		if ( taskId != null ) {
			Contexts.beginBusinessProcessContextViaTask( taskId );
		}
		else if ( processId != null ) {
			Contexts.beginBusinessProcessContextViaProcess( processId );
		}
	}

}
