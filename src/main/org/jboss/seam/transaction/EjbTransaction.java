package org.jboss.seam.transaction;

import static org.jboss.seam.annotations.Install.FRAMEWORK;

import java.rmi.RemoteException;

import javax.ejb.EJBException;
import javax.ejb.Remove;
import javax.ejb.SessionSynchronization;
import javax.ejb.Stateful;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;

import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.Install;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.annotations.intercept.BypassInterceptors;

/**
 * Receives JTA transaction completion notifications from 
 * the EJB container, and passes them on to the registered
 * Synchronizations. Unlike its superclass, this implementation
 * is fully aware of container managed transactions and is 
 * able to register Synchronizations for the container 
 * transaction.
 * 
 * @author Gavin King
 *
 */
@Stateful
@Name("org.jboss.seam.transaction.transaction")
@Scope(ScopeType.EVENT)
@Install(precedence=FRAMEWORK, value=false)
@BypassInterceptors
@TransactionAttribute(TransactionAttributeType.SUPPORTS)
public class EjbTransaction extends Transaction 
      implements LocalEjbTransaction, SessionSynchronization
{
   
   public void beforeCompletion() throws EJBException, RemoteException
   {
      getSynchronizations().beforeTransactionCompletion();
   }
   
   public void afterCompletion(boolean success) throws EJBException, RemoteException
   {
      getSynchronizations().afterTransactionCompletion(success);
   }
   
   @Override
   protected boolean isAwareOfContainerTransactions()
   {
      return true;
   }
   
   @Override
   protected void afterCommit(boolean success)
   {
      //noop, let JTA notify us
   }
   
   @Override
   protected void afterRollback()
   {
      //noop, let JTA notify us
   }
   
   @Override
   protected void beforeCommit()
   {
      //noop, let JTA notify us
   }
   
   @Remove
   public void destroy() {}
   
}
