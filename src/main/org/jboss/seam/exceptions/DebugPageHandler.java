/**
 * 
 */
package org.jboss.seam.exceptions;

import org.jboss.seam.contexts.Contexts;
import org.jboss.seam.faces.JsfManager;
import org.jboss.seam.faces.RedirectException;
import org.jboss.seam.log.LogProvider;
import org.jboss.seam.log.Logging;

public class DebugPageHandler extends ExceptionHandler
{

   private static final LogProvider log = Logging.getLogProvider(DebugPageHandler.class);

   @Override
   public void handle(Exception e) throws Exception
   {
      log.error("redirecting to debug page", e);
      org.jboss.seam.faces.Redirect redirect = org.jboss.seam.faces.Redirect.instance();
      redirect.setViewId("/debug.xhtml");
      JsfManager manager = JsfManager.instance();
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