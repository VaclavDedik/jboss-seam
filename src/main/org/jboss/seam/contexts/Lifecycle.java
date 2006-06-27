/*
 * JBoss, Home of Professional Open Source �
 * 
 * Distributable under LGPL license. 
�* See terms of license at gnu.org. �
 */
package org.jboss.seam.contexts;

import java.util.Set;

import javax.faces.context.ExternalContext;
import javax.faces.event.PhaseId;
import javax.servlet.ServletContext;
import javax.servlet.ServletRequest;
import javax.servlet.http.HttpSession;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jboss.seam.Component;
import org.jboss.seam.ScopeType;
import org.jboss.seam.Seam;
import org.jboss.seam.core.Init;
import org.jboss.seam.core.Manager;

/**
 * @author Gavin King
 * @author <a href="mailto:theute@jboss.org">Thomas Heute</a>
 * @version $Revision$
 */
public class Lifecycle
{

   private static final Log log = LogFactory.getLog( Lifecycle.class );

   public static void beginRequest(ExternalContext externalContext) {
      log.debug( ">>> Begin web request" );
      Contexts.eventContext.set( new WebRequestContext( ContextAdaptor.getRequest(externalContext) ) );
      Contexts.sessionContext.set( new WebSessionContext( ContextAdaptor.getSession(externalContext, true) ) );
      Contexts.applicationContext.set( new FacesApplicationContext(externalContext) );
      Contexts.conversationContext.set(null); //in case endRequest() was never called
   }

   public static void beginRequest(ServletContext servletContext, HttpSession session, ServletRequest request) {
      log.debug( ">>> Begin web request" );
      Contexts.eventContext.set( new WebRequestContext( ContextAdaptor.getRequest(request) ) );
      Contexts.sessionContext.set( new WebSessionContext( ContextAdaptor.getSession(session) ) );
      Contexts.applicationContext.set( new WebApplicationContext(servletContext) );
      Contexts.conversationContext.set(null); //in case endRequest() was never called
   }

   public static void beginCall()
   {
      log.debug( ">>> Begin call" );
      ServletContext servletContext = getServletContext();
      if (servletContext==null)
      {
         throw new IllegalStateException("Attempted to invoke a Seam component outside the context of a web application");
      }

      Contexts.eventContext.set( new MapContext(ScopeType.EVENT) );
      Contexts.sessionContext.set( new MapContext(ScopeType.SESSION) );
      Contexts.conversationContext.set( new MapContext(ScopeType.CONVERSATION) );
      Contexts.businessProcessContext.set( new BusinessProcessContext() );
      Contexts.applicationContext.set( new WebApplicationContext(servletContext) );
   }
   
   public static void endCall()
   {
      try
      {
         Contexts.destroy( Contexts.getSessionContext() );
         flushAndDestroyContexts();
         if ( Manager.instance().isLongRunningConversation() )
         {
            throw new IllegalStateException("Do not start long-running conversations in direct calls to EJBs");
         }
      }
      finally
      {
         clearThreadlocals();
         log.debug( "<<< End call" );
      }
      
   }

   public static void beginInitialization(ServletContext servletContext)
   {
      Context context = new WebApplicationContext(servletContext);
      Contexts.applicationContext.set(context);
   }

   public static void endInitialization()
   {
	   //instantiate all application-scoped @Startup components
      Context context = Contexts.getApplicationContext();
      for ( String name: context.getNames() ) 
      {
    	   Object object = context.get(name);
    	   if ( object instanceof Component )
    	   {
	         Component component = (Component) object;
	         if ( component.isStartup() && component.getScope()==ScopeType.APPLICATION )
	         {
               startup(component);
	         }
    	   }
      }
      
      Contexts.applicationContext.set(null);
   }

   private static void startup(Component component)
   {
      if ( component.isStartup() )
      {
         for (String dependency: component.getDependencies() )
         {
            Component dependentComponent = Component.forName(dependency);
            if (dependentComponent!=null)
            {
               startup( dependentComponent );
            }
         }
      }
      Component.getInstance( component.getName(), component.getScope(), true );
   }

   public static void endApplication(ServletContext servletContext)
   {
      log.debug("Undeploying, destroying application context");

      Context tempApplicationContext = new WebApplicationContext(servletContext);
      Contexts.applicationContext.set( tempApplicationContext );
      Contexts.destroy(tempApplicationContext);
      Contexts.applicationContext.set(null);
      Contexts.eventContext.set(null);
      Contexts.sessionContext.set(null);
      Contexts.conversationContext.set(null);
   }

   /***
    * Instantiate @Startup components for session scoped component
    */
   public static void beginSession(ServletContext servletContext, ContextAdaptor session) 
   {
      log.debug("Session started");
      
      Context context = new WebApplicationContext(servletContext);
      Contexts.applicationContext.set(context);

      Context tempSessionContext = new WebSessionContext( session );
      Contexts.sessionContext.set(tempSessionContext);
      
      //instantiate all session-scoped @Startup components
      for ( String name : context.getNames() ) 
      {
         Object object = context.get(name);
         if (object instanceof Component) {
            Component component = (Component) object;
            if ( component.isStartup() && component.getScope() == ScopeType.SESSION ) 
            {
               startup(component);
            }
         }
      }
      
      Contexts.sessionContext.set(null);
      Contexts.applicationContext.set(null);
   }

