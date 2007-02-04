/**
 * 
 */
package org.jboss.seam.exceptions;

import org.jboss.seam.contexts.Contexts;
import org.jboss.seam.core.Exceptions;
import org.jboss.seam.core.Manager;

public class DebugPageHandler extends ExceptionHandler
{

   @Override
   public void handle(Exception e) throws Exception
   {
      Exceptions.log.error("redirecting to debug page", e);
      Contexts.getConversationContext().set("org.jboss.seam.debug.lastException", e);
      org.jboss.seam.core.Redirect redirect = org.jboss.seam.core.Redirect.instance();
      redirect.setViewId("/debug.xhtml");
      Manager manager = Manager.instance();
      manager.beforeRedirect();
      redirect.setParameter( manager.getConversationIdParameter(), manager.getCurrentConversationId() );
      redirect.execute();
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
      return "Debug";
   }
}