package org.jboss.seam.mock;

import org.jboss.embedded.Bootstrap;
import org.jboss.seam.mock.embedded.BootstrapWrapper;

public class EmbeddedBootstrap
{
   
   public void startAndDeployResources() throws Exception
   {
      Bootstrap bootstrap = BootstrapWrapper.getInstance();
      bootstrap.bootstrap();

      if (resourceExists("seam.properties")) 
      {
         bootstrap.deployResourceBases("seam.properties");
      }
      if (resourceExists("META-INF/components.xml")) 
      {
         bootstrap.deployResourceBases("META-INF/components.xml");
      }
      if (resourceExists("META-INF/seam.properties")) 
      {
         bootstrap.deployResourceBases("META-INF/seam.properties");
      }
   }

   private boolean resourceExists(String name)
   {
      return Thread.currentThread().getContextClassLoader().getResource(name)!=null;
   }
}