   public static void endSession(ServletContext servletContext, ContextAdaptor session)
   {
      log.debug("End of session, destroying contexts");

      Context tempAppContext = new WebApplicationContext(servletContext);
      Contexts.applicationContext.set(tempAppContext);

      //this is used just as a place to stick the ConversationManager
      Context tempEventContext = new MapContext(ScopeType.EVENT);
      Contexts.eventContext.set(tempEventContext);

      //this is used (a) for destroying session-scoped components
      //and is also used (b) by the ConversationManager
      Context tempSessionContext = new WebSessionContext( session );
      Contexts.sessionContext.set(tempSessionContext);

      Set<String> conversationIds = Manager.instance().getSessionConversationIds();
      log.debug("destroying conversation contexts: " + conversationIds);
      for (String conversationId: conversationIds)
      {
         Contexts.destroy( new ServerConversationContext(session, conversationId) );
      }

      log.debug("destroying session context");
      Contexts.destroy(tempSessionContext);
      Contexts.sessionContext.set(null);

      Contexts.destroy(tempEventContext);
      Contexts.eventContext.set(null);

      Contexts.conversationContext.set(null);
      Contexts.applicationContext.set(null);
   }
   
   public static void flushClientConversation()
   {
      boolean flushNeeded = Contexts.isConversationContextActive() && 
            !Seam.isSessionInvalid() && 
            Init.instance().isClientSideConversations();
      
      if ( flushNeeded )
      {
         log.debug("flushing client-side conversation context");
         Contexts.getConversationContext().flush();
      }
   }
   
   public static void flushPage()
   {
      if ( Contexts.isPageContextActive() )
      {
         log.debug("flushing page context");
         Contexts.getPageContext().flush();
      }
   }

   public static void endRequest(ExternalContext externalContext) {

      log.debug("After render response, destroying contexts");

      try
      {
         flushAndDestroyContexts();

         if ( Seam.isSessionInvalid() )
         {
            ContextAdaptor.getSession(externalContext, true).invalidate(); //huh? we create a session just to invalidate it?
            //actual session context will be destroyed from the listener
         }
      }
      finally
      {
         clearThreadlocals();
      }

      log.debug( "<<< End web request" );
   }

   public static void endRequest() {

      log.debug("After request, destroying contexts");

      try
      {
         flushAndDestroyContexts();
      }
      finally
      {
         clearThreadlocals();
      }

      log.debug( "<<< End web request" );
   }

   public static void endRequest(HttpSession session) {

      log.debug("After request, destroying contexts");

      try
      {
         flushAndDestroyContexts();

         if ( Seam.isSessionInvalid() )
         {
            ContextAdaptor.getSession(session).invalidate(); //huh? we create a session just to invalidate it?
            //actual session context will be destroyed from the listener
         }
      }
      finally
      {
         clearThreadlocals();
      }

      log.debug( "<<< End web request" );
   }

   private static void clearThreadlocals() {
      Contexts.eventContext.set( null );
      Contexts.pageContext.set( null );
      Contexts.sessionContext.set( null );
      Contexts.conversationContext.set( null );
      Contexts.businessProcessContext.set( null );
      Contexts.applicationContext.set( null );
   }

   private static void flushAndDestroyContexts() 
   {

      if ( Contexts.isEventContextActive() )
      {
         log.debug("destroying event context");
         Contexts.destroy( Contexts.getEventContext() );
      }
      
      if ( Contexts.isConversationContextActive() )
      {
         if ( !Manager.instance().isLongRunningConversation() )
         {
            log.debug("destroying conversation context");
            Contexts.destroy( Contexts.getConversationContext() );
         }
         if ( !Seam.isSessionInvalid() && !Init.instance().isClientSideConversations() )
         {
            log.debug("flushing server-side conversation context");
            Contexts.getConversationContext().flush();
         }
      }
      
   }

   public static void resumePage()
   {
      Contexts.pageContext.set( new PageContext() );
   }
   
   public static void resumeConversation(ExternalContext externalContext)
   {
      Init init = Init.instance();
      Context conversationContext = init.isClientSideConversations() ?
            (Context) new ClientConversationContext() :
            (Context) new ServerConversationContext( ContextAdaptor.getSession(externalContext, true) );
      Contexts.conversationContext.set( conversationContext );
      Contexts.businessProcessContext.set( new BusinessProcessContext() );
   }

   public static void resumeConversation(HttpSession session)
   {
      Context conversationContext = new ServerConversationContext( ContextAdaptor.getSession(session) );
      Contexts.conversationContext.set( conversationContext );
      Contexts.businessProcessContext.set( new BusinessProcessContext() );
   }
   
   private static ThreadLocal<PhaseId> phaseId = new ThreadLocal<PhaseId>();
   
   public static PhaseId getPhaseId()
   {
      return phaseId.get();
   }
   
   public static void setPhaseId(PhaseId phase)
   {
      phaseId.set(phase);
   }
   
   private static ThreadLocal<ServletRequest> servletRequest = new ThreadLocal<ServletRequest>();
   private static ServletContext servletContext;
   
   public static ServletContext getServletContext() {
      return servletContext;
   }

   public static void setServletContext(ServletContext servletContext) {
      Lifecycle.servletContext = servletContext;
   }
   
   public static ServletRequest getServletRequest() {
      return servletRequest.get();
   }

   public static void setServletRequest(ServletRequest servletRequest) {
      Lifecycle.servletRequest.set(servletRequest);
   }
   
   private static ThreadLocal<Boolean> destroying = new ThreadLocal<Boolean>();
   private static ThreadLocal<Boolean> exception = new ThreadLocal<Boolean>();
   
   public static void startDestroying()
   {
      destroying.set(true);
   }
   
   public static void stopDestroying()
   {
      destroying.set(false);
   }
   
   public static boolean isDestroying()
   {
      Boolean value = destroying.get();
      return value!=null && value.booleanValue();
   }

   public static boolean isException() {
      Boolean value = exception.get();
      return value!=null && value.booleanValue();
   }

   public static void setException(boolean ex) {
      exception.set(ex);
   }

}
