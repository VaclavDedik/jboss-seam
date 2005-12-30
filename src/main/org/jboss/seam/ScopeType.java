/*
 * JBoss, Home of Professional Open Source
 *
 * Distributable under LGPL license.
 * See terms of license at gnu.org.
 */
package org.jboss.seam;

import org.jboss.seam.contexts.Context;
import org.jboss.seam.contexts.Contexts;

/**
 * The available scopes (contexts).
 * 
 * @author <a href="mailto:theute@jboss.org">Thomas Heute</a>
 * @version $Revision$
 */
public enum ScopeType 
{
   STATELESS,
   EVENT,
   PAGE,
   CONVERSATION,
   SESSION,
   APPLICATION,
   PROCESS,
   UNSPECIFIED;
   
   public Context getContext() {
      switch (this)
      {
         case STATELESS: 
            return Contexts.getStatelessContext();
         case EVENT: 
            if ( !Contexts.isEventContextActive() )
            {
               throw new IllegalStateException("No event context active");
            }
            return Contexts.getEventContext();
         case PAGE:
            if ( !Contexts.isPageContextActive() )
            {
               throw new IllegalStateException("No page context active");
            }
            return Contexts.getPageContext();
         case CONVERSATION: 
            if ( !Contexts.isConversationContextActive() )
            {
               throw new IllegalStateException("No conversation context active");
            }
            return Contexts.getConversationContext();
         case SESSION: 
            if ( !Contexts.isSessionContextActive() )
            {
               throw new IllegalStateException("No session context active");
            }
             return Contexts.getSessionContext();
         case APPLICATION: 
            if ( !Contexts.isApplicationContextActive() )
            {
               throw new IllegalStateException("No application context active");
            }
             return Contexts.getApplicationContext();
         case PROCESS: 
            if ( !Contexts.isBusinessProcessContextActive() )
            {
               throw new IllegalStateException("No process context active");
            }
             return Contexts.getBusinessProcessContext();
         default: 
            throw new IllegalArgumentException();
      }
   }
   
   public String getPrefix()
   {
      return "org.jboss.seam." + toString();
   }

}


