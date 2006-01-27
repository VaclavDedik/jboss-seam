//$Id$
package org.jboss.seam.util;

import javax.ejb.EJBContext;
import javax.naming.NamingException;
import javax.transaction.Status;
import javax.transaction.SystemException;
import javax.transaction.UserTransaction;

import org.jboss.logging.Logger;

public class Transactions
{
   public static final String EJBCONTEXT_NAME = "java:comp/EJBContext";
   public static final String USER_TRANSACTION_NAME = "java:comp/UserTransaction";
   
   public static Logger log = Logger.getLogger(Transactions.class);
   
   public static boolean isTransactionActive() throws SystemException, NamingException
   {
      return getUserTransaction().getStatus()==Status.STATUS_ACTIVE;
   }

   public static boolean isTransactionActiveOrMarkedRollback() throws SystemException, NamingException
   {
      int status = getUserTransaction().getStatus();
      return status==Status.STATUS_ACTIVE || status == Status.STATUS_MARKED_ROLLBACK;
   }

   public static UserTransaction getUserTransaction() throws NamingException
   {
      return (UserTransaction) NamingHelper.getInitialContext().lookup(USER_TRANSACTION_NAME);
   }

   public static EJBContext getEJBContext() throws NamingException
   {
      return (EJBContext) NamingHelper.getInitialContext().lookup(EJBCONTEXT_NAME);
   }

   private Transactions() {}

   public static void setUserTransactionRollbackOnly() throws SystemException, NamingException {
      UserTransaction userTransaction = getUserTransaction();
      if ( userTransaction.getStatus()!=Status.STATUS_NO_TRANSACTION )
      {
         userTransaction.setRollbackOnly();         
      }
   }
   
}
