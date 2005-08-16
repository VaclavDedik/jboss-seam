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
         case CONVERSATION: return Contexts.getConversationContext();
         case SESSION: return Contexts.getSessionContext();
         case APPLICATION: return Contexts.getApplicationContext();
         case PROCESS: return Contexts.getBusinessProcessContext();
         default: throw new IllegalArgumentException();
      }
   }
}


