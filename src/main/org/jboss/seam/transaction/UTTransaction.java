package org.jboss.seam.transaction;

import javax.transaction.HeuristicMixedException;
import javax.transaction.HeuristicRollbackException;
import javax.transaction.NotSupportedException;
import javax.transaction.RollbackException;
import javax.transaction.SystemException;

/**
 * Wraps EJBContext transaction management in a
 * UserTransaction interface.
 * 
 * @author Mike Youngstrom
 * @author Gavin King
 * 
 */
public class UTTransaction extends UserTransaction
{
   
   private javax.transaction.UserTransaction delegate;

   UTTransaction(javax.transaction.UserTransaction delegate)
   {
      this.delegate = delegate;
   }
   
   public void begin() throws NotSupportedException, SystemException
   {
      delegate.begin();
   }

   public void commit() throws RollbackException, HeuristicMixedException,
            HeuristicRollbackException, SecurityException, IllegalStateException, SystemException
   {
      delegate.commit();
   }

   public int getStatus() throws SystemException
   {
      return delegate.getStatus();
   }

   public void rollback() throws IllegalStateException, SecurityException, SystemException
   {
      delegate.rollback();
   }

   public void setRollbackOnly() throws IllegalStateException, SystemException
   {
      delegate.setRollbackOnly();
   }

   public void setTransactionTimeout(int timeout) throws SystemException
   {
      delegate.setTransactionTimeout(timeout);
   }

}
