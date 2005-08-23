//$Id$
package org.jboss.seam.util;

import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.transaction.Status;
import javax.transaction.SystemException;
import javax.transaction.UserTransaction;

public class Transactions
{
   public static boolean isTransactionActive() throws SystemException, NamingException
   {
      return getUserTransaction().getStatus()==Status.STATUS_ACTIVE;
   }

   public static UserTransaction getUserTransaction() throws NamingException
   {
      return (UserTransaction) new InitialContext().lookup("java:comp/UserTransaction");
   }

   public static boolean isTransactionActiveOrMarkedRollback() throws SystemException, NamingException
   {
      int status = getUserTransaction().getStatus();
      return status==Status.STATUS_ACTIVE || status == Status.STATUS_MARKED_ROLLBACK;
   }
}
