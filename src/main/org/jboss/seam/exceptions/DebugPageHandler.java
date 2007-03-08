/**
 * 
 */
package org.jboss.seam.exceptions;

import org.jboss.seam.contexts.Contexts;
import org.jboss.seam.core.Manager;
import org.jboss.seam.core.RedirectException;
import org.jboss.seam.log.LogProvider;
import org.jboss.seam.log.Logging;

public class DebugPageHandler extends ExceptionHandler
{

   private static final LogProvider log = Logging.getLogProvider(DebugPageHandler.class);

   @Override
   public void handle(Exception e) throws Exception
   {
      log.error("redirecting to debug page", e);
      Contexts.getConversationContext().set("org.jboss.seam.debug.lastException", e);
      org.jboss.seam.core.Redirect redirect = org.jboss.seam.core.Redirect.instance();
      redirect.setViewId("/debug.xhtml");
      Manager manager = Manager.instance();
      manager.beforeRedirect("/debug.xhtml");
      redirect.setParameter( manager.getConversationIdParameter(), manager.getCurrentConversationId() );
      
      try
      {
         redirect.execute();
      }
      catch (RedirectException re)
      {
         //do nothing
         log.debug("could not redirect", re);
      }
      
      Contexts.getConversationContext().flush();
   }

   @Override
   public boolean isHandler(Exception e)
   {
      return true;
   }
   
   @Override
   public String toString()
   {
      return "DebugPageHandler";
   }
}