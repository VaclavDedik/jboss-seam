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

	static void destroyCurrentConversationContext() {
      log.info("destroying conversation context");
		getConversationContext().destroy();
		setConversationId(null);
	}

	static void initCurrentConversationContext() {
      log.info("creating new conversation context");
		setConversationId( ConversationContext.generateConversationId() );
	}

}
