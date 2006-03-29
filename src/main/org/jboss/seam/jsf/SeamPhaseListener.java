/*
 * JBoss, Home of Professional Open Source
 *
 * Distributable under LGPL license.
 * See terms of license at gnu.org.
 */
package org.jboss.seam.jsf;

import static javax.faces.event.PhaseId.ANY_PHASE;
import static javax.faces.event.PhaseId.RENDER_RESPONSE;
import static javax.faces.event.PhaseId.RESTORE_VIEW;

import java.util.Map;

import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.faces.event.PhaseEvent;
import javax.faces.event.PhaseId;
import javax.faces.event.PhaseListener;

import org.jboss.logging.Logger;
import org.jboss.seam.contexts.Contexts;
import org.jboss.seam.contexts.Lifecycle;
import org.jboss.seam.contexts.Session;
import org.jboss.seam.core.FacesMessages;
import org.jboss.seam.core.Init;
import org.jboss.seam.core.Manager;
import org.jboss.seam.core.Pageflow;
import org.jboss.seam.core.Pages;

/**
 * Manages the Seam contexts associated with a JSF request.
 * Manages the thread/context associations throught the
 * lifecycle of a JSF request.
 *
 * @author Gavin King
 */
public class SeamPhaseListener implements PhaseListener
{

   private static final Logger log = Logger.getLogger( SeamPhaseListener.class );

   public PhaseId getPhaseId()
   {
      return ANY_PHASE;
   }

   public void beforePhase(PhaseEvent event)
   {
      log.trace( "before phase: " + event.getPhaseId() );
      
      Lifecycle.setPhaseId( event.getPhaseId() );

      if ( event.getPhaseId() == RESTORE_VIEW )
      {
         Lifecycle.beginRequest( event.getFacesContext().getExternalContext() );
      }
      else if ( event.getPhaseId() == RENDER_RESPONSE )
      {
         Pages.instance().callAction();
         FacesMessages.instance().beforeRenderResponse();
         Manager.instance().prepareBackswitch(event);
         //beforeSaveState();
         //Manager.instance().conversationTimeout( event.getFacesContext().getExternalContext() );
      }

   }

   public void afterPhase(PhaseEvent event)
   {
      log.trace( "after phase: " + event.getPhaseId() );

      if ( event.getPhaseId() == RESTORE_VIEW )
      {
         restoreAnyConversationContext(event);
      }
      else if ( event.getPhaseId() == RENDER_RESPONSE )
      {
         if ( !Init.instance().isClientSideConversations() ) 
         {
            // difficult question: is it really safe to do this here?
            // right now we do have to do it after committing the Seam
            // transaction because we can't close EMs inside a txn
            // (this might be a bug in HEM)
            Manager.instance().conversationTimeout( event.getFacesContext().getExternalContext() );
         }
         Lifecycle.endRequest( event.getFacesContext().getExternalContext() );
      }
      else if ( event.getFacesContext().getResponseComplete() )
      {
         beforeSaveState( event.getFacesContext() );
         Lifecycle.endRequest( event.getFacesContext().getExternalContext() );
      }

      Lifecycle.setPhaseId(null);
      
   }
   
   /**
    * Called just before the StateManager serializes the component tree
    */
   static void beforeSaveState(FacesContext ctx) {
      log.debug( "Before saving state" );
   
      /*if ( !Init.instance().isClientSideConversations() ) 
      {
         // difficult question: does this really need to happen before 
         // storeAnyConversationContext, or could it be done later
         Manager.instance().conversationTimeout( ctx.getExternalContext() );
      }*/
      storeAnyConversationContext(ctx);
   }

   private static void restoreAnyConversationContext(PhaseEvent event)
   {
      Lifecycle.resumePage();
      ExternalContext externalContext = event.getFacesContext().getExternalContext();
      Manager.instance().restoreConversation( getParameters(event) );
      Lifecycle.resumeConversation( externalContext );
      if ( Init.instance().isJbpmInstalled() )
      {
         Pageflow.instance().validatePageflow();
      }
      
      log.debug( "After restore view, conversation context: " + Contexts.getConversationContext() );
   }

   static void storeAnyConversationContext(FacesContext ctx)
   {
      Lifecycle.flushClientConversation();
      if ( !Contexts.isConversationContextActive() )
      {
         log.debug( "No active conversation context" );
      }
      else
      {
         Session session = Session.getSession(ctx.getExternalContext(), true);
         Manager.instance().storeConversation( session );
      }
      Lifecycle.flushPage();
   }

   private static Map getParameters(PhaseEvent event) {
      return event.getFacesContext().getExternalContext().getRequestParameterMap();
   }

   private static Map getAttributes(FacesContext facesContext)
   {
      return facesContext.getViewRoot().getAttributes();
   }

}
