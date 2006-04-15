/*
 * JBoss, Home of Professional Open Source
 *
 * Distributable under LGPL license.
 * See terms of license at gnu.org.
 */
package org.jboss.seam.jsf;

import javax.faces.event.PhaseEvent;
import javax.faces.event.PhaseId;
import javax.naming.NamingException;
import javax.persistence.EntityManager;
import javax.transaction.SystemException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jboss.seam.Seam;
import org.jboss.seam.contexts.Contexts;
import org.jboss.seam.core.Init;
import org.jboss.seam.core.ManagedHibernateSession;
import org.jboss.seam.core.ManagedJbpmContext;
import org.jboss.seam.core.ManagedPersistenceContext;
import org.jboss.seam.util.Naming;
import org.jboss.seam.util.Transactions;

/**
 * Transaction management for transaction-scoped persistence contexts.
 * A single transaction spans the entire JSF request.
 * 
 * @see SeamExtendedManagedPersistencePhaseListener
 * @author Gavin King
 */
public class SeamTransactionManagedPersistencePhaseListener extends SeamPhaseListener
{
   private static final Log log = LogFactory.getLog( SeamTransactionManagedPersistencePhaseListener.class );

   @Override
   public void beforePhase(PhaseEvent event)
   {
      if ( event.getPhaseId()==PhaseId.RESTORE_VIEW )
      {
         begin();
      }
      super.beforePhase( event );
   }

   @Override
   public void afterPhase(PhaseEvent event)
   {

      boolean commit = event.getPhaseId()==PhaseId.RENDER_RESPONSE ||
            event.getFacesContext().getResponseComplete();
      boolean flush = event.getPhaseId()==PhaseId.INVOKE_APPLICATION || 
            event.getFacesContext().getRenderResponse();
      if ( commit )
      {
         commit();
      }
      else if ( flush )
      {
         flush();
      }

      super.afterPhase( event );
      
   }

   private void begin() {
      try
      {
         if ( !Transactions.isTransactionActiveOrMarkedRollback() )
         {
            log.debug( "Starting transaction prior to RESTORE_VIEW phase" );
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
         if ( Transactions.isTransactionActive() )
         {
            log.debug( "Committing transaction after RENDER_RESPONSE phase or responseComplete()" );
            Transactions.getUserTransaction().commit();
         }
      }
      catch (Exception e)
      {
         //TODO: what should we *really* do here??
         throw new IllegalStateException("Could not commit transaction", e);
      }
   }

   @Override
   protected void afterPageActions()
   {
      flush();
   }

   private void flush() {
      try
      {
         log.debug( "Flushing persistence contexts after INVOKE_APPLICATION phase" );
         if ( Transactions.isTransactionActive() )
         {
            Init init = Init.instance();
            for (String unitName : init.getManagedPersistenceContexts())
            {
               flushEntityManager(unitName);
            }
            for (String sfName : init.getManagedSessions())
            {
               flushSession(sfName);
            }
            if ( init.isJbpmInstalled() )
            {
               flushJbpm();
            }
         }
      }
      catch (Exception e)
      {
         //TODO: what should we *really* do here??
         throw new IllegalStateException("Could not flush to database", e);
      }
   }

   private void flushEntityManager(String unitName) throws NamingException, SystemException
   {
      log.trace( "flushing EntityManager [" + unitName + "]" );
      ManagedPersistenceContext managedContext = (ManagedPersistenceContext) Contexts.getConversationContext().get(unitName);
      if ( managedContext!=null ) managedContext.getEntityManager().flush();
      EntityManager em = (EntityManager) Naming.getInitialContext().lookup("java:/EntityManagers/" + unitName);
      if ( em!=null ) em.flush();
   }

   private void flushSession(String sfName)
   {
      log.trace( "flushing Hibernate session [" + sfName + "]" );
      ManagedHibernateSession managedSession = (ManagedHibernateSession) Contexts.getConversationContext().get(sfName);
      if ( managedSession!=null ) managedSession.getSession().flush();
   }

   private void flushJbpm() throws Exception
   {
      log.trace( "flushing jBPM session" );
      ManagedJbpmContext managed = (ManagedJbpmContext) Contexts.getEventContext()
            .get( Seam.getComponentName(ManagedJbpmContext.class) );
      if ( managed != null )
      {
         // need to make sure that the seam BusinessProcessContext gets flushed to
         // the jBPM ContextInstance prior to flushing the jBPM session...
         Contexts.getBusinessProcessContext().flush();
         managed.getJbpmContext().getSession().flush();
      }
   }

}
