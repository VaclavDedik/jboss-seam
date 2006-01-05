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
 * Allows the conversation timeout to be set per-conversation,
 * and the conversation description and switchable outcome to
 * be set when the application requires workspace management
 * functionality.
 * 
 * @author Gavin King
 *
 */
@Scope(ScopeType.CONVERSATION)
@Name("conversation")
@Intercept(NEVER)
public class Conversation implements Serializable {
   
   private Integer timeout;
   String description;
   String viewId;

   /**
    * Get the timeout for this converstaion instance.
    * @return the timeout in millis
    */
   public Integer getTimeout() {
      return timeout;
   }
   
   /**
    * Set the timeout for this converstaion instance.
    * @param timeout the timeout in millis
    */
   public void setTimeout(Integer timeout) {
      this.timeout = timeout;
   }
   
   /**
    * Get the conversation id.
    */
   public String getId()
   {
      return Manager.instance().getCurrentConversationId();
   }
   
   public String getDescription()
   {
      return description==null ? 
            Manager.instance().getCurrentConversationDescription() : 
            description;
   }
   
   public String getViewId()
   {
      return viewId==null ? 
            Manager.instance().getCurrentConversationViewId() :
            viewId;
   }
   
   /**
    * Sets the description of this conversation, for use
    * in the conversation list, breadcrumbs, or conversation
    * switcher.
    */
   public void setDescription(String description)
   {
      this.description = description;
   }
   
   /**
    * Sets the JSF outcome to be used when we switch back to this
    * conversation from the conversation list, breadcrumbs, or 
    * conversation switcher.
    */
   public void setViewId(String outcome)
   {
      this.viewId = outcome;
   }
   
   public static Conversation instance()
   {
      if ( !Contexts.isConversationContextActive() )
      {
         throw new IllegalStateException("No active conversation context");
      }
      return (Conversation) Component.getInstance(Conversation.class, ScopeType.CONVERSATION, true);
   }
   
   void flush()
   {
      //we need to flush this stuff asynchronously to handle 
      //nested and temporary conversations nicely
      if ( description!=null || viewId!=null )
      {
         Manager manager = Manager.instance();
         if ( !manager.isLongRunningConversation() )
         {
            throw new IllegalStateException("only long-running conversation outcomes are switchable");
         }
         if (description!=null) manager.setCurrentConversationDescription(description);
         if (viewId!=null) manager.setCurrentConversationViewId(viewId);
         if (timeout!=null) manager.setCurrentConversationTimeout(timeout);
         description = null;
         viewId = null;
      }
   }

}
