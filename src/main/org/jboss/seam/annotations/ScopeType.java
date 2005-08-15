/*
 * JBoss, Home of Professional Open Source
 *
 * Distributable under LGPL license.
 * See terms of license at gnu.org.
 */
package org.jboss.seam.annotations;

import org.jboss.seam.Context;
import org.jboss.seam.Contexts;

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
   CONVERSATION,
   SESSION,
   APPLICATION,
   PROCESS;
   
   public Context getContext() {
      switch (this)
      {
         case STATELESS: return Contexts.getStatelessContext();
         
         case EVENT: return Contexts.getEventContext();
         
         case CONVERSATION: return Contexts.isConversationContextActive() ? 
               Contexts.getConversationContext() : Contexts.getEventContext();
         
         case SESSION: return Contexts.isSessionContextActive() ?
               Contexts.getSessionContext() : Contexts.getEventContext();
         
         case APPLICATION: return Contexts.isApplicationContextActive() ?
               Contexts.getApplicationContext() : Contexts.getEventContext();
         
         case PROCESS: return Contexts.isBusinessProcessContextActive() ?
               Contexts.getBusinessProcessContext() :  Contexts.getEventContext();
         
         default: throw new IllegalArgumentException();
      }
   }
}


