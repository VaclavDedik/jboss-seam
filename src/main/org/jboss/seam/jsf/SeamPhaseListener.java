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

import java.io.IOException;
import java.util.Map;

import javax.faces.application.Application;
import javax.faces.application.StateManager;
import javax.faces.component.UIViewRoot;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
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
   
   private boolean setStateManager = false;
   
   private void setStateManager(FacesContext facesContext) 
   {
      if (setStateManager) return;
      Application app = facesContext.getApplication();
      StateManager stateManager = new StateManagerInterceptor( app.getStateManager() );
      app.setStateManager( stateManager);
      setStateManager = true;
   }

   public PhaseId getPhaseId()
   {
      return ANY_PHASE;
   }

   public void beforePhase(PhaseEvent event)
   {
      setStateManager( event.getFacesContext() );
      log.trace( "before phase: " + event.getPhaseId() );
      
      if ( event.getPhaseId() == RESTORE_VIEW )
      {
         //If this is a faces request, set up some contexts at the
         //start of RESTORE_VIEW, and the rest at the end
         Lifecycle.beginRequest( event.getFacesContext().getExternalContext() );
      }
      else if ( event.getPhaseId() == RENDER_RESPONSE )
      {
         //If this is a non-faces request, we need to set up contexts at the
         //start of the RENDER_RESPONSE phase
         boolean isNonFacesRequest = event.getFacesContext().getRenderKit().getResponseStateManager()
               .getComponentStateToRestore( event.getFacesContext() )==null;
         if (isNonFacesRequest) 
         {
            Lifecycle.beginRequest( event.getFacesContext().getExternalContext() );
            restoreAnyConversationContext( event );
            restoreAnyBusinessProcessContext();
         }
         
         //beforeSaveState();
         //Manager.instance().conversationTimeout( event.getFacesContext().getExternalContext() );

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
         ExternalContext externalContext = event.getFacesContext().getExternalContext();
         Manager.instance().conversationTimeout( externalContext );
         Lifecycle.endRequest( externalContext );
      }
   }
   
   /**
    * Called just before the StateManager serializes the component tree
    */
   private void beforeSaveState(FacesContext ctx) {
      storeAnyBusinessProcessContext(); // needs to come *before* storing conversation!
      storeAnyConversationContext(ctx);
   }

   private static void restoreAnyConversationContext(PhaseEvent event)
   {
      String conversationId = Manager.instance().restore( getAttributes( event.getFacesContext() ) );
      Lifecycle.resumeConversation( event.getFacesContext().getExternalContext(), conversationId );
      log.debug( "After restore view, conversation context: " + Contexts.getConversationContext() );
   }

   private static void storeAnyConversationContext(FacesContext ctx)
   {
      log.debug( "Before render response" );
      Lifecycle.flushConversation();
      if ( !Contexts.isConversationContextActive() )
      {
         log.debug( "No active conversation context" );
      }
      else
      {
         Manager.instance().store( getAttributes(ctx) );
      }
   }

   private static void storeAnyBusinessProcessContext()
   {
      if ( !Contexts.isConversationContextActive() )
      {
         log.debug( "No active business process context" );
      }
      else
      {
         //Abstract this stuff and move to Manager (I don't like 
         //the typecast to BusinessProcessContext
         Context conversation = Contexts.getConversationContext();
         BusinessProcessContext jbpmContext = (BusinessProcessContext) Contexts.getBusinessProcessContext();

         log.trace( "Storing business process state" );
         conversation.set( JBPM_STATE_MAP, jbpmContext.getRecoverableState() );
      }
   }

   private static void restoreAnyBusinessProcessContext()
   {
      Map state = (Map) Contexts.getConversationContext().get( JBPM_STATE_MAP );
      Lifecycle.resumeBusinessProcess( state );
      log.trace( "After restore view, business process context: " + Contexts.getBusinessProcessContext() );
   }

   private static Map getAttributes(FacesContext facesContext)
   {
      return facesContext.getViewRoot().getAttributes();
   }
   
   /**
    * A wrapper for the JSF implementation's StateManager that allows
    * us to intercept saving of the serialized component tree. This
    * is quite ugly but was needed in order to allow conversations to
    * be started and manipulated during the RENDER_RESPONSE phase.
    * 
    * @author Gavin King
    */
   private final class StateManagerInterceptor extends StateManager {
      private final StateManager stateManager;

      private StateManagerInterceptor(StateManager sm) {
         this.stateManager = sm;
      }

      protected Object getComponentStateToSave(FacesContext ctx) {
         throw new UnsupportedOperationException();
      }

      protected Object getTreeStructureToSave(FacesContext ctx) {
         throw new UnsupportedOperationException();
      }

      protected void restoreComponentState(FacesContext ctx, UIViewRoot viewRoot, String str) {
         throw new UnsupportedOperationException();
      }

      protected UIViewRoot restoreTreeStructure(FacesContext ctx, String str1, String str2) {
         throw new UnsupportedOperationException();
      }

      public UIViewRoot restoreView(FacesContext ctx, String str1, String str2) {
         return stateManager.restoreView(ctx, str1, str2);
      }

      public SerializedView saveSerializedView(FacesContext ctx) {
         beforeSaveState(ctx);
         return stateManager.saveSerializedView(ctx);
      }

      public void writeState(FacesContext ctx, SerializedView sv) throws IOException {
         stateManager.writeState(ctx, sv);
      }

      public boolean isSavingStateInClient(FacesContext ctx) {
         return stateManager.isSavingStateInClient(ctx);
      }
   }

}
