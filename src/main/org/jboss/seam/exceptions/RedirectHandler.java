package org.jboss.seam.exceptions;

import org.jboss.seam.contexts.Contexts;
import org.jboss.seam.core.Conversation;
import org.jboss.seam.core.RedirectException;
import org.jboss.seam.log.LogProvider;
import org.jboss.seam.log.Logging;

public abstract class RedirectHandler extends ExceptionHandler
{

   private static final LogProvider log = Logging.getLogProvider(RedirectHandler.class);

   protected abstract String getViewId(Exception e);

   @Override
   public void handle(Exception e) throws Exception
   {
      String viewId = getViewId(e);
      
      if (log.isDebugEnabled())
      {
         log.debug("redirecting to: " + viewId);
      }
      
      addFacesMessage( e, getMessage(e) );
      
      if ( Contexts.isConversationContextActive() && isEnd(e) ) 
      {
         Conversation.instance().end();
      }
      
      try
      {
         redirect(viewId, null);
      }
      catch (RedirectException re)
      {
         //do nothing
         log.debug("could not redirect", re);
      }
   }

   @Override
   public String toString()
   {
      return "RedirectHandler";
   }
}