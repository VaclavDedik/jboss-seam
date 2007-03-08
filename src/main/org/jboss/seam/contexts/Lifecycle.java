/*
 * JBoss, Home of Professional Open Source �
 *
 * Distributable under LGPL license.
 * See terms of license at gnu.org. �
 */
package org.jboss.seam.contexts;

import java.util.Set;

import javax.faces.context.ExternalContext;
import javax.faces.event.PhaseId;
import javax.servlet.ServletContext;
import javax.servlet.ServletRequest;
import javax.servlet.http.HttpSession;

import org.jboss.seam.Component;
import org.jboss.seam.ScopeType;
import org.jboss.seam.Seam;
import org.jboss.seam.core.BusinessProcess;
import org.jboss.seam.core.ConversationEntries;
import org.jboss.seam.core.Init;
import org.jboss.seam.core.Manager;
import org.jboss.seam.core.Mutable;
import org.jboss.seam.log.LogProvider;
import org.jboss.seam.log.Logging;

/**
 * @author Gavin King
 * @author <a href="mailto:theute@jboss.org">Thomas Heute</a>
 * @version $Revision$
 */
public class Lifecycle
{

   private static final LogProvider log = Logging.getLogProvider(Lifecycle.class);

   public static void beginRequest(ExternalContext externalContext) 
   {
      log.debug( ">>> Begin web request" );
      Contexts.eventContext.set( new WebRequestContext( ContextAdaptor.getRequest(externalContext) ) );
      Contexts.sessionContext.set( new WebSessionContext( ContextAdaptor.getSession(externalContext) ) );
      Contexts.applicationContext.set( new FacesApplicationContext(externalContext) );
      Contexts.conversationContext.set(null); //in case endRequest() was never called
      //Events.instance(); //TODO: only for now, until we have a way to do EL outside of JSF!
   }

   public static void beginRequest(ServletContext servletContext, HttpSession session, ServletRequest request) 
   {
      log.debug( ">>> Begin web request" );
      Contexts.eventContext.set( new WebRequestContext( ContextAdaptor.getRequest(request) ) );
      Contexts.sessionContext.set( new WebSessionContext( ContextAdaptor.getSession(session) ) );
      Contexts.applicationContext.set( new WebApplicationContext(servletContext) );
      Contexts.conversationContext.set(null); //in case endRequest() was never called
   }

   public static void beginCall()
   {
      log.debug( ">>> Begin call" );
      Contexts.eventContext.set( new MapContext(ScopeType.EVENT) );
      Contexts.sessionContext.set( new MapContext(ScopeType.SESSION) );
      Contexts.conversationContext.set( new MapContext(ScopeType.CONVERSATION) );
      Contexts.businessProcessContext.set( new BusinessProcessContext() );
      Contexts.applicationContext.set( new WebApplicationContext( getServletContext() ) );
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

   public static void mockApplication()
   {
      Contexts.applicationContext.set( new WebApplicationContext( getServletContext() ) );
   }

   public static void unmockApplication()
   {
      Contexts.applicationContext.set(null);
   }

   public static Context beginMethod()
   {
      Context result = Contexts.methodContext.get();
      Contexts.methodContext.set( new MapContext(ScopeType.METHOD) );
      return result;
   }

   public static void endMethod(Context context)
   {
      Contexts.methodContext.set(context);
   }

   public static void beginInitialization(ServletContext servletContext)
   {
      Contexts.applicationContext.set( new WebApplicationContext(servletContext) );
      Contexts.eventContext.set( new MapContext(ScopeType.EVENT) );
      Contexts.conversationContext.set( new MapContext(ScopeType.CONVERSATION) );
   }

   public static void beginReinitialization(ServletContext servletContext, HttpSession session)
   {
      Contexts.applicationContext.set( new WebApplicationContext(servletContext) );
      Contexts.eventContext.set( new MapContext(ScopeType.EVENT) );
      Contexts.sessionContext.set( new WebSessionContext( ContextAdaptor.getSession(session) ) );
      Contexts.conversationContext.set( new MapContext(ScopeType.CONVERSATION) );
   }

   public static void beginExceptionRecovery(ExternalContext externalContext)
   {
      Contexts.applicationContext.set( new FacesApplicationContext(externalContext) );
      Contexts.eventContext.set( new WebRequestContext( ContextAdaptor.getRequest(externalContext) ) );
      Contexts.sessionContext.set( new WebSessionContext( ContextAdaptor.getSession(externalContext) ) );
      Contexts.conversationContext.set( new ServerConversationContext( ContextAdaptor.getSession(externalContext) ) );
      Contexts.pageContext.set(null);
      Contexts.businessProcessContext.set(null); //TODO: is this really correct?
   }

   public static void endInitialization()
   {
	   //instantiate all application-scoped @Startup components
      Context context = Contexts.getApplicationContext();
      for ( String name: context.getNames() )
      {
    	   Object object = context.get(name);
    	   if ( object!=null && (object instanceof Component) )
    	   {
	         Component component = (Component) object;
	         if ( component.isStartup() && component.getScope()==ScopeType.APPLICATION )
	         {
               startup(component);
	         }
    	   }
      }

      Contexts.destroy( Contexts.getConversationContext() );
      Contexts.conversationContext.set(null);
      Contexts.destroy( Contexts.getEventContext() );
      Contexts.eventContext.set(null);
      Contexts.sessionContext.set(null);
      Contexts.applicationContext.set(null);
   }

   private static void startup(Component component)
   {
      if ( component.isStartup() )
      {
         for ( String dependency: component.getDependencies() )
         {
            Component dependentComponent = Component.forName(dependency);
            if (dependentComponent!=null)
            {
               startup( dependentComponent );
            }
         }
      }

      if ( !component.getScope().getContext().isSet( component.getName() ) ) 
      {
         log.info("starting up: " + component.getName());
         component.newInstance();
      }
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
      
      //Normally called synchronously with a JSF request, but there are some
      //special cases!

      boolean applicationContextActive = Contexts.isApplicationContextActive();
      boolean eventContextActive = Contexts.isEventContextActive();
      boolean conversationContextActive = Contexts.isConversationContextActive();
      if ( !applicationContextActive )
      {
         Context tempApplicationContext = new WebApplicationContext(servletContext);
         Contexts.applicationContext.set(tempApplicationContext);
      }
      Context tempEventContext = null;
      if ( !eventContextActive )
      {
         tempEventContext = new MapContext(ScopeType.EVENT);
         Contexts.eventContext.set(tempEventContext);
      }
      Context tempConversationContext = null;
      if ( !conversationContextActive )
      {
         tempConversationContext = new MapContext(ScopeType.CONVERSATION);
         Contexts.conversationContext.set(tempConversationContext);
      }

      Context tempSessionContext = new WebSessionContext(session);
      Contexts.sessionContext.set(tempSessionContext);

      //instantiate all session-scoped @Startup components
      for ( String name : Contexts.getApplicationContext().getNames() )
      {
         Object object = Contexts.getApplicationContext().get(name);
         if ( object!=null && (object instanceof Component) ) 
         {
            Component component = (Component) object;
            if ( component.isStartup() && component.getScope() == ScopeType.SESSION )
            {
               startup(component);
            }
         }
      }

      Contexts.sessionContext.set(null);
      
      if ( !conversationContextActive )
      {
         Contexts.destroy(tempConversationContext);
         Contexts.conversationContext.set(null);
      }
      if ( !eventContextActive ) 
      {
         Contexts.destroy(tempEventContext);
         Contexts.eventContext.set(null);
      }
      if ( !applicationContextActive ) 
      {
         Contexts.applicationContext.set(null);
      }
      
   }

   public static void endSession(ServletContext servletContext, ContextAdaptor session)
   {
      log.debug("End of session, destroying contexts");
      
      //This code assumes that sessions are only destroyed at the very end of a  
      //web request, after the request-bound context objects have been destroyed,
      //or during session timeout, when there are no request-bound contexts.
      
      if ( Contexts.isEventContextActive() || Contexts.isApplicationContextActive() )
      {
         throw new IllegalStateException("Please end the HttpSession via Seam.invalidateSession()");
      }
      
      Context tempApplicationContext = new WebApplicationContext(servletContext);
      Contexts.applicationContext.set(tempApplicationContext);

      //this is used just as a place to stick the ConversationManager
      Context tempEventContext = new MapContext(ScopeType.EVENT);
      Contexts.eventContext.set(tempEventContext);

      //this is used (a) for destroying session-scoped components
      //and is also used (b) by the ConversationManager
      Context tempSessionContext = new WebSessionContext(session);
      Contexts.sessionContext.set(tempSessionContext);

      Set<String> conversationIds = ConversationEntries.instance().getConversationIds();
      log.debug("destroying conversation contexts: " + conversationIds);
      for (String conversationId: conversationIds)
      {
         destroyConversationContext(session, conversationId);
      }
      
      //we need some conversation-scope components for destroying
      //the session context...
      Context tempConversationContext = new MapContext(ScopeType.CONVERSATION);
      Contexts.conversationContext.set(tempConversationContext);

      log.debug("destroying session context");
      Contexts.destroy(tempSessionContext);
      Contexts.sessionContext.set(null);
      
      Contexts.destroy(tempConversationContext);
      Contexts.conversationContext.set(null);

      Contexts.destroy(tempEventContext);
      Contexts.eventContext.set(null);

      Contexts.applicationContext.set(null);
   }

   public static void endRequest(ExternalContext externalContext) 
   {

      log.debug("After render response, destroying contexts");
      try
      {
         flushAndDestroyContexts();

         if ( Seam.isSessionInvalid() )
         {
            clearThreadlocals();
            Lifecycle.setPhaseId(null);
            ContextAdaptor.getSession(externalContext).invalidate(); //huh? we create a session just to invalidate it?
            //actual session context will be destroyed from the listener
         }
      }
      finally
      {
         clearThreadlocals();
      }

      log.debug( "<<< End web request" );
   }

   public static void endRequest() 
   {

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

   public static void endRequest(HttpSession session) 
   {

      log.debug("After request, destroying contexts");

      try
      {
         flushAndDestroyContexts();

         if ( Seam.isSessionInvalid() )
         {
            clearThreadlocals();
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

   private static void clearThreadlocals() 
   {
      Contexts.eventContext.set(null);
      Contexts.pageContext.set(null);
      Contexts.sessionContext.set(null);
      Contexts.conversationContext.set(null);
      Contexts.businessProcessContext.set(null);
      Contexts.applicationContext.set(null);
   }

   private static void flushAndDestroyContexts()
   {

      if ( Contexts.isConversationContextActive() )
      {

         if ( Contexts.isBusinessProcessContextActive() )
         {
            //TODO: it would be nice if BP context spanned redirects along with the conversation
            //      this would also require changes to BusinessProcessContext
            boolean destroyBusinessProcessContext = !Init.instance().isJbpmInstalled() ||
                  !BusinessProcess.instance().hasActiveProcess();
            if (destroyBusinessProcessContext)
            {
               //TODO: note that this occurs from Lifecycle.endRequest(), after
               //      the Seam-managed txn was committed, but Contexts.destroy()
               //      calls BusinessProcessContext.getNames(), which hits the
               //      database!
               log.debug("destroying business process context");
               Contexts.destroy( Contexts.getBusinessProcessContext() );
            }
         }

         if ( !Manager.instance().isLongRunningConversation() )
         {
            log.debug("destroying conversation context");
            Contexts.destroy( Contexts.getConversationContext() );
         }
         if ( !Init.instance().isClientSideConversations() )
         {
            //note that we need to flush even if the session is
            //about to be invalidated, since we still need
            //to destroy the conversation context in endSession()
            log.debug("flushing server-side conversation context");
            Contexts.getConversationContext().flush();
         }

         //uses the event and session contexts
         Manager.instance().unlockConversation();

      }
      
      if ( Contexts.isSessionContextActive() )
      {
         log.debug("flushing session context");
         Contexts.getSessionContext().flush();
      }
      
      //destroy the event context after the
      //conversation context, since we need
      //the manager to flush() conversation
      if ( Contexts.isEventContextActive() )
      {
         log.debug("destroying event context");
         Contexts.destroy( Contexts.getEventContext() );
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
            (Context) new ServerConversationContext( ContextAdaptor.getSession(externalContext) );
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

   public static ServletContext getServletContext() 
   {
      if (servletContext==null)
      {
         throw new IllegalStateException("Attempted to invoke a Seam component outside the context of a web application");
      }
      return servletContext;
   }

   public static void setServletContext(ServletContext servletContext) 
   {
      Lifecycle.servletContext = servletContext;
   }

   public static ServletRequest getServletRequest() 
   {
      return servletRequest.get();
   }

   public static void setServletRequest(ServletRequest servletRequest) 
   {
      Lifecycle.servletRequest.set(servletRequest);
   }

   private static ThreadLocal<Boolean> destroying = new ThreadLocal<Boolean>();

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

   public static boolean isAttributeDirty(Object attribute)
   {
      return attribute instanceof Mutable && ( (Mutable) attribute ).clearDirty();
   }

   public static void destroyConversationContext(ContextAdaptor session, String conversationId)
   {
      ServerConversationContext conversationContext = new ServerConversationContext(session, conversationId);
      Context old = Contexts.getConversationContext();
      Contexts.conversationContext.set(conversationContext);
      try
      {
         Contexts.destroy(conversationContext);
         if ( !Seam.isSessionInvalid() ) //its also unnecessary during a session timeout
         {
            conversationContext.clear();
            conversationContext.flush();
         }
      }
      finally
      {
         Contexts.conversationContext.set(old);
      }
   }

}
