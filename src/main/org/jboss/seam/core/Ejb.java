//$Id$
package org.jboss.seam.core;

import static org.jboss.seam.InterceptionType.NEVER;

import org.jboss.ejb3.embedded.EJB3StandaloneBootstrap;
import org.jboss.logging.Logger;
import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.Create;
import org.jboss.seam.annotations.Destroy;
import org.jboss.seam.annotations.Intercept;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.annotations.Startup;

/**
 * A seam component that bootstraps the embedded EJB container
 * 
 * @author Gavin King
 */
@Scope(ScopeType.APPLICATION)
@Intercept(NEVER)
@Startup
@Name("org.jboss.seam.core.ejb")
public class Ejb
{
   private static final Logger log = Logger.getLogger(Ejb.class);
   
   //private EJB3StandaloneDeployer deployer;
   
   @Create
   public void startup() throws Exception
   {
      log.info("starting the embedded EJB container");
      EJB3StandaloneBootstrap.boot(null);
      deploy("META-INF/jboss-beans.xml");
      deploy("jboss-beans.xml");
      EJB3StandaloneBootstrap.scanClasspath();
   }

   private void deploy(String name)
   {
      if ( Thread.currentThread().getContextClassLoader().getResource(name)!=null )
      {
         EJB3StandaloneBootstrap.deployXmlResource(name);
      }
   }
   
   @Destroy
   public void shutdown() throws Exception
   {
      log.info("stopping the embedded EJB container");
      try
      {
         EJB3StandaloneBootstrap.shutdown();
      }
      catch (Exception e) {}
   }
   
}
