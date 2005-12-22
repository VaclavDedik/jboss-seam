package org.jboss.seam.core;

import static org.jboss.seam.InterceptionType.NEVER;

import java.io.Serializable;

import org.jboss.seam.Component;
import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.Intercept;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.contexts.Contexts;

/**
 * Allows the conversation timeout to be set per-conversation.
 * 
 * @author Gavin King
 *
 */
@Scope(ScopeType.CONVERSATION)
@Name("conversation")
@Intercept(NEVER)
public class Conversation implements Serializable {
   
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
   
   public String getDescription()
   {
      return Manager.instance().getCurrentConversationDescription();
   }
   
   public void setDescription(String description)
   {
      Manager.instance().setCurrentConversationDescription(description);
   }

   public void setOutcome(String outcome)
   {
      Manager.instance().setCurrentConversationOutcome(outcome);
   }

   public static Conversation instance()
   {
      if ( !Contexts.isConversationContextActive() )
      {
         throw new IllegalStateException("No active conversation context");
      }
      return (Conversation) Component.getInstance(Conversation.class, true);
   }
   
   public String switchableOutcome(String outcome, String description)
   {
      setDescription(description);    
      return switchableOutcome(outcome);
   }
   
   public String switchableOutcome(String outcome)
   {
      setOutcome(outcome);
      return outcome;
   }
   
}
