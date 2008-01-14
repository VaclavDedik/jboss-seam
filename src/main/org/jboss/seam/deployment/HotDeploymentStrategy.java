package org.jboss.seam.deployment;

import java.io.File;
import java.lang.reflect.Constructor;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.Set;

import org.jboss.seam.util.Reflections;

/**
 * @author Pete Muir
 *
 */
public class HotDeploymentStrategy extends DeploymentStrategy
{
   
   public static final String HOT_DEPLOYMENT_DIRECTORY_PATH = "WEB-INF/dev";
   
   private ClassLoader hotDeployClassLoader;
   
   private File[] hotDeploymentPaths;
   
   private ComponentDeploymentHandler componentDeploymentHandler;
   
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

   public File[] getHotDeploymentPaths()
   {
      return hotDeploymentPaths;
   }

   public boolean isFromHotDeployClassLoader(Class componentClass)
   {
      return componentClass.getClassLoader() == hotDeployClassLoader;
   }

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

   @Override
   public String[] getResourceNames()
   {
      return null;
   }

   @Override
   public ClassLoader getScannableClassLoader()
   {
      return getClassLoader();
   }
   
   public Set<Class<Object>> getScannedComponentClasses()
   {
      return componentDeploymentHandler.getClasses();
   }
   
   @Override
   public void scan()
   {
      getScanner().scanClassLoader();
   }
}
