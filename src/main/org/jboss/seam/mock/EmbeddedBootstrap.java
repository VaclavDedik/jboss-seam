package org.jboss.seam.mock;

import org.jboss.embedded.Bootstrap;

public class EmbeddedBootstrap
{
    public void startAndDeployResources() 
        throws Exception
    {
       Bootstrap bootstrap = Bootstrap.getInstance();
       bootstrap.bootstrap();

       if (resourceExists("seam.properties")) {
           bootstrap.deployResourceBase("seam.properties");
       }
       if (resourceExists("META-INF/components.xml")) {
           bootstrap.deployResourceBase("META-INF/components.xml");
       }
       if (resourceExists("META-INF/seam.properties")) {
           bootstrap.deployResourceBase("META-INF/seam.properties");
       }
   }

   private boolean resourceExists(String name)
   {
      return Thread.currentThread().getContextClassLoader().getResource(name)!=null;
   }
}
