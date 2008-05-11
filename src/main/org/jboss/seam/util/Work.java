package org.jboss.seam.util;

import javax.transaction.Status;
import javax.transaction.UserTransaction;

import org.jboss.seam.log.LogProvider;
import org.jboss.seam.log.Logging;
import org.jboss.seam.transaction.Transaction;

/**
 * Performs work in a JTA transaction.
 * 
 * @author Gavin King
 */
public abstract class Work<T>
{
   private static final LogProvider log = Logging.getLogProvider(Work.class);
   
   protected abstract T work() throws Exception;
   
   protected boolean isNewTransactionRequired(boolean transactionActive)
   {
      return !transactionActive;
   }
   
   public final T workInTransaction() throws Exception
   {
      boolean transactionActive = Transaction.instance().isActiveOrMarkedRollback()
              || Transaction.instance().isRolledBack(); //TODO: temp workaround, what should we really do in this case??
      boolean newTransactionRequired = isNewTransactionRequired(transactionActive);
      UserTransaction userTransaction = newTransactionRequired ? Transaction.instance() : null;
      
      if (newTransactionRequired) 
      {
         log.debug("beginning transaction");
         userTransaction.begin();
      }
      
      try
      {
         T result = work();
         if (newTransactionRequired) 
         {
            if (Transaction.instance().isMarkedRollback())
            {
               log.debug("rolling back transaction");
               userTransaction.rollback(); 
            }
            else
            {
               log.debug("committing transaction");
               userTransaction.commit();
            }
         }
         return result;
      }
      catch (Exception e)
      {
         if (newTransactionRequired && userTransaction.getStatus() != Status.STATUS_NO_TRANSACTION) 
         {
            log.debug("rolling back transaction");
            userTransaction.rollback();
         }
         throw e;
      }
      
   }
}
