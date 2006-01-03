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

import org.jboss.logging.Logger;
import org.jboss.seam.Seam;
import org.jboss.seam.contexts.Contexts;
import org.jboss.seam.core.Init;
import org.jboss.seam.core.ManagedHibernateSession;
import org.jboss.seam.core.ManagedJbpmContext;
import org.jboss.seam.core.ManagedPersistenceContext;
import org.jboss.seam.util.NamingHelper;
import org.jboss.seam.util.Transactions;

/**
 * Adds extra semantics relating to transactions and various "persistence contexts"
 * during phase processing.  Specifically:<ol>
 * <li>Prior to the {@link PhaseId#UPDATE_MODEL_VALUES} phase, a JTA transaction is
 * begun.  The transaction is committed after the {@link PhaseId#RENDER_RESPONSE}
 * phase.
 * <li>After the {@link PhaseId#INVOKE_APPLICATION} pahse, managed persistence
 * contexts are flushed.  This includes EJB3 {@link EntityManager)s,
 * Hibernate {@link org.hibernate.Session}s, and the managed jBPM
 * {@link org.jbpm.JbpmContext} (if used).
 * </ol>
 */
public class SeamTransactionManagedPersistencePhaseListener extends SeamPhaseListener
{
   private static final Logger log = Logger.getLogger( SeamTransactionManagedPersistencePhaseListener.class );

   @Override
   public void beforePhase(PhaseEvent event)
   {
      if ( event.getPhaseId()==PhaseId.RESTORE_VIEW )
      {
         try
         {
            log.debug( "Starting transaction prior to UPDATE_MODEL_VALUES phase" );
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
      
      boolean commit = event.getPhaseId()==PhaseId.RENDER_RESPONSE ||
            event.getFacesContext().getResponseComplete();
      if ( commit )
      {
         try
         {
            if ( Transactions.isTransactionActive() )
            {
               log.debug( "Commiting transaction after RENDER_RESPONSE phase" );
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
   }

   private void flushEntityManager(String unitName) throws NamingException
   {
      log.trace( "flushing EntityManager [" + unitName + "]" );
      ManagedPersistenceContext managedContext = (ManagedPersistenceContext) Contexts.getConversationContext().get(unitName);
      if ( managedContext!=null ) managedContext.getEntityManager().flush();
      EntityManager em = (EntityManager) NamingHelper.getInitialContext().lookup("java:/EntityManagers/" + unitName);
      if ( em!=null ) em.flush();
   }

   private void flushSession(String sfName)
   {
      log.trace( "flushing Hibernate session [" + sfName + "]" );
      ManagedHibernateSession managedSession = (ManagedHibernateSession) Contexts.getConversationContext().get(sfName);
      if ( managedSession!=null ) managedSession.getSession().flush();
   }

   private void flushJbpm()
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
