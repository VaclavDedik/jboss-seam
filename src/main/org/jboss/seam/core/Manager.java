/*
 * JBoss, Home of Professional Open Source
 *
 * Distributable under LGPL license.
 * See terms of license at gnu.org.
 */
package org.jboss.seam.core;

import static org.jboss.seam.InterceptionType.NEVER;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import javax.faces.context.ExternalContext;
import javax.servlet.http.HttpServletResponse;

import org.jboss.logging.Logger;
import org.jboss.seam.Component;
import org.jboss.seam.ScopeType;
import org.jboss.seam.Seam;
import org.jboss.seam.annotations.Destroy;
import org.jboss.seam.annotations.Intercept;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.contexts.Contexts;
import org.jboss.seam.contexts.ConversationContext;
import org.jboss.seam.contexts.Session;
import org.jboss.seam.util.Id;

/**
 * @author Gavin King
 * @author <a href="mailto:theute@jboss.org">Thomas Heute</a>
 * @version $Revision$
 */
@Scope(ScopeType.EVENT)
@Name("org.jboss.seam.core.manager")
@Intercept(NEVER)
public class Manager
{
   private static Logger log = Logger.getLogger(Manager.class);

   private static final String NAME = Seam.getComponentName(Manager.class);
   public static final String CONVERSATION_ID_MAP = NAME + ".conversationIdActivityMap";
   public static final String CONVERSATION_OWNER_NAME = NAME + ".conversationOwnerName";
   public static final String CONVERSATION_ID = NAME + ".conversationId";

      
   //A map of all conversations for the session,
   //to the last activity time, which is flushed
   //stored in the session context at the end
   //of each request
   private Map<String, ConversationEntry> conversationIdEntryMap;
   private boolean dirty = false;
   
   //The id of the current conversation
   private String currentConversationId;
   
   //Is the current conversation "long-running"?
   private boolean isLongRunningConversation;
   
   public String getCurrentConversationId()
   {
      return currentConversationId;
   }
   
   public void setCurrentConversationId(String id)
   {
      currentConversationId = id;
   }
   
   public Set<String> getSessionConversationIds()
   {
      return getConversationIdEntryMap().keySet();
   }

