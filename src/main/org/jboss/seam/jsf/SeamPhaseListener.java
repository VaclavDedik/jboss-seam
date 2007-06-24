/*
 * JBoss, Home of Professional Open Source
 *
 * Distributable under LGPL license.
 * See terms of license at gnu.org.
 */
package org.jboss.seam.jsf;

import static javax.faces.event.PhaseId.ANY_PHASE;
import static javax.faces.event.PhaseId.INVOKE_APPLICATION;
import static javax.faces.event.PhaseId.PROCESS_VALIDATIONS;
import static javax.faces.event.PhaseId.RENDER_RESPONSE;
import static javax.faces.event.PhaseId.RESTORE_VIEW;

import java.lang.reflect.Method;
import java.util.Map;

import javax.faces.application.FacesMessage;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.faces.event.PhaseEvent;
import javax.faces.event.PhaseId;
import javax.faces.event.PhaseListener;

import org.jboss.seam.Seam;
import org.jboss.seam.contexts.Context;
import org.jboss.seam.contexts.Contexts;
import org.jboss.seam.contexts.FacesLifecycle;
import org.jboss.seam.core.ConversationList;
import org.jboss.seam.core.ConversationPropagation;
import org.jboss.seam.core.ConversationStack;
import org.jboss.seam.core.Events;
import org.jboss.seam.core.Init;
import org.jboss.seam.core.Manager;
import org.jboss.seam.exceptions.Exceptions;
import org.jboss.seam.faces.FacesManager;
import org.jboss.seam.faces.FacesMessages;
import org.jboss.seam.faces.FacesPage;
import org.jboss.seam.faces.Switcher;
import org.jboss.seam.faces.Validation;
import org.jboss.seam.log.LogProvider;
import org.jboss.seam.log.Logging;
import org.jboss.seam.navigation.Pages;
import org.jboss.seam.pageflow.Pageflow;
import org.jboss.seam.persistence.PersistenceContexts;
import org.jboss.seam.transaction.Transaction;
import org.jboss.seam.util.Reflections;

/**
 * Manages the Seam contexts associated with a JSF request
 * throughout the lifecycle of the request. Performs
 * transaction demarcation when Seam transaction management
 * is enabled. Hacks the JSF lifecyle to provide page
 * actions and page parameters.
 *
 * @author Gavin King
 */
public class SeamPhaseListener implements PhaseListener
{
   private static final long serialVersionUID = -9127555729455066493L;
   
   private static final LogProvider log = Logging.getLogProvider(SeamPhaseListener.class);
   
   private static boolean exists = false;
   
   private static final Method SET_RENDER_PARAMETER;
   private static final Class ACTION_RESPONSE;
   private static final Class PORTLET_REQUEST;
   
   static
   {
      Method method = null;
      Class actionResponseClass = null;
      Class portletRequestClass = null;
      try
      {
         Class[] parameterTypes = { String.class, String.class };
         actionResponseClass = Class.forName("javax.portlet.ActionResponse");
         portletRequestClass = Class.forName("javax.portlet.PortletRequest");
         method = actionResponseClass.getMethod("setRenderParameter", parameterTypes);
      }
      catch (Exception e) {}
      SET_RENDER_PARAMETER = method;
      ACTION_RESPONSE = actionResponseClass;
      PORTLET_REQUEST = portletRequestClass;
   }

   public SeamPhaseListener()
   {
      if (exists) 
      {
         log.warn("There should only be one Seam phase listener per application");
      }
      exists=true;
   }
   
   public PhaseId getPhaseId()
   {
      return ANY_PHASE;
   }
   
   public void beforePhase(PhaseEvent event)
   {
      log.trace( "before phase: " + event.getPhaseId() );
      
      FacesLifecycle.setPhaseId( event.getPhaseId() );

      try
      {
         if ( isPortletRequest(event) )
         {
            beforePortletPhase(event);
         }
         else
         {
            beforeServletPhase(event);
         }
         raiseEventsBeforePhase(event);
      }
      catch (Exception e)
      {
         log.error("uncaught exception", e);
         try
         {
            Exceptions.instance().handle(e);
         }
         catch (Exception ehe) 
         {
            log.error("swallowing exception", e);
         }
      }

   }

   private void beforeServletPhase(PhaseEvent event)
   {
      if ( event.getPhaseId() == RESTORE_VIEW )
      {
         beforeRestoreView( event.getFacesContext() );
      }
      
      handleTransactionsBeforePhase(event);         
      
      if ( event.getPhaseId() == RENDER_RESPONSE )
      {
         beforeRender(event);
      }
      
   }
   
