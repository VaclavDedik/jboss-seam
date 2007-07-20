package org.jboss.seam.transaction;

import static org.jboss.seam.annotations.Install.FRAMEWORK;

import java.rmi.RemoteException;
import java.util.LinkedList;

import javax.ejb.EJBException;
import javax.ejb.Remove;
import javax.ejb.SessionSynchronization;
import javax.ejb.Stateful;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.transaction.Synchronization;

import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.Install;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.annotations.intercept.BypassInterceptors;

/**
 * Receives JTA transaction completion notifications from 
 * the EJB container, and passes them on to the registered
 * Synchronizations. This implementation
 * is fully aware of container managed transactions and is 
 * able to register Synchronizations for the container 
 * transaction.
 * 
 * @author Gavin King
 *
 */
@Stateful
@Name("org.jboss.seam.transaction.synchronizations")
@Scope(ScopeType.EVENT)
@Install(precedence=FRAMEWORK, dependencies="org.jboss.seam.transaction.ejbTransaction")
@BypassInterceptors
@TransactionAttribute(TransactionAttributeType.SUPPORTS)
public class EjbSynchronizations implements LocalEjbSynchronizations, SessionSynchronization
{
   //maintain two lists to work around a bug in JBoss EJB3 where a new SessionSynchronization
   //gets registered each time the bean is called
   protected LinkedList<SynchronizationRegistry> synchronizations = new LinkedList<SynchronizationRegistry>();
   protected LinkedList<SynchronizationRegistry> committing = new LinkedList<SynchronizationRegistry>();
   
   public void afterBegin()
   {
      synchronizations.addLast( new SynchronizationRegistry() );
   }
   
   public void beforeCompletion() throws EJBException, RemoteException
   {
      SynchronizationRegistry sync = synchronizations.removeLast();
      sync.beforeTransactionCompletion();
      committing.addLast(sync);
   }
   
   public void afterCompletion(boolean success) throws EJBException, RemoteException
   {
      committing.removeFirst().afterTransactionCompletion(success);
   }
   
   public boolean isAwareOfContainerTransactions()
   {
      return true;
   }
   
   public void afterCommit(boolean success)
   {
      //noop, let JTA notify us
   }
   
   public void afterRollback()
   {
      //noop, let JTA notify us
   }
   
   public void beforeCommit()
   {
      //noop, let JTA notify us
   }
   
   public void registerSynchronization(Synchronization sync)
   {
      synchronizations.getLast().registerSynchronization(sync);
   }
   
   @Remove
   public void destroy() {}
   
}
