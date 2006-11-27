/*
 * JBoss, Home of Professional Open Source
 *
 * Distributable under LGPL license.
 * See terms of license at gnu.org.
 */
package org.jboss.seam.core;

import static org.jboss.seam.InterceptionType.NEVER;
import static org.jboss.seam.annotations.Install.BUILT_IN;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;
import java.util.StringTokenizer;

import javax.faces.application.FacesMessage;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.faces.event.PhaseId;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jboss.seam.Component;
import org.jboss.seam.ScopeType;
import org.jboss.seam.Seam;
import org.jboss.seam.annotations.Install;
import org.jboss.seam.annotations.Intercept;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.contexts.ContextAdaptor;
import org.jboss.seam.contexts.Contexts;
import org.jboss.seam.contexts.Lifecycle;
import org.jboss.seam.contexts.ServerConversationContext;
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
@Install(precedence=BUILT_IN)
@Intercept(NEVER)
public class Manager
{
   private static final Log log = LogFactory.getLog(Manager.class);

   //The id of the current conversation
   private String currentConversationId;
   private List<String> currentConversationIdStack;

   //Is the current conversation "long-running"?
   private boolean isLongRunningConversation;
   
   private boolean updateModelValuesCalled;

   private boolean controllingRedirect;
   
   private boolean destroyBeforeRedirect;
   
   private int conversationTimeout = 600000; //10 mins
   private int concurrentRequestTimeout = 1000; //one second
   
   private String conversationIdParameter = "conversationId";
   private String parentConversationIdParameter = "parentConversationId";
   private String conversationIsLongRunningParameter = "conversationIsLongRunning";

   public String getCurrentConversationId()
   {
      return currentConversationId;
   }

   /**
    * Only public for the unit tests!
    * @param id
    */
   public void setCurrentConversationId(String id)
   {
      currentConversationId = id;
      currentConversationEntry = null;
   }
   
   public void updateCurrentConversationId(String id)
   {
      String[] names = Contexts.getConversationContext().getNames();
      Object[] values = new Object[names.length];
      for (int i=0; i<names.length; i++)
      {
         values[i] = Contexts.getConversationContext().get(names[i]);
         Contexts.getConversationContext().remove(names[i]);
      }
      Contexts.getConversationContext().flush();
      
      ConversationEntries.instance().updateConversationId( getCurrentConversationId(), id );
      currentConversationIdStack.set(0, id);
      setCurrentConversationId(id);
      //TODO: update nested conversations!!!!!
      
      for (int i=0; i<names.length; i++)
      {
         Contexts.getConversationContext().set(names[i], values[i]);
      }
   }

   private static void touchConversationStack(List<String> stack)
   {
      if ( stack!=null )
      {
         //iterate in reverse order, so that current conversation 
         //sits at top of conversation lists
         ListIterator<String> iter = stack.listIterator( stack.size() );
         while ( iter.hasPrevious() )
         {
            String conversationId = iter.previous();
            ConversationEntry conversationEntry = ConversationEntries.instance().getConversationEntry(conversationId);
            if (conversationEntry!=null)
            {
               conversationEntry.touch();
            }
         }
      }
   }
   
   private static void endNestedConversations(String id)
   {
      for ( ConversationEntry ce: ConversationEntries.instance().getConversationEntries() )
      {
         if ( ce.getConversationIdStack().contains(id) )
         {
            ce.end();
         }
      }
   }
   
   /**
    * Get the name of the component that started the current
    * conversation.
    * 
    * @deprecated
    */
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

   public List<String> getCurrentConversationIdStack()
   {
      return currentConversationIdStack;
   }

   private void setCurrentConversationIdStack(List<String> stack)
   {
      currentConversationIdStack = stack;
   }

   private List<String> createCurrentConversationIdStack(String id)
   {
      currentConversationIdStack = new ArrayList<String>();
      currentConversationIdStack.add(id);
      return currentConversationIdStack;
   }

   public String getCurrentConversationDescription()
   {
      ConversationEntry ce = getCurrentConversationEntry();
      if ( ce==null ) return null;
      return ce.getDescription();
   }

   public Integer getCurrentConversationTimeout()
   {
      ConversationEntry ce = getCurrentConversationEntry();
      if ( ce==null ) return null;
      return ce.getTimeout();
   }

   public String getCurrentConversationViewId()
   {
      ConversationEntry ce = getCurrentConversationEntry();
      if ( ce==null ) return null;
      return ce.getViewId();
   }
   
   public String getParentConversationViewId()
   {
      ConversationEntry conversationEntry = ConversationEntries.instance().getConversationEntry(getParentConversationId());
      return conversationEntry==null ? null : conversationEntry.getViewId();
   }
   
   public String getParentConversationId()
   {
      return currentConversationIdStack==null || currentConversationIdStack.size()<2 ?
            null : currentConversationIdStack.get(1);
   }

   public String getRootConversationId()
   {
      return currentConversationIdStack==null || currentConversationIdStack.size()<1 ?
            null : currentConversationIdStack.get( currentConversationIdStack.size()-1 );
   }

   public boolean isLongRunningConversation()
   {
      return isLongRunningConversation;
   }

   public boolean isLongRunningOrNestedConversation()
   {
      return isLongRunningConversation() || isNestedConversation();
   }

   public boolean isReallyLongRunningConversation()
   {
      return isLongRunningConversation() && 
            !getCurrentConversationEntry().isRemoveAfterRedirect() &&
            !Seam.isSessionInvalid();
   }
   
   public boolean isNestedConversation()
   {
      return currentConversationIdStack!=null && 
            currentConversationIdStack.size()>1;
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
      Manager instance = (Manager) Component.getInstance(Manager.class, ScopeType.EVENT);
      if (instance==null)
      {
         throw new IllegalStateException("No Manager could be created, make sure the Component exists in application scope");
      }
      return instance;
   }

   /**
    * Clean up timed-out conversations
    */
   public void conversationTimeout(ContextAdaptor session)
   {
      long currentTime = System.currentTimeMillis();
      List<ConversationEntry> entries = new ArrayList<ConversationEntry>( ConversationEntries.instance().getConversationEntries() );
      for (ConversationEntry conversationEntry: entries)
      {
         boolean locked = conversationEntry.lockNoWait(); //we had better not wait for it, or we would be waiting for ALL other requests
         try
         {
            long delta = currentTime - conversationEntry.getLastRequestTime();
            if ( delta > conversationEntry.getTimeout() )
            {
               if ( locked )
               { 
                  if ( log.isDebugEnabled() )
                  {
                     log.debug("conversation timeout for conversation: " + conversationEntry.getId());
                  }
               }
               else
               {
                  //if we could not acquire the lock, someone has left a garbage lock lying around
                  //the reason garbage locks can exist is that we don't require a servlet filter to
                  //exist - but if we do use SeamExceptionFilter, it will clean up garbage and this
                  //case should never occur
                  
                  //NOTE: this is slightly broken - in theory there is a window where a new request 
                  //      could have come in and got the lock just before us but called touch() just 
                  //      after we check the timeout - but in practice this would be extremely rare, 
                  //      and that request will get an IllegalMonitorStateException when it tries to 
                  //      unlock() the CE
                  log.info("destroying conversation with garbage lock: " + conversationEntry.getId());
               }
               destroyConversation( conversationEntry.getId(), session );
            }
         }
         finally
         {
            if (locked) conversationEntry.unlock();
         }
      }
   }

   /**
    * Clean up all state associated with a conversation
    */
   private void destroyConversation(String conversationId, ContextAdaptor session)
   {
      ServerConversationContext conversationContext = new ServerConversationContext(session, conversationId);
      Contexts.destroy( conversationContext );
      conversationContext.clear();
      conversationContext.flush();
      ConversationEntries.instance().removeConversationEntry(conversationId);
   }
   
   /**
    * Touch the conversation stack, destroy ended conversations, 
    * and timeout inactive conversations.
    */
   public void endRequest(ContextAdaptor session)
   {
      if ( isLongRunningConversation() )
      {
         if ( log.isDebugEnabled() )
         {
            log.debug("Storing conversation state: " + getCurrentConversationId());
         }
         touchConversationStack( getCurrentConversationIdStack() );
      }
      else
      {
         if ( log.isDebugEnabled() )
         {
            log.debug("Discarding conversation state: " + getCurrentConversationId());
         }
         //now safe to remove the entry
         removeCurrentConversationAndDestroyNestedContexts(session);
      }

      if ( !Init.instance().isClientSideConversations() ) 
      {
         // difficult question: is it really safe to do this here?
         // right now we do have to do it after committing the Seam
         // transaction because we can't close EMs inside a txn
         // (this might be a bug in HEM)
         Manager.instance().conversationTimeout(session);
      }
   }
   
   public void unlockConversation()
   {
      ConversationEntry ce = getCurrentConversationEntry();
      if (ce!=null) 
      {
         if ( ce.isLockedByCurrentThread() )
         {
            ce.unlock();
         }
      }
      else if ( isNestedConversation() )
      {
         ConversationEntries.instance().getConversationEntry( getParentConversationId() ).unlock();
      }
   }

   private void removeCurrentConversationAndDestroyNestedContexts(ContextAdaptor session) 
   {
      ConversationEntries.instance().removeConversationEntry( getCurrentConversationId() );
      destroyNestedConversationContexts( session, getCurrentConversationId() );
   }

   private void destroyNestedConversationContexts(ContextAdaptor session, String conversationId) 
   {
      List<ConversationEntry> entries = new ArrayList<ConversationEntry>( ConversationEntries.instance().getConversationEntries() );
      for  ( ConversationEntry ce: entries )
      {
         if ( ce.getConversationIdStack().contains(conversationId) )
         {
            String entryConversationId = ce.getId();
            log.debug("destroying nested conversation: " + entryConversationId);
            destroyConversation(entryConversationId, session);
         }
      }
   }

   private String getPropagationFromRequestParameter(Map parameters)
   {
      Object type = parameters.get("conversationPropagation");
      if (type==null)
      {
         return null;
      }
      else if (type instanceof String)
      {
         return (String) type;
      }
      else
      {
         return ( (String[]) type )[0];
      }
   }
   
   /**
    * Initialize the request conversation context, taking
    * into account conversation propagation style, and
    * any conversation id passed as a request parameter
    * or in the PAGE context.
    * 
    * @param parameters the request parameters
    * @return false if the conversation id referred to a 
    *         long-running conversation that was not found
    */
   public boolean restoreConversation(Map parameters)
   {
      
      //First, try to get the conversation id from a request parameter
      String storedConversationId = getRequestParameterValue(parameters, conversationIdParameter);
      String storedParentConversationId = getRequestParameterValue(parameters, parentConversationIdParameter);
      Boolean isLongRunningConversation = "true".equals( getRequestParameterValue(parameters, conversationIsLongRunningParameter) );
      
      if ( isMissing(storedConversationId) )
      {
         if ( Contexts.isPageContextActive() )
         {
            //if it is not passed as a request parameter,
            //try to get it from the page context
            org.jboss.seam.core.FacesPage page = org.jboss.seam.core.FacesPage.instance();
            storedConversationId = page.getConversationId();
            storedParentConversationId = null;
            isLongRunningConversation = page.isConversationLongRunning();
            //if (isLongRunningConversation==null) isLongRunningConversation = false;
         }
      }

      else
      {
         log.debug("Found conversation id in request parameter: " + storedConversationId);
      }
      
      //TODO: this approach is deprecated, remove code:
      if ( "new".equals(storedConversationId) )
      {
         storedConversationId = null;
         storedParentConversationId = null;
         isLongRunningConversation = false;
      }
      //end code to remove

      String propagation = getPropagationFromRequestParameter(parameters);
      if ( "none".equals(propagation) )
      {
         storedConversationId = null;
         storedParentConversationId = null;
         isLongRunningConversation = false;
      }
      
      return restoreConversation(storedConversationId, storedParentConversationId, isLongRunningConversation) 
            || "end".equals(propagation);
      
   }
   
   /**
    * Look for a conversation propagation style in the request
    * parameters and begin, nest or join the conversation,
    * as necessary.
    * 
    * @param parameters the request parameters
    */
   public void handleConversationPropagation(Map parameters)
   {
      
      String propagation = getPropagationFromRequestParameter(parameters);
      
      if ( propagation!=null && propagation.startsWith("begin") )
      {
         if ( isLongRunningConversation )
         {
            throw new IllegalStateException("long-running conversation already active");
         }
         beginConversation(null);
         if (propagation.length()>6)
         {
            Pageflow.instance().begin( propagation.substring(6) );
         }
      }
      else if ( propagation!=null && propagation.startsWith("join") )
      {
         if ( !isLongRunningConversation )
         {
            beginConversation(null);
            if (propagation.length()>5)
            {
               Pageflow.instance().begin( propagation.substring(5) );
            }
         }
      }
      else if ( propagation!=null && propagation.startsWith("nest") )
      {
         beginNestedConversation(null);
         if (propagation.length()>5)
         {
            Pageflow.instance().begin( propagation.substring(5) );
         }
      }
      else if ( "end".equals(propagation) )
      {
         endConversation(false);
      }

   }
   
