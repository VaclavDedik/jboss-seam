package org.jboss.seam.deployment;

/**
 * A no-op version of the URLScanner that merely returns whether the deployment
 * handler would in fact handle this file. It does not process the file
 * in any way. This allows us to use this scanner for timestamp checking.
 * 
 * @author Dan Allen
 */
public class TimestampURLScanner extends URLScanner
{
   public TimestampURLScanner(DeploymentStrategy deploymentStrategy)
   {
      super(deploymentStrategy);
   }

   @Override
   protected boolean handleItem(String name)
   {
      for (DeploymentHandler handler : getDeploymentStrategy().getDeploymentHandlers().values())
      {
         if (handler instanceof ClassDeploymentHandler)
         {
            if (name.endsWith(".class"))
            {
               return true;
            }
         }
         else
         {
            if (name.endsWith(handler.getMetadata().getFileNameSuffix()))
            {
               return true;
            }
         }
      }
      return false;
   }
  
}
