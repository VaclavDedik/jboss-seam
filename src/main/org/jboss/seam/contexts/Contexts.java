/*
 * JBoss, Home of Professional Open Source
 *
 * Distributable under LGPL license.
 * See terms of license at gnu.org.
 */
package org.jboss.seam.contexts;

import java.lang.reflect.Method;
import java.util.Map;
import java.util.Set;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpSession;

import org.jboss.logging.Logger;
import org.jboss.seam.Component;
import org.jboss.seam.core.Manager;
import org.jboss.seam.util.Reflections;

/**
 * Provides access to the current contexts associated with the thread.
 * 
 * @author Gavin King
 * @author <a href="mailto:theute@jboss.org">Thomas Heute</a>
 * @version $Revision$
 */
public class Contexts {

	public static final String PROCESS_INTERCEPTORS = "org.jboss.seam.processInterceptors";
   public static final String SESSION_INVALID = "org.jboss.seam.sessionInvalid";

   private static final Logger log = Logger.getLogger( Contexts.class );

   private static final ThreadLocal<Context> applicationContext = new ThreadLocal<Context>();
	private static final ThreadLocal<Context> eventContext = new ThreadLocal<Context>();
	private static final ThreadLocal<Context> sessionContext = new ThreadLocal<Context>();
	private static final ThreadLocal<Context> conversationContext = new ThreadLocal<Context>();
   private static final ThreadLocal<Context> businessProcessContext = new ThreadLocal<Context>();

	public static Context getEventContext() {
		return eventContext.get();
	}

	public static Context getSessionContext() {
		return sessionContext.get();
	}

	public static Context getApplicationContext() {
		return applicationContext.get();
	}

	public static Context getStatelessContext() {
		return new StatelessContext();
	}

	public static Context getConversationContext() {
		return conversationContext.get();
	}

    public static Context getBusinessProcessContext() {
	    return businessProcessContext.get();
    }

	public static void beginRequest(HttpSession session) {
		log.info( ">>> Begin web request" );
		//eventContext.set( new WebRequestContext( request ) );
      eventContext.set( new EventContext() );
		sessionContext.set( new WebSessionContext(session) );
      applicationContext.set( new WebApplicationContext( session.getServletContext() ) );
	}

   public static void beginInitialization(ServletContext servletContext)
   {
      Context context = new WebApplicationContext( servletContext );
      applicationContext.set( context );
   }
   public static void endInitialization()
   {
      applicationContext.set(null);
   }
   
   public static void endApplication(ServletContext servletContext)
   {
      Context tempApplicationContext = new WebApplicationContext( servletContext );
      applicationContext.set( tempApplicationContext );
      log.info("destroying application context");
      destroy(tempApplicationContext);
      applicationContext.set(null);
      eventContext.set(null);
      sessionContext.set(null);
      conversationContext.set(null);
   }

   public static void endSession(HttpSession session)
   {
      log.info("End of session, destroying contexts");
      
      Context tempAppContext = new WebApplicationContext(session.getServletContext() );
      applicationContext.set(tempAppContext);
      
      //this is used just as a place to stick the ConversationManager
      Context tempEventContext = new EventContext();
      eventContext.set(tempEventContext);
      
      //this is used (a) for destroying session-scoped components
      //and is also used (b) by the ConversationManager
      Context tempSessionContext = new WebSessionContext( session );
      sessionContext.set(tempSessionContext);
      
      Set<String> ids = Manager.instance().getSessionConversationIds();
      log.info("destroying conversation contexts: " + ids);
      for (String conversationId: ids)
      {
         destroy( new ConversationContext(session, conversationId) );
      }

      log.info("destroying session context");
      destroy(tempSessionContext);
      sessionContext.set(null);
      
      destroy(tempEventContext);
      eventContext.set(null);
      
      conversationContext.set(null);
      
      destroy(tempAppContext);
      applicationContext.set(null);
   }

	public static void endRequest(HttpSession session) {
      
      log.info("After render response, destroying contexts");
      
      if ( Contexts.isEventContextActive() )
      {
         log.info("destroying event context");
         destroy( Contexts.getEventContext() );
      }
      if ( Contexts.isConversationContextActive() )
      {
         getConversationContext().flush();
         if ( !Manager.instance().isLongRunningConversation() )
         {
            log.info("destroying conversation context");
            destroy( Contexts.getConversationContext() );
         }
      }
      if ( Contexts.isBusinessProcessContextActive() )
      {
         getBusinessProcessContext().flush();
      }

      if ( isSessionInvalid() )
      {
         session.invalidate();
         //actual session context will be destroyed from the listener
      }
      
		eventContext.set( null );
		sessionContext.set( null );
		conversationContext.set( null );
      
		if ( businessProcessContext.get() != null ) {
			( ( BusinessProcessContext ) businessProcessContext.get() ).release();
			businessProcessContext.set( null );
		}
      
      applicationContext.set( null );

      log.info( "<<< End web request" );
	}

