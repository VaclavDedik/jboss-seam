//$Id$
package org.jboss.seam.test;

import java.sql.Connection;
import java.util.Properties;

import javax.naming.InitialContext;
import javax.sql.DataSource;
import javax.transaction.UserTransaction;

import org.jboss.seam.Component;
import org.jboss.seam.contexts.Contexts;
import org.jboss.seam.contexts.Lifecycle;
import org.jboss.seam.core.ManagedDataSource;
import org.jboss.seam.mock.MockServletContext;
import org.testng.annotations.Test;

public class CoreTest
{
   @Test
   public void testDatasource() throws Exception
   {
      Properties props = new Properties();
      props.setProperty("datasource.driverClass", "org.hsqldb.jdbcDriver");
      props.setProperty("datasource.connectionUrl", "jdbc:hsqldb:.");
      props.setProperty("datasource.userName", "sa");
      Lifecycle.beginInitialization( new MockServletContext() );
      Contexts.getApplicationContext().set(Component.PROPERTIES, props);
      Component component = new Component(ManagedDataSource.class, "datasource");
      ManagedDataSource sds = (ManagedDataSource) component.newInstance();
      Lifecycle.endInitialization();
      
      sds.startup(component);
      DataSource ds = (DataSource) new InitialContext().lookup("datasource");
      assert ds!=null;
      UserTransaction ut = (UserTransaction) new InitialContext().lookup("java:comp/UserTransaction");
      assert ut!=null;
      ut.begin();
      Connection conn = ds.getConnection();
      conn.createStatement().executeQuery("call current_date");
      conn.createStatement().executeUpdate("create table foo (bar varchar)");
      conn.createStatement().executeUpdate("insert into foo values ('foo bar')");
      conn.close();
      ut.commit();
      ut.begin();
      conn = ds.getConnection();
      assert 1==conn.createStatement().executeUpdate("delete from foo");
      conn.close();
      ut.rollback();
      ut.begin();
      conn = ds.getConnection();
      assert 1==conn.createStatement().executeUpdate("delete from foo");
      conn.createStatement().executeUpdate("drop table foo");
      conn.close();
      ut.commit();
   }
}
