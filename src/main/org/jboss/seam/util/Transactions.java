//$Id$
package org.jboss.seam.util;

import static javax.transaction.Status.STATUS_ACTIVE;
import static javax.transaction.Status.STATUS_MARKED_ROLLBACK;

import javax.naming.NameNotFoundException;
import javax.naming.NamingException;
import javax.transaction.SystemException;
import javax.transaction.UserTransaction;

public class Transactions
{   
   private static String userTransactionName = "UserTransaction";
   private static final String STANDARD_USER_TRANSACTION_NAME = "java:comp/UserTransaction";
   
   public static void setTransactionRollbackOnly() throws SystemException, NamingException 
   {
      try
      {
         getUserTransaction().setRollbackOnly();
      }
      catch (NamingException ne)
      {
         EJB.getEJBContext().setRollbackOnly();
      }
   }
   
   public static boolean isTransactionActive() throws SystemException, NamingException
   {
      try
      {
         return getUserTransaction().getStatus()==STATUS_ACTIVE;
      }
      catch (NamingException ne)
      {
         try
         {
            return !EJB.getEJBContext().getRollbackOnly();
         }
         catch (IllegalStateException ise)
         {
            return false;
         }
      }
   }

   public static boolean isTransactionActiveOrMarkedRollback() throws SystemException, NamingException
   {
      try
      {
         int status = getUserTransaction().getStatus();
         return status==STATUS_ACTIVE || status == STATUS_MARKED_ROLLBACK;
      }
      catch (NamingException ne)
      {
         try
         {
            EJB.getEJBContext().getRollbackOnly();
            return true;
         }
         catch (IllegalStateException ise)
         {
            return false;
         }
      }
   }
   
   public static boolean isTransactionMarkedRollback() throws SystemException, NamingException
   {
      try
      {
         return getUserTransaction().getStatus() == STATUS_MARKED_ROLLBACK;
      }
      catch (NamingException ne)
      {
         try
         {
            return EJB.getEJBContext().getRollbackOnly();
         }
         catch (IllegalStateException ise)
         {
            return false;
         }
      }
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
   
   private Transactions() {}

   public static void setUserTransactionName(String userTransactionName)
   {
      Transactions.userTransactionName = userTransactionName;
   }

   public static String getUserTransactionName()
   {
      return userTransactionName;
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