	public static boolean isConversationContextActive() {
		return getConversationContext() != null;
	}

	public static boolean isEventContextActive() {
		return eventContext.get() != null;
	}

	public static boolean isSessionContextActive() {
		return sessionContext.get() != null;
	}

	public static boolean isApplicationContextActive() {
		return applicationContext.get() != null;
	}

    public static boolean isBusinessProcessContextActive() {
        return businessProcessContext.get() != null;
    }
   
   public static void resumeConversation(HttpSession session, String id)
   {
      conversationContext.set( new ConversationContext(session, id) );
   }
   
   public static void removeFromAllContexts(String name)
   {
      if (isEventContextActive())
      {
         log.info("removing from event context");
         getEventContext().remove(name);
      }
      if (isConversationContextActive())
      {
         log.info("removing from conversation context");
         getConversationContext().remove(name);
      }
      if (isSessionContextActive())
      {
         log.info("removing from session context");
         getSessionContext().remove(name);
      }
      if (isBusinessProcessContextActive())
      {
         log.info("removing from process context");
         getBusinessProcessContext().remove(name);
      }
      if (isApplicationContextActive())
      {
         log.info("removing from application context");
         getApplicationContext().remove(name);
      }
   }

   public static Object lookupInStatefulContexts(String name)
   {
      if (isEventContextActive())
      {
         Object result = getEventContext().get(name);
         if (result!=null)
         {
            log.info("found in event context");
            return result;
         }
      }
      
      if (isConversationContextActive())
      {
         Object result = getConversationContext().get(name);
         if (result!=null)
         {
            log.info("found in conversation context");
            return result;
         }
      }
      
      if (isSessionContextActive())
      {
         Object result = getSessionContext().get(name);
         if (result!=null)
         {
            log.info("found in session context");
            return result;
         }
      }
      
      if (isBusinessProcessContextActive())
      {
         Object result = getBusinessProcessContext().get(name);
         if (result!=null)
         {
            log.info("found in business process context");
            return result;
         }
      }
      
      if (isApplicationContextActive())
      {
         Object result = getApplicationContext().get(name);
         if (result!=null)
         {
            log.info("found in application context");
            return result;
         }
      }
      
      return null;
      
   }
      
   public static Object lookupInAllContexts(String name)
   {
      Object result = lookupInStatefulContexts(name);
      if (result!=null) return result;
      
      result = getStatelessContext().get(name);
      if (result!=null)
      {
         log.info("found in stateless context");
         return result;
      }
      else {
         log.info("not found in any context");
         return null;
      }
   }
   
   public static void destroy(Context context)
   {
      for ( String name: context.getNames() ) {
         Component component = Component.forName(name);
         log.info("destroying: " + name);
         if ( component!=null )
         {
            callDestroyMethod( component, context.get(name) );
         }
      }
   }

   private static void callDestroyMethod(Component component, Object instance)
   {
      if ( component.hasDestroyMethod() )
      {
         String methodName = component.getDestroyMethod().getName();
         try {
            Method method = instance.getClass().getMethod(methodName);
            Reflections.invokeAndWrap( method, instance );
         }
         catch (NoSuchMethodException e)
         {
            log.warn("could not find destroy method", e);
         }
      }
   }

	public static void beginBusinessProcessContext() {
		if ( isBusinessProcessContextActive() ) {
			throw new IllegalStateException( "There is already a BusinessProcessContext active" );
		}
		businessProcessContext.set ( new BusinessProcessContext() );
	}

	public static void recoverBusinessProcessContext(Map state) {
		if ( isBusinessProcessContextActive() ) {
			throw new IllegalStateException( "There is already a BusinessProcessContext active" );
		}
		BusinessProcessContext ctx = new BusinessProcessContext();
		ctx.recover( state );
		businessProcessContext.set( ctx );
	}

	public static void endBusinessProcessContext() {
		businessProcessContext.set( null );
	}
   
   public static void invalidateSession()
   {
      getSessionContext().set(SESSION_INVALID, true);
   }
   
   public static boolean isSessionInvalid()
   {
      Boolean isSessionInvalid = (Boolean) getSessionContext().get(SESSION_INVALID);
      return isSessionInvalid!=null && isSessionInvalid;
   }
   
}