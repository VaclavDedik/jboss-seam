package org.jboss.seam.util;

import javax.transaction.UserTransaction;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public abstract class Work<T>
{
   private static final Log log = LogFactory.getLog(Work.class);
   
   protected abstract T work() throws Exception;
   
   protected boolean isTransactional()
   {
      return true;
   }
   
   public final T workInTransaction() throws Exception
   {
      boolean begin = isTransactional() && !Transactions.isTransactionActiveOrMarkedRollback();
      UserTransaction userTransaction = begin ? Transactions.getUserTransaction() : null;
      
      if (begin) 
      {
         log.debug("beginning transaction");
         userTransaction.begin();
      }
      try
      {
         T result = work();
         if (begin) 
         {
            log.debug("committing transaction");
            userTransaction.commit();
         }
         return result;
      }
      catch (Exception e)
      {
         if (begin) 
         {
            log.debug("rolling back transaction");
            userTransaction.rollback();
         }
         throw e;
      }
   }
}
