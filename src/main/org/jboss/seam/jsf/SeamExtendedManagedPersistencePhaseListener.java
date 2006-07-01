//$Id$
package org.jboss.seam.jsf;

import javax.faces.event.PhaseEvent;
import javax.faces.event.PhaseId;

import org.jboss.seam.core.Init;

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
   
   @Override
   public void handleTransactionsBeforePhase(PhaseEvent event)
   {
      PhaseId phaseId = event.getPhaseId();
      boolean beginTran = phaseId==PhaseId.RESTORE_VIEW || 
            ( phaseId==PhaseId.RENDER_RESPONSE && !Init.instance().isClientSideConversations() );
      
      if ( beginTran ) 
      {
         begin(phaseId);
      }
   }

   @Override
   public void handleTransactionsAfterPhase(PhaseEvent event)
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
   }

   @Override
   protected void handleTransactionsAfterPageActions(PhaseEvent event)
   {
      commitOrRollback(PhaseId.INVOKE_APPLICATION);
      if ( !event.getFacesContext().getResponseComplete() )
      {
         begin(PhaseId.INVOKE_APPLICATION);
      }
   }

}
