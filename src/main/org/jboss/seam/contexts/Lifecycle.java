//$Id$
package org.jboss.seam.contexts;

import java.util.Map;
import java.util.Set;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpSession;

import org.jboss.logging.Logger;
import org.jboss.seam.Component;
import org.jboss.seam.Seam;
import org.jboss.seam.core.Manager;

public class Lifecycle
{

   private static final Logger log = Logger.getLogger( Lifecycle.class );

   public static void beginRequest(HttpSession session) {
      log.info( ">>> Begin web request" );
      //eventContext.set( new WebRequestContext( request ) );
      Contexts.eventContext.set( new EventContext() );
      Contexts.sessionContext.set( new WebSessionContext(session) );
      Contexts.applicationContext.set( new WebApplicationContext( session.getServletContext() ) );
      Contexts.conversationContext.set(null); //in case endRequest() was never called
   }

   public static void beginInitialization(ServletContext servletContext)
   {
      Context context = new WebApplicationContext( servletContext );
      Contexts.applicationContext.set( context );
   }

   public static void endInitialization()
   {
	  //instantiate @Startup components
      Context context = Contexts.getApplicationContext();
      for ( String name: context.getNames() ) {
    	  Object object = context.get(name);
    	  if ( object instanceof Component )
    	  {
	        Component component = (Component) object;
	        if ( component.isStartup() )
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
            startup( Component.forName(dependency) );
         }
      }
      Component.getInstance( component.getName(), true );
   }

   public static void endApplication(ServletContext servletContext)
   {
      Context tempApplicationContext = new WebApplicationContext( servletContext );
      Contexts.applicationContext.set( tempApplicationContext );
      log.info("destroying application context");
      Contexts.destroy(tempApplicationContext);
      Contexts.applicationContext.set(null);
      Contexts.eventContext.set(null);
      Contexts.sessionContext.set(null);
      Contexts.conversationContext.set(null);
   }

   public static void endSession(HttpSession session)
   {
      log.info("End of session, destroying contexts");

      Context tempAppContext = new WebApplicationContext(session.getServletContext() );
      Contexts.applicationContext.set(tempAppContext);

      //this is used just as a place to stick the ConversationManager
      Context tempEventContext = new EventContext();
      Contexts.eventContext.set(tempEventContext);

      //this is used (a) for destroying session-scoped components
      //and is also used (b) by the ConversationManager
      Context tempSessionContext = new WebSessionContext( session );
      Contexts.sessionContext.set(tempSessionContext);

      Set<String> ids = Manager.instance().getSessionConversationIds();
      log.info("destroying conversation contexts: " + ids);
      for (String conversationId: ids)
      {
         Contexts.destroy( new ConversationContext(session, conversationId) );
      }

      log.info("destroying session context");
      Contexts.destroy(tempSessionContext);
      Contexts.sessionContext.set(null);

      Contexts.destroy(tempEventContext);
      Contexts.eventContext.set(null);

      Contexts.conversationContext.set(null);
      Contexts.applicationContext.set(null);
   }

   public static void endRequest(HttpSession session) {

      log.info("After render response, destroying contexts");

      try
      {

         if ( Contexts.isBusinessProcessContextActive() )
         {
            Contexts.getBusinessProcessContext().flush();
            Contexts.destroy( Contexts.getBusinessProcessContext() );
         }

         if ( Contexts.isEventContextActive() )
         {
            log.info("destroying event context");
            Contexts.destroy( Contexts.getEventContext() );
         }

         if ( Contexts.isConversationContextActive() )
         {
            if ( !Seam.isSessionInvalid() )
            {
               Contexts.getConversationContext().flush();
            }
            if ( !Manager.instance().isLongRunningConversation() )
            {
               log.info("destroying conversation context");
               Contexts.destroy( Contexts.getConversationContext() );
            }
         }

         if ( Seam.isSessionInvalid() )
         {
            session.invalidate();
            //actual session context will be destroyed from the listener
         }

      }
      finally
      {
         Contexts.eventContext.set( null );
         Contexts.sessionContext.set( null );
         Contexts.conversationContext.set( null );
         Contexts.businessProcessContext.set( null );
         Contexts.applicationContext.set( null );
      }

      log.info( "<<< End web request" );
   }

   public static void resumeConversation(HttpSession session, String id)
   {
      Contexts.conversationContext.set( new ConversationContext(session, id) );
   }

   public static void recoverBusinessProcessContext(Map state)
   {
      Contexts.businessProcessContext.set( new BusinessProcessContext( state ) );
   }

}
