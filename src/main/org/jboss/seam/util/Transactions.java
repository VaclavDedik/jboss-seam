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
         setUTRollbackOnly();
      }
      catch (NameNotFoundException ne)
      {
         setEJBCRollbackOnly();
      }
   }

   public static boolean isTransactionActive() throws SystemException, NamingException
   {
      try
      {
         return isUTTransactionActive();
      }
      catch (NameNotFoundException ne)
      {
         return isEJBCTransactionActive();
      }
      //temporary workaround for a bad bug in Glassfish!
      catch (IllegalStateException ise)
      {
         return isEJBCTransactionActive();
      }
   }

   public static boolean isTransactionActiveOrMarkedRollback() throws SystemException, NamingException
   {
      try
      {
         return isUTTransactionActiveOrMarkedRollback();
      }
      catch (NameNotFoundException ne)
      {
         return isEJBCTransactionActiveOrMarkedRollback();
      }
      //temporary workaround for a bad bug in Glassfish!
      catch (IllegalStateException ise)
      {
         return isEJBCTransactionActiveOrMarkedRollback();
      }
   }

   public static boolean isTransactionMarkedRollback() throws SystemException, NamingException
   {
      try
      {
         return isUTTransactionMarkedRollback();
      }
      catch (NameNotFoundException ne)
      {
         return isEJBCTransactionMarkedRollback();
      }
      //temporary workaround for a bad bug in Glassfish!
      catch (IllegalStateException ise)
      {
         return isEJBCTransactionMarkedRollback();
      }
   }

   private static void setEJBCRollbackOnly() throws NamingException
   {
      EJB.getEJBContext().setRollbackOnly();
   }

   private static void setUTRollbackOnly() throws SystemException, NamingException
   {
      getUserTransaction().setRollbackOnly();
   }
   
   private static boolean isEJBCTransactionActive() throws NamingException
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

   private static boolean isUTTransactionActive() throws SystemException, NamingException
   {
      return getUserTransaction().getStatus() == STATUS_ACTIVE;
   }

   private static boolean isEJBCTransactionActiveOrMarkedRollback() throws NamingException
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

   private static boolean isUTTransactionActiveOrMarkedRollback() throws SystemException, NamingException
   {
      int status = getUserTransaction().getStatus();
      return status==STATUS_ACTIVE || status == STATUS_MARKED_ROLLBACK;
   }
   
   private static boolean isEJBCTransactionMarkedRollback() throws NamingException
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

   private static boolean isUTTransactionMarkedRollback() throws SystemException, NamingException
   {
      return getUserTransaction().getStatus() == STATUS_MARKED_ROLLBACK;
   }
   
   public static UserTransaction getUserTransaction() throws NamingException
   {
      try
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
      //not really necessary, but just in case...
      catch (IllegalStateException ise)
      {
         throw new NameNotFoundException("Lookup " + userTransactionName + " threw IllegalStateException: " + ise.getMessage());
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
