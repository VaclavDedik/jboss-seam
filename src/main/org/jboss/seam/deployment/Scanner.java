package org.jboss.seam.deployment;

/**
 * @author Pete Muir
 *
 */
public interface Scanner
{
   
   public void scanClassLoader();
   
   public void scanResources();
   
   public DeploymentStrategy getDeploymentStrategy();
   
}
