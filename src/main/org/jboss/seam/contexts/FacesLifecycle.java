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

import org.jboss.seam.log.LogProvider;
import org.jboss.seam.log.Logging;
import org.jboss.seam.navigation.Pages;
import org.jboss.seam.web.Session;

/**
 * Methods for setup and teardown of Seam contexts at the
 * beginning and end of JSF requests.
 * 
 * @see org.jboss.seam.jsf.SeamPhaseListener
 * @author Gavin King
 * @author <a href="mailto:theute@jboss.org">Thomas Heute</a>
 */
public class FacesLifecycle
{
   private static ThreadLocal<PhaseId> phaseId = new ThreadLocal<PhaseId>();

   private static final LogProvider log = Logging.getLogProvider(FacesLifecycle.class);
   
   public static void setPhaseId(PhaseId phase)
   {
      phaseId.set(phase);
   }
   
   public static PhaseId getPhaseId()
   {
      return phaseId.get();
   }

   public static void clearPhaseId()
   {
      setPhaseId(null);
   }

   public static void beginRequest(ExternalContext externalContext) 
   {
      log.debug( ">>> Begin JSF request" );
      Contexts.eventContext.set( new EventContext( externalContext.getRequestMap() ) );
      Contexts.applicationContext.set( new ApplicationContext( externalContext.getApplicationMap() ) );
      Contexts.sessionContext.set( new SessionContext( externalContext.getSessionMap() ) );
      Session session = Session.getInstance();
      if ( session!=null && session.isInvalidDueToNewScheme( Pages.getRequestScheme( FacesContext.getCurrentInstance() ) ) )
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
      ServerConversationContext conversationContext = new ServerConversationContext( externalContext.getSessionMap() );
      Contexts.conversationContext.set(conversationContext);
      Contexts.pageContext.set(null);
      Contexts.businessProcessContext.set(null); //TODO: is this really correct?
      conversationContext.unflush();
   }

   public static void endRequest(ExternalContext externalContext) 
   {
      log.debug("After render response, destroying contexts");
      try
      {
         Session session = Session.getInstance();
         boolean sessionInvalid = session!=null && session.isInvalid();
         
         Contexts.flushAndDestroyContexts();

         if (sessionInvalid)
         {
            Lifecycle.clearThreadlocals();
            clearPhaseId();
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
   private static void invalidateSession(ExternalContext externalContext)
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
      ServerConversationContext conversationContext = new ServerConversationContext( externalContext.getSessionMap() );
      /*Context conversationContext = Init.instance().isClientSideConversations() ?
            (Context) new ClientConversationContext() :
            (Context) new ServerConversationContext( externalContext.getSessionMap() );*/
      Contexts.conversationContext.set(conversationContext);
      Contexts.businessProcessContext.set( new BusinessProcessContext() );
      conversationContext.unflush();
   }

   public static void resumePage()
   {
      Contexts.pageContext.set( new PageContext() );
   }

}
