//$Id$
package org.jboss.seam.servlet;

import javax.servlet.http.HttpSessionEvent;
import javax.servlet.http.HttpSessionListener;

import org.jboss.seam.contexts.Contexts;

/**
 * Destroys Seam components when the session times out
 * @author Gavin King
 */
public class SeamSessionListener implements HttpSessionListener
{

   public void sessionCreated(HttpSessionEvent event) {}

   public void sessionDestroyed(HttpSessionEvent event) {
      Contexts.endSession( event.getSession() );
   }

}
