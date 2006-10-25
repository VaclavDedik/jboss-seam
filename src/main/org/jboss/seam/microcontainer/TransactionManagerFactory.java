//$Id$
package org.jboss.seam.microcontainer;

import javax.naming.InitialContext;
import javax.transaction.TransactionManager;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jboss.resource.connectionmanager.TransactionSynchronizer;
import org.jboss.seam.util.Naming;
import org.jboss.seam.util.Transactions;
import org.jboss.tm.TransactionManagerLocator;
import org.jboss.tm.usertx.client.ServerVMClientUserTransaction;
import org.jboss.util.naming.NonSerializableFactory;

/**
 * A factory that bootstraps a JTA TransactionManager
 * 
 * @author Gavin King
 */
public class TransactionManagerFactory
{
   
   private static final Log log = LogFactory.getLog(TransactionManagerFactory.class);

   public TransactionManager getTransactionManager() throws Exception
   {
      
      log.info("starting JTA transaction manager");
      InitialContext initialContext = Naming.getInitialContext();

      //create a TransactionManager and bind to JNDI
      TransactionManager transactionManager = TransactionManagerLocator.getInstance().locate();
      TransactionSynchronizer.setTransactionManager(transactionManager);
      NonSerializableFactory.rebind( initialContext, "java:/TransactionManager", transactionManager );
      
      //create a UserTransaction and bind to JNDI
      ServerVMClientUserTransaction ut = new ServerVMClientUserTransaction(transactionManager);
      //TODO: parse the UserTransaction name and create subcontexts
      NonSerializableFactory.rebind( initialContext, Transactions.getUserTransactionName(), ut );
      
      return transactionManager;

   }

}
