package org.jboss.seam.transaction;

import javax.ejb.EJBContext;
import javax.transaction.HeuristicMixedException;
import javax.transaction.HeuristicRollbackException;
import javax.transaction.NotSupportedException;
import javax.transaction.RollbackException;
import javax.transaction.Status;
import javax.transaction.Synchronization;
import javax.transaction.SystemException;
import javax.transaction.UserTransaction;

/**
 * Wraps EJBContext transaction management in a
 * UserTransaction interface. Note that container managed
 * transactions cannot be controlled by the application,
 * so begin(), commit() and rollback() are disallowed in
 * a CMT.
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
      ejbContext.getUserTransaction().begin();
      parent.afterBegin();
   }

   public void commit() throws RollbackException, HeuristicMixedException,
            HeuristicRollbackException, SecurityException, IllegalStateException, SystemException
   {
      UserTransaction userTransaction = ejbContext.getUserTransaction();
      boolean success = false;
      parent.beforeCommit();
      try
      {
         userTransaction.commit();
         success = true;
      }
      finally
      {
         parent.afterCommit(success);
      }
   }

   public void rollback() throws IllegalStateException, SecurityException, SystemException
   {
      UserTransaction userTransaction = ejbContext.getUserTransaction();
      try
      {
         userTransaction.rollback();
      }
      finally
      {
         parent.afterRollback();
      }
   }

   public int getStatus() throws SystemException
   {
      try
      {
         //TODO: not correct for SUPPORTS or NEVER!
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
         return ejbContext.getUserTransaction().getStatus();
      }
   }

   public void setRollbackOnly() throws IllegalStateException, SystemException
   {
      ejbContext.setRollbackOnly();
   }

   public void setTransactionTimeout(int timeout) throws SystemException
   {
      ejbContext.getUserTransaction().setTransactionTimeout(timeout);
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
