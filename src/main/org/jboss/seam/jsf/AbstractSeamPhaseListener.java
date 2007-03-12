package org.jboss.seam.jsf;

import static javax.faces.event.PhaseId.ANY_PHASE;

import java.util.Map;

import javax.faces.application.FacesMessage;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.faces.event.PhaseEvent;
import javax.faces.event.PhaseId;
import javax.faces.event.PhaseListener;
import javax.faces.model.DataModel;

import org.jboss.seam.Seam;
import org.jboss.seam.TransactionException;
import org.jboss.seam.contexts.Context;
import org.jboss.seam.contexts.ContextAdaptor;
import org.jboss.seam.contexts.Contexts;
import org.jboss.seam.contexts.Lifecycle;
import org.jboss.seam.core.ConversationList;
import org.jboss.seam.core.ConversationStack;
import org.jboss.seam.core.Events;
import org.jboss.seam.core.FacesMessages;
import org.jboss.seam.core.FacesPage;
import org.jboss.seam.core.Init;
import org.jboss.seam.core.Manager;
import org.jboss.seam.core.Pageflow;
import org.jboss.seam.core.Pages;
import org.jboss.seam.core.PersistenceContexts;
import org.jboss.seam.core.Switcher;
import org.jboss.seam.core.Validation;
import org.jboss.seam.log.LogProvider;
import org.jboss.seam.log.Logging;
import org.jboss.seam.util.Transactions;

public abstract class AbstractSeamPhaseListener implements PhaseListener
{
   
   private static final LogProvider log = Logging.getLogProvider(AbstractSeamPhaseListener.class);
   
   public PhaseId getPhaseId()
   {
      return ANY_PHASE;
   }
   /**
    * Restore the page and conversation contexts during a JSF request
    */
   public void afterRestoreView(FacesContext facesContext)
   {
      Lifecycle.resumePage();
      Map parameters = facesContext.getExternalContext().getRequestParameterMap();
      boolean conversationFound = Manager.instance().restoreConversation(parameters);
      Lifecycle.resumeConversation( facesContext.getExternalContext() );
      if (!conversationFound)
      {
         Pages.instance().redirectToNoConversationView();
      }
      if ( Init.instance().isJbpmInstalled() )
      {
         Pageflow.instance().validatePageflow();
      }
      Manager.instance().handleConversationPropagation(parameters);
      
      if ( log.isDebugEnabled() )
      {
         log.debug( "After restoring conversation context: " + Contexts.getConversationContext() );
      }
   }
  
   /**
    * Look for a DataModel row selection in the request parameters,
    * and apply it to the DataModel.
    * 
    * @param parameters the request parameters
    */
   private void selectDataModelRow(Map parameters)
   {
      String dataModelSelection = (String) parameters.get("dataModelSelection");
      if (dataModelSelection!=null)
      {
         int colonLoc = dataModelSelection.indexOf(':');
         int bracketLoc = dataModelSelection.indexOf('[');
         if (colonLoc>0 && bracketLoc>colonLoc)
         {
            String var = dataModelSelection.substring(0, colonLoc);
            String name = dataModelSelection.substring(colonLoc+1, bracketLoc);
            int index = Integer.parseInt( dataModelSelection.substring( bracketLoc+1, dataModelSelection.length()-1 ) );
            Object value = Contexts.lookupInStatefulContexts(name);
            if (value!=null)
            {
               DataModel dataModel = (DataModel) value;
               if ( index<dataModel.getRowCount() )
               {
                  dataModel.setRowIndex(index);
                  Contexts.getEventContext().set( var, dataModel.getRowData() );
               }
               else
               {
                  log.warn("DataModel row was unavailable");
                  Contexts.getEventContext().remove(var);
               }
            }
         }
      }
   }
   
