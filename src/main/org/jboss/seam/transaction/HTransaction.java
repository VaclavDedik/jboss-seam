package org.jboss.seam.transaction;

import javax.transaction.HeuristicMixedException;
import javax.transaction.HeuristicRollbackException;
import javax.transaction.NotSupportedException;
import javax.transaction.RollbackException;
import javax.transaction.Status;
import javax.transaction.SystemException;

/**
 * Wraps Hibernate transaction management in a
 * UserTransaction interface.
 * 
 * @author Gavin King
 * 
 */
class HTransaction extends UserTransaction
{
   
   private org.hibernate.Transaction delegate;

   HTransaction(org.hibernate.Transaction delegate)
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
      return delegate.isActive() ? Status.STATUS_ACTIVE : Status.STATUS_NO_TRANSACTION;
   }

   public void rollback() throws IllegalStateException, SecurityException, SystemException
   {
      delegate.rollback();
   }

   public void setRollbackOnly() throws IllegalStateException, SystemException
   {
      delegate.rollback();
   }

   public void setTransactionTimeout(int timeout) throws SystemException
   {
      throw new UnsupportedOperationException();
   }

}
