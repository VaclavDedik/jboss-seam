//$Id$
package org.jboss.seam.core;

import static org.jboss.seam.InterceptionType.NEVER;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jboss.ejb3.embedded.EJB3StandaloneBootstrap;
import org.jboss.ejb3.embedded.EJB3StandaloneDeployer;
import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.Create;
import org.jboss.seam.annotations.Destroy;
import org.jboss.seam.annotations.Install;
import org.jboss.seam.annotations.Intercept;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.annotations.Startup;
import org.jboss.seam.util.Naming;

/**
 * A seam component that bootstraps the embedded EJB container
 * 
 * @author Gavin King
 */
@Scope(ScopeType.APPLICATION)
@Intercept(NEVER)
@Startup
@Name("org.jboss.seam.core.ejb")
@Install(false)
public class Ejb
{
   private static final Log log = LogFactory.getLog(Ejb.class);
   
   private EJB3StandaloneDeployer deployer;
   private boolean started;
   
   @Create
   public void startup() throws Exception
   {
      log.info("starting the embedded EJB container");
      EJB3StandaloneBootstrap.boot(null);
      deploy("META-INF/jboss-beans.xml");
      deploy("jboss-beans.xml");
      
      deployer = EJB3StandaloneBootstrap.createDeployer();
      deployer.getArchivesByResource().add("seam.properties");
      
      // need to set the InitialContext properties that deployer will use
      // to initial EJB containers
      deployer.setJndiProperties(Naming.getInitialContextProperties());
      
      deployer.create();
      deployer.start();
      //EJB3StandaloneBootstrap.scanClasspath();
      started = true;
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
      if (started)
      {
         log.info("stopping the embedded EJB container");
         deployer.stop();
         deployer.destroy();
         deployer = null;
      }
      EJB3StandaloneBootstrap.shutdown();
   }
   
}
