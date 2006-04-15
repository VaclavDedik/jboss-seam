//$Id$
package org.jboss.seam.microcontainer;

import javax.transaction.TransactionManager;

import org.jboss.logging.Logger;
import org.jboss.resource.adapter.jdbc.local.LocalTxDataSource;
import org.jboss.resource.connectionmanager.CachedConnectionManager;
import org.jboss.resource.connectionmanager.CachedConnectionManagerReference;
import org.jboss.seam.util.Naming;

/**
 * A factory that configures and creates a JCA datasource
 * 
 * @author Gavin King
 */
public class DataSourceFactory
{
   private static final Logger log = Logger.getLogger(DataSourceFactory.class);
   
   private LocalTxDataSource ds;
   
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
   private String jndiName;

   
   private TransactionManager transactionManager;
   
   public Object getDataSource() throws Exception
   {
      
      log.info("starting Datasource at JNDI name: " + jndiName);

      CachedConnectionManager ccm = new CachedConnectionManager();
      CachedConnectionManagerReference ccmr = new CachedConnectionManagerReference();
      ccmr.setCachedConnectionManager(ccm);
      ccmr.setTransactionManager(transactionManager);
      
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
      ds.setTransactionManager(transactionManager);
      ds.setJndiName(jndiName);
      ds.setCachedConnectionManager(ccmr);
      ds.setInitialContextProperties(Naming.getInitialContextProperties());
      ds.start();
      
      return ds;
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

   public String getJndiName()
   {
      return jndiName;
   }

   public void setJndiName(String jndiName)
   {
      this.jndiName = jndiName;
   }

   public TransactionManager getTransactionManager()
   {
      return transactionManager;
   }

   public void setTransactionManager(TransactionManager transactionManager)
   {
      this.transactionManager = transactionManager;
   }

}
