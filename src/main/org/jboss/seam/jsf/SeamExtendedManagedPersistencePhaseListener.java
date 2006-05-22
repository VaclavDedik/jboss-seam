//$Id$
package org.jboss.seam.jsf;

import javax.faces.event.PhaseEvent;
import javax.faces.event.PhaseId;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jboss.seam.core.Init;
import org.jboss.seam.util.Transactions;

/**
 * Transaction management for extended persistence contexts.
 * A transaction spans the restore view, apply request values, process validations,
 * update model values and invoke application phases. It is committed when
 * invoke application is complete, or renderResponse() or responseComplete() is
 * called. A second transaction spans the render response phase.
 * 
 * @author Gavin King
 */
public class SeamExtendedManagedPersistencePhaseListener extends SeamPhaseListener
{
   private static final Log log = LogFactory.getLog( SeamExtendedManagedPersistencePhaseListener.class );
   
   @Override
   public void beforePhase(PhaseEvent event)
   {
      PhaseId phaseId = event.getPhaseId();
      //boolean beginTran = phaseId==PhaseId.UPDATE_MODEL_VALUES || 
      boolean beginTran = phaseId==PhaseId.RESTORE_VIEW || 
            ( phaseId==PhaseId.RENDER_RESPONSE && !Init.instance().isClientSideConversations() );
      
      if ( beginTran ) 
      {
         begin(phaseId);
      }
      
      super.beforePhase( event );
   }

   @Override
   public void afterPhase(PhaseEvent event)
   {
      PhaseId phaseId = event.getPhaseId();
      boolean commitTran = phaseId==PhaseId.INVOKE_APPLICATION || 
            event.getFacesContext().getRenderResponse() ||
            event.getFacesContext().getResponseComplete() ||
            ( phaseId==PhaseId.RENDER_RESPONSE && !Init.instance().isClientSideConversations() );
      
      if (commitTran)
      { 
         commitOrRollback(phaseId); //we commit before destroying contexts, cos the contexts have the PC in them
      }

      super.afterPhase( event );      
   }

   @Override
   protected void afterPageActions()
   {
      commitOrRollback(PhaseId.INVOKE_APPLICATION);
      begin(PhaseId.INVOKE_APPLICATION);
   }

   private void begin(PhaseId phaseId) {
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
         //TODO: what should we *really* do here??
         throw new IllegalStateException("Could not start transaction", e);
      }
   }

   private void commitOrRollback(PhaseId phaseId) {
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
         //TODO: what should we *really* do here??
         throw new IllegalStateException("Could not commit transaction", e);
      }
   }

}
