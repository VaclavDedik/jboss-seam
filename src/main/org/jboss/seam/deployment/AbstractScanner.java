package org.jboss.seam.deployment;

import javassist.bytecode.ClassFile;

import org.jboss.seam.log.LogProvider;
import org.jboss.seam.log.Logging;

/**
 * Abstract base class for {@link Scanner} providing common functionality
 * 
 * @author Pete Muir
 *
 */
public abstract class AbstractScanner implements Scanner
{
   
   private static final LogProvider log = Logging.getLogProvider(Scanner.class);
   
   private DeploymentStrategy deploymentStrategy;
   
   public AbstractScanner(DeploymentStrategy deploymentStrategy)
   {
      this.deploymentStrategy = deploymentStrategy;
      ClassFile.class.getPackage(); //to force loading of javassist, throwing an exception if it is missing
   }
   
   protected final void handleItem(String name)
   {
      log.trace("found " + name);
      getDeploymentStrategy().handle(name);
   }
   
   public DeploymentStrategy getDeploymentStrategy()
   {
      return deploymentStrategy;
   }

}
