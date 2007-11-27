package org.jboss.seam.mock.embedded;


import org.jboss.embedded.Bootstrap;
import org.jboss.embedded.DeploymentGroup;
import org.jboss.kernel.Kernel;

public class BootstrapWrapper extends Bootstrap
{
   private static Bootstrap instance;
   
   public BootstrapWrapper(Kernel kernel)
   {
      super(kernel);
   }
   
   public static synchronized Bootstrap getInstance()
   {
      if (instance == null) instance = new BootstrapWrapper(createKernel());
      return instance;
   }
   
   @Override
   public DeploymentGroup createDeploymentGroup()
   {
      DeploymentGroup group = new DeploymentGroupWrapper();
      group.setClassLoader(loader);
      group.setMainDeployer(mainDeployer);
      group.setKernel(kernel);
      return group;
   }
}