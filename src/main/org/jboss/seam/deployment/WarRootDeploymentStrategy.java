package org.jboss.seam.deployment;

import java.io.File;
import java.util.Set;

import org.jboss.seam.log.LogProvider;
import org.jboss.seam.log.Logging;

/**
 * A special deployment strategy that can be used to scan the war root. This
 * is treated as a special case. 
 *
 * @author pmuir
 *
 */
public class WarRootDeploymentStrategy extends DeploymentStrategy
{
   
   private static LogProvider log = Logging.getLogProvider(WarRootDeploymentStrategy.class);

   private ClassLoader classLoader;
   
   private File[] warRoot;
   
   public static final String HANDLERS_KEY = "org.jboss.seam.deployment.deploymentHandlers";
   
   public static final String NAME = "warRootDeploymentStrategy";
   
   private DotPageDotXmlDeploymentHandler dotPageDotXmlDeploymentHandler;
   
   public WarRootDeploymentStrategy(ClassLoader classLoader, File warRoot)
   {
      this.classLoader = classLoader;
      this.warRoot = new File[1];
      if (warRoot != null)
      {
         this.warRoot[0] = warRoot;
      }
      else
      {
         log.warn("Unable to discover war root, .page.xml files won't be found");
         this.warRoot = new File[0];
      }
      dotPageDotXmlDeploymentHandler = new DotPageDotXmlDeploymentHandler();
      getDeploymentHandlers().put(DotPageDotXmlDeploymentHandler.NAME, dotPageDotXmlDeploymentHandler);
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
   
   public Set<String> getDotPageDotXmlFileNames()
   {
      return dotPageDotXmlDeploymentHandler.getFiles();
   }

}
