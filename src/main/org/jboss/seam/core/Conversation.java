package org.jboss.seam.core;

import static org.jboss.seam.InterceptionType.NEVER;

import java.io.Serializable;

import org.jboss.seam.Component;
import org.jboss.seam.ScopeType;
import org.jboss.seam.Seam;
import org.jboss.seam.annotations.FlushModeType;
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
@Name("org.jboss.seam.core.conversation")
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
      return timeout==null ?
            Manager.instance().getCurrentConversationTimeout() :
            timeout;
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
      //nested and temporary conversations which have no
      //ConversationEntry
      if ( !Manager.instance().isLongRunningConversation() )
      {
         throw new IllegalStateException("only long-running conversation outcomes are switchable");
      }
      
      ConversationEntry entry = Manager.instance().getCurrentConversationEntry();
      if (viewId!=null)
      {
         entry.setViewId(viewId);
      }
      if (description!=null)
      {
         entry.setDescription(description);
      }
      if (timeout!=null)
      {
         entry.setTimeout(timeout);
      }
      
      description = null;
      viewId = null;
      timeout = null;
   }
   
   /**
    * Switch back to the last defined view-id for the
    * current conversation.
    * 
    * @return true if a redirect occurred
    */
   public boolean redirect()
   {
      Manager manager = Manager.instance();
      String viewId = manager.getCurrentConversationViewId();
      return redirect(manager, viewId);
   }

   private boolean redirect(Manager manager, String viewId)
   {
      if (viewId==null)
      {
         return false;
      }
      else
      {
         manager.redirect(viewId);
         return true;
      }         
   }
   
   /**
    * End a child conversation and redirect to the last defined
    * view-id for the parent conversation.
    * 
    * @return true if a redirect occurred
    */
   public boolean endAndRedirect()
   {
      end();
      Manager manager = Manager.instance();
      String viewId = manager.getParentConversationViewId();
      return redirect(manager, viewId);
   }
   
   /**
    * Leave the scope of the current conversation
    */
   public void leave()
   {
      Manager.instance().leaveConversation();
   }
   
   /**
    * Start a long-running conversation.
    */
   public void begin()
   {
      begin( Seam.getComponentName(Conversation.class) );
      //TODO: let them pass a pageflow name as a request parameter
   }
   
   /**
    * Start a long-running conversation.
    */
   public void begin(String componentName)
   {
      if ( !Manager.instance().isLongRunningConversation() )
      {
         Manager.instance().beginConversation(componentName);
      }
      //TODO: let them pass a pageflow name as a request parameter
   }
   
   public void beginPageflow(String pageflowName)
   {
      Pageflow.instance().begin(pageflowName);
   }
   
   /**
    * End a long-runnning conversation.
    */
   public void end()
   {
      Manager.instance().endConversation(false);   
   }
   
   /**
    * End a long-runnning conversation and destroy
    * it before a redirect.
    */
   public void endBeforeRedirect()
   {
      Manager.instance().endConversation(true);   
   }   
   
   /**
    * Is this conversation long-running? Note that this method returns
    * false even when the conversation has been temporarily promoted
    * to long-running for the course of a redirect, so it does what
    * the user really expects.
    */
   public boolean isLongRunning()
   {
      return Manager.instance().isReallyLongRunningConversation();
   }
   
   /**
    * Is this conversation a nested conversation?
    */
   public boolean isNested()
   {
      return Manager.instance().isNestedConversation();
   }
   
   /**
    * Get the id of the immediate parent of a nested conversation
    */
   public String getParentId()
   {
      return Manager.instance().getParentConversationId();
   }
   
   /**
    * Get the id of root conversation of a nested conversation
    */
   public String getRootId()
   {
      return Manager.instance().getRootConversationId();
   }
   
   /**
    * "Pop" the conversation stack, switching to the parent conversation
    */
   public void pop()
   {
      String parentId = getParentId();
      if (parentId!=null)
      {
         Manager.instance().switchConversation(parentId);
      }
   }
   
   /**
    * Pop the conversation stack and redirect to the last defined
    * view-id for the parent conversation.
    * 
    * @return true if a redirect occurred
    */
   public boolean redirectToParent()
   {
      pop();
      return redirect();
   }
   
   /**
    * Switch to the root conversation
    */
   public void root()
   {
      String rootId = getRootId();
      if (rootId!=null)
      {
         Manager.instance().switchConversation(rootId);
      }
   }

   /**
    * Switch to the root conversation and redirect to the 
    * last defined view-id for the root conversation.
    * 
    * @return true if a redirect occurred
    */
   public boolean redirectToRoot()
   {
      root();
      return redirect();
   }
   
   /**
    * Change the flush mode of all Seam-managed peristence 
    * contexts in this conversation.
    */
   public void changeFlushMode(FlushModeType flushMode)
   {
      PersistenceContexts.instance().changeFlushMode(flushMode);
   }

}
