//$Id$
package org.jboss.seam.util;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Map;
import java.util.Properties;

import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.transaction.Status;
import javax.transaction.SystemException;
import javax.transaction.UserTransaction;

import org.jboss.logging.Logger;
import org.jboss.seam.Seam;
import org.jboss.seam.core.Init;

public class Transactions
{
   public static Logger log = Logger.getLogger(Transactions.class);

   private static InitialContext initialContext; 
   
   public static boolean isTransactionActive() throws SystemException, NamingException
   {
      return getUserTransaction().getStatus()==Status.STATUS_ACTIVE;
   }

   public static UserTransaction getUserTransaction() throws NamingException
   {
      if (initialContext == null)
      {
         // TODO: We shouldn't have to get the properties from seam.properties again
         // getUserTransaction is called after the request ends, so the applicationContext is already set to null
         // and we cannot user the Init component
         log.debug("Create new Initial Context");
         InputStream stream = Seam.class.getResourceAsStream("/seam.properties");
         Map<String, String> properties = new HashMap<String, String>();
         if (stream!=null)
         {
            Properties props = new Properties();
            try
            {
               props.load(stream);
            }
            catch (IOException ioe)
            {
               log.error("Could not read seam.properties", ioe);
            }
            ( (Map) properties ).putAll(props);
         }
         initialContext = NamingHelper.getInitialContext(properties);
      }
      return (UserTransaction) initialContext.lookup("java:comp/UserTransaction");
   }

   public static boolean isTransactionActiveOrMarkedRollback() throws SystemException, NamingException
   {
      int status = getUserTransaction().getStatus();
      return status==Status.STATUS_ACTIVE || status == Status.STATUS_MARKED_ROLLBACK;
   }
}
