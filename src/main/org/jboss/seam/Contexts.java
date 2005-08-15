/*
 *  * JBoss, Home of Professional Open Source  *  * Distributable under LGPL license.  * See terms
 * of license at gnu.org.  
 */
package org.jboss.seam;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;

import org.jboss.logging.Logger;

/**
 * @author Gavin King
 * @author <a href="mailto:theute@jboss.org">Thomas Heute</a>
 * @version $Revision$
 */
public class Contexts {

	private static Logger log = Logger.getLogger( Contexts.class );

	private static ThreadLocal<Context> eventContext = new ThreadLocal<Context>();
	private static ThreadLocal<Context> sessionContext = new ThreadLocal<Context>();
	private static ThreadLocal<String> conversationId = new ThreadLocal<String>();
	private static ThreadLocal<Context> applicationContext = new ThreadLocal<Context>();

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
		if ( conversationId == null ) {
			throw new IllegalStateException( "no active conversation" );
		}
		return new ConversationContext( getSessionContext(), getConversationContextId() );
	}

	public static String getConversationContextId() {
		return conversationId.get();
	}

	public static Context getBusinessProcessContext() {
		return null;
	}

	static void beginWebRequest(HttpServletRequest request) {
		log.info( "Begin web request" );
		eventContext.set( new WebRequestContext( request ) );
		sessionContext.set( new WebSessionContext( request.getSession() ) );
		ServletContext servletContext = request.getSession().getServletContext();
		applicationContext.set( new WebApplicationContext( servletContext ) );
		//setConversationId( request.getParameter( "org.jboss.seam.ConversationId" ) );
	}

	static void endWebRequest() {
		log.info( "End web request" );
		eventContext.set( null );
		sessionContext.set( null );
		applicationContext.set( null );
		setConversationId( null );
	}

	static void setConversationId(String conversationId) {
		Contexts.conversationId.set( conversationId );
	}

	public static boolean isConversationContextActive() {
		return getConversationContextId() != null;
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
		return false;
	}

	public static void endConversation() {
      if ( !isConversationContextActive() )
      {
         throw new IllegalStateException("No conversation is currently active");
      }
      log.info("destroying conversation context");
		getConversationContext().destroy();
		setConversationId(null);
	}

	public static void beginConversation() {
      if ( isConversationContextActive() )
      {
         throw new IllegalStateException("A conversation is already active");
      }
      log.info("creating new conversation context");
		setConversationId( ConversationContext.generateConversationId() );
	}
   
   public static void remove(String name)
   {
      if (isEventContextActive())
      {
         SeamVariableResolver.log.info("removing from event context");
         getEventContext().remove(name);
      }
      if (isConversationContextActive())
      {
         SeamVariableResolver.log.info("removing from conversation context");
         getConversationContext().remove(name);
      }
      if (isSessionContextActive())
      {
         SeamVariableResolver.log.info("removing from session context");
         getSessionContext().remove(name);
      }
      if (isBusinessProcessContextActive())
      {
         SeamVariableResolver.log.info("removing from process context");
         getBusinessProcessContext().remove(name);
      }
      if (isApplicationContextActive())
      {
         SeamVariableResolver.log.info("removing from application context");
         getApplicationContext().remove(name);
      }
   }

   public static Object lookup(String name)
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
      
      Object result = getStatelessContext().get(name);
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

}
