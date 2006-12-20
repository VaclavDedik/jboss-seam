/**
 * 
 */
package org.jboss.seam.exceptions;

import javax.faces.event.PhaseId;

import org.jboss.seam.contexts.Context;
import org.jboss.seam.contexts.Contexts;
import org.jboss.seam.contexts.Lifecycle;
import org.jboss.seam.core.Exceptions;
import org.jboss.seam.core.Manager;

public class DebugPageHandler extends ExceptionHandler
{

   @Override
   public Object handle(Exception e) throws Exception
   {
      Exceptions.log.error("redirecting to debug page", e);
      Context conversationContext = Contexts.getConversationContext();
      conversationContext.set("org.jboss.seam.debug.lastException", e);
      conversationContext.set("org.jboss.seam.debug.phaseId", Lifecycle.getPhaseId().toString());
      org.jboss.seam.core.Redirect redirect = org.jboss.seam.core.Redirect.instance();
      redirect.setViewId("/debug.xhtml");
      Manager manager = Manager.instance();
      manager.beforeRedirect();
      redirect.setParameter( manager.getConversationIdParameter(), manager.getCurrentConversationId() );
      redirect.execute();
      conversationContext.flush();
      return rethrow(e);
   }

   @Override
   public boolean isHandler(Exception e)
   {
      return Lifecycle.getPhaseId()!=PhaseId.RENDER_RESPONSE && 
            Lifecycle.getPhaseId()!=null &&
            Contexts.isConversationContextActive();
   }
   
   @Override
   public String toString()
   {
      return "Debug";
   }
}