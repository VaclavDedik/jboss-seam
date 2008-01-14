package org.jboss.seam.deployment;

/**
 * The Scanner is used to find resources to be processed by Seam
 * 
 * The processing is done by {@link DeploymentHandler}s
 * 
 * @author Pete Muir
 *
 */
public interface Scanner
{
   /**
    * Scan the "scannable" classloader.
    * 
    * Method should scan the {@link DeploymentStrategy#getScannableClassLoader()}
    * and pass all found resources to {@link DeploymentStrategy#handle(String)}
    * to be processed by any registered deployment handlers
    */
   public void scanClassLoader();
   
   /**
    * Scan any classloader containing the given resource.
    * 
    * Method should scan any classloader containing {@link DeploymentStrategy#getResourceNames()}
    * and pass all found resources to {@link DeploymentStrategy#handle(String)}
    * to be processed by any registered deployment handlers 
    */
   public void scanResources();
   
   /**
    * Get the deployment strategy this scanner is used by
    */
   public DeploymentStrategy getDeploymentStrategy();
   
}
