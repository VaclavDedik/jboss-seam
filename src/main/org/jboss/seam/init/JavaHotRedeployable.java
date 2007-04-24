//$Id$
package org.jboss.seam.init;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;

import org.jboss.seam.deployment.ComponentScanner;

/**
 * Hot redeployment of Java classes
 *
 * @author Emmanuel Bernard
 */
public class JavaHotRedeployable implements RedeployableStrategy
{

   protected File[] paths;
   protected ClassLoader classLoader;

   public JavaHotRedeployable(URL resource)
   {
      try
      {
         String path = resource.toExternalForm();
         String hotDeployDirectory = path.substring(9, path.length() - 46) + "dev";
         File directory = new File(hotDeployDirectory);
         if (directory.exists())
         {
            URL url = directory.toURL();
            /*File[] jars = directory.listFiles( new FilenameFilter() {
                  public boolean accept(File file, String name) { return name.endsWith(".jar"); }
            } );
            URL[] urls = new URL[jars.length];
            for (int i=0; i<jars.length; i++)
            {
               urls[i] = jars[i].toURL();
            }*/
            URL[] urls = { url };
            classLoader = new URLClassLoader(urls, Thread.currentThread().getContextClassLoader());
            paths = new File[] { directory };
         }

      }
      catch (MalformedURLException mue)
      {
         throw new RuntimeException(mue);
      }
   }

   public ClassLoader getClassLoader()
   {
      return classLoader;
   }

   public File[] getPaths()
   {
      return paths;
   }

   public ComponentScanner getScanner()
   {
      //no classloader means we did not find the path
      return classLoader != null ? new ComponentScanner(null, classLoader) : null;
   }

   public boolean isFromHotDeployClassLoader(Class componentClass)
   {
      return componentClass.getClassLoader() == classLoader;
   }
}
