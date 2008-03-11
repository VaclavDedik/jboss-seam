package org.jboss.seam.deployment;

import java.lang.annotation.Annotation;
import java.util.Map;
import java.util.Set;

import org.jboss.seam.contexts.Contexts;

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
   
   /**
    * The files used to identify a Seam archive
    */
   public static final String[] RESOURCE_NAMES = {"seam.properties", "META-INF/seam.properties", "META-INF/components.xml"};
   
   /**
    * The contextual variable name this deployment strategy is made available at
    * during Seam startup.
    */
   public static final String NAME = "deploymentStrategy";
   
   /**
    * The key under which to list extra deployment handlers.
    * 
    * This can be specified as a System property or in 
    * /META-INF/seam-deployment.properties
    */
   public static final String HANDLERS_KEY = "org.jboss.seam.deployment.deploymentHandlers";

   private ComponentDeploymentHandler componentDeploymentHandler;
   private NamespaceDeploymentHandler namespaceDeploymentHandler;
   private AnnotationDeploymentHandler annotationDeploymentHandler;
   
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
      annotationDeploymentHandler = new AnnotationDeploymentHandler(getPropertyValues(AnnotationDeploymentHandler.ANNOTATIONS_KEY), classLoader);
      getDeploymentHandlers().put(AnnotationDeploymentHandler.NAME, annotationDeploymentHandler);
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
   
   public Map<String, Set<Class<Object>>> getAnnotatedClasses()
   {
      return annotationDeploymentHandler.getClasses();
   }
   
   @Override
   public void scan()
   {
      getScanner().scanResources(RESOURCE_NAMES);
   }
   
   public static StandardDeploymentStrategy instance()
   {
      if (Contexts.getEventContext().isSet(NAME))
      {
         return (StandardDeploymentStrategy) Contexts.getEventContext().get(NAME);
      }
      return null;
   }
}
