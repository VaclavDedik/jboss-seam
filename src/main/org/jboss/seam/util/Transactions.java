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
      return (UserTransaction) NamingHelper.getInitialContext().lookup("java:comp/UserTransaction");
   }

   public static EJBContext getEJBContext() throws NamingException
   {
      return (EJBContext) NamingHelper.getInitialContext().lookup("java:comp/EJBContext");
   }

   private Transactions() {}
   
}
