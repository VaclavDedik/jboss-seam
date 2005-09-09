//$Id$
package org.jboss.seam.core;

import static org.jboss.seam.InterceptionType.NEVER;

import javax.naming.InitialContext;

import org.jboss.naming.NonSerializableFactory;
import org.jboss.naming.Util;
import org.jboss.resource.adapter.jdbc.local.LocalTxDataSource;
import org.jboss.resource.connectionmanager.TransactionSynchronizer;
import org.jboss.seam.Component;
import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.Create;
import org.jboss.seam.annotations.Destroy;
import org.jboss.seam.annotations.Intercept;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.annotations.Startup;
import org.jboss.tm.TxManager;
import org.jboss.tm.usertx.client.ServerVMClientUserTransaction;
import org.jnp.server.SingletonNamingServer;

@Scope(ScopeType.APPLICATION)
@Intercept(NEVER)
@Startup
public class ManagedDataSource
{
   //private static final Logger log = Logger.getLogger(JTADatasource.class);
   
   private TxManager tm;
   private LocalTxDataSource ds;
   private SingletonNamingServer sns;
   
   private int maxSize = 20;
   private int minSize = 0;
   private int blockingTimeout = 30000;
   private int idleTimeout = 900000;
   
   private String connectionUrl;
   private String driverClass;
   private String userName;
   private String password;
   private int preparedStatementCacheSize;
   private String checkValidConnectionSql;
   
   @Create
   public void startup(Component component) throws Exception
   {
      //start up JNDI
      sns = new SingletonNamingServer();
      InitialContext ctx = new InitialContext();
      Util.createSubcontext(ctx, "java:/comp");
      
      //create a TransactionManager
      tm = TxManager.getInstance();
      TransactionSynchronizer.setTransactionManager(tm);
      
      //create a UserTransaction and bind to JNDI
      ServerVMClientUserTransaction ut = new ServerVMClientUserTransaction(tm);
      NonSerializableFactory.rebind(ctx, "java:comp/UserTransaction", ut);
      
      //create a JCA Datasource
      ds = new LocalTxDataSource();
      ds.setConnectionURL(connectionUrl);
      ds.setDriverClass(driverClass);
      ds.setUserName(userName);
      ds.setPassword(password);
      ds.setPreparedStatementCacheSize(preparedStatementCacheSize);
      ds.setCheckValidConnectionSQL(checkValidConnectionSql);
      ds.setMaxSize(maxSize);
      ds.setMinSize(minSize);
      ds.setBlockingTimeout(blockingTimeout);
      ds.setIdleTimeout(idleTimeout);
      ds.setTransactionManager(tm);
      ds.setJndiName( component.getName() );
      ds.start();
   }
   
   @Destroy
   public void shutdown()
   {
      //TODO: we need a ds.stop() method
      ds = null;
      tm = null;
      sns = null;
   }

   public int getBlockingTimeout()
   {
      return blockingTimeout;
   }

   public void setBlockingTimeout(int blockingTimeout)
   {
      this.blockingTimeout = blockingTimeout;
   }

   public int getIdleTimeout()
   {
      return idleTimeout;
   }

   public void setIdleTimeout(int idleTimeout)
   {
      this.idleTimeout = idleTimeout;
   }

   public int getMaxSize()
   {
      return maxSize;
   }

   public void setMaxSize(int maxSize)
   {
      this.maxSize = maxSize;
   }

   public int getMinSize()
   {
      return minSize;
   }

   public void setMinSize(int minSize)
   {
      this.minSize = minSize;
   }

   public String getConnectionUrl()
   {
      return connectionUrl;
   }

   public void setConnectionUrl(String connectionUrl)
   {
      this.connectionUrl = connectionUrl;
   }

   public String getDriverClass()
   {
      return driverClass;
   }

   public void setDriverClass(String driverClass)
   {
      this.driverClass = driverClass;
   }

   public String getPassword()
   {
      return password;
   }

   public void setPassword(String password)
   {
      this.password = password;
   }

   public String getUserName()
   {
      return userName;
   }

   public void setUserName(String userName)
   {
      this.userName = userName;
   }

   public int getPreparedStatementCacheSize()
   {
      return preparedStatementCacheSize;
   }

   public void setPreparedStatementCacheSize(int preparedStatementCacheSize)
   {
      this.preparedStatementCacheSize = preparedStatementCacheSize;
   }

   public String getCheckValidConnectionSql()
   {
      return checkValidConnectionSql;
   }

   public void setCheckValidConnectionSql(String checkValidConnectionSql)
   {
      this.checkValidConnectionSql = checkValidConnectionSql;
   }


}
