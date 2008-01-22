package org.jboss.seam.deployment;

import java.io.File;
import java.lang.reflect.Constructor;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.Set;

import org.jboss.seam.util.Reflections;

/**
 * A deployment strategy for hot deployable Java Seam components
 * 
 * @author Pete Muir
 *
 */
public class HotDeploymentStrategy extends DeploymentStrategy
{
   /**
    * The default path at which hot deployable Seam components are placed
    */
   public static final String HOT_DEPLOYMENT_DIRECTORY_PATH = "WEB-INF/dev";
   
   private ClassLoader hotDeployClassLoader;
   
   private File[] hotDeploymentPaths;
   
   private ComponentDeploymentHandler componentDeploymentHandler;
   
   /**
    * @param classLoader The parent classloader of the hot deployment classloader
    * @param hotDeployDirectory The directory in which hot deployable Seam 
    * components are placed
    */
   public HotDeploymentStrategy(ClassLoader classLoader, File hotDeployDirectory)
   {
      initHotDeployClassLoader(classLoader, hotDeployDirectory);
      componentDeploymentHandler = new ComponentDeploymentHandler();
      getDeploymentHandlers().put(ComponentDeploymentHandler.NAME, componentDeploymentHandler);
   }
   
   private void initHotDeployClassLoader(ClassLoader classLoader, File hotDeployDirectory)
   {
      try
      {
         if (hotDeployDirectory.exists())
         {
            URL url = hotDeployDirectory.toURL();
            URL[] urls = { url };
            hotDeployClassLoader = new URLClassLoader(urls, classLoader);
            hotDeploymentPaths = new File[] { hotDeployDirectory };
         }

      }
      catch (MalformedURLException mue)
      {
         throw new RuntimeException(mue);
      }
   }

   /**
    * Get all hot deployable paths
    */
   public File[] getHotDeploymentPaths()
   {
      return hotDeploymentPaths;
   }

   /**
    * Return true if the component is from a hot deployment classloader
    */
   public boolean isFromHotDeployClassLoader(Class componentClass)
   {
      return componentClass.getClassLoader() == hotDeployClassLoader;
   }

   /**
    * Dynamically instantiate a {@link HotDeploymentStrategy}
    * 
    * Needed to prevent dependency on optional librarires
    * @param className The strategy to use 
    * @param classLoader The classloader to use with this strategy
    * @param hotDeployDirectory The directory which contains hot deployable
    * Seam components
    */
   public static HotDeploymentStrategy createInstance(String className, ClassLoader classLoader, File hotDeployDirectory)
   {
      try
      {
         Class initializer = Reflections.classForName(className);
         Constructor ctr = initializer.getConstructor(ClassLoader.class, File.class);
         return (HotDeploymentStrategy) ctr.newInstance(classLoader, hotDeployDirectory);
      }
      catch (Exception e)
      {
         throw new IllegalArgumentException("No such deployment strategy " + className, e);
      }
   }

   @Override
   public ClassLoader getClassLoader()
   {
      return hotDeployClassLoader;
   }
   
   /**
    * Get all Components which the strategy has scanned and handled
    */
   public Set<Class<Object>> getScannedComponentClasses()
   {
      return componentDeploymentHandler.getClasses();
   }

   @Override
   public void scan()
   {
      getScanner().scanDirectories(getHotDeploymentPaths());
      
   }
}
