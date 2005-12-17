//$Id$
package org.jboss.seam.util;

import javax.ejb.EJBContext;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.transaction.Status;
import javax.transaction.SystemException;
import javax.transaction.UserTransaction;

import org.jboss.logging.Logger;

public class Transactions
{
   public static Logger log = Logger.getLogger(Transactions.class);

   private static InitialContext initialContext; 
   
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
      setupInitialContext();
      return (UserTransaction) initialContext.lookup("java:comp/UserTransaction");
   }

   public static EJBContext getEJBContext() throws NamingException
   {
      setupInitialContext();
      return (EJBContext) initialContext.lookup("java:comp/EJBContext");
   }

   private static void setupInitialContext() throws NamingException {
      if (initialContext == null)
      {
         initialContext = NamingHelper.getInitialContext();
      }
   }

}
