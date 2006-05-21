//$Id$
package org.jboss.seam.jsf;

import javax.faces.event.PhaseEvent;
import javax.faces.event.PhaseId;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
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
public class SeamExtendedManagedPersistencePortletPhaseListener extends SeamPortletPhaseListener
{
   private static final Log log = LogFactory.getLog( SeamExtendedManagedPersistencePortletPhaseListener.class );
   
   @Override
   public void beforePhase(PhaseEvent event)
   {
      boolean beginTran = event.getPhaseId()==PhaseId.RESTORE_VIEW || 
            event.getPhaseId()==PhaseId.RENDER_RESPONSE || 
            event.getPhaseId()==PhaseId.INVOKE_APPLICATION;
      
      if ( beginTran ) 
      {
         begin();
      }
      
      super.beforePhase( event );
   }

   @Override
   public void afterPhase(PhaseEvent event)
   {
      boolean commitTran = event.getPhaseId()==PhaseId.INVOKE_APPLICATION || 
            event.getFacesContext().getRenderResponse() ||
            event.getPhaseId()==PhaseId.RENDER_RESPONSE;
      
      if (commitTran)
      { 
         commit(); //we commit before destroying contexts, cos the contexts have the PC in them
      }

      super.afterPhase( event );      
   }

   @Override
   protected void afterPageActions()
   {
      commit();
      begin();
   }

   private void begin() {
      try 
      {
         if ( !Transactions.isTransactionActiveOrMarkedRollback() )
         {
            log.debug("beginning transaction");
            Transactions.getUserTransaction().begin();
         }
      }
      catch (Exception e)
      {
         //TODO: what should we *really* do here??
         throw new IllegalStateException("Could not start transaction", e);
      }
   }

   private void commit() {
      try 
      {
         if ( Transactions.isTransactionActiveOrMarkedRollback() )
         {
            log.debug("committing transaction");
            Transactions.getUserTransaction().commit();
         }
      }
      catch (Exception e)
      {
         //TODO: what should we *really* do here??
         throw new IllegalStateException("Could not commit transaction", e);
      }
   }

}
