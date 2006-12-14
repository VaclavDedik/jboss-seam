/*
 * JBoss, Home of Professional Open Source
 *
 * Distributable under LGPL license.
 * See terms of license at gnu.org.
 */
package org.jboss.seam.contexts;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jboss.seam.Component;
import org.jboss.seam.core.Events;

/**
 * Provides access to the current contexts associated with the thread.
 * 
 * @author Gavin King
 * @author <a href="mailto:theute@jboss.org">Thomas Heute</a>
 * @version $Revision$
 */
public class Contexts {

   private static final Log log = LogFactory.getLog( Contexts.class );

   static final ThreadLocal<Context> applicationContext = new ThreadLocal<Context>();
   static final ThreadLocal<Context> methodContext = new ThreadLocal<Context>();
   static final ThreadLocal<Context> eventContext = new ThreadLocal<Context>();
   static final ThreadLocal<Context> pageContext = new ThreadLocal<Context>();
   static final ThreadLocal<Context> sessionContext = new ThreadLocal<Context>();
   static final ThreadLocal<Context> conversationContext = new ThreadLocal<Context>();
   static final ThreadLocal<Context> businessProcessContext = new ThreadLocal<Context>();

	public static Context getEventContext() 
   {
		return eventContext.get();
	}

   public static Context getMethodContext() 
   {
      return methodContext.get();
   }

   public static Context getPageContext() 
   {
      return pageContext.get();
   }

	public static Context getSessionContext() 
   {
		return sessionContext.get();
	}

	public static Context getApplicationContext() 
   {
		return applicationContext.get();
	}

	public static Context getConversationContext() 
   {
		return conversationContext.get();
	}

    public static Context getBusinessProcessContext() 
    {
	    return businessProcessContext.get();
    }

	public static boolean isConversationContextActive() 
   {
		return getConversationContext() != null;
	}

	public static boolean isEventContextActive() 
   {
		return eventContext.get() != null;
	}

   public static boolean isMethodContextActive() 
   {
      return methodContext.get() != null;
   }

   public static boolean isPageContextActive() 
   {
      return pageContext.get() != null;
   }

	public static boolean isSessionContextActive() 
   {
		return sessionContext.get() != null;
	}

	public static boolean isApplicationContextActive() 
   {
		return applicationContext.get() != null;
	}

    public static boolean isBusinessProcessContextActive() 
    {
        return businessProcessContext.get() != null;
    }
   
   public static void removeFromAllContexts(String name)
   {
      log.debug("removing from all contexts: " + name);
      if (isMethodContextActive())
      {
         getMethodContext().remove(name);
      }
      if (isEventContextActive())
      {
         getEventContext().remove(name);
      }
      if (isPageContextActive())
      {
         getPageContext().remove(name);
      }
      if (isConversationContextActive())
      {
         getConversationContext().remove(name);
      }
      if (isSessionContextActive())
      {
         getSessionContext().remove(name);
      }
      if (isBusinessProcessContextActive())
      {
         getBusinessProcessContext().remove(name);
      }
      if (isApplicationContextActive())
      {
         getApplicationContext().remove(name);
      }
   }

   public static Object lookupInStatefulContexts(String name)
   {
      if (isMethodContextActive())
      {
         Object result = getMethodContext().get(name);
         if (result!=null)
         {
            log.debug("found in method context: " + name);
            return result;
         }
      }
      
      if (isEventContextActive())
      {
         Object result = getEventContext().get(name);
         if (result!=null)
         {
            log.debug("found in event context: " + name);
            return result;
         }
      }
      
      if (isPageContextActive())
      {
         Object result = getPageContext().get(name);
         if (result!=null)
         {
            log.debug("found in page context: " + name);
            return result;
         }
      }
      
      if (isConversationContextActive())
      {
         Object result = getConversationContext().get(name);
         if (result!=null)
         {
            log.debug("found in conversation context: " + name);
            return result;
         }
      }
      
      if (isSessionContextActive())
      {
         Object result = getSessionContext().get(name);
         if (result!=null)
         {
            log.debug("found in session context: " + name);
            return result;
         }
      }
      
      if (isBusinessProcessContextActive())
      {
         Object result = getBusinessProcessContext().get(name);
         if (result!=null)
         {
            log.debug("found in business process context: " + name);
            return result;
         }
      }
      
      if (isApplicationContextActive())
      {
         Object result = getApplicationContext().get(name);
         if (result!=null)
         {
            log.debug("found in application context: " + name);
            return result;
         }
      }
      
      return null;
      
   }
   
   public static void destroy(Context context)
   {
      if ( Events.exists() ) Events.instance().raiseEvent("org.jboss.seam.preDestroyContext." + context.getType().toString());
      
      Lifecycle.startDestroying();
      try
      {
         for ( String name: context.getNames() ) {
            Component component = Component.forName(name);
            log.debug("destroying: " + name);
            if ( component!=null )
            {
               Object object = context.get(name);
               if (object!=null) //in a portal environment, this is possible
               {
                  if ( Events.exists() ) Events.instance().raiseEvent("org.jboss.seam.preDestroy." + name);
                  try
                  {
                     component.callDestroyMethod(object);
                  }
                  catch (Exception e)
                  {
                     log.warn("Could not destroy component: " + name, e);
                  }
               }
            }
         }
      }
      finally
      {
         Lifecycle.stopDestroying();
      }
      
      if ( Events.exists() ) Events.instance().raiseEvent("org.jboss.seam.postDestroyContext." + context.getType().toString());
   }
   
}