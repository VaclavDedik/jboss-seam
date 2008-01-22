package org.jboss.seam.deployment;

import static org.jboss.seam.util.Strings.split;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.MissingResourceException;
import java.util.ResourceBundle;

import org.jboss.seam.log.LogProvider;
import org.jboss.seam.log.Logging;

/**
 * A {@link DeploymentStrategy} coordinates the deploy of resources for a Seam
 * application.
 * 
 * @author Pete Muir
 *
 */
public abstract class DeploymentStrategy
{
   
   private static final LogProvider log = Logging.getLogProvider(DeploymentStrategy.class);

   private Scanner scanner;
   
   private Map<String, DeploymentHandler> deploymentHandlers;
   
   public DeploymentStrategy()
   {
      this.deploymentHandlers = new HashMap<String, DeploymentHandler>();
   }
   
   /**
    * Do the scan for resources
    * 
    */
   public abstract void scan();
   
   /**
    * Get the scanner being used
    */
   protected Scanner getScanner()
   {
      if (scanner == null)
      {
         scanner = createScanner();
      }
      return scanner;
   }
   
   /**
    * Get the classloader to use
    */
   public abstract ClassLoader getClassLoader();

   /**
    * Get (or modify) any registered {@link DeploymentHandler}s
    * 
    * Implementations of {@link DeploymentStrategy} may add default 
    * {@link DeploymentHandler}s 
    */
   public Map<String, DeploymentHandler> getDeploymentHandlers()
   {
      return this.deploymentHandlers;
   }

   /**
    * Handle a resource using any registered {@link DeploymentHandler}s
    * 
    * @param name Path to a resource to handle
    */
   public void handle(String name)
   {
      for (String key: getDeploymentHandlers().keySet())
      {
         DeploymentHandler deploymentHandler = getDeploymentHandlers().get(key);
         deploymentHandler.handle(name, getClassLoader());
      }
   }
   
   private Scanner createScanner()
   {
      Scanner scanner = getScannerFromResource();
      if (scanner == null)
      {
         scanner = getScannerFromSystemProperty();
      }
      if (scanner == null)
      {
         log.debug("Using default URLScanner");
         scanner = new URLScanner(this);
      }
      return scanner;
   }
   
   private Scanner getScannerFromSystemProperty()
   {
      String scanners = System.getProperty("org.jboss.seam.deployment.scanners");
      if (scanners != null)
      {
         log.debug("Tring to load scanner from system property");
         Scanner scanner = loadScanner(scanners);
         if (scanner != null)
         {
            log.debug("Using " + scanner.getClass().getName() + " specified in /META-INF/seam-scanner.properties");
            return scanner;
         }
      }
      return null;
   }
   
   private Scanner getScannerFromResource()
   {
      // Load scanner.properties from the classpath, try to 
      // load any scanners specified there
      try 
      {
         String scanners = ResourceBundle.getBundle("META-INF/seam-scanner", Locale.getDefault(), getClassLoader()).getString("org.jboss.seam.deployment.scanners");
         log.debug("Tring to load scanner from /META-INF/seam-scanner.properties");
         Scanner scanner = loadScanner(scanners);
         if (scanner != null)
         {
            log.debug("Using " + scanner.getClass().getName() + " specified in /META-INF/seam-scanner.properties");
            return scanner;
         }
         log.debug("Unable to load any scanner from /META-INF/seam-scanner.properties");
      }
      catch (MissingResourceException e) 
      {
         // no-op
      }
      return null;
   }
   
   private Scanner loadScanner(String scanners)
   {
      for (String className : split(scanners, ":"))
      {
         Scanner scanner = instantiateScanner(className);
         if (scanner != null)
         {         
            return scanner;
         }
      }
      return null;
   }
   
   private Scanner instantiateScanner(String className)
   {
      try
      {
         Class<Scanner> scannerClass =  (Class<Scanner>) getClassLoader().loadClass(className);
         Constructor<Scanner> constructor = scannerClass.getConstructor(new Class[] {DeploymentStrategy.class});
         return constructor.newInstance( new Object[] {this} );
      }
      catch (ClassNotFoundException e)
      {
         log.trace("Unable to use " + className + " as scanner (class not found)", e);
      }
      catch (NoClassDefFoundError e) 
      {
         log.trace("Unable to use " + className + " as scanner (dependency not found)", e);
      }
      catch (ClassCastException e) 
      {
         log.trace("Unable to use " + className + " as scanner (class does not implement org.jboss.seam.deployment.Scanner)");
      }
      catch (InstantiationException e)
      {
         log.trace("Unable to instantiate scanner " + className, e);
      }
      catch (IllegalAccessException e)
      {
         log.trace("Unable to instantiate scanner " + className, e);
      }
      catch (SecurityException e)
      {
         log.trace(className + " must declare public " + className + "( ClassLoader classLoader, String ... resourceNames )", e);
      }
      catch (NoSuchMethodException e)
      {
         log.trace(className + " must declare public " + className + "( ClassLoader classLoader, String ... resourceNames )", e);
      }
      catch (IllegalArgumentException e)
      {
         log.trace(className + " must declare public " + className + "( ClassLoader classLoader, String ... resourceNames )", e);
      }
      catch (InvocationTargetException e)
      {
         log.trace(className + " must declare public " + className + "( ClassLoader classLoader, String ... resourceNames )", e);
      }
      return null;
   }
   
}
