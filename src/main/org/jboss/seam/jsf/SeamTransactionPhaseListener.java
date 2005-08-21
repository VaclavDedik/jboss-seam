//$Id$
package org.jboss.seam.jsf;

import javax.faces.event.PhaseEvent;
import javax.faces.event.PhaseId;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.persistence.EntityManager;
import javax.transaction.Status;
import javax.transaction.SystemException;
import javax.transaction.UserTransaction;

public class SeamTransactionPhaseListener extends SeamPhaseListener
{

   @Override
   public void beforePhase(PhaseEvent event)
   {
      if ( event.getPhaseId()==PhaseId.UPDATE_MODEL_VALUES )
      {
         try 
         {
            getUserTransaction().begin();
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
            if ( isTransactionActive() )
            {
               getUserTransaction().commit();
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
            if ( isTransactionActive() )
            {
               getEntityManager().flush();
            }
         }
         catch (Exception e)
         {
            //TODO: what should we *really* do here??
            throw new IllegalStateException("Could not flush to database", e);
         }
      }
   }

   static boolean isTransactionActive() throws SystemException, NamingException
   {
      return getUserTransaction().getStatus()==Status.STATUS_ACTIVE;
   }

   static boolean isTransactionActiveOrMarkedRollback() throws SystemException, NamingException
   {
      int status = getUserTransaction().getStatus();
      return status==Status.STATUS_ACTIVE || status == Status.STATUS_MARKED_ROLLBACK;
   }

   private static EntityManager getEntityManager() throws NamingException
   {
      //TODO: allow configuration of the JNDI name!
      return (EntityManager) new InitialContext().lookup("java:/EntityManagers/data");
   }

   static UserTransaction getUserTransaction() throws NamingException
   {
      return (UserTransaction) new InitialContext().lookup("java:comp/UserTransaction");
   }

}
