package org.jboss.seam.exception;

import org.jboss.seam.contexts.Contexts;
import org.jboss.seam.faces.FacesManager;
import org.jboss.seam.faces.RedirectException;
import org.jboss.seam.log.LogProvider;
import org.jboss.seam.log.Logging;

/**
 * Implements automagic redirection to the Seam debug page.
 * 
 * @author Gavin King
 *
 */
public class DebugPageHandler extends ExceptionHandler
{

   private static final LogProvider log = Logging.getLogProvider(DebugPageHandler.class);

   @Override
   public void handle(Exception e) throws Exception
   {
      log.error("redirecting to debug page", e);
      org.jboss.seam.faces.Redirect redirect = org.jboss.seam.faces.Redirect.instance();
      redirect.setViewId("/debug.xhtml");
      FacesManager manager = FacesManager.instance();
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