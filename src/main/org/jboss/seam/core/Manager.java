/*
 * JBoss, Home of Professional Open Source
 *
 * Distributable under LGPL license.
 * See terms of license at gnu.org.
 */
package org.jboss.seam.core;

import static org.jboss.seam.InterceptionType.NEVER;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;

import javax.faces.application.FacesMessage;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.faces.event.PhaseEvent;
import javax.portlet.ActionResponse;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jboss.seam.Component;
import org.jboss.seam.ScopeType;
import org.jboss.seam.Seam;
import org.jboss.seam.annotations.Intercept;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.contexts.ContextAdaptor;
import org.jboss.seam.contexts.Contexts;
import org.jboss.seam.contexts.ServerConversationContext;
import org.jboss.seam.pageflow.Page;
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
   private static final Log log = LogFactory.getLog(Manager.class);

   private static final String NAME = Seam.getComponentName(Manager.class);
   public static final String CONVERSATION_ID = NAME + ".conversationId";
   public static final String CONVERSATION_IS_LONG_RUNNING = NAME + ".conversationIsLongRunning";
   public static final String PAGEFLOW_COUNTER = NAME + ".pageflowCounter";
   public static final String PAGEFLOW_NODE_NAME = NAME + ".pageflowNodeName";
   public static final String PAGEFLOW_NAME = NAME + ".pageflowName";

   //The id of the current conversation
   private String currentConversationId;
   private List<String> currentConversationIdStack;

   //Is the current conversation "long-running"?
   private boolean isLongRunningConversation;
   
   private boolean updateModelValuesCalled;

   private boolean controllingRedirect;
   
   private boolean destroyBeforeRedirect;
   
   private int conversationTimeout = 600000; //10 mins
   
   private String conversationIdParameter = "conversationId";
   private String conversationIsLongRunningParameter = "conversationIsLongRunning";

   private boolean conversationAlreadyStored;

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

   private void touchConversationStack()
   {
      List<String> stack = getCurrentConversationIdStack();
      if ( stack!=null )
      {
         for ( String conversationId: stack )
         {
            ConversationEntry conversationEntry = ConversationEntries.instance().getConversationEntry(conversationId);
            if (conversationEntry!=null)
            {
               conversationEntry.touch();
            }
         }
      }

      //do this last, to bring it to the top of the conversation list
      if ( isLongRunningConversation() )
      {
         getCurrentConversationEntry().touch();
      }

   }

   /**
    * Get the name of the component that started the current
    * conversation.
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

   public boolean isReallyLongRunningConversation()
   {
      return isLongRunningConversation() && 
            !getCurrentConversationEntry().isRemoveAfterRedirect() &&
            !Seam.isSessionInvalid();
   }
   
   public boolean isNestedConversation()
   {
      return currentConversationIdStack.size()>1;
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
      List<ConversationEntry> entries = new ArrayList<ConversationEntry>( ConversationEntries.instance().getConversationEntries() );
      for (ConversationEntry conversationEntry: entries)
      {
         long delta = currentTime - conversationEntry.getLastRequestTime();
         if ( delta > conversationEntry.getTimeout() )
         {
            if ( conversationEntry.lock() ) //no need to release it...
            {
               log.debug("conversation timeout for conversation: " + conversationEntry.getId());
               ContextAdaptor session = ContextAdaptor.getSession(externalContext, true);
               destroyConversation( conversationEntry.getId(), session );
            }
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
    * Flush the server-side conversation context to the session and
    * write the conversation id and pageflow info to the response
    * if we have a long running conversation, or discard the state
    * of a temporary conversation.
    */
   public void storeConversation(ContextAdaptor session, Object response)
   {
      if ( isLongRunningConversation() )
      {
         touchConversationStack();
         if ( !Seam.isSessionInvalid() ) 
         {
            storeLongRunningConversation(response);
         }
      }
      else
      {
         discardTemporaryConversation(session, response);
      }
      conversationAlreadyStored = true;
   }
   
   public void unlockConversation()
   {
      ConversationEntry ce = getCurrentConversationEntry();
      if (ce!=null) ce.unlock();
   }

   private void storeLongRunningConversation(Object response)
   {
      if ( log.isDebugEnabled() )
      {
         log.debug("Storing conversation state: " + getCurrentConversationId());
      }
      Conversation.instance().flush();
      //if the session is invalid, don't put the conversation id
      //in the view, 'cos we are expecting the conversation to
      //be destroyed by the servlet session listener
      if ( Contexts.isPageContextActive() ) 
      {
         //TODO: we really only need to execute this code when we are in the 
         //      RENDER_RESPONSE phase, ie. not before redirects
         if ( isReallyLongRunningConversation() )
         {
            Contexts.getPageContext().set( CONVERSATION_ID, getCurrentConversationId() );
            Contexts.getPageContext().set( CONVERSATION_IS_LONG_RUNNING, true );
         }
      }
      writeConversationIdToResponse( response, getCurrentConversationId() );
      
      if ( Contexts.isPageContextActive() && Init.instance().isJbpmInstalled() )
      {
         Pageflow pageflow = Pageflow.instance();
         if ( pageflow.isInProcess() )
         {
            Contexts.getPageContext().set( PAGEFLOW_NAME, pageflow.getProcessInstance().getProcessDefinition().getName() );
            Contexts.getPageContext().set( PAGEFLOW_NODE_NAME, pageflow.getNode().getName() );
            Contexts.getPageContext().set( PAGEFLOW_COUNTER, pageflow.getPageflowCounter() );
         }
      }
   }

   private void discardTemporaryConversation(ContextAdaptor session, Object response)
   {
      if (log.isDebugEnabled())
      {
         log.debug("Discarding conversation state: " + getCurrentConversationId());
      }

      List<String> stack = getCurrentConversationIdStack();
      if ( stack.size()>1 )
      {
         String outerConversationId = stack.get(1);
         if ( Contexts.isPageContextActive() )
         {
            Contexts.getPageContext().set(CONVERSATION_ID, outerConversationId);
         }
         writeConversationIdToResponse(response, outerConversationId);
      }
      else
      {
         if ( Contexts.isPageContextActive() )
         {
            Contexts.getPageContext().remove(CONVERSATION_ID);
         }
      }

      //now safe to remove the entry
      removeCurrentConversationAndDestroyNestedContexts(session);
   }
   
   /**
    * Write out the conversation id as a servlet response header or portlet
    * render parameter.
    */
   private void writeConversationIdToResponse(Object response, String conversationId)
   {
      if (response instanceof HttpServletResponse)
      {
         ( (HttpServletResponse) response ).setHeader(conversationIdParameter, conversationId);
      }
      else if (response instanceof ActionResponse)
      {
         ( (ActionResponse) response ).setRenderParameter(conversationIdParameter, conversationId);
      }
   }

   private void removeCurrentConversationAndDestroyNestedContexts(ContextAdaptor session) {
      ConversationEntries.instance().removeConversationEntry( getCurrentConversationId() );
      destroyNestedContexts( session, getCurrentConversationId() );
   }

   private void destroyNestedContexts(ContextAdaptor session, String conversationId) {
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
      Boolean isLongRunningConversation = "true".equals( getRequestParameterValue(parameters, conversationIsLongRunningParameter) );
      
      if ( isMissing(storedConversationId) && Contexts.isPageContextActive() )
      {
         //if it is not passed as a request parameter,
         //try to get it from the page context
         storedConversationId = (String) Contexts.getPageContext().get(CONVERSATION_ID);
         isLongRunningConversation = (Boolean) Contexts.getPageContext().get(CONVERSATION_IS_LONG_RUNNING);
         if (isLongRunningConversation==null) isLongRunningConversation = false;
      }

      else if (storedConversationId!=null)
      {
         log.debug("Found conversation id in request parameter: " + storedConversationId);
      }
      
      //TODO: this approach is deprecated, remove code:
      if ( "new".equals(storedConversationId) )
      {
         storedConversationId = null;
         isLongRunningConversation = false;
      }
      //end code to remove

      String propagation = getPropagationFromRequestParameter(parameters);
      if ( "none".equals(propagation) )
      {
         storedConversationId = null;
         isLongRunningConversation = false;
      }

      return restoreAndLockConversation(storedConversationId, isLongRunningConversation);
      
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
    * conversation id.
    */
   public boolean restoreAndLockConversation(String storedConversationId, boolean isLongRunningConversation) {
      ConversationEntry ce = storedConversationId==null ? 
            null : ConversationEntries.instance().getConversationEntry(storedConversationId);
      if ( ce!=null && ce.lock() )
      {

         //we found an id and obtained the lock, so restore the long-running conversation
         log.debug("Restoring conversation with id: " + storedConversationId);
         setLongRunningConversation(true);
         setCurrentConversationId(storedConversationId);
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
      entry.lock();
      return entry;
   }

   /**
    * Promote a temporary conversation and make it long-running
    * 
    * @param initiator the name of the component starting the conversation.
    */
   public void beginConversation(String initiator)
   {
      setLongRunningConversation(true);
      createConversationEntry().setInitiatorComponentName(initiator);
      Conversation.instance(); //force instantiation of the Conversation in the outer (non-nested) conversation
      if ( Events.exists() ) Events.instance().raiseEvent("org.jboss.seam.beginConversation");
   }

   /**
    * Make a long-running conversation temporary.
    */
   public void endConversation(boolean beforeRedirect)
   {
      if ( Events.exists() ) Events.instance().raiseEvent("org.jboss.seam.endConversation");
      setLongRunningConversation(false);
      destroyBeforeRedirect = beforeRedirect;
   }
   
   /**
    * Begin a new nested conversation.
    * 
    * @param ownerName the name of the component starting the conversation
    */
   public void beginNestedConversation(String ownerName)
   {
      List<String> oldStack = getCurrentConversationIdStack();
      String id = Id.nextId();
      setCurrentConversationId(id);
      createCurrentConversationIdStack(id).addAll(oldStack);
      ConversationEntry conversationEntry = createConversationEntry();
      conversationEntry.setInitiatorComponentName(ownerName);
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
   public boolean swapConversation(String id)
   {
      ConversationEntry ce = ConversationEntries.instance().getConversationEntry(id);
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

   private String encodeConversationId(String url) {
      if ( destroyBeforeRedirect || Seam.isSessionInvalid() )
      {
         return url;
      }
      else
      {
         StringBuilder builder = new StringBuilder( url.length() + conversationIdParameter.length() + 5 )
               .append(url)
               .append( url.contains("?") ? '&' : '?' )
               .append(conversationIdParameter)
               .append('=')
               .append( getCurrentConversationId() );
         if ( isReallyLongRunningConversation() )
         {
            builder.append('&')
                  .append(conversationIsLongRunningParameter)
                  .append('=')
                  .append("true");
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
      Map<String, Object> renderParameters = RenderParameters.instance();
      if (renderParameters!=null)
      {
         url = encodeParameters(url, renderParameters);
      }
      /*Map<String, Object> pageParameters = Pages.instance().getParameters(viewId);
      if (pageParameters!=null)
      {
         url = encodeParameters(url, pageParameters);
      }*/
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
    */
   public void prepareBackswitch(PhaseEvent event) {
      if ( isLongRunningConversation() )
      {
         //important: only do this stuff when a long-running
         //           conversation exists, otherwise we would
         //           force creation of a conversation entry
         
         Conversation conversation = Conversation.instance();

         //stuff from jPDL takes precedence
         Page page = Init.instance().isJbpmInstalled() && Pageflow.instance().isInProcess() ?
               Pageflow.instance().getPage() : null;
         if (page==null)
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
         else
         {
            //use stuff from the pageflow definition
            if ( page.hasDescription() )
            {
               conversation.setDescription( page.getDescription() );
               conversation.setViewId( page.getViewId() );
            }
            conversation.setTimeout( page.getTimeout() );
         }

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
      FacesMessages.instance().addFromResourceBundle( 
            FacesMessage.SEVERITY_WARN, 
            "org.jboss.seam.NoConversation", 
            "The conversation ended, timed out or was processing another request" 
         );
      
      //stuff from jPDL takes precedence
      String pageflowName = (String) Contexts.getPageContext().get(Manager.PAGEFLOW_NAME);
      String pageflowNodeName = (String) Contexts.getPageContext().get(Manager.PAGEFLOW_NODE_NAME);
      
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

   public boolean isConversationAlreadyStored()
   {
      return conversationAlreadyStored;
   }

   public boolean isUpdateModelValuesCalled()
   {
      return updateModelValuesCalled;
   }

   public void setUpdateModelValuesCalled(boolean updateModelValuesCalled)
   {
      this.updateModelValuesCalled = updateModelValuesCalled;
   }

}