   private void beforePortletPhase(PhaseEvent event)
   {

      FacesContext facesContext = event.getFacesContext();
      
      if ( event.getPhaseId() == RESTORE_VIEW || event.getPhaseId() == RENDER_RESPONSE )
      {
         beforeRestoreView(facesContext);
      }
      
      //delegate to subclass:
      handleTransactionsBeforePhase(event);
      
      if ( event.getPhaseId() == RENDER_RESPONSE )
      {
         afterRestoreView(facesContext);         
         beforeRender(event);
      }
   }

   public void afterPhase(PhaseEvent event)
   {
      log.trace( "after phase: " + event.getPhaseId() );
      
      try
      {
         raiseEventsAfterPhase(event);
         if ( isPortletRequest(event) )
         {
            afterPortletPhase(event);
         }
         else
         {
            afterServletPhase(event);
         }
      }
      catch (Exception e)
      {
         log.error("uncaught exception", e);
         try
         {
            Exceptions.instance().handle(e);
         }
         catch (Exception ehe) 
         {
            log.error("swallowing exception", e);
         }
      }

      FacesLifecycle.clearPhaseId();
      
   }

   private void afterServletPhase(PhaseEvent event)
   {
  
      FacesContext facesContext = event.getFacesContext();
      
      if ( event.getPhaseId() == RESTORE_VIEW )
      {
         afterRestoreView(facesContext);
      }      
      else if ( event.getPhaseId() == INVOKE_APPLICATION )
      {
         afterInvokeApplication();
      }
      else if ( event.getPhaseId() == PROCESS_VALIDATIONS )
      {
         afterProcessValidations( event.getFacesContext() );
      }
            
      //has to happen after, since restoreAnyConversationContext() 
      //can add messages
      FacesMessages.afterPhase();
      
      handleTransactionsAfterPhase(event);
            
      if ( event.getPhaseId() == RENDER_RESPONSE )
      {
         afterRender(facesContext);
      }
      else if ( facesContext.getResponseComplete() )
      {
         afterResponseComplete(facesContext);
      }
   }
   
   private void afterPortletPhase(PhaseEvent event)
   {
      FacesContext facesContext = event.getFacesContext();
      
      if ( event.getPhaseId() == RESTORE_VIEW )
      {
         afterRestoreView(facesContext);
      }
      else if ( event.getPhaseId() == INVOKE_APPLICATION )
      {
         afterInvokeApplication();
      }
      else if ( event.getPhaseId() == PROCESS_VALIDATIONS )
      {
         afterProcessValidations( event.getFacesContext() );
      }
      
      FacesMessages.afterPhase();
      
      //delegate to subclass:
      handleTransactionsAfterPhase(event);
            
      if ( event.getPhaseId() == RENDER_RESPONSE )
      {
         //writeConversationIdToResponse( facesContext.getExternalContext().getResponse() );
         afterRender(facesContext);
      }
      else if ( event.getPhaseId() == INVOKE_APPLICATION || facesContext.getRenderResponse() || facesContext.getResponseComplete() )
      {
         Manager manager = Manager.instance();
         manager.beforeRedirect();
         if ( manager.isLongRunningConversation() )
         {
            setPortletRenderParameter(
                  facesContext.getExternalContext().getResponse(), 
                  manager.getConversationIdParameter(), 
                  manager.getCurrentConversationId()
               );
         }
         afterResponseComplete(facesContext);
      }
      
   }
   
   private static void setPortletRenderParameter(Object response, String conversationIdParameter, String conversationId)
   {
      if ( ACTION_RESPONSE.isInstance(response) )
      {
         Reflections.invokeAndWrap(SET_RENDER_PARAMETER, response, conversationIdParameter, conversationId);
      }
   }
   
   private static boolean isPortletRequest(PhaseEvent event)
   {
      return PORTLET_REQUEST!=null && 
            PORTLET_REQUEST.isInstance( event.getFacesContext().getExternalContext().getRequest() );
   }
   
   public void handleTransactionsBeforePhase(PhaseEvent event)
   {
      if ( Init.instance().isTransactionManagementEnabled() ) 
      {
         PhaseId phaseId = event.getPhaseId();
         boolean beginTran = phaseId == PhaseId.RENDER_RESPONSE || 
               phaseId == ( Transaction.instance().isConversationContextRequired() ? PhaseId.APPLY_REQUEST_VALUES : PhaseId.RESTORE_VIEW );
               //( phaseId == PhaseId.RENDER_RESPONSE && !Init.instance().isClientSideConversations() );
         
         if (beginTran) 
         {
            begin(phaseId);
         }
      }
   }
   
