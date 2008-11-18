package org.jboss.seam.deployment;

import java.io.File;


/**
 * An accelerated version of the WarRootDeploymentStrategy that uses the SimpleURLScanner
 * to determine the timestamp of the latest file.
 * 
 * @author Dan Allen
 */
public class WarRootTimestampCheckStrategy extends TimestampCheckStrategy
{
   private WarRootDeploymentStrategy delegateStrategy;
   
   public WarRootTimestampCheckStrategy(WarRootDeploymentStrategy warRootDeploymentStrategy)
   {
      delegateStrategy = warRootDeploymentStrategy;
      getDeploymentHandlers().putAll(delegateStrategy.getDeploymentHandlers());
   }

   @Override
   public DeploymentStrategy getDelegateStrategy()
   {
      return delegateStrategy;
   }

   @Override
   public void scan()
   {
      getScanner().scanDirectories(delegateStrategy.getFiles().toArray(new File[0]), delegateStrategy.getExcludedDirectories());
   }

}
