/*
 * JBoss, Home of Professional Open Source
 *
 * Distributable under LGPL license.
 * See terms of license at gnu.org.
 */
package org.jboss.seam.core;

import static org.jboss.seam.InterceptionType.NEVER;

import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Map;
import java.util.Set;

import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.faces.event.PhaseEvent;
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
import org.jboss.seam.contexts.ServerConversationContext;
import org.jboss.seam.contexts.Session;
import org.jboss.seam.jbpm.Page;
import org.jboss.seam.util.Id;

/**
 * The Seam conversation manager.
 *
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
   public static final String CONVERSATION_ID_MAP = NAME + ".conversationIdEntryMap";
   public static final String CONVERSATION_ID = NAME + ".conversationId";
   public static final String PAGEFLOW_COUNTER = NAME + ".pageflowCounter";


   //A map of all conversations for the session,
   //to the last activity time, which is flushed
   //stored in the session context at the end
   //of each request
   private Map<String, ConversationEntry> conversationIdEntryMap;
   private boolean dirty = false;

   //The id of the current conversation
   private String currentConversationId;
   private LinkedList<String> currentConversationIdStack;

   //Is the current conversation "long-running"?
   private boolean isLongRunningConversation;


   private int conversationTimeout = 600000; //10 mins

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

   private ConversationEntry removeConversationEntry(String conversationId)
   {
      Map<String, ConversationEntry> entryMap = getConversationIdEntryMap();
      if ( entryMap.containsKey(conversationId) ) //might be a request-only conversationId, not yet existing in session
      {
         dirty();
         return entryMap.remove(conversationId);
      }
      else
      {
         return null; //does this ever occur??
      }
   }

   private void touchConversationStack()
   {
      LinkedList<String> stack = getCurrentConversationIdStack();
      if ( stack!=null )
      {
         for ( String conversationId: stack )
         {
            ConversationEntry conversationEntry = getConversationEntry(conversationId);
            if (conversationEntry!=null)
            {
               conversationEntry.touch();
               dirty();
            }
         }
      }

      //do this last, to bring it to the top of the conversation list
      if ( isLongRunningConversation() )
      {
         getCurrentConversationEntry().touch();
      }

   }

   private ConversationEntry getConversationEntry(String conversationId) {
      return getConversationIdEntryMap().get(conversationId);
   }

   public Object getCurrentConversationInitiator()
   {
      ConversationEntry ce = getCurrentConversationEntry();
      if (ce!=null)
      {
         return ce.getInitiatorComponentName();
      }
      else
      {
         return null;
      }
   }

   public LinkedList<String> getCurrentConversationIdStack()
   {
      return currentConversationIdStack;
   }

   private void setCurrentConversationIdStack(LinkedList<String> stack)
   {
      currentConversationIdStack = stack;
   }

   private void setCurrentConversationIdStack(String id)
   {
      currentConversationIdStack = new LinkedList<String>();
      currentConversationIdStack.add(id);
   }

   public void setCurrentConversationDescription(String description)
   {
      getCurrentConversationEntry().setDescription(description);
      dirty();
   }

   public void setCurrentConversationViewId(String viewId)
   {
      getCurrentConversationEntry().setViewId(viewId);
      dirty();
   }

   public void setCurrentConversationTimeout(int timeout)
   {
      getCurrentConversationEntry().setTimeout(timeout);
      dirty();
   }

   public String getCurrentConversationDescription()
   {
      if ( conversationIdEntryMap==null ) return null;
      ConversationEntry ce = conversationIdEntryMap.get(currentConversationId);
      if ( ce==null ) return null;
      return ce.getDescription();
   }

   public String getCurrentConversationViewId()
   {
      if ( conversationIdEntryMap==null ) return null;
      ConversationEntry ce = conversationIdEntryMap.get(currentConversationId);
      if ( ce==null ) return null;
      return ce.getViewId();
   }

   @Destroy
   public void flushConversationIdMapToSession()
   {
      if ( Contexts.isSessionContextActive() ) // this method might be called from the session listener
      {
         if (dirty)
         {
            Contexts.getSessionContext().set(CONVERSATION_ID_MAP, conversationIdEntryMap);
         }
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

   public static Manager instance()
   {
      if ( !Contexts.isEventContextActive() )
      {
         throw new IllegalStateException("No active event context");
      }
      Manager instance = (Manager) Component.getInstance(Manager.class, ScopeType.EVENT, true);
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
      Iterator<Map.Entry<String, ConversationEntry>> entries = getConversationIdEntryMap().entrySet().iterator();
      while ( entries.hasNext() )
      {
         Map.Entry<String, ConversationEntry> entry = entries.next();
         ConversationEntry conversationEntry = entry.getValue();
         long delta = currentTime - conversationEntry.getLastRequestTime();
         if ( delta > conversationEntry.getTimeout() )
         {
            String conversationId = entry.getKey();
            log.debug("conversation timeout for conversation: " + conversationId);
            Session session = Session.getSession(externalContext, true);
            destroyConversation(conversationId, session, entries);
         }
      }
   }

   private void destroyConversation(String conversationId, Session session, Iterator iter)
   {
      ServerConversationContext conversationContext = new ServerConversationContext(session, conversationId);
      Contexts.destroy( conversationContext );
      conversationContext.clear();
      conversationContext.flush();
      iter.remove();
      dirty();
   }

   public void storeConversation(Map attributes, Session session)
   {
      if ( isLongRunningConversation() )
      {
         touchConversationStack();

         if ( !Seam.isSessionInvalid() )
         {
            log.debug("Storing conversation state: " + currentConversationId);
            Conversation.instance().flush();
            //if the session is invalid, don't put the conversation id
            //in the view, 'cos we are expecting the conversation to
            //be destroyed by the servlet session listener
            attributes.put(CONVERSATION_ID, currentConversationId);

            if ( Init.instance().isJbpmInstalled() )
            {
               Pageflow pageflow = Pageflow.instance();
               if ( pageflow.isInProcess() )
               {
                  attributes.put( PAGEFLOW_COUNTER, pageflow.getPageflowCounter() );
               }
            }
         }
         //even if the session is invalid, still put the id in the map,
         //so it can be cleaned up along with all the other conversations
      }
      else
      {
         log.debug("Discarding conversation state: " + currentConversationId);

         LinkedList<String> stack = Manager.instance().getCurrentConversationIdStack();
         if ( stack.size()>1 )
         {
            String outerConversationId = stack.get(1);
            attributes.put(CONVERSATION_ID, outerConversationId);
         }
         else
         {
            attributes.remove(CONVERSATION_ID);
         }

         //now safe to remove the entry
         removeCurrentConversationAndDestroyNestedContexts(session);

      }
   }

   public void storeConversation(HttpServletResponse response, Session session)
   {
      if ( isLongRunningConversation() )
      {
         touchConversationStack();

         log.debug("Storing conversation state: " + currentConversationId);
         Conversation.instance().flush();
         if ( !Seam.isSessionInvalid() )
         {
            //if the session is invalid, don't put the conversation id
            //in the view, 'cos we are expecting the conversation to
            //be destroyed by the servlet session listener
            response.setHeader("conversationId", currentConversationId);
         }
         //even if the session is invalid, still put the id in the map,
         //so it can be cleaned up along with all the other conversations
      }
      else
      {
         log.debug("Discarding conversation state: " + currentConversationId);

         LinkedList<String> stack = Manager.instance().getCurrentConversationIdStack();
         if ( stack.size()>1 )
         {
            String outerConversationId = stack.get(1);
            response.setHeader("conversationId", outerConversationId);
         }

         //now safe to remove the entry
         removeCurrentConversationAndDestroyNestedContexts(session);
      }
   }

   private void removeCurrentConversationAndDestroyNestedContexts(Session session) {
      removeConversationEntry(currentConversationId);
      destroyNestedContexts(session, currentConversationId);
   }

   private void destroyNestedContexts(Session session, String conversationId) {
      Iterator<ConversationEntry> entries = getConversationIdEntryMap().values().iterator();
      while ( entries.hasNext() )
      {
         ConversationEntry ce = entries.next();
         if ( ce.getConversationIdStack().contains(conversationId) )
         {
            String entryConversationId = ce.getId();
            log.debug("destroying nested conversation: " + entryConversationId);
            destroyConversation(entryConversationId, session, entries);
         }
      }
   }

   public void restoreConversation(Map attributes, Map parameters)
   {

      //First, try to get the conversation id from a request parameter
      String storedConversationId = (String) parameters.get("conversationId");
      if ( isMissing(storedConversationId) && attributes!=null )
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
         setCurrentConversationId(storedConversationId);
         ConversationEntry ce = getCurrentConversationEntry();
         setCurrentConversationIdStack( ce.getConversationIdStack() );

         if ( ce.isRemoveAfterRedirect() )
         {
            setLongRunningConversation(false);
         }

      }
      else
      {
         //there was no id in either place, so there is no
         //long-running conversation to restore
         log.debug("No stored conversation");
         initializeTemporaryConversation();
      }

   }

   private boolean isMissing(String storedConversationId) {
      return storedConversationId==null || "".equals(storedConversationId);
   }

   public void initializeTemporaryConversation()
   {
      String id = Id.nextId();
      setCurrentConversationId(id);
      setCurrentConversationIdStack(id);
      setLongRunningConversation(false);
   }

   private ConversationEntry createConversationEntry()
   {
      ConversationEntry ce = new ConversationEntry( getCurrentConversationId(), getCurrentConversationIdStack() );
      getConversationIdEntryMap().put( getCurrentConversationId() , ce );
      dirty();
      return ce;
   }

   public void beginConversation(String initiator)
   {
      setLongRunningConversation(true);
      createConversationEntry().setInitiatorComponentName(initiator);
      Conversation.instance(); //force instantiation of the Conversation in the outer (non-nested) conversation
   }

   public void endConversation()
   {
      setLongRunningConversation(false);
   }

   public void beginNestedConversation(String ownerName)
   {
      LinkedList<String> stack = getCurrentConversationIdStack();
      String id = Id.nextId();
      setCurrentConversationId(id);
      setCurrentConversationIdStack(id);
      getCurrentConversationIdStack().addAll(stack);
      ConversationEntry conversationEntry = createConversationEntry();
      conversationEntry.setInitiatorComponentName(ownerName);
   }

   public ConversationEntry getCurrentConversationEntry() {
      return getConversationEntry( getCurrentConversationId() );
   }
   
   public void leaveConversation()
   {
      initializeTemporaryConversation();
   }

   public boolean swapConversation(String id)
   {
      ConversationEntry ce = getConversationEntry(id);
      if (ce!=null)
      {
         setCurrentConversationId(id);
         setCurrentConversationIdStack( ce.getConversationIdStack() );
         setLongRunningConversation(true);
         return true;
      }
      else
      {
         return false;
      }
   }

   public int getConversationTimeout() {
      return conversationTimeout;
   }

   public void setConversationTimeout(int conversationTimeout) {
      this.conversationTimeout = conversationTimeout;
   }

   private void beforeRedirect()
   {
      ConversationEntry ce = getConversationEntry(currentConversationId);
      if (ce==null)
      {
         ce = createConversationEntry();
      }
      //ups, we don't really want to destroy it on this request after all!
      ce.setRemoveAfterRedirect( !isLongRunningConversation() );
      setLongRunningConversation(true);
   }

   /**
    * Beware of side-effect!
    */
   public String encodeConversationId(String url) {
      beforeRedirect();
      return url + "?conversationId=" + getCurrentConversationId();
   }

   public void redirect(String viewId)
   {
      FacesContext context = FacesContext.getCurrentInstance();
      String url = context.getApplication().getViewHandler().getActionURL( context, viewId );
      url = encodeConversationId(url);
      ExternalContext externalContext = context.getExternalContext();
      try
      {
         externalContext.redirect( externalContext.encodeActionURL(url) );
      }
      catch (IOException ioe)
      {
         throw new RuntimeException("could not redirect to: " + url, ioe);
      }
      context.responseComplete(); //work around MyFaces bug in 1.1.1
   }
   
   public void prepareBackswitch(PhaseEvent event) {
      if ( isLongRunningConversation() )
      {
         //important: only do this stuff when a long-running
         //           conversation exists, otherwise we would
         //           force creation of a conversation entry
         
         Conversation conversation = Conversation.instance();

         //stuff from jPDL takes precedence
         Page page = Init.instance().isJbpmInstalled() ?
               Pageflow.instance().getPage() : null;
         if (page!=null)
         {
            if ( page.hasDescription() )
            {
               conversation.setDescription( page.getDescription() );
               conversation.setViewId( page.getViewId() );
            }
            conversation.setTimeout( page.getTimeout() );
         }
         else
         {
            //handle stuff defined in pages.xml
            String viewId = event.getFacesContext().getViewRoot().getViewId();
            Pages pages = Pages.instance();
            if (pages!=null) //for tests
            {
               if ( pages.hasDescription(viewId) )
               {
                  conversation.setDescription( pages.getDescription(viewId) );
                  conversation.setViewId(viewId);
               }
               conversation.setTimeout( pages.getTimeout(viewId) );
            }
         }

      }
   }

}