   public void handleTransactionsAfterPhase(PhaseEvent event)
   {
      if ( Init.instance().isTransactionManagementEnabled() ) 
      {
         PhaseId phaseId = event.getPhaseId();
         boolean commitTran = phaseId == PhaseId.INVOKE_APPLICATION || 
               event.getFacesContext().getRenderResponse() || //TODO: no need to commit the tx if we failed to restore the view
               event.getFacesContext().getResponseComplete() ||
               phaseId == PhaseId.RENDER_RESPONSE;
               //( phaseId == PhaseId.RENDER_RESPONSE && !Init.instance().isClientSideConversations() );
         
         if (commitTran)
         { 
            commitOrRollback(phaseId); //we commit before destroying contexts, cos the contexts have the PC in them
         }
      }
   }
   
   protected void handleTransactionsAfterPageActions(PhaseEvent event)
   {
      if ( Init.instance().isTransactionManagementEnabled() ) 
      {
         commitOrRollback(PhaseId.INVOKE_APPLICATION);
         if ( !event.getFacesContext().getResponseComplete() )
         {
            begin(PhaseId.INVOKE_APPLICATION);
         }
      }
   }
   
   protected void afterInvokeApplication() 
   {
      if ( Init.instance().isTransactionManagementEnabled() ) 
      {
         addTransactionFailedMessage();
      }
   }

   protected void afterProcessValidations(FacesContext facesContext)
   {
      Validation.instance().afterProcessValidations(facesContext);
   }
   
   /**
    * Set up the Seam contexts, except for the conversation
    * context
    */
   protected void beforeRestoreView(FacesContext facesContext)
   {
      FacesLifecycle.beginRequest( facesContext.getExternalContext() );
   }
   
   /**
    * Restore the page and conversation contexts during a JSF request
    */
   protected void afterRestoreView(FacesContext facesContext)
   {
      FacesLifecycle.resumePage();
      Map parameters = facesContext.getExternalContext().getRequestParameterMap();
      ConversationPropagation.instance().restoreConversationId(parameters);
      boolean conversationFound = Manager.instance().restoreConversation();
      FacesLifecycle.resumeConversation( facesContext.getExternalContext() );
      if (!conversationFound)
      {
         Pages.instance().redirectToNoConversationView();
      }
      Manager.instance().handleConversationPropagation(parameters);
      if ( Init.instance().isJbpmInstalled() )
      {
         Pageflow.instance().validatePageflow();
      }
      
      if ( log.isDebugEnabled() )
      {
         log.debug( "After restoring conversation context: " + Contexts.getConversationContext() );
      }
      
      Pages.instance().postRestore(facesContext);
            
   }
  
   public void raiseEventsBeforePhase(PhaseEvent event)
   {
      if ( Contexts.isApplicationContextActive() )
      {
         Events.instance().raiseEvent("org.jboss.seam.beforePhase", event);
      }
      
      /*if ( Contexts.isConversationContextActive() && Init.instance().isJbpmInstalled() && Pageflow.instance().isInProcess() )
      {
         String name;
         PhaseId phaseId = event.getPhaseId();
         if ( phaseId == PhaseId.PROCESS_VALIDATIONS )
         {
            name = "process-validations";
         }
         else if ( phaseId == PhaseId.UPDATE_MODEL_VALUES )
         {
            name = "update-model-values";
         }
         else if ( phaseId == PhaseId.INVOKE_APPLICATION )
         {
            name = "invoke-application";
         }
         else if ( phaseId == PhaseId.RENDER_RESPONSE )
         {
            name = "render-response";
         }
         else
         {
            return;
         }
         Pageflow.instance().processEvents(name);
      }*/
   }
   
   public void raiseEventsAfterPhase(PhaseEvent event)
   {
      if ( Contexts.isApplicationContextActive() )
      {
         Events.instance().raiseEvent("org.jboss.seam.afterPhase", event);
      }
   }
   
   /**
    * Add a faces message when Seam-managed transactions fail.
    */
   protected void addTransactionFailedMessage()
   {
      try
      {
         if ( Transaction.instance().isRolledBackOrMarkedRollback() )
         {
            FacesMessages.instance().addFromResourceBundleOrDefault(
                     FacesMessage.SEVERITY_WARN, 
                     "org.jboss.seam.TransactionFailed", 
                     "Transaction failed"
                  );
         }
      }
      catch (Exception e) {} //swallow silently, not important
   }
   
