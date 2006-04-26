//$Id$
package org.jboss.seam.util;

import javax.ejb.EJBContext;
import javax.naming.NameNotFoundException;
import javax.naming.NamingException;
import javax.transaction.RollbackException;
import javax.transaction.Status;
import javax.transaction.Synchronization;
import javax.transaction.SystemException;
import javax.transaction.TransactionManager;
import javax.transaction.UserTransaction;

public class Transactions
{
   public static final String EJBCONTEXT_NAME = "java:comp.ejb3/EJBContext";
   
   private static String userTransactionName = "UserTransaction";
   private static final String STANDARD_USER_TRANSACTION_NAME = "java:comp/UserTransaction";
   
   private static String transactionManagerName = "java:/TransactionManager";
   
   public static boolean isTransactionActive() throws SystemException, NamingException
   {
      return getUserTransaction().getStatus()==Status.STATUS_ACTIVE;
   }

   public static boolean isTransactionActiveOrMarkedRollback() throws SystemException, NamingException
   {
      int status = getUserTransaction().getStatus();
      return status==Status.STATUS_ACTIVE || status == Status.STATUS_MARKED_ROLLBACK;
   }
   
   public static TransactionManager getTransactionManager() throws NamingException
   {
      return (TransactionManager) Naming.getInitialContext().lookup(transactionManagerName);
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

   public static EJBContext getEJBContext() throws NamingException
   {
      return (EJBContext) Naming.getInitialContext().lookup(EJBCONTEXT_NAME);
   }

   public static void setUserTransactionRollbackOnly() throws SystemException, NamingException {
      UserTransaction userTransaction = getUserTransaction();
      if ( userTransaction.getStatus()!=Status.STATUS_NO_TRANSACTION )
      {
         userTransaction.setRollbackOnly();         
      }
   }
   
   public static void registerSynchronization(Synchronization sync) 
         throws SystemException, RollbackException, NamingException
   {
      getTransactionManager().getTransaction().registerSynchronization(sync);
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
   
}
