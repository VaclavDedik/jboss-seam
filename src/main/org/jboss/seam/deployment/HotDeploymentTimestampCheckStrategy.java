package org.jboss.seam.deployment;

import java.io.File;

/**
 * An accelerated version of the HotDeploymentStrategy that uses the SimpleURLScanner
 * to determine the timestamp of the latest file.
 * 
 * @author Dan Allen
 */
public class HotDeploymentTimestampCheckStrategy extends TimestampCheckStrategy
{
   private HotDeploymentStrategy delegateStrategy;
   
   public HotDeploymentTimestampCheckStrategy(HotDeploymentStrategy hotDeploymentStrategy)
   {
      delegateStrategy = hotDeploymentStrategy;
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
      getScanner().scanDirectories(delegateStrategy.getFiles().toArray(new File[0]));
   }
   
}
