//$Id$
package org.jboss.seam.core;

import static org.jboss.seam.InterceptionType.NEVER;
import static org.jboss.seam.annotations.Install.BUILT_IN;

import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.Install;
import org.jboss.seam.annotations.Intercept;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.annotations.Unwrap;
import org.jboss.seam.contexts.Context;
import org.jboss.seam.contexts.Contexts;

/**
 * Support for injecting the conversation context
 * 
 * @author Gavin King
 */
@Scope(ScopeType.APPLICATION)
@Intercept(NEVER)
@Install(precedence=BUILT_IN)
@Name("org.jboss.seam.core.conversationContext")
public class ConversationContext
{
   @Unwrap
   public Context getContext()
   {
      return Contexts.isConversationContextActive() ? 
            Contexts.getConversationContext() : null;
   }
}
