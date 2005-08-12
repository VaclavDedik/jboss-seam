/*
 * JBoss, Home of Professional Open Source
 *
 * Distributable under LGPL license.
 * See terms of license at gnu.org.
 */
package org.jboss.seam;

import javax.servlet.http.HttpServletRequest;

import org.jboss.logging.Logger;

/**
 * @author Gavin King
 * @author <a href="mailto:theute@jboss.org">Thomas Heute</a>
 * @version $Revision$
 */
public class Contexts {

    private static Logger log = Logger.getLogger(Contexts.class);
   
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
		if (conversationId==null) {
			throw new IllegalStateException("no active conversation");
		}
		return new ConversationContext( getSessionContext(), conversationId.get() );
	}
    
    public static Context getBusinessProcessContext() {
       return null;
    }
	
	public static void beginWebRequest(HttpServletRequest request) {
        log.debug("Begin Web Request");
        eventContext.set( new WebRequestContext(request) );
        sessionContext.set( new WebSessionContext( request.getSession() ) );
        applicationContext.set( new WebApplicationContext( request.getSession().getServletContext() ) );
	}
	
	static void endWebRequest() {
        log.debug("End Web Request");
		eventContext.set(null);
		sessionContext.set(null);
		applicationContext.set(null);
	}
	
	static void setConversationId(String conversationId) {
		Contexts.conversationId.set(conversationId);
	}
	
	static boolean isConversationContextActive() {
		return conversationId.get() != null;
	}
	
	static boolean isEventContextActive() {
		return eventContext.get() != null;
	}

	static boolean isLoginContextActive() {
		return sessionContext.get() != null;
	}

	static boolean isApplicationContextActive() {
		return applicationContext.get() != null;
	}

   public static boolean isBusinessProcessContextActive()
   {
      return false;
   }

}
