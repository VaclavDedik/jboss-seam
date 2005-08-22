/*
 * JBoss, Home of Professional Open Source
 *
 * Distributable under LGPL license.
 * See terms of license at gnu.org.
 */
package org.jboss.seam.contexts;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;

import org.jboss.logging.Logger;
import org.jboss.seam.Component;
import org.jboss.seam.finders.ComponentFinder;

/**
 * Provides access to the current contexts associated with the thread.
 * 
 * @author Gavin King
 * @author <a href="mailto:theute@jboss.org">Thomas Heute</a>
 * @version $Revision$
 */
public class Contexts {

	private static final Logger log = Logger.getLogger( Contexts.class );

	private static final ThreadLocal<Context> eventContext = new ThreadLocal<Context>();
	private static final ThreadLocal<Context> sessionContext = new ThreadLocal<Context>();
	private static final ThreadLocal<Context> conversationContext = new ThreadLocal<Context>();
	private static final ThreadLocal<Context> applicationContext = new ThreadLocal<Context>();
   private static final ThreadLocal<Context> businessProcessContext = new ThreadLocal<Context>();

   private static final ThreadLocal<Boolean> isLongRunningConversation = new ThreadLocal<Boolean>();
   private static final ThreadLocal<Boolean> isSessionInvalid = new ThreadLocal<Boolean>();
   private static final ThreadLocal<Boolean> isProcessing = new ThreadLocal<Boolean>();

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

	public static void beginWebRequest(HttpServletRequest request) {
		log.info( ">>> Begin web request" );
		eventContext.set( new WebRequestContext( request ) );
		sessionContext.set( new WebSessionContext( request.getSession() ) );
		ServletContext servletContext = request.getSession().getServletContext();
		applicationContext.set( new WebApplicationContext( servletContext ) );
      isSessionInvalid.set(false);
	}

	public static void endWebRequest(HttpServletRequest request) {
		log.info( "<<< End web request" );
		//clean up all threadlocals
      if ( isSessionInvalid.get() )
      {
         isSessionInvalid.set(false);
         request.getSession(false).invalidate();
      }
      
		eventContext.set( null );
		sessionContext.set( null );
		applicationContext.set( null );
		conversationContext.set( null );
      
		if ( businessProcessContext.get() != null ) {
			( ( BusinessProcessContext ) businessProcessContext.get() ).release();
			businessProcessContext.set( null );
		}
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
   
   public static void endConversation() 
   {
      log.info("Ending conversation");
      isLongRunningConversation.set(false);
   }

   public static void beginConversation() 
   {
      log.info("Beginning conversation");
      isLongRunningConversation.set(true);
   }
   
   public static boolean isLongRunningConversation()
   {
      return isLongRunningConversation.get();
   }
   
   public static void setLongRunningConversation(boolean value)
   {
      isLongRunningConversation.set(value);
   }
   
   public static void setConversationContext(Context context)
   {
      conversationContext.set(context);
   }
   
   public static void remove(String name)
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
      
   public static Object lookup(String name)
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
      ComponentFinder finder = new ComponentFinder();
      for ( String name: context.getNames() ) {
         Component component = finder.getComponent(name);
         if ( component!=null )
         {
            Object instance = context.get(name);
            if ( component.hasDestroyMethod() )
            {
               String methodName = component.getDestroyMethod().getName();
               try {
                  instance.getClass().getMethod(methodName).invoke(instance);
               }
               catch (Exception e)
               {
                  log.warn("exception calling destroy method", e);
               }
            }
         }
      }
   }

	public static void beginBusinessProcessContextViaTask(Long taskId) {
		if ( isBusinessProcessContextActive() ) {
			throw new IllegalStateException( "There is already a BusinessProcessContext active" );
		}
		BusinessProcessContext ctx = new BusinessProcessContext();
		ctx.prepareForTask( taskId );
		businessProcessContext.set( ctx );
	}

	public static void beginBusinessProcessContextViaProcess(Long processId) {
		if ( isBusinessProcessContextActive() ) {
			throw new IllegalStateException( "There is already a BusinessProcessContext active" );
		}
		BusinessProcessContext ctx = new BusinessProcessContext();
		ctx.prepareForProcessInstance( processId );
		businessProcessContext.set( ctx );
	}

	public static void beginBusinessProcessContextViaProcess(String processDefinitionName) {
		if ( isBusinessProcessContextActive() ) {
			throw new IllegalStateException( "There is already a BusinessProcessContext active" );
		}
		BusinessProcessContext ctx = new BusinessProcessContext();
		ctx.prepareForProcessInstance( processDefinitionName );
		businessProcessContext.set( ctx );
	}

	public static void endBusinessProcess() {
		businessProcessContext.set( null );
	}

   public static void setProcessing(boolean processing)
   {
      isProcessing.set(processing);
   }
   
   public static boolean isProcessing()
   {
      return isProcessing.get();
   }
   
   public static void invalidateSession()
   {
      isSessionInvalid.set(true);
   }
   
   public static boolean isSessionInvalid()
   {
      return isSessionInvalid.get();
   }
   
}