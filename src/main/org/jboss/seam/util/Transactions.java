//$Id$
package org.jboss.seam.util;

import static javax.transaction.Status.STATUS_ACTIVE;
import static javax.transaction.Status.STATUS_MARKED_ROLLBACK;
import static javax.transaction.Status.STATUS_NO_TRANSACTION;

import javax.naming.NameNotFoundException;
import javax.naming.NamingException;
import javax.transaction.SystemException;
import javax.transaction.UserTransaction;

public class Transactions
{   
   private static String userTransactionName = "UserTransaction";
   private static final String STANDARD_USER_TRANSACTION_NAME = "java:comp/UserTransaction";
   
   public static boolean isTransactionActive() throws SystemException, NamingException
   {
      return getUserTransaction().getStatus()==STATUS_ACTIVE;
   }

   public static boolean isTransactionActiveOrMarkedRollback() throws SystemException, NamingException
   {
      int status = getUserTransaction().getStatus();
      return status==STATUS_ACTIVE || status == STATUS_MARKED_ROLLBACK;
   }
   
   public static boolean isTransactionMarkedRollback() throws SystemException, NamingException
   {
      return getUserTransaction().getStatus() == STATUS_MARKED_ROLLBACK;
   }
   
   public static UserTransaction getUserTransaction() throws NamingException
   {
      try
      {
         return (UserTransaction) Naming.getInitialContext().lookup(userTransactionName);
      }
      catch (NameNotFoundException nnfe)
      {
         return (UserTransaction) Naming.getInitialContext().lookup(STANDARD_USER_TRANSACTION_NAME);
      }
   }

   public static void setUserTransactionRollbackOnly() throws SystemException, NamingException {
      UserTransaction userTransaction = getUserTransaction();
      if ( userTransaction.getStatus()!=STATUS_NO_TRANSACTION )
      {
         userTransaction.setRollbackOnly();         
      }
   }
   
   private Transactions() {}

   public static void setUserTransactionName(String userTransactionName)
   {
      Transactions.userTransactionName = userTransactionName;
   }

   public static String getUserTransactionName()
   {
      return userTransactionName;
   }
   
   public static boolean isTransactionAvailableAndMarkedRollback() throws SystemException
   {
      try
      {
         return getUserTransaction().getStatus() == STATUS_MARKED_ROLLBACK;
      }
      catch (NamingException ne)
      {
         return false;
      }
   }

   /*private static String transactionManagerName = "java:/TransactionManager";
   
   public static TransactionManager getTransactionManager() throws NamingException
   {
      return (TransactionManager) Naming.getInitialContext().lookup(transactionManagerName);
   }

   public static void registerSynchronization(Synchronization sync) 
         throws SystemException, RollbackException, NamingException
   {
      getTransactionManager().getTransaction().registerSynchronization(sync);
   }

   public static String getTransactionManagerName()
   {
      return transactionManagerName;
   }

   public static void setTransactionManagerName(String transactionManagerName)
   {
      Transactions.transactionManagerName = transactionManagerName;
   }*/
   
}
