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
import java.util.Set;

import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.faces.el.MethodBinding;
import javax.faces.event.PhaseEvent;
import javax.faces.event.PhaseId;
import javax.faces.event.PhaseListener;
import javax.faces.model.DataModel;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
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

   private static final Log log = LogFactory.getLog( SeamPhaseListener.class );

   public PhaseId getPhaseId()
   {
      return ANY_PHASE;
   }

   public void beforePhase(PhaseEvent event)
   {
      log.trace( "before phase: " + event.getPhaseId() );
      
      Lifecycle.setPhaseId( event.getPhaseId() );

      FacesContext facesContext = event.getFacesContext();
      
      if ( event.getPhaseId() == RESTORE_VIEW )
      {
         Lifecycle.beginRequest( facesContext.getExternalContext() );
      }
      else if ( event.getPhaseId() == RENDER_RESPONSE )
      {
         callPageActions(event);
         FacesMessages.instance().beforeRenderResponse();
         Manager.instance().prepareBackswitch(event);
         
         //if the page actions called responseComplete(),
         //we need to call beforeSaveState(), since the
         //component tree will not get rendered
         if ( facesContext.getResponseComplete() )
         {
            beforeSaveState( facesContext );
            //MyFaces bug: ?
            Lifecycle.endRequest( facesContext.getExternalContext() );
         }
      }

   }

   private void callPageActions(PhaseEvent event)
   {
      Lifecycle.setPhaseId( PhaseId.INVOKE_APPLICATION );
      boolean actionsWereCalled = false;
      try
      {
         actionsWereCalled = callAction( event.getFacesContext() ) || actionsWereCalled;
         actionsWereCalled = Pages.instance().callAction() || actionsWereCalled;
      }
      finally
      {
         Lifecycle.setPhaseId( PhaseId.RENDER_RESPONSE );
         if (actionsWereCalled) 
         {
            FacesMessages.afterPhase();
            afterPageActions();
         }
      }
   }
   
   protected void afterPageActions() {}

   private boolean callAction(FacesContext facesContext)
   {
      //TODO: refactor with Pages.callAction!!
      
      boolean result = false;
      
      String outcome = (String) facesContext.getExternalContext().getRequestParameterMap().get("actionOutcome");
      String fromAction = outcome;
      
      if (outcome==null)
      {
         String action = (String) facesContext.getExternalContext().getRequestParameterMap().get("actionMethod");
         if (action!=null)
         {
            String expression = "#{" + action + "}";
            if ( !isActionAllowed(facesContext, expression) ) return result;
            result = true;
            MethodBinding actionBinding = facesContext.getApplication().createMethodBinding(expression, null);
            outcome = (String) actionBinding.invoke( facesContext, null );
            fromAction = expression;
         }
      }
      
      if (outcome!=null)
      {
         facesContext.getApplication().getNavigationHandler().handleNavigation(facesContext, fromAction, outcome);
      }
      
      return result;
   }

   private boolean isActionAllowed(FacesContext facesContext, String expression)
   {
      Map applicationMap = facesContext.getExternalContext().getApplicationMap();
      Set actions = (Set) applicationMap.get("org.jboss.seam.actions");
      if (actions==null) return false;
      synchronized (actions)
      {
         return actions.contains(expression);
      }
   }

   public void afterPhase(PhaseEvent event)
   {
      log.trace( "after phase: " + event.getPhaseId() );

      FacesContext facesContext = event.getFacesContext();
      
      if ( event.getPhaseId() == RESTORE_VIEW )
      {
         restoreAnyConversationContext(event);
      }
      
      //has to happen after, since restoreAnyConversationContext() 
      //can add messages
      FacesMessages.afterPhase();
            
      if ( event.getPhaseId() == RENDER_RESPONSE )
      {
         if ( !Init.instance().isClientSideConversations() ) 
         {
            // difficult question: is it really safe to do this here?
            // right now we do have to do it after committing the Seam
            // transaction because we can't close EMs inside a txn
            // (this might be a bug in HEM)
            Manager.instance().conversationTimeout( facesContext.getExternalContext() );
         }
         Lifecycle.endRequest( facesContext.getExternalContext() );
      }
      else if ( event.getPhaseId() != RESTORE_VIEW && facesContext.getResponseComplete() )
      {
         //responseComplete() was called by one of the other
         //phases, so we will never get to the RENDER_RESPONSE
         //phase
         beforeSaveState( facesContext );
         Lifecycle.endRequest( facesContext.getExternalContext() );
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
      Map parameters = getParameters(event);
      Manager.instance().restoreConversation( parameters );
      Lifecycle.resumeConversation( externalContext );
      if ( Init.instance().isJbpmInstalled() )
      {
         Pageflow.instance().validatePageflow();
      }
      Manager.instance().handleConversationPropagation(parameters);
      selectDataModelRow(parameters);
      
      log.debug( "After restore view, conversation context: " + Contexts.getConversationContext() );
   }
   
   private static void selectDataModelRow(Map parameters)
   {
      String dataModelSelection = (String) parameters.get("dataModelSelection");
      if (dataModelSelection!=null)
      {
         int loc = dataModelSelection.indexOf('[');
         String name = dataModelSelection.substring(0, loc);
         int index = Integer.parseInt( dataModelSelection.substring( loc+1, dataModelSelection.length()-1 ) );
         Object value = Contexts.lookupInStatefulContexts(name);
         if (value!=null)
         {
            ( (DataModel) value ).setRowIndex(index);
         }
      }
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
         Manager.instance().storeConversation( session, ctx.getExternalContext().getResponse() );
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