   /**
    * Initialize the request conversation context, given the 
    * conversation id. If no conversation entry is found, or
    * conversationId is null, initialize a new temporary
    * conversation context.
    * 
    * @return true if the conversation with the given id was found
    */
   public boolean restoreConversation(String conversationId)
   {
      return restoreConversation(conversationId, null, false);
   }

   /**
    * Initialize the request conversation context, given the 
    * conversation id and optionally a parent conversation id.
    * If no conversation entry is found for the first id, try
    * the parent, and if that also fails, initialize a new 
    * temporary conversation context.
    */
   private boolean restoreConversation(String conversationId, String parentConversationId, boolean isLongRunningConversation) {
      ConversationEntry ce = null;
      if (conversationId!=null)
      {
         ConversationEntries entries = ConversationEntries.instance();
         ce = entries.getConversationEntry(conversationId);
         if (ce==null)
         {
            ce = entries.getConversationEntry(parentConversationId);
         }
      }
      
      return restoreAndLockConversation(ce, isLongRunningConversation);
   }

   private boolean restoreAndLockConversation(ConversationEntry ce, boolean isLongRunningConversation)
   {
      if ( ce!=null && ce.lock() )
      {
         // do this asap, since there is a window where conversationTimeout() might  
         // try to destroy the conversation, even if he cannot obtain the lock!
         touchConversationStack( ce.getConversationIdStack() );

         //we found an id and obtained the lock, so restore the long-running conversation
         log.debug("Restoring conversation with id: " + ce.getId());
         setLongRunningConversation(true);
         setCurrentConversationId( ce.getId() );
         setCurrentConversationIdStack( ce.getConversationIdStack() );

         boolean removeAfterRedirect = ce.isRemoveAfterRedirect() && !(
               Init.instance().isDebug() &&
               "/debug.xhtml".equals( FacesContext.getCurrentInstance().getViewRoot().getViewId() )
            );
         
         if (removeAfterRedirect)
         {
            setLongRunningConversation(false);
            ce.setRemoveAfterRedirect(false);
         }
         
         return true;

      }
      else
      {
         //there was no id in either place, so there is no
         //long-running conversation to restore
         log.debug("No stored conversation, or concurrent call to the stored conversation");
         initializeTemporaryConversation();
         return !isLongRunningConversation;
      }
   }

   /**
    * Retrieve the conversation id from the request parameters.
    * 
    * @param parameters the request parameters
    * @return the conversation id
    */
   private String getRequestParameterValue(Map parameters, String parameterName) {
      Object object = parameters.get(parameterName);
      if (object==null)
      {
         return null;
      }
      else
      {
         if ( object instanceof String )
         {
            //when it comes from JSF it is (usually?) a plain string
            return (String) object;
         }
         else
         {
            //in a servlet it is a string array
            String[] values = (String[]) object;
            if (values.length!=1)
            {
               throw new IllegalArgumentException("expected exactly one value for conversationId request parameter");
            }
            return values[0];
         }
      }
   }

   private boolean isMissing(String storedConversationId) {
      return storedConversationId==null || "".equals(storedConversationId);
   }
   
   /**
    * Initialize a new temporary conversation context,
    * and assign it a conversation id.
    */
   public void initializeTemporaryConversation()
   {
      String id = Id.nextId();
      setCurrentConversationId(id);
      createCurrentConversationIdStack(id);
      setLongRunningConversation(false);
   }

   private ConversationEntry createConversationEntry()
   {
      ConversationEntry entry = ConversationEntries.instance()
            .createConversationEntry( getCurrentConversationId(), getCurrentConversationIdStack() );
      if ( !entry.isNested() ) 
      {
         //if it is a newly created nested 
         //conversation, we already own the
         //lock
         entry.lock();
      }
      return entry;
   }

