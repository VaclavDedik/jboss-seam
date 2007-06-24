package org.jboss.seam.transaction;

import javax.ejb.EJBContext;
import javax.transaction.HeuristicMixedException;
import javax.transaction.HeuristicRollbackException;
import javax.transaction.NotSupportedException;
import javax.transaction.RollbackException;
import javax.transaction.Status;
import javax.transaction.Synchronization;
import javax.transaction.SystemException;

/**
 * Wraps EJBContext transaction management in a
 * UserTransaction interface. Note that container managed
 * transaction cannot be controlled by the application,
 * so begin(), commit() and rollback() all throw
 * UnsupportOperationException.
 * 
 * 
 * @author Mike Youngstrom
 * @author Gavin King
 * 
 */
public class CMTTransaction extends AbstractUserTransaction
{
   
   private final EJBContext ejbContext;
   private final Transaction parent;

   public CMTTransaction(EJBContext ejbContext, Transaction parent)
   {
      this.parent = parent;
      this.ejbContext = ejbContext;
      if (ejbContext==null)
      {
         throw new IllegalArgumentException("null EJBContext");
      }
   }

   public void begin() throws NotSupportedException, SystemException
   {
      throw new UnsupportedOperationException("container managed transaction");
   }

   public void commit() throws RollbackException, HeuristicMixedException,
            HeuristicRollbackException, SecurityException, IllegalStateException, SystemException
   {
      throw new UnsupportedOperationException("container managed transaction");
   }

   public int getStatus() throws SystemException
   {
      try
      {
         if ( !ejbContext.getRollbackOnly() )
         {
            return Status.STATUS_ACTIVE;
         }
         else
         {
            return Status.STATUS_MARKED_ROLLBACK;
         }
      }
      catch (IllegalStateException ise)
      {
         return Status.STATUS_NO_TRANSACTION;
      }
   }

   public void rollback() throws IllegalStateException, SecurityException, SystemException
   {
      throw new UnsupportedOperationException("container managed transaction");
   }

   public void setRollbackOnly() throws IllegalStateException, SystemException
   {
      ejbContext.setRollbackOnly();
   }

   public void setTransactionTimeout(int timeout) throws SystemException
   {
      throw new UnsupportedOperationException("container managed transaction");
   }
   
   @Override
   public void registerSynchronization(Synchronization sync)
   {
      if ( parent.isAwareOfContainerTransactions() )
      {
         parent.registerSynchronization(sync);
      }
      else
      {
         throw new UnsupportedOperationException("cannot register synchronization with container transaction, use <transaction:ejb-transaction/>");
      }
   }

}
