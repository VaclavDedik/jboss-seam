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

import javax.faces.event.PhaseEvent;
import javax.faces.event.PhaseId;
import javax.faces.event.PhaseListener;

import org.jboss.logging.Logger;
import org.jboss.seam.Session;
import org.jboss.seam.contexts.BusinessProcessContext;
import org.jboss.seam.contexts.Context;
import org.jboss.seam.contexts.Contexts;
import org.jboss.seam.contexts.Lifecycle;
import org.jboss.seam.core.Manager;

/**
 * Manages the thread/context associations throught the
 * lifecycle of a JSF request.
 *
 * @author Gavin King
 */
public class SeamPhaseListener implements PhaseListener
{

   private static final String JBPM_STATE_MAP = "org.jboss.seam.bpm.recoverableState";

   private static Logger log = Logger.getLogger( SeamPhaseListener.class );

   public PhaseId getPhaseId()
   {
      return ANY_PHASE;
   }

   public void beforePhase(PhaseEvent event)
   {
      log.trace( "before phase: " + event.getPhaseId() );
      
      if ( event.getPhaseId() == RESTORE_VIEW )
      {
         Lifecycle.beginRequest( event.getFacesContext().getExternalContext() );
         log.trace( "About to restore view" );
      }
      else if ( event.getPhaseId() == RENDER_RESPONSE )
      {
         storeAnyBusinessProcessContext(); // needs to come *before* storing conversation!
         storeAnyConversationContext( event );
         Manager.instance().conversationTimeout( event.getFacesContext().getExternalContext() );
      }

      Lifecycle.setPhaseId( event.getPhaseId() );

   }

   public void afterPhase(PhaseEvent event)
   {
      log.trace( "after phase: " + event.getPhaseId() );

      Lifecycle.setPhaseId(null);

      if ( event.getPhaseId() == RESTORE_VIEW )
      {
         restoreAnyConversationContext( event );
         restoreAnyBusinessProcessContext();
      }
      else if ( event.getPhaseId() == RENDER_RESPONSE )
      {
         Lifecycle.endRequest( event.getFacesContext().getExternalContext() );
      }
   }

   private static void restoreAnyConversationContext(PhaseEvent event)
   {
      String conversationId = Manager.instance().restore( getAttributes( event ) );
      Lifecycle.resumeConversation( Session.getSession(event.getFacesContext().getExternalContext(), true), conversationId );
      log.debug( "After restore view, conversation context: " + Contexts.getConversationContext() );
   }

   private static void storeAnyConversationContext(PhaseEvent event)
   {
      log.debug( "Before render response" );
      Lifecycle.flushConversation();
      if ( !Contexts.isConversationContextActive() )
      {
         log.debug( "No active conversation context" );
      }
      else
      {
         Manager.instance().store( getAttributes( event ) );
      }
   }

   private static void storeAnyBusinessProcessContext()
   {
      if ( !Contexts.isConversationContextActive() )
      {
         log.debug( "No active conversation context" );
      }
      else
      {
         Context conversation = Contexts.getConversationContext();
         BusinessProcessContext jbpmContext = ( BusinessProcessContext ) Contexts.getBusinessProcessContext();

         log.trace( "storing bpm recoverable state" );
         conversation.set( JBPM_STATE_MAP, jbpmContext.getRecoverableState() );
      }
   }

   private static void restoreAnyBusinessProcessContext()
   {
      Context conversation = Contexts.getConversationContext();
      Map state = ( Map ) conversation.get( JBPM_STATE_MAP );
      log.trace( "restoring bpm state from : " + state );
      Lifecycle.recoverBusinessProcessContext( state );
   }

   private static Map getAttributes(PhaseEvent event)
   {
      return event.getFacesContext().getViewRoot().getAttributes();
   }

}