   /**
    * Promote a temporary conversation and make it long-running
    * 
    * @param initiator the name of the component starting the conversation.
    */
   @SuppressWarnings("deprecation")
   public void beginConversation(String initiator)
   {
      log.debug("Beginning long-running conversation");
      setLongRunningConversation(true);
      createConversationEntry().setInitiatorComponentName(initiator);
      Conversation.instance(); //force instantiation of the Conversation in the outer (non-nested) conversation
      storeConversationToViewRootIfNecessary();
      if ( Events.exists() ) Events.instance().raiseEvent("org.jboss.seam.beginConversation");
   }

   /**
    * Begin a new nested conversation.
    * 
    * @param ownerName the name of the component starting the conversation
    */
   @SuppressWarnings("deprecation")
   public void beginNestedConversation(String ownerName)
   {
      log.debug("Beginning nested conversation");
      List<String> oldStack = getCurrentConversationIdStack();
      if (oldStack==null)
      {
         throw new IllegalStateException("No long-running conversation active");
      }
      String id = Id.nextId();
      setCurrentConversationId(id);
      createCurrentConversationIdStack(id).addAll(oldStack);
      ConversationEntry conversationEntry = createConversationEntry();
      conversationEntry.setInitiatorComponentName(ownerName);
      storeConversationToViewRootIfNecessary();
      if ( Events.exists() ) Events.instance().raiseEvent("org.jboss.seam.beginConversation");
   }
   
   /**
    * Make a long-running conversation temporary.
    */
   public void endConversation(boolean beforeRedirect)
   {
      log.debug("Ending long-running conversation");
      if ( Events.exists() ) Events.instance().raiseEvent("org.jboss.seam.endConversation");
      setLongRunningConversation(false);
      destroyBeforeRedirect = beforeRedirect;
      endNestedConversations( getCurrentConversationId() );
      storeConversationToViewRootIfNecessary();
   }
   
   private void storeConversationToViewRootIfNecessary()
   {
      FacesContext facesContext = FacesContext.getCurrentInstance();
      if ( facesContext!=null && Lifecycle.getPhaseId()==PhaseId.RENDER_RESPONSE )
      {
         FacesPage.instance().storeConversation();
      }
   }

   // two reasons for this: 
   // (1) a cache
   // (2) so we can unlock() it after destruction of the session context 
   private ConversationEntry currentConversationEntry; 
   public ConversationEntry getCurrentConversationEntry() {
      if (currentConversationEntry==null)
      {
         currentConversationEntry = ConversationEntries.instance().getConversationEntry( getCurrentConversationId() );
      }
      return currentConversationEntry;
   }
   
   /**
    * Leave the scope of the current conversation, leaving
    * it completely intact.
    */
   public void leaveConversation()
   {
      initializeTemporaryConversation();
   }

