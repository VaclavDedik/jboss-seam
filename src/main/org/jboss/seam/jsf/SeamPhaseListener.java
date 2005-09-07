/*
 * JBoss, Home of Professional Open Source
 *
 * Distributable under LGPL license.
 * See terms of license at gnu.org.
 */
package org.jboss.seam.jsf;

import static javax.faces.event.PhaseId.ANY_PHASE;
import static javax.faces.event.PhaseId.APPLY_REQUEST_VALUES;
import static javax.faces.event.PhaseId.INVOKE_APPLICATION;
import static javax.faces.event.PhaseId.PROCESS_VALIDATIONS;
import static javax.faces.event.PhaseId.RENDER_RESPONSE;
import static javax.faces.event.PhaseId.RESTORE_VIEW;
import static javax.faces.event.PhaseId.UPDATE_MODEL_VALUES;

import java.util.Map;

import javax.faces.event.PhaseEvent;
import javax.faces.event.PhaseId;
import javax.faces.event.PhaseListener;
import javax.servlet.http.HttpSession;

import org.jboss.logging.Logger;
import org.jboss.seam.contexts.Contexts;
import org.jboss.seam.contexts.Lifecycle;
import org.jboss.seam.contexts.BusinessProcessContext;
import org.jboss.seam.contexts.Context;
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
         Lifecycle.beginRequest( getSession( event ) );
         Manager.instance().setProcessInterceptors( false );
         log.info( "About to restore view" );
      }
      else if ( event.getPhaseId() == RENDER_RESPONSE )
      {
         storeAnyBusinessProcessContext(); // needs to come *before* storing conversation!
         storeAnyConversationContext( event );
         Manager.instance().conversationTimeout( getSession( event ) );
      }
      else if ( event.getPhaseId() == INVOKE_APPLICATION )
      {
         Manager.instance().setProcessInterceptors( true );
         log.info( "About to invoke application" );
      }
      else if ( event.getPhaseId() == UPDATE_MODEL_VALUES )
      {
         log.info( "About to update model values" );
      }
      else if ( event.getPhaseId() == PROCESS_VALIDATIONS )
      {
         log.info( "About to process validations" );
      }
      else if ( event.getPhaseId() == APPLY_REQUEST_VALUES )
      {
         log.info( "About to apply request values" );
      }
   }

   public void afterPhase(PhaseEvent event)
   {
      log.trace( "after phase: " + event.getPhaseId() );

      if ( event.getPhaseId() == RESTORE_VIEW )
      {
         restoreAnyConversationContext( event );
         restoreAnyBusinessProcessContext();
      }
      else if ( event.getPhaseId() == RENDER_RESPONSE )
      {
         Lifecycle.endRequest( getSession( event ) );
      }
      else if ( event.getPhaseId() == INVOKE_APPLICATION )
      {
         log.info( "After invoke application" );
         Manager.instance().setProcessInterceptors( false );
      }
      else if ( event.getPhaseId() == UPDATE_MODEL_VALUES )
      {
         log.info( "After update model values" );
      }
      else if ( event.getPhaseId() == PROCESS_VALIDATIONS )
      {
         log.info( "After process validations" );
      }
      else if ( event.getPhaseId() == APPLY_REQUEST_VALUES )
      {
         log.info( "After apply request values" );
      }
   }

   private static void restoreAnyConversationContext(PhaseEvent event)
   {
      String conversationId = Manager.instance().restore( getAttributes( event ) );
      Lifecycle.resumeConversation( getSession( event ), conversationId );
      log.info( "After restore view, conversation context: " + Contexts.getConversationContext() );
   }

   private static void storeAnyConversationContext(PhaseEvent event)
   {
      log.info( "Before render response" );
      if ( !Contexts.isConversationContextActive() )
      {
         log.info( "No active conversation context" );
      }
      else
      {
         Manager.instance().store( getAttributes( event ) );
      }
   }

   private static void storeAnyBusinessProcessContext()
   {
      Context conversation = Contexts.getConversationContext();
      BusinessProcessContext jbpmContext = ( BusinessProcessContext ) Contexts.getBusinessProcessContext();

      log.trace( "storing bpm recoverable state" );
      conversation.set( JBPM_STATE_MAP, jbpmContext.getRecoverableState() );
   }

   private static void restoreAnyBusinessProcessContext()
   {
      Context conversation = Contexts.getConversationContext();
      Map state = ( Map ) conversation.get( JBPM_STATE_MAP );
      log.trace( "restoring bpm state from : " + state );
      Lifecycle.recoverBusinessProcessContext( state );
   }

   private static HttpSession getSession(PhaseEvent event)
   {
      return ( HttpSession ) event.getFacesContext().getExternalContext().getSession( true );
   }

   private static Map getAttributes(PhaseEvent event)
   {
      return event.getFacesContext().getViewRoot().getAttributes();
   }

}
