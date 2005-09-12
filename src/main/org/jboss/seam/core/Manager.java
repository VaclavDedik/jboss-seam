//$Id$
package org.jboss.seam.core;

import static org.jboss.seam.InterceptionType.NEVER;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import javax.servlet.http.HttpSession;

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
import org.jboss.seam.util.Id;

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
   public static final String TASK_ID = NAME + ".jbpmTaskId";
   public static final String TASK_NAME = NAME + ".jbpmTaskName";
   public static final String PROCESS_ID = NAME + ".jbpmProcessId";
   public static final String PROCESS_NAME = NAME + ".jbpmProcessName";

      
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
   
   private int conversationTimeout = 600000; //10 minutes

   private Long taskId;
   private String taskName;
   private Long processId;
   private String processName;

   public Long getTaskId()
   {
      return taskId;
   }

   public void setTaskId(Long taskId)
   {
      this.taskId = taskId;
   }

   public String getTaskName()
   {
      return taskName;
   }

   public void setTaskName(String taskName)
   {
      this.taskName = taskName;
   }

   public Long getProcessId()
   {
      return processId;
   }

   public void setProcessId(Long processId)
   {
      this.processId = processId;
   }

   public String getProcessName()
   {
      return processName;
   }

   public void setProcessName(String processName)
   {
      this.processName = processName;
   }

   public int getConversationTimeout()
   {
      return conversationTimeout;
   }
   public void setConversationTimeout(int conversationTimeout)
   {
      this.conversationTimeout = conversationTimeout;
   }
   
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
         if ( delta > conversationTimeout )
         {
            String conversationId = entry.getKey();
            log.debug("conversation timeout for conversation: " + conversationId);
            ConversationContext conversationContext = new ConversationContext( session, conversationId );
            conversationContext.clear();
            Contexts.destroy( conversationContext );
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
         Contexts.getSessionContext().set(CONVERSATION_ID_MAP, conversationIdMap);
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
      Manager instance = (Manager) Component.getInstance( NAME, true );
      if (instance==null)
      {
         throw new IllegalStateException("No ConversationManager could be created, make sure the Component exists in application scope");
      }
      return instance;
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
         log.info("Storing conversation state: " + currentConversationId);
         if ( !Seam.isSessionInvalid() ) 
         {
            //if the session is invalid, don't put the conversation id
            //in the view, 'cos we are expecting the conversation to
            //be destroyed by the servlet session listener
            attributes.put(CONVERSATION_ID, currentConversationId);
            if (taskId==null)
            {
               attributes.remove( TASK_ID );
               attributes.remove( TASK_NAME );
            }
            else
            {
               attributes.put( TASK_ID, taskId );
               attributes.put( TASK_NAME, taskName );
            }
            if (processId==null)
            {
               attributes.remove( PROCESS_ID );
               attributes.remove( PROCESS_NAME );
            }
            else
            {
               attributes.put( PROCESS_ID, processId );
               attributes.put( PROCESS_NAME, processName );
            }
         }
         //even if the session is invalid, still put the id in the map,
         //so it can be cleaned up along with all the other conversations
         addConversationId(currentConversationId);
      }
      else 
      {
         log.debug("Discarding conversation state: " + currentConversationId);
         attributes.remove(CONVERSATION_ID);
         //TODO: should we also remove task and process ids here?
         removeConversationId(currentConversationId);
      }
   }
   
   public String restore(Map attributes)
   {
      String storedConversationId = (String) attributes.get(CONVERSATION_ID);
      boolean isStoredConversation = storedConversationId!=null && 
            getSessionConversationIds().contains(storedConversationId);
      if ( isStoredConversation )
      {
         log.debug("Restoring conversation with id: " + storedConversationId);
         setLongRunningConversation(true);
         currentConversationId = storedConversationId;
         taskId = ( Long ) attributes.get( TASK_ID );
         taskName = ( String ) attributes.get( TASK_NAME );
         processId = ( Long ) attributes.get( PROCESS_ID );
         processName = ( String ) attributes.get( PROCESS_NAME );
      }
      else
      {
         log.debug("No stored conversation");
         currentConversationId = Id.nextId();
         setLongRunningConversation(false);
      }
      return currentConversationId;
   }
   
}