   protected void beforeRender(PhaseEvent event)
   {  
      
      FacesContext facesContext = event.getFacesContext();
      
      if ( Contexts.isPageContextActive() )
      {
         Context pageContext = Contexts.getPageContext();
         //after every time that the view may have changed,
         //we need to flush the page context, since the 
         //attribute map is being discarder
         pageContext.flush();
         //force refresh of the conversation lists (they are kept in PAGE context)
         pageContext.remove( Seam.getComponentName(ConversationList.class) );
         pageContext.remove( Seam.getComponentName(Switcher.class) );
         pageContext.remove( Seam.getComponentName(ConversationStack.class) );
      }
      
      preRenderPage(event);
      
      if ( facesContext.getResponseComplete() )
      {
         //workaround for a bug in MyFaces prior to 1.1.3
         if ( Init.instance().isMyFacesLifecycleBug() ) 
         {
            FacesLifecycle.endRequest( facesContext.getExternalContext() );
         }
      }
      else //if the page actions did not call responseComplete()
      {
         FacesMessages.instance().beforeRenderResponse();
         //do this both before and after render, since conversations 
         //and pageflows can begin during render
         FacesManager.instance().prepareBackswitch(facesContext); 
      }
      
      FacesPage.instance().storeConversation();
      FacesPage.instance().storePageflow();
      
      PersistenceContexts persistenceContexts = PersistenceContexts.instance();
      if (persistenceContexts != null) 
      {
          persistenceContexts.beforeRender();
      }
   }
   
   protected void afterRender(FacesContext facesContext)
   {
      //do this both before and after render, since conversations 
      //and pageflows can begin during render
      FacesManager.instance().prepareBackswitch(facesContext);
      
      PersistenceContexts persistenceContexts = PersistenceContexts.instance();
      if (persistenceContexts != null) 
      {
          persistenceContexts.afterRender();
      }
      
      ExternalContext externalContext = facesContext.getExternalContext();
      Manager.instance().endRequest( externalContext.getSessionMap() );
      FacesLifecycle.endRequest(externalContext);
   }
   
   protected void afterResponseComplete(FacesContext facesContext)
   {
      //responseComplete() was called by one of the other phases, 
      //so we will never get to the RENDER_RESPONSE phase
      //Note: we can't call Manager.instance().beforeRedirect() here, 
      //since a redirect is not the only reason for a responseComplete
      ExternalContext externalContext = facesContext.getExternalContext();
      Manager.instance().endRequest( externalContext.getSessionMap() );
      FacesLifecycle.endRequest( facesContext.getExternalContext() );
   }
   
   private boolean preRenderPage(PhaseEvent event)
   {
      if ( Pages.isDebugPage() )
      {
         return false;
      }
      else
      {
         FacesLifecycle.setPhaseId(PhaseId.INVOKE_APPLICATION);
         boolean actionsWereCalled = false;
         try
         {
            actionsWereCalled = Pages.instance().preRender( event.getFacesContext() );
            return actionsWereCalled;
         }
         finally
         {
            FacesLifecycle.setPhaseId(PhaseId.RENDER_RESPONSE);
            if (actionsWereCalled) 
            {
               FacesMessages.afterPhase();
               handleTransactionsAfterPageActions(event); //TODO: does it really belong in the finally?
            }
         }
      }
   }
      
   void begin(PhaseId phaseId) 
   {
      try 
      {
         if ( !Transaction.instance().isActiveOrMarkedRollback() )
         {
            log.debug("beginning transaction prior to phase: " + phaseId);
            Transaction.instance().begin();
         }
      }
      catch (Exception e)
      {
         throw new IllegalStateException("Could not start transaction", e);
      }
   }
   
   void commitOrRollback(PhaseId phaseId) 
   {
      try 
      {
         if ( Transaction.instance().isActive() )
         {
            log.debug("committing transaction after phase: " + phaseId);
            Transaction.instance().commit();
         }
         else if ( Transaction.instance().isRolledBackOrMarkedRollback() )
         {
            log.debug("rolling back transaction after phase: " + phaseId);
            Transaction.instance().rollback();
         }
      }
      catch (Exception e)
      {
         throw new IllegalStateException("Could not commit transaction", e);
      }
   }
   
}
