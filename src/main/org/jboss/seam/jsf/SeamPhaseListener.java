/*
 * JBoss, Home of Professional Open Source
 *
 * Distributable under LGPL license.
 * See terms of license at gnu.org.
 */
package org.jboss.seam.jsf;

import static javax.faces.event.PhaseId.INVOKE_APPLICATION;
import static javax.faces.event.PhaseId.RENDER_RESPONSE;
import static javax.faces.event.PhaseId.RESTORE_VIEW;

import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.faces.event.PhaseEvent;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jboss.seam.contexts.Lifecycle;
import org.jboss.seam.core.FacesMessages;
import org.jboss.seam.core.Init;
import org.jboss.seam.core.Manager;
import org.jboss.seam.util.Transactions;

/**
 * Manages the Seam contexts associated with a JSF request.
 * 
 * Manages the thread/context associations throughoutt the
 * lifecycle of the JSF request.
 *
 * @author Gavin King
 */
public class SeamPhaseListener extends AbstractSeamPhaseListener
{

   private static final Log log = LogFactory.getLog( SeamPhaseListener.class );
   
   public void beforePhase(PhaseEvent event)
   {
      log.trace( "before phase: " + event.getPhaseId() );
      
      Lifecycle.setPhaseId( event.getPhaseId() );

      //delegate to subclass:
      handleTransactionsBeforePhase(event);
      
      if ( event.getPhaseId() == RESTORE_VIEW )
      {
         Lifecycle.beginRequest( event.getFacesContext().getExternalContext() );
      }
      else if ( event.getPhaseId() == RENDER_RESPONSE )
      {
         beforeRender(event);
      }

   }
   
   public void afterPhase(PhaseEvent event)
   {
      log.trace( "after phase: " + event.getPhaseId() );

      FacesContext facesContext = event.getFacesContext();
      
      if ( event.getPhaseId() == RESTORE_VIEW )
      {
         restoreAnyConversationContext(facesContext);
         if ( !event.getFacesContext().getRenderResponse() )
         {
            Manager.instance().setNonFacesRequest(false);
         }
      }      
      else if ( event.getPhaseId() == INVOKE_APPLICATION )
      {
         try
         {
            if ( Transactions.isTransactionAvailableAndMarkedRollback() )
            {
               FacesMessages.instance().addFromResourceBundle(
                        FacesMessage.SEVERITY_WARN, 
                        "org.jboss.seam.TransactionFailed", 
                        "Transaction failed"
                     );
            }
         }
         catch (Exception e) {} //swallow silently, not important
      }
            
      //has to happen after, since restoreAnyConversationContext() 
      //can add messages
      FacesMessages.afterPhase();
      
      //delegate to subclass:
      handleTransactionsAfterPhase(event);
            
      if ( event.getPhaseId() == RENDER_RESPONSE )
      {
         if ( !Manager.instance().isConversationAlreadyStored() ) 
         {
            //for JSF pages with no form, storeAnyConversationContext()
            //will not get called from SeamStateManager.saveSerializedView()
            storeAnyConversationContext(facesContext);
         }
         
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
      else if ( facesContext.getResponseComplete() )
      {
         //responseComplete() was called by one of the other phases, 
         //so we will never get to the RENDER_RESPONSE phase
         //Note: we can't call Manager.instance().beforeRedirect() here, 
         //since a redirect is not the only reason for a responseComplete
         storeAnyConversationContext(facesContext);
         Lifecycle.endRequest( facesContext.getExternalContext() );
      }
      
      Lifecycle.setPhaseId(null);
      
   }
   
   protected void handleTransactionsAfterPhase(PhaseEvent event) {}
   protected void handleTransactionsBeforePhase(PhaseEvent event) {}

}
