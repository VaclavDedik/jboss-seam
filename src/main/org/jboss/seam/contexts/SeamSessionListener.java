//$Id$
package org.jboss.seam.contexts;

import java.util.Set;

import javax.servlet.http.HttpSession;
import javax.servlet.http.HttpSessionEvent;
import javax.servlet.http.HttpSessionListener;

import org.jboss.logging.Logger;
import org.jboss.seam.jsf.SeamPhaseListener;

public class SeamSessionListener implements HttpSessionListener
{
   
   private static final Logger log = Logger.getLogger(SeamSessionListener.class);

   public void sessionCreated(HttpSessionEvent event) {}

   public void sessionDestroyed(HttpSessionEvent event) {
      HttpSession session = event.getSession();
      Set<String> ids = SeamPhaseListener.getConversationIds( session );
      log.info("destroying conversation contexts: " + ids);
      for (String conversationId: ids)
      {
         Contexts.destroy( new ConversationContext(session, conversationId) );         
      }

      log.info("destroying session context");
      Contexts.destroy( new WebSessionContext( session ) );
   }

}
