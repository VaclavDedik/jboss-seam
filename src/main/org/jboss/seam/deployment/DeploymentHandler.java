package org.jboss.seam.deployment;

/**
 * @author Pete Muir
 *
 */
public interface DeploymentHandler
{
   public void handle(String name, ClassLoader classLoader);
}
