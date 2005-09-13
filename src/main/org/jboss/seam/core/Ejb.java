//$Id$
package org.jboss.seam.core;

import static org.jboss.seam.InterceptionType.NEVER;

import javax.naming.InitialContext;

import org.jboss.ejb3.embedded.EJB3StandaloneBootstrap;
import org.jboss.ejb3.embedded.EJB3StandaloneDeployer;
import org.jboss.logging.Logger;
import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.Create;
import org.jboss.seam.annotations.Destroy;
import org.jboss.seam.annotations.Intercept;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.annotations.Startup;
import org.jboss.tm.TransactionManagerInitializer;

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
   
   private EJB3StandaloneDeployer deployer;
   
   @Create
   public void startup() throws Exception
   {
      
      log.info("starting the embedded EJB container");
      EJB3StandaloneBootstrap.boot(null);
      deploy("META-INF/jboss-beans.xml");
      deploy("jboss-beans.xml");
      
      deployer = new EJB3StandaloneDeployer();
      deployer.getArchivesByResource().add("seam.properties");

      // need to set the InitialContext properties that deployer will use
      // to initial EJB containers
      //deployer.setJndiProperties(getInitialContextProperties());

      deployer.create();
      deployer.start();
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
      deployer.stop();
      InitialContext ctx = new InitialContext();
      ctx.unbind(TransactionManagerInitializer.JNDI_NAME);
      ctx.unbind(TransactionManagerInitializer.JNDI_IMPORTER);
      ctx.unbind(TransactionManagerInitializer.JNDI_EXPORTER);
      deployer.destroy();
      deployer = null;
   }
   
}