   /**
    * Switch to another long-running conversation.
    * 
    * @param id the id of the conversation to switch to
    * @return true if the conversation exists
    */
   public boolean switchConversation(String id)
   {
      ConversationEntry ce = ConversationEntries.instance().getConversationEntry(id);
      if (ce!=null)
      {
         if ( ce.lock() )
         {
            unlockConversation();
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

   /**
    * Temporarily promote a temporary conversation to
    * a long running conversation for the duration of
    * a browser redirect. After the redirect, the 
    * conversation will be demoted back to a temporary
    * conversation.
    */
   public void beforeRedirect()
   {
      if (!destroyBeforeRedirect)
      {
         ConversationEntry ce = getCurrentConversationEntry();
         if (ce==null)
         {
            ce = createConversationEntry();
         }
         //ups, we don't really want to destroy it on this request after all!
         ce.setRemoveAfterRedirect( !isLongRunningConversation() );
         setLongRunningConversation(true);
      }
   }

   /**
    * Add the conversation id to a URL, if necessary
    */
   public String encodeConversationId(String url) {
      if ( Seam.isSessionInvalid() )
      {
         return url;
      }
      else if (destroyBeforeRedirect)
      {
         if ( isNestedConversation() )
         {
            return new StringBuilder( url.length() + conversationIdParameter.length() + 5 )
                  .append(url)
                  .append( url.contains("?") ? '&' : '?' )
                  .append(conversationIdParameter)
                  .append('=')
                  .append( getParentConversationId() )
                  .append('&')
                  .append(conversationIsLongRunningParameter)
                  .append("=true")
                  .toString();
         }
         else
         {
            return url;
         }
      }
      else
      {
         StringBuilder builder = new StringBuilder( url.length() + conversationIdParameter.length() + 5 )
               .append(url)
               .append( url.contains("?") ? '&' : '?' )
               .append(conversationIdParameter)
               .append('=')
               .append( getCurrentConversationId() );
         if ( isNestedConversation() && !isReallyLongRunningConversation() )
         {
            builder.append('&')
                  .append(parentConversationIdParameter)
                  .append('=')
                  .append( getParentConversationId() );
         }
         if ( isReallyLongRunningConversation() )
         {
            builder.append('&')
                  .append(conversationIsLongRunningParameter)
                  .append("=true");
         }
         return builder.toString();
      }
   }

   /**
    * Redirect to the given view id, encoding the conversation id
    * into the request URL.
    * 
    * @param viewId the JSF view id
    */
   public void redirect(String viewId)
   {
      redirect(viewId, null, true);
   }
   
   public void interpolateAndRedirect(String url)
   {
      Map<String, Object> parameters = new HashMap<String, Object>();
      int loc = url.indexOf('?');
      if (loc>0)
      {
         StringTokenizer tokens = new StringTokenizer( url.substring(loc), "?=&" );
         while ( tokens.hasMoreTokens() )
         {
            String name = tokens.nextToken();
            String value = Interpolator.instance().interpolate( tokens.nextToken() );
            parameters.put(name, value);
         }
         url = url.substring(0, loc);
      }
      redirect(url, parameters, true);
   }
   
   /**
    * Add the parameters to a URL
    */
   public String encodeParameters(String url, Map<String, Object> parameters)
   {
      if ( parameters.isEmpty() ) return url;
      
      StringBuilder builder = new StringBuilder(url);
      for ( Map.Entry<String, Object> param: parameters.entrySet() )
      {
         Object parameterValue = param.getValue();
         String parameterName = param.getKey();
         if (parameterValue instanceof Iterable)
         {
            for ( Object value: (Iterable) parameterValue )
            {
               builder.append('&')
                  .append(parameterName)
                  .append('=')
                  .append(value);
            }
         }
         else
         {
            builder.append('&')
                  .append(parameterName)
                  .append('=')
                  .append(parameterValue);
         }
      }
      if ( url.indexOf('?')<0 ) 
      {
         builder.setCharAt( url.length() ,'?' );
      }
      return builder.toString();
   }
   
   /**
    * Redirect to the given view id, after encoding parameters and conversation id 
    * into the request URL.
    * 
    * @param viewId the JSF view id
    * @param parameters request parameters to be encoded (possibly null)
    * @param includeConversationId determines if the conversation id is to be encoded
    */
   public void redirect(String viewId, Map<String, Object> parameters, boolean includeConversationId)
   {
      FacesContext context = FacesContext.getCurrentInstance();
      String url = context.getApplication().getViewHandler().getActionURL(context, viewId);
      if (parameters!=null) 
      {
         url = encodeParameters(url, parameters);
      }
      if (includeConversationId)
      {
         url = encodeConversationId(url);
         beforeRedirect();
      }
      ExternalContext externalContext = context.getExternalContext();
      controllingRedirect = true;
      try
      {
         externalContext.redirect( externalContext.encodeActionURL(url) );
      }
      catch (IOException ioe)
      {
         throw new RuntimeException("could not redirect to: " + url, ioe);
      }
      finally
      {
         controllingRedirect = false;
      }
      context.responseComplete(); //work around MyFaces bug in 1.1.1
   }
   
   /**
    * Called by the Seam Redirect Filter when a redirect is called.
    * Appends the conversationId parameter if necessary.
    * 
    * @param url the requested URL
    * @return the resulting URL with the conversationId appended
    */
   public String appendConversationIdFromRedirectFilter(String url)
   {
      boolean appendConversationId = !controllingRedirect && 
            !url.contains("?" + getConversationIdParameter() +"=");
      if (appendConversationId)
      {
         
         url = encodeConversationId(url);
         beforeRedirect();
      }
      return url;
   }

   /**
    * If a page description is defined, remember the description and
    * view id for the current page, to support conversation switching.
    * Called just before the render phase.
    */
   public void prepareBackswitch(FacesContext facesContext) {
      
      Conversation conversation = Conversation.instance();

      //stuff from jPDL takes precedence
      org.jboss.seam.pageflow.Page pageflowPage = 
            isLongRunningConversation() &&
            Init.instance().isJbpmInstalled() && 
            Pageflow.instance().isInProcess() ?
                  Pageflow.instance().getPage() : null;
      
      if (pageflowPage==null)
      {
         //handle stuff defined in pages.xml
         String viewId = facesContext.getViewRoot().getViewId();
         Pages pages = Pages.instance();
         if (pages!=null) //for tests
         {
            org.jboss.seam.core.Page pageEntry = pages.getPage(viewId);
            if ( pageEntry.isSwitchEnabled() )
            {
               conversation.setViewId(viewId);
            }
            if ( pageEntry.hasDescription() )
            {
               conversation.setDescription( pageEntry.renderDescription() );
            }
            conversation.setTimeout( Pages.instance().getTimeout(viewId) );
         }
      }
      else
      {
         //use stuff from the pageflow definition
         if ( pageflowPage.isSwitchEnabled() )
         {
            conversation.setViewId( pageflowPage.getViewId() );
         }
         if ( pageflowPage.hasDescription() )
         {
            conversation.setDescription( pageflowPage.getDescription() );
         }
         conversation.setTimeout( pageflowPage.getTimeout() );
      }
      
      if ( isLongRunningConversation() )
      {
         //important: only do this stuff when a long-running
         //           conversation exists, otherwise we would
         //           force creation of a conversation entry
         conversation.flush();
      }

   }

   public String getConversationIdParameter()
   {
      return conversationIdParameter;
   }

   public void setConversationIdParameter(String conversationIdParameter)
   {
      this.conversationIdParameter = conversationIdParameter;
   }

   public String getConversationIsLongRunningParameter()
   {
      return conversationIsLongRunningParameter;
   }

   public void setConversationIsLongRunningParameter(String conversationIdLongRunning)
   {
      this.conversationIsLongRunningParameter = conversationIdLongRunning;
   }

   public void redirectToNoConversationView()
   {
      noConversation();
      
      //stuff from jPDL takes precedence
      org.jboss.seam.core.FacesPage facesPage = org.jboss.seam.core.FacesPage.instance();
      String pageflowName = facesPage.getPageflowName();
      String pageflowNodeName = facesPage.getPageflowNodeName();
      
      String noConversationViewId = null;
      if (pageflowName==null || pageflowNodeName==null)
      {
         String viewId = FacesContext.getCurrentInstance().getViewRoot().getViewId();
         Pages pages = Pages.instance();
         if (pages!=null) //for tests
         {
            noConversationViewId = pages.getNoConversationViewId(viewId);
         }
      }
      else
      {
         noConversationViewId = Pageflow.instance().getNoConversationViewId(pageflowName, pageflowNodeName);
      }
      
      if (noConversationViewId!=null)
      {
         redirect( noConversationViewId );
      }
   }

   protected void noConversation()
   {
      FacesMessages.instance().addFromResourceBundle( 
            FacesMessage.SEVERITY_WARN, 
            "org.jboss.seam.NoConversation", 
            "The conversation ended, timed out or was processing another request" 
         );
   }

   public boolean isUpdateModelValuesCalled()
   {
      return updateModelValuesCalled;
   }

   public void setUpdateModelValuesCalled(boolean updateModelValuesCalled)
   {
      this.updateModelValuesCalled = updateModelValuesCalled;
   }

   public int getConcurrentRequestTimeout()
   {
      return concurrentRequestTimeout;
   }

   public void setConcurrentRequestTimeout(int requestWait)
   {
      this.concurrentRequestTimeout = requestWait;
   }

   @Override
   public String toString()
   {
      return "Manager(" + currentConversationIdStack + ")";
   }

   protected String getParentConversationIdParameter()
   {
      return parentConversationIdParameter;
   }

   protected void setParentConversationIdParameter(String nestedConversationIdParameter)
   {
      this.parentConversationIdParameter = nestedConversationIdParameter;
   }

}
