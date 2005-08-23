//$Id$
package org.jboss.seam.jsf;

import javax.faces.event.PhaseEvent;
import javax.faces.event.PhaseId;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.persistence.EntityManager;

import org.jboss.seam.components.ManagedHibernateSession;
import org.jboss.seam.components.ManagedPersistenceContext;
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
                  flushEntityManager(unitName);
               }
               for (String sfName : settings.getSessionFactoryNames())
               {
                  flushSession(sfName);
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

   private void flushEntityManager(String unitName) throws NamingException
   {
      ManagedPersistenceContext managedContext = (ManagedPersistenceContext) Contexts.getConversationContext().get(unitName);
      if ( managedContext!=null ) managedContext.getEntityManager().flush();
      EntityManager em = (EntityManager) new InitialContext().lookup("java:/EntityManagers/" + unitName);
      if ( em!=null ) em.flush();
   }

   private void flushSession(String sfName)
   {
      ManagedHibernateSession managedSession = (ManagedHibernateSession) Contexts.getConversationContext().get(sfName);
      if ( managedSession!=null ) managedSession.getSession().flush();
   }

}
