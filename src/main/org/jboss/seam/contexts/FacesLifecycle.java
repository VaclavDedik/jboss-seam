/*
 * JBoss, Home of Professional Open Source
 *
 * Distributable under LGPL license.
 * See terms of license at gnu.org.
 */
package org.jboss.seam.contexts;

import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.faces.event.PhaseId;

import org.jboss.seam.core.Init;
import org.jboss.seam.core.ServletSession;
import org.jboss.seam.log.LogProvider;
import org.jboss.seam.log.Logging;
import org.jboss.seam.navigation.Pages;

/**
 * @author Gavin King
 * @author <a href="mailto:theute@jboss.org">Thomas Heute</a>
 */
public class FacesLifecycle
{

   private static final LogProvider log = Logging.getLogProvider(FacesLifecycle.class);
   
   public static void setPhaseId(PhaseId phase)
   {
      Lifecycle.setPhaseId(phase);
   }
   
   public static PhaseId getPhaseId()
   {
      return (PhaseId) Lifecycle.getPhaseId();
   }

   public static void clearPhaseId()
   {
      Lifecycle.setPhaseId(null);
   }

   public static void beginRequest(ExternalContext externalContext) 
   {
      log.debug( ">>> Begin JSF request" );
      Contexts.eventContext.set( new EventContext( externalContext.getRequestMap() ) );
      Contexts.applicationContext.set( new ApplicationContext( externalContext.getApplicationMap() ) );
      Contexts.sessionContext.set( new SessionContext( externalContext.getSessionMap() ) );
      ServletSession servletSession = ServletSession.getInstance();
      if ( servletSession!=null && servletSession.isInvalidDueToNewScheme( Pages.getRequestScheme( FacesContext.getCurrentInstance() ) ) )
      {
         invalidateSession(externalContext);
      }
      Contexts.conversationContext.set(null); //in case endRequest() was never called
      //Events.instance(); //TODO: only for now, until we have a way to do EL outside of JSF!
   }

   public static void beginExceptionRecovery(ExternalContext externalContext)
   {
      log.debug(">>> Begin exception recovery");
      Contexts.applicationContext.set( new ApplicationContext( externalContext.getApplicationMap() ) );
      Contexts.eventContext.set( new EventContext( externalContext.getRequestMap() ) );
      Contexts.sessionContext.set( new SessionContext( externalContext.getSessionMap() ) );
      Contexts.conversationContext.set( new ServerConversationContext( externalContext.getSessionMap() ) );
      Contexts.pageContext.set(null);
      Contexts.businessProcessContext.set(null); //TODO: is this really correct?
   }

   public static void endRequest(ExternalContext externalContext) 
   {
      log.debug("After render response, destroying contexts");
      try
      {
         ServletSession servletSession = ServletSession.getInstance();
         boolean sessionInvalid = servletSession!=null && servletSession.isInvalid();
         
         Contexts.flushAndDestroyContexts();

         if (sessionInvalid)
         {
            Lifecycle.clearThreadlocals();
            Lifecycle.setPhaseId(null);
            invalidateSession(externalContext);
            //actual session context will be destroyed from the listener
         }
      }
      finally
      {
         Lifecycle.clearThreadlocals();
         log.debug( "<<< End JSF request" );
      }
   }
   
   /**
    * Invalidate the session, no matter what kind of session it is
    * (portlet or servlet). Why is this method not on ExternalContext?!
    * Oh boy, those crazy rascals in the JSF EG...
    */
   public static void invalidateSession(ExternalContext externalContext)
   {
      Object session = externalContext.getSession(false);
      if (session!=null)
      {
         try
         {
            session.getClass().getMethod("invalidate").invoke(session);
         }
         catch (Exception e)
         {
            throw new RuntimeException(e);
         }
      }
   }

   public static void resumeConversation(ExternalContext externalContext)
   {
      Init init = Init.instance();
      Context conversationContext = init.isClientSideConversations() ?
            (Context) new ClientConversationContext() :
            (Context) new ServerConversationContext( externalContext.getSessionMap() );
      Contexts.conversationContext.set( conversationContext );
      Contexts.businessProcessContext.set( new BusinessProcessContext() );
   }

   public static void resumePage()
   {
      Contexts.pageContext.set( new PageContext() );
   }

}
