//$Id$
package org.jboss.seam.servlet;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

import org.jboss.ejb3.embedded.EJB3StandaloneBootstrap;
import org.jboss.ejb3.embedded.EJB3StandaloneDeployer;
import org.jboss.logging.Logger;
import org.jboss.seam.init.Initialization;

/**
 * Boostraps Seam inside JBoss
 * 
 * @author Gavin King
 */
public class SeamTomcatStartupListener implements ServletContextListener
{

   private static final Logger log = Logger.getLogger(SeamTomcatStartupListener.class);
   
   private EJB3StandaloneDeployer deployer;
   
   public void contextInitialized(ServletContextEvent event) 
   {
      log.info("Welcome to Seam on Tomcat");
      EJB3StandaloneBootstrap.boot(null);

      deployer = new EJB3StandaloneDeployer();
      deployer.getArchivesByResource().add("META-INF/persistence.xml");

      try
      {
         deployer.create();
         deployer.start();
      }
      catch (RuntimeException re)
      {
         throw re;
      }
      catch (Exception e)
      {
         throw new RuntimeException(e);
      }
      new Initialization( event.getServletContext() ).init();
   }

   public void contextDestroyed(ServletContextEvent event) 
   {
      if (deployer==null) return;
      try
      {
         deployer.stop();
         deployer.destroy();
      }
      catch (RuntimeException re)
      {
         throw re;
      }
      catch (Exception e)
      {
         throw new RuntimeException(e);
      }
      deployer = null;
   }

}
