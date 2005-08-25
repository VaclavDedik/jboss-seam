//$Id$
package org.jboss.seam.components;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import javax.servlet.http.HttpSession;

import org.jboss.logging.Logger;
import org.jboss.seam.Components;
import org.jboss.seam.ScopeType;
import org.jboss.seam.Seam;
import org.jboss.seam.annotations.Destroy;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.contexts.Contexts;
import org.jboss.seam.contexts.ConversationContext;
import org.jboss.seam.contexts.Id;

@Scope(ScopeType.EVENT)
@Name("org.jboss.seam.components.conversationManager")
public class ConversationManager
{
   private static Logger log = Logger.getLogger(ConversationManager.class);

   private static final String NAME = Seam.getComponentName(ConversationManager.class);
   private static final String CONVERSATION_ID_MAP = "org.jboss.seam.allConversationIds";
   private static  final String CONVERSATION_OWNER_NAME = "org.jboss.seam.conversationOwnerName";
   private static final String CONVERSATION_ID = "org.jboss.seam.conversationId";
      
   //A map of all conversations for the session,
   //to the last activity time, which is flushed
   //stored in the session context at the end
   //of each request
   private Map<String, Long> conversationIdMap;
   private boolean dirty = false;
   
   //The id of the current conversation
   private String currentConversationId;
   
   //Is the current conversation "long-running"?
   private boolean isLongRunningConversation;
   
   //Are we processing interceptors?
   private boolean processInterceptors = false;
   
   public Set<String> getSessionConversationIds()
   {
      return getConversationIdMap().keySet();
   }

   private Map<String, Long> getConversationIdMap()
   {
      if (conversationIdMap==null)
      {
         conversationIdMap = (Map<String, Long>) Contexts.getSessionContext().get(CONVERSATION_ID_MAP);
         if (conversationIdMap==null)
         {
            conversationIdMap = new HashMap<String, Long>();
         }
      }
      return conversationIdMap;
   }
   
   /**
    * Make sure the session notices that we changed something
    */
   private void dirty()
   {
      dirty = true;
   }

   private void removeConversationId(String conversationId)
   {
      Set<String> ids = getSessionConversationIds();
      if ( ids.contains(conversationId) ) //might be a request-only conversationId, not yet existing in session
      {
         ids.remove(conversationId);
         dirty();
      }
   }

   private void addConversationId(String conversationId)
   {
      getConversationIdMap().put( conversationId, System.currentTimeMillis() );
      dirty();
   }
   
   /**
    * Clean up timed-out conversations
    */
   public void conversationTimeout(HttpSession session)
   {
      long currentTime = System.currentTimeMillis();
      Map<String, Long> ids = getConversationIdMap();
      Iterator<Map.Entry<String, Long>> iter = ids.entrySet().iterator();
      while ( iter.hasNext() )
      {
         Map.Entry<String, Long> entry = iter.next();
         long delta = currentTime - entry.getValue();
         Settings settings = (Settings) Contexts.getApplicationContext().get(Settings.class);
         int conversationTimeout = settings.getConversationTimeout();
         if ( delta > conversationTimeout )
         {
            String conversationId = entry.getKey();
            log.info("conversation timeout for conversation: " + conversationId);
            Contexts.destroy( new ConversationContext( session, conversationId ) );
            iter.remove();
            dirty();
         }
      }
   }
   
   @Destroy
   public void flush()
   {
      if (dirty)
      {
         log.info("flushing");
         Contexts.getSessionContext().set(CONVERSATION_ID_MAP, conversationIdMap);
      }
      else
      {
         log.info("no need to flush");
      }
   }
   
   public boolean isLongRunningConversation()
   {
      return isLongRunningConversation;
   }

   public void setLongRunningConversation(boolean isLongRunningConversation)
   {
      this.isLongRunningConversation = isLongRunningConversation;
   }

   public Object getConversationOwnerName()
   {
      return Contexts.getConversationContext().get(CONVERSATION_OWNER_NAME);
   }

   public void setConversationOwnerName(String name)
   {
      Contexts.getConversationContext().set(CONVERSATION_OWNER_NAME, name);
   }

   public static ConversationManager instance()
   {
      return (ConversationManager) Components.getComponentInstance( NAME, true );
   }

   public boolean isProcessInterceptors()
   {
      return processInterceptors;
   }

   public void setProcessInterceptors(boolean processInterceptors)
   {
      this.processInterceptors = processInterceptors;
   }
   
   public void store(Map attributes)
   {
      if ( isLongRunningConversation() ) 
      {
         String conversationId = currentConversationId;
         log.info("Storing conversation state: " + conversationId);
         if ( !Contexts.isSessionInvalid() ) 
         {
            //if the session is invalid, don't put the conversation id
            //in the view, 'cos we are expecting the conversation to
            //be destroyed by the servlet session listener
            attributes.put(CONVERSATION_ID, conversationId);
         }
         //even if the session is invalid, still put the id in the map,
         //so it can be cleaned up along with all the other conversations
         addConversationId(conversationId);
      }
      else 
      {
         String conversationId = currentConversationId;
         log.info("Discarding conversation state: " + conversationId);
         attributes.remove(CONVERSATION_ID);
         removeConversationId(conversationId);
      }
   }
   
   public String restore(Map attributes)
   {
      String storedConversationId = (String) attributes.get(CONVERSATION_ID);
      boolean isStoredConversation = storedConversationId!=null && 
            getSessionConversationIds().contains(storedConversationId);
      if ( isStoredConversation )
      {
         log.info("Restoring conversation with id: " + storedConversationId);
         setLongRunningConversation(true);
         currentConversationId = storedConversationId;
      }
      else
      {
         log.info("No stored conversation");
         currentConversationId = Id.nextId();
         setLongRunningConversation(false);
      }
      return currentConversationId;
   }
   
}
