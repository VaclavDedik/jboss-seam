//$Id$
package org.jboss.seam.core;

import static org.jboss.seam.InterceptionType.NEVER;

import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.transaction.TransactionManager;

import org.jboss.logging.Logger;
import org.jboss.naming.NonSerializableFactory;
import org.jboss.resource.connectionmanager.TransactionSynchronizer;
import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.Create;
import org.jboss.seam.annotations.Destroy;
import org.jboss.seam.annotations.Intercept;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.annotations.Startup;
import org.jboss.seam.annotations.Unwrap;
import org.jboss.tm.TxManager;
import org.jboss.tm.usertx.client.ServerVMClientUserTransaction;

/**
 * A seam component that boostraps a JTA TransactionManager
 * 
 * @author Gavin King
 */
@Scope(ScopeType.APPLICATION)
@Intercept(NEVER)
@Startup(depends="org.jboss.seam.core.jndi")
@Name("org.jboss.seam.core.jta")
public class Jta
{
   
   private static final Logger log = Logger.getLogger(Jta.class);

   private TransactionManager transactionManager;

   @Create
   public void startup() throws Exception
   {
      
      log.info("starting JTA transaction manager");
      
      //create a TransactionManager and bind to JNDI
      transactionManager = TxManager.getInstance();
      TransactionSynchronizer.setTransactionManager(transactionManager);
      NonSerializableFactory.rebind( new InitialContext(), "java:/TransactionManager", transactionManager );
      
      //create a UserTransaction and bind to JNDI
      ServerVMClientUserTransaction ut = new ServerVMClientUserTransaction(transactionManager);
      NonSerializableFactory.rebind( new InitialContext(), "java:comp/UserTransaction", ut );

   }
   
   @Destroy
   public void shutdown()
   {
      try
      {
         new InitialContext().unbind("java:/TransactionManager");
         new InitialContext().unbind("java:/UserTransaction");
      }
      catch (NamingException ne) {}
      
      transactionManager = null;
   }
   
   @Unwrap
   public TransactionManager getTransactionManager()
   {
      return transactionManager;
   }

}
