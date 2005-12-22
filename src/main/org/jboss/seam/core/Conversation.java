package org.jboss.seam.core;

import static org.jboss.seam.InterceptionType.NEVER;

import org.jboss.seam.Component;
import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.Intercept;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;

/**
 * Allows the conversation timeout to be set per-conversation.
 * 
 * @author Gavin King
 *
 */
@Scope(ScopeType.CONVERSATION)
@Name("org.jboss.seam.core.conversation")
@Intercept(NEVER)
public class Conversation {
   
   private int timeout = 600000; //10 minutes

   public int getTimeout() {
      return timeout;
   }

   public void setTimeout(int timeout) {
      this.timeout = timeout;
   }
   
   public String getId()
   {
      return Manager.instance().getCurrentConversationId();
   }

   public static Conversation instance()
   {
      return (Conversation) Component.getInstance( Conversation.class, true );
   }
   
}
