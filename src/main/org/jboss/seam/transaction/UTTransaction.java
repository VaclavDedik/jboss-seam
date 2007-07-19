package org.jboss.seam.transaction;

import javax.transaction.HeuristicMixedException;
import javax.transaction.HeuristicRollbackException;
import javax.transaction.NotSupportedException;
import javax.transaction.RollbackException;
import javax.transaction.Synchronization;
import javax.transaction.SystemException;

/**
 * Wraps JTA transaction management in a Seam UserTransaction 
 * interface.
 * 
 * @author Mike Youngstrom
 * @author Gavin King
 * 
 */
public class UTTransaction extends AbstractUserTransaction
{
   
   private final javax.transaction.UserTransaction delegate;

   UTTransaction(javax.transaction.UserTransaction delegate)
   {
      this.delegate = delegate;
      if (delegate==null)
      {
         throw new IllegalArgumentException("null UserTransaction");
      }
   }
   
   public void begin() throws NotSupportedException, SystemException
   {
      delegate.begin();
      getSynchronizations().afterBegin();
   }

   public void commit() throws RollbackException, HeuristicMixedException,
            HeuristicRollbackException, SecurityException, IllegalStateException, SystemException
   {
      boolean success = false;
      Synchronizations synchronizations = getSynchronizations();
      synchronizations.beforeCommit();
      try
      {
         delegate.commit();
         success = true;
      }
      finally
      {
         synchronizations.afterCommit(success);
      }
   }

   public void rollback() throws IllegalStateException, SecurityException, SystemException
   {
      try
      {
         delegate.rollback();
      }
      finally
      {
         getSynchronizations().afterRollback();
      }
   }

   public int getStatus() throws SystemException
   {
      return delegate.getStatus();
   }

   public void setRollbackOnly() throws IllegalStateException, SystemException
   {
      delegate.setRollbackOnly();
   }

   public void setTransactionTimeout(int timeout) throws SystemException
   {
      delegate.setTransactionTimeout(timeout);
   }

   @Override
   public void registerSynchronization(Synchronization sync)
   {
      getSynchronizations().registerSynchronization(sync);
   }
  
}