   public void beforePhase(PhaseEvent event)
   {
      if ( Contexts.isApplicationContextActive() )
      {
         Events.instance().raiseEvent("org.jboss.seam.beforePhase", event);
      }
      
      if ( Contexts.isConversationContextActive() && Init.instance().isJbpmInstalled() && Pageflow.instance().isInProcess() )
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
      }
   }
   
   public void afterPhase(PhaseEvent event)
   {
      if ( Contexts.isApplicationContextActive() )
      {
         Events.instance().raiseEvent("org.jboss.seam.afterPhase", event);
      }
   }
   
   protected void beforeUpdateModelValues(PhaseEvent event)
   {
      Pages.instance().applyViewRootValues( event.getFacesContext() );
      Manager.instance().setUpdateModelValuesCalled(true);
   }
   /**
    * Give the subclasses an opportunity to do stuff
    */
   protected void afterInvokeApplication() {}
   
   /**
    * Add a faces message when Seam-managed transactions fail.
    */
   protected void addTransactionFailedMessage()
   {
      try
      {
         if ( Transactions.isTransactionMarkedRollback() )
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
      if ( !Manager.instance().isUpdateModelValuesCalled() )
      {
         Pages.instance().applyRequestParameterValues(facesContext);
      }
      
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
      selectDataModelRow( facesContext.getExternalContext().getRequestParameterMap() );
      
      enterPage(event);
      
      if ( facesContext.getResponseComplete() )
      {
         //workaround for a bug in MyFaces prior to 1.1.3
         if ( Init.instance().isMyFacesLifecycleBug() ) 
         {
            Lifecycle.endRequest( facesContext.getExternalContext() );
         }
      }
      else //if the page actions did not call responseComplete()
      {
         FacesMessages.instance().beforeRenderResponse();
         //do this both before and after render, since conversations 
         //and pageflows can begin during render
         Manager.instance().prepareBackswitch(facesContext); 
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
      Manager.instance().prepareBackswitch(facesContext);
      
      PersistenceContexts persistenceContexts = PersistenceContexts.instance();
      if (persistenceContexts != null) 
      {
          persistenceContexts.afterRender();
      }
      
      ExternalContext externalContext = facesContext.getExternalContext();
      Manager.instance().endRequest( ContextAdaptor.getSession(externalContext) );
      Lifecycle.endRequest(externalContext);
   }
   
   protected void afterResponseComplete(FacesContext facesContext)
   {
      //responseComplete() was called by one of the other phases, 
      //so we will never get to the RENDER_RESPONSE phase
      //Note: we can't call Manager.instance().beforeRedirect() here, 
      //since a redirect is not the only reason for a responseComplete
      ExternalContext externalContext = facesContext.getExternalContext();
      Manager.instance().endRequest( ContextAdaptor.getSession(externalContext) );
      Lifecycle.endRequest( facesContext.getExternalContext() );
   }
   
   private boolean enterPage(PhaseEvent event)
   {
      Lifecycle.setPhaseId( PhaseId.INVOKE_APPLICATION );
      boolean actionsWereCalled = false;
      try
      {
         actionsWereCalled = Pages.instance().enterPage( event.getFacesContext() );
         return actionsWereCalled;
      }
      finally
      {
         Lifecycle.setPhaseId( PhaseId.RENDER_RESPONSE );
         if (actionsWereCalled) 
         {
            FacesMessages.afterPhase();
            handleTransactionsAfterPageActions(event); //TODO: does it really belong in the finally?
         }
      }
   }
   
   protected void handleTransactionsAfterPageActions(PhaseEvent event) {}
   
   private static boolean exists = false;
   
   protected AbstractSeamPhaseListener()
   {
      if (exists) log.warn("There should only be one Seam phase listener per application");
      exists=true;
   }
   /////////Do not really belong here:
   
   void begin(PhaseId phaseId) 
   {
      try 
      {
         if ( !Transactions.isTransactionActiveOrMarkedRollback() )
         {
            log.debug("beginning transaction prior to phase: " + phaseId);
            Transactions.getUserTransaction().begin();
         }
      }
      catch (Exception e)
      {
         throw new TransactionException("Could not start transaction", e);
      }
   }
   
   void commitOrRollback(PhaseId phaseId) 
   {
      try 
      {
         if ( Transactions.isTransactionActive() )
         {
            log.debug("committing transaction after phase: " + phaseId);
            Transactions.getUserTransaction().commit();
         }
         else if ( Transactions.isTransactionMarkedRollback() )
         {
            log.debug("rolling back transaction after phase: " + phaseId);
            Transactions.getUserTransaction().rollback();
         }
      }
      catch (Exception e)
      {
         throw new TransactionException("Could not commit transaction", e);
      }
   }
   
   protected void afterProcessValidations(FacesContext facesContext)
   {
      Validation.instance().afterProcessValidations(facesContext);
   }
}
