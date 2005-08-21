//$Id$
package org.jboss.seam.contexts;

import javax.servlet.http.HttpSessionEvent;
import javax.servlet.http.HttpSessionListener;

import org.jboss.logging.Logger;

public class SeamSessionListener implements HttpSessionListener
{
   
   private static final Logger log = Logger.getLogger(SeamSessionListener.class);

   public void sessionCreated(HttpSessionEvent event) {}

   public void sessionDestroyed(HttpSessionEvent event) {
      if ( Contexts.isConversationContextActive() )
      {
         log.info("destroying conversation context");
         Contexts.destroy( Contexts.getConversationContext() );
      }
      if ( Contexts.isSessionContextActive() )
      {
         log.info("destroying session context");
         Contexts.destroy( Contexts.getSessionContext() );
      }
   }

}
