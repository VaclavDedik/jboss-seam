//$Id$
package org.jboss.seam;

import java.util.Map;

import javax.faces.event.PhaseEvent;
import javax.faces.event.PhaseId;
import javax.faces.event.PhaseListener;

import org.jboss.logging.Logger;

public class SeamPhaseListener implements PhaseListener
{

   private static final String CONVERSATION_ID = "org.jboss.seam.ConversationId";

   private static Logger log = Logger.getLogger(SeamPhaseListener.class);

   public void afterPhase(PhaseEvent event)
   {
      if (event.getPhaseId() == PhaseId.RESTORE_VIEW)
      {
         String conversationId = (String) getAttributes(event).get(CONVERSATION_ID);
         log.info("After restore view: " + conversationId);
         Contexts.setConversationId(conversationId);
      }
   }

   public void beforePhase(PhaseEvent event)
   {
      if (event.getPhaseId() == PhaseId.RENDER_RESPONSE)
      {
         String conversationId = Contexts.getConversationContextId();
         log.info("Before render response: " + conversationId);
         getAttributes(event).put(CONVERSATION_ID, conversationId);
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
