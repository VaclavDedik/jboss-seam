//$Id$
package org.jboss.seam.jsf;

import javax.faces.event.PhaseEvent;
import javax.faces.event.PhaseId;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.persistence.EntityManager;

import org.jboss.seam.components.Settings;
import org.jboss.seam.contexts.Contexts;
import org.jboss.seam.util.Transactions;

public class SeamTransactionManagedPersistencePhaseListener extends SeamPhaseListener
{

   @Override
   public void beforePhase(PhaseEvent event)
   {
      if ( event.getPhaseId()==PhaseId.UPDATE_MODEL_VALUES )
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
      if ( event.getPhaseId()==PhaseId.RENDER_RESPONSE )
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
      else if ( event.getPhaseId()==PhaseId.INVOKE_APPLICATION )
      {
         try 
         {
            if ( Transactions.isTransactionActive() )
            {
               Settings settings = Contexts.getApplicationContext().get(Settings.class);
               for (String unitName : settings.getPersistenceUnitNames())
               {
                  getEntityManager(unitName).flush();
               }
            }
         }
         catch (Exception e)
         {
            //TODO: what should we *really* do here??
            throw new IllegalStateException("Could not flush to database", e);
         }
      }
   }

   private static EntityManager getEntityManager(String unitName) throws NamingException
   {
      //TODO: allow configuration of the JNDI name!
      return (EntityManager) new InitialContext().lookup("java:/EntityManagers/" + unitName);
   }

}
