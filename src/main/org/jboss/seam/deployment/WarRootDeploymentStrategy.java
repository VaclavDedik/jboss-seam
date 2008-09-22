package org.jboss.seam.deployment;

import java.io.File;

/**
 * A special deployment strategy that can be used to scan the war root. This
 * is treated as a special case. 
 *
 * @author pmuir
 *
 */
public class WarRootDeploymentStrategy extends DeploymentStrategy
{

   private ClassLoader classLoader;
   
   private File[] warRoot;
   
   public static final String HANDLERS_KEY = "org.jboss.seam.deployment.deploymentHandlers";
   
   public static final String NAME = "warRootDeploymentStrategy";
   
   public WarRootDeploymentStrategy(ClassLoader classLoader, File warRoot)
   {
      this.classLoader = classLoader;
      this.warRoot = new File[1];
      this.warRoot[0] = warRoot; 
      getDeploymentHandlers().put(DotPageDotXmlDeploymentHandler.NAME, new DotPageDotXmlDeploymentHandler());
   }
   
   @Override
   public ClassLoader getClassLoader()
   {
      return classLoader;
   }

   @Override
   protected String getDeploymentHandlersKey()
   {
      return HANDLERS_KEY;
   }

   @Override
   public void handle(String name)
   {
      if (!(name.startsWith("WEB-INF") || name.startsWith("/WEB-INF")))
      {
         super.handle(name);
      }
   }
   
   @Override
   public void scan()
   {
      getScanner().scanDirectories(warRoot);
   }

}
