package org.jboss.seam.deployment;

import java.util.Set;

/**
 * The standard deployment strategy used with Seam, deploys non-hot-deployable
 * Seam components and namespaces
 * 
 * @author Pete Muir
 *
 */
public class StandardDeploymentStrategy extends DeploymentStrategy
{

   private ClassLoader classLoader;
   
   public static final String[] RESOURCE_NAMES = {"seam.properties", "META-INF/seam.properties", "META-INF/components.xml"};

   private ComponentDeploymentHandler componentDeploymentHandler;
   private NamespaceDeploymentHandler namespaceDeploymentHandler;
   
   /**
    * @param classLoader The classloader used to load and handle resources
    */
   public StandardDeploymentStrategy(ClassLoader classLoader)
   {
      this.classLoader = Thread.currentThread().getContextClassLoader();
      componentDeploymentHandler = new ComponentDeploymentHandler();
      getDeploymentHandlers().put(ComponentDeploymentHandler.NAME, componentDeploymentHandler);
      namespaceDeploymentHandler = new NamespaceDeploymentHandler();
      getDeploymentHandlers().put(NamespaceDeploymentHandler.NAME, namespaceDeploymentHandler);
   }

   @Override
   public ClassLoader getClassLoader()
   {
      return classLoader;
   }

   /**
    * Get all scanned and handled annotated components known to this strategy
    */
   public Set<Class<Object>> getScannedComponentClasses()
   {
      return componentDeploymentHandler.getClasses();
   }
   
   /**
    * Get all scanned and handled components.xml files
    */
   public Set<String> getScannedComponentResources()
   {
      return componentDeploymentHandler.getResources();
   }
   
   /**
    * Get all scanned and handled Seam namespaces
    */
   public Set<Package> getScannedNamespaces()
   {
      return namespaceDeploymentHandler.getPackages();
   }
   
   @Override
   public void scan()
   {
      getScanner().scanResources(RESOURCE_NAMES);
   }
   
}
