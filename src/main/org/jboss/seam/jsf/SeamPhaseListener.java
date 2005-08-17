//$Id$
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

/**
 * Manages the thread/context associations throught the
 * lifecycle of a JSF request.
 * 
 * @author Gavin King
 */
public class SeamPhaseListener implements PhaseListener
{

   private static final String CONVERSATION = "org.jboss.seam.Conversation";

   private static Logger log = Logger.getLogger(SeamPhaseListener.class);

   public void afterPhase(PhaseEvent event)
   {
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
   }

   public void beforePhase(PhaseEvent event)
   {
      if (event.getPhaseId() == PhaseId.RESTORE_VIEW)
      {
         log.info("Before restore view");
         Contexts.beginWebRequest( (HttpServletRequest) event.getFacesContext().getExternalContext().getRequest() );
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
      }
   }

   private Map getAttributes(PhaseEvent event)
   {
      return event.getFacesContext().getViewRoot().getAttributes();
   }

   public PhaseId getPhaseId()
   {
      return PhaseId.ANY_PHASE;
   }

}
