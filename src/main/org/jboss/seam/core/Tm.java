//$Id$
package org.jboss.seam.core;

import static org.jboss.seam.InterceptionType.NEVER;

import javax.naming.InitialContext;
import javax.transaction.TransactionManager;

import org.jboss.naming.NonSerializableFactory;
import org.jboss.resource.connectionmanager.TransactionSynchronizer;
import org.jboss.seam.Component;
import org.jboss.seam.ScopeType;
import org.jboss.seam.Seam;
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
@Name("org.jboss.seam.core.tm")
public class Tm
{
   
   private TxManager tm;

   @Create
   public void startup() throws Exception
   {
      //force JNDI startup
      Component.getInstance( Seam.getComponentName(Jndi.class), true );
      
      //create a TransactionManager and bind to JNDI
      tm = TxManager.getInstance();
      TransactionSynchronizer.setTransactionManager(tm);
      NonSerializableFactory.rebind( new InitialContext(), "java:/TransactionManager", tm );
      
      //create a UserTransaction and bind to JNDI
      ServerVMClientUserTransaction ut = new ServerVMClientUserTransaction(tm);
      NonSerializableFactory.rebind( new InitialContext(), "java:comp/UserTransaction", ut );

   }
   
   @Destroy
   public void shutdown()
   {
      tm = null;
   }
   
   @Unwrap
   public TransactionManager getTransactionManager()
   {
      return tm;
   }

}
