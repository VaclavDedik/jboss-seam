package org.jboss.seam.deployment;

import static org.jboss.seam.util.Strings.split;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

import org.jboss.seam.log.LogProvider;
import org.jboss.seam.log.Logging;

/**
 * A {@link DeploymentStrategy} coordinates the deployment of resources for a Seam
 * application.
 * 
 * @author Pete Muir
 *
 */
public abstract class DeploymentStrategy
{
   
   private static final LogProvider log = Logging.getLogProvider(DeploymentStrategy.class);

   private Scanner scanner;
   
   private List<File> files = new ArrayList<File>();
   
   private Set<String> excludes = new HashSet<String>();
   private Set<String> wildCardExcludes = new HashSet<String>();
   
   private Map<String, DeploymentHandler> deploymentHandlers;
   
   /**
    * The key under which to list possible scanners. System properties take
    * precedence over /META-INF/seam-scanner.properties. Entries will be tried
    * in sequential order until a Scanner can be loaded. 
    * 
    * This can be specified as a System property or in 
    * /META-INF/seam-deployment.properties
    */
   public static final String SCANNERS_KEY = "org.jboss.seam.deployment.scanners";
   
   
   
   /**
    * The resource bundle used to control Seam deployment
    */
   public static final String RESOURCE_BUNDLE = "META-INF/seam-deployment.properties";
   
   // All resource bundles to use, including legacy names
   private static final String[] RESOURCE_BUNDLES = { RESOURCE_BUNDLE, "META-INF/seam-scanner.properties" };
   
   /**
    * Get a list of possible values for a given key.
    * 
    * First, System properties are tried, followed by the specified resource
    * bundle (first in classpath only).
    * 
    * Colon (:) deliminated lists are split out.
    * 
    */
   protected List<String> getPropertyValues(String key)
   {
      List<String>values = new ArrayList<String>();
      addPropertyFromSystem(key, values);
      addPropertyFromResourceBundle(key, values);
      return values;
   }
   
   private void addPropertyFromSystem(String key, List<String> values)
   {
      addProperty(key, System.getProperty(key), values);
   }
   
   private void addPropertyFromResourceBundle(String key, List<String> values)
   {
      for (String resourceName : RESOURCE_BUNDLES)
      {
         try
         {
            // Hard to cache as we have to get it off the correct classloader
            Enumeration<URL> urlEnum = getClassLoader().getResources(resourceName);
            while ( urlEnum.hasMoreElements() )
            {
               URL url = urlEnum.nextElement();
               Properties properties = new Properties();
               properties.load(url.openStream());
               addProperty(key, properties.getProperty(key), values);
            }
         }
         catch (IOException e)
         {
            // No-op, optional file
         }
      }
   }
   
   /*
    * Add the property to the set of properties only if it hasn't already been added
    */
   private void addProperty(String key, String value, List<String> values)
   {
      if (value != null)
      {
         String[] properties = split(value, ":");
         for (String property : properties)
         {
            values.add(property);
         }
         
      }
   }
   
   /**
    * Do the scan for resources
    * 
    * Should only be called by Seam
    * 
    */
   public abstract void scan();
   
   /**
    * Get the scanner being used
    * 
    */
   protected Scanner getScanner()
   {
      if (scanner == null)
      {
         initScanner();
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
      if (deploymentHandlers == null)
      {
         initDeploymentHandlers();
      }
      return this.deploymentHandlers;
   }
   
   private void initDeploymentHandlers()
   {
      this.deploymentHandlers = new HashMap<String, DeploymentHandler>();
      addHandlers(getPropertyValues(getDeploymentHandlersKey()));
   }

   protected abstract String getDeploymentHandlersKey();
   
   /**
    * Handle a resource using any registered {@link DeploymentHandler}s
    * 
    * @param name Path to a resource to handle
    */
   public void handle(String name)
   {
      for (String exclude: excludes)
      {
         if (name.equals(exclude)) 
         {
            return;
         }
      }
      for (String exclude: wildCardExcludes)
      {
         if (name.startsWith(exclude))
         {
            return;
         }
      }
      for (String key: getDeploymentHandlers().keySet())
      {
         getDeploymentHandlers().get(key).handle(name, getClassLoader());
      }
   }
      
   private void initScanner()
   {
      List<String> scanners = getPropertyValues(SCANNERS_KEY);
      for ( String className : scanners )
      {
         Scanner scanner = instantiateScanner(className);
         if (scanner != null)
         {
            log.debug("Using " + scanner.toString());
            this.scanner = scanner;
            return;
         }        
      }
      log.debug("Using default URLScanner");
      this.scanner = new URLScanner(this);
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
   
   private void addHandlers(List<String> handlers)
   {
      for (String handler : handlers)
      {
         addHandler(handler);
      }
   }
   
   private void addHandler(String className)
   {
      DeploymentHandler deploymentHandler = instantiateDeploymentHandler(className);
      if (deploymentHandler != null)
      {
         log.debug("Adding " + deploymentHandler + " as a deployment handler");
         deploymentHandlers.put(deploymentHandler.getName(), deploymentHandler);
      }
   }
   
   private DeploymentHandler instantiateDeploymentHandler(String className)
   {
      try
      {
         Class<DeploymentHandler> clazz = (Class<DeploymentHandler>) getClassLoader().loadClass(className);
         return clazz.newInstance();
      }
      catch (ClassNotFoundException e)
      {
         log.trace("Unable to use " + className + " as a deployment handler (class not found)", e);
      }
      catch (NoClassDefFoundError e) 
      {
         log.trace("Unable to use " + className + " as a deployment handler (dependency not found)", e);
      }
      catch (InstantiationException e)
      {
         log.trace("Unable to instantiate deployment handler " + className, e);
      }
      catch (IllegalAccessException e)
      {
         log.trace("Unable to instantiate deployment handler " + className, e);
      }
      return null;
   }

   public List<File> getFiles()
   {
      return files;
   }
   
   public void setFiles(List<File> files)
   {
      this.files = files;
   }
   
   public void addExclude(String path)
   {
      if (path == null)
      {
         throw new NullPointerException("Cannot exclude a null path");
      }
      if (path.endsWith("*"))
      {
         wildCardExcludes.add(path.substring(0, path.length() - 1));
      }
      else
      {
         excludes.add(path);
      }
   }
}
