//$Id$
package org.jboss.seam.core;

import static org.jboss.seam.InterceptionType.NEVER;

import org.jboss.ejb3.embedded.EJB3StandaloneBootstrap;
import org.jboss.ejb3.embedded.EJB3StandaloneDeployer;
import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.Create;
import org.jboss.seam.annotations.Destroy;
import org.jboss.seam.annotations.Intercept;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.annotations.Startup;

@Scope(ScopeType.APPLICATION)
@Intercept(NEVER)
@Startup
@Name("org.jboss.seam.core.ejb")
public class Ejb
{
   private static EJB3StandaloneDeployer deployer;
   
   @Create
   public void startup() throws Exception
   {
      EJB3StandaloneBootstrap.boot(null);

      deployer = new EJB3StandaloneDeployer();
      deployer.getArchivesByResource().add("META-INF/persistence.xml");

      // need to set the InitialContext properties that deployer will use
      // to initial EJB containers
      //deployer.setJndiProperties(getInitialContextProperties());

      deployer.create();
      deployer.start();
   }
   
   @Destroy
   public void shutdown() throws Exception
   {
      deployer.stop();
      deployer.destroy();
      deployer = null;
   }
}