   public Map<String, ConversationEntry> getConversationIdEntryMap()
   {
      if (conversationIdEntryMap==null)
      {
         conversationIdEntryMap = (Map<String, ConversationEntry>) Contexts.getSessionContext().get(CONVERSATION_ID_MAP);
         if (conversationIdEntryMap==null)
         {
            conversationIdEntryMap = new HashMap<String, ConversationEntry>();
         }
      }
      return conversationIdEntryMap;
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

   private void updateConversationEntry(String conversationId)
   {
      getConversationEntry(conversationId).touch();
      dirty();
   }

   private ConversationEntry getConversationEntry(String conversationId) {
      ConversationEntry ce = getConversationIdEntryMap().get(conversationId);
      if (ce==null)
      {
         ce = new ConversationEntry( getCurrentConversationId() );
         getConversationIdEntryMap().put(conversationId, ce);
      }
      return ce;
   }
   
   public void setCurrentConversationDescription(String description)
   {
      getConversationEntry( getCurrentConversationId() ).setDescription(description);
      dirty();
   }
   
   public void setCurrentConversationOutcome(String outcome)
   {
      getConversationEntry( getCurrentConversationId() ).setOutcome(outcome);
      dirty();
   }
   
   public String getCurrentConversationDescription()
   {
      if ( conversationIdEntryMap==null ) return null;
      ConversationEntry ce = conversationIdEntryMap.get(currentConversationId);
      if ( ce==null ) return null;
      return ce.getDescription();
   }
   
   @Destroy
   public void flush()
   {
      if (dirty)
      {
         Contexts.getSessionContext().set(CONVERSATION_ID_MAP, conversationIdEntryMap);
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

   public static Manager instance()
   {
      if ( !Contexts.isEventContextActive() )
      {
         throw new IllegalStateException("No active event context");
      }
      Manager instance = (Manager) Component.getInstance(Manager.class, true);
      if (instance==null)
      {
         throw new IllegalStateException("No Manager could be created, make sure the Component exists in application scope");
      }
      return instance;
   }

   /**
    * Clean up timed-out conversations
    */
   public void conversationTimeout(ExternalContext externalContext)
   {
      long currentTime = System.currentTimeMillis();
      Map<String, ConversationEntry> ids = getConversationIdEntryMap();
      Iterator<Map.Entry<String, ConversationEntry>> iter = ids.entrySet().iterator();
      while ( iter.hasNext() )
      {
         Map.Entry<String, ConversationEntry> entry = iter.next();
         long delta = currentTime - entry.getValue().getLastRequestTime();
         if ( delta > Conversation.instance().getTimeout() )
         {
            String conversationId = entry.getKey();
            log.debug("conversation timeout for conversation: " + conversationId);
            ConversationContext conversationContext = new ConversationContext( Session.getSession(externalContext, true), conversationId );
            Contexts.destroy( conversationContext );
            conversationContext.clear();
            iter.remove();
            dirty();
         }
      }
   }
   
   public void store(Map attributes)
   {
      if ( isLongRunningConversation() ) 
      {
         log.debug("Storing conversation state: " + currentConversationId);
         if ( !Seam.isSessionInvalid() ) 
         {
            //if the session is invalid, don't put the conversation id
            //in the view, 'cos we are expecting the conversation to
            //be destroyed by the servlet session listener
            attributes.put(CONVERSATION_ID, currentConversationId);
         }
         //even if the session is invalid, still put the id in the map,
         //so it can be cleaned up along with all the other conversations
         updateConversationEntry(currentConversationId);
      }
      else 
      {
         log.debug("Discarding conversation state: " + currentConversationId);
         attributes.remove(CONVERSATION_ID);
         removeConversationId(currentConversationId);
      }
   }
   
   public void store(HttpServletResponse response)
   {
      if ( isLongRunningConversation() ) 
      {
         log.debug("Storing conversation state: " + currentConversationId);
         if ( !Seam.isSessionInvalid() ) 
         {
            //if the session is invalid, don't put the conversation id
            //in the view, 'cos we are expecting the conversation to
            //be destroyed by the servlet session listener
            response.setHeader("conversationId", currentConversationId);
         }
         //even if the session is invalid, still put the id in the map,
         //so it can be cleaned up along with all the other conversations
         updateConversationEntry(currentConversationId);
      }
      else 
      {
         log.debug("Discarding conversation state: " + currentConversationId);
         removeConversationId(currentConversationId);
      }
   }
   
   public String restore(Map attributes, Map parameters)
   {
      String storedConversationId;
      //First, try to get the conversation id from a request parameter
      storedConversationId = (String) parameters.get("conversationId");
      if ( storedConversationId==null && attributes!=null )
      {
         //if it is not passed as a request parameter, try to get it from
         //the JSF component tree
         storedConversationId = (String) attributes.get(CONVERSATION_ID);
      }
      
      else if (storedConversationId!=null) 
      {
         log.debug("Found conversation id in request parameter: " + storedConversationId);
      }
      
      if ( "new".equals(storedConversationId) )
      {
         storedConversationId = null;
      }
      
      boolean isStoredConversation = storedConversationId!=null && 
            getSessionConversationIds().contains(storedConversationId);
      if ( isStoredConversation )
      {
         //we found an id, so restore the long-running conversation
         log.debug("Restoring conversation with id: " + storedConversationId);
         setLongRunningConversation(true);
         currentConversationId = storedConversationId;
      }
      else
      {
         //there was no id in either place, so there is no
         //long-running conversation to restore
         log.debug("No stored conversation");
         currentConversationId = Id.nextId();
         setLongRunningConversation(false);
      }
      return currentConversationId;
   }
   
}
