//$Id$
package org.jboss.seam.components;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import javax.servlet.http.HttpSession;

import org.jboss.logging.Logger;
import org.jboss.seam.ScopeType;
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

   private static final String CONVERSATION_ID_MAP = "org.jboss.seam.allConversationIds";
   
   private Map<String, Long> conversationIdMap;
   private String currentConversationId;
   private boolean dirty = false;
   
   public Set<String> getConversationIds()
   {
      return getConversationIdMap().keySet();
   }

   public Map<String, Long> getConversationIdMap()
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
   public void dirty()
   {
      dirty = true;
   }

   public void removeConversationId(String conversationId)
   {
      Set<String> ids = getConversationIds();
      if ( ids.contains(conversationId) ) //might be a request-only conversationId, not yet existing in session
      {
         ids.remove(conversationId);
         dirty();
      }
   }

   public void addConversationId(String conversationId)
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
   
   public String createConversationId()
   {
      currentConversationId = Id.nextId();
      return currentConversationId;
   }

   public String getCurrentConversationId()
   {
      return currentConversationId;
   }

   public void setCurrentConversationId(String currentConversationId)
   {
      this.currentConversationId = currentConversationId;
   }
}
