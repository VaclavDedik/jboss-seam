package org.jboss.seam.transaction;

import javax.transaction.HeuristicMixedException;
import javax.transaction.HeuristicRollbackException;
import javax.transaction.NotSupportedException;
import javax.transaction.RollbackException;
import javax.transaction.Status;
import javax.transaction.SystemException;

/**
 * When no kind of transaction management
 * exists.
 * 
 * @author Mike Youngstrom
 * @author Gavin King
 * 
 */
public class NoTransaction extends UserTransaction
{
   
   public void begin() throws NotSupportedException, SystemException
   {
      throw new UnsupportedOperationException();
   }

   public void commit() throws RollbackException, HeuristicMixedException,
            HeuristicRollbackException, SecurityException, IllegalStateException, SystemException
   {
      throw new UnsupportedOperationException();
   }

   public int getStatus() throws SystemException
   {
      return Status.STATUS_NO_TRANSACTION;
   }

   public void rollback() throws IllegalStateException, SecurityException, SystemException
   {
      throw new UnsupportedOperationException();
   }

   public void setRollbackOnly() throws IllegalStateException, SystemException
   {
      throw new UnsupportedOperationException();
   }

   public void setTransactionTimeout(int timeout) throws SystemException
   {
      throw new UnsupportedOperationException();
   }

}
