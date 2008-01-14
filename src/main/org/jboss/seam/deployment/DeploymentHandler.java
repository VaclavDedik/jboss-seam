package org.jboss.seam.deployment;

/**
 * A deployment handler is responsible for processing found resources
 * 
 * All deployment handlers should specify a unique name under which they
 * will be registered with the {@link DeploymentStrategy}
 * 
 * @author Pete Muir
 *
 */
public interface DeploymentHandler
{
   /**
    * Handle a resource
    * @param name The path to the resource
    * @param classLoader The ClassLoader on which the resource was found
    */
   public void handle(String name, ClassLoader classLoader);
}
