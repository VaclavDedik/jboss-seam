//$Id$
package org.jboss.seam.servlet;

import java.util.Set;

import javax.servlet.http.HttpSession;
import javax.servlet.http.HttpSessionEvent;
import javax.servlet.http.HttpSessionListener;

import org.jboss.logging.Logger;
import org.jboss.seam.contexts.Contexts;
import org.jboss.seam.contexts.ConversationContext;
import org.jboss.seam.contexts.WebSessionContext;
import org.jboss.seam.jsf.SeamPhaseListener;

/**
 * Destroys Seam components when the session times out
 * @author Gavin King
 */
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
