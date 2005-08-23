//$Id$
package org.jboss.seam.jsf;

import javax.faces.event.PhaseEvent;
import javax.faces.event.PhaseId;

import org.jboss.seam.util.Transactions;

public class SeamExtendedManagedPersistencePhaseListener extends SeamPhaseListener
{
   
   @Override
   public void beforePhase(PhaseEvent event)
   {
      if ( event.getPhaseId()==PhaseId.UPDATE_MODEL_VALUES || event.getPhaseId()==PhaseId.RENDER_RESPONSE )
      {
         try 
         {
            Transactions.getUserTransaction().begin();
         }
         catch (Exception e)
         {
            //TODO: what should we *really* do here??
            throw new IllegalStateException("Could not start transaction", e);
         }
      }
      super.beforePhase( event );
   }

   @Override
   public void afterPhase(PhaseEvent event)
   {
      super.afterPhase( event );
      if ( event.getPhaseId()==PhaseId.RENDER_RESPONSE || event.getPhaseId()==PhaseId.INVOKE_APPLICATION )
      {
         try 
         {
            if ( Transactions.isTransactionActive() )
            {
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

}
