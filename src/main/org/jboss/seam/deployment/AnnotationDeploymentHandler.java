package org.jboss.seam.deployment;

import java.io.IOException;
import java.lang.annotation.Annotation;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javassist.bytecode.ClassFile;
import org.jboss.seam.log.LogProvider;
import org.jboss.seam.log.Logging;

public class AnnotationDeploymentHandler extends AbstractDeploymentHandler
{

   /**
    * Name under which this {@link DeploymentHandler} is registered
    */
   public static final String NAME = "org.jboss.seam.deployment.AnnotationDeploymentHandler";
   
   public static final String ANNOTATIONS_KEY = "org.jboss.seam.deployment.annotationTypes";
   
   private Map<String, Set<Class<Object>>> classes;
   private Set<Class<? extends Annotation>> annotations;
   
   private static final LogProvider log = Logging.getLogProvider(AnnotationDeploymentHandler.class);
   
   public AnnotationDeploymentHandler(List<String> annotationTypes, ClassLoader classLoader)
   {
      annotations = new HashSet<Class<? extends Annotation>>();
      for (String classname: annotationTypes)
      {
         try
         {
            annotations.add((Class<? extends Annotation>) classLoader.loadClass(classname));
         }
         catch (ClassNotFoundException cnfe) 
         {
            log.warn("could not load annotation class: " + classname, cnfe);
         }
         catch (NoClassDefFoundError ncdfe) 
         {
            log.warn("could not load annotation class (missing dependency): " + classname, ncdfe);
         }
         catch (ClassCastException cce)
         {
            log.warn("could not load annotation class (not an annotation): " + classname, cce);
         }
      }
      
      classes = new HashMap<String, Set<Class<Object>>>();
      for (Class annotation: annotations)
      {
         classes.put(annotation.getName(), new HashSet<Class<Object>>());
      }
   }

   /**
    * Get annotated classes
    */
   public Map<String, Set<Class<Object>>> getClasses()
   {
      return Collections.unmodifiableMap(classes);
   }
   
   
   public String getName()
   {
      return NAME;
   }

   public void handle(String name, ClassLoader classLoader)
   {
      if (name.endsWith(".class")) 
      {
         String classname = filenameToClassname(name);
         try 
         {
            ClassFile classFile = getClassFile(name, classLoader);
            Class clazz = null;
            for (Class<? extends Annotation> annotationType: annotations)
            {
               if (hasAnnotation(classFile, annotationType)) 
               {
                  log.trace("found class annotated with " + annotationType + ": " + name);
                  if (clazz == null)
                  {
                     try
                     {
                        clazz = classLoader.loadClass(classname);
                     }
                     catch (ClassNotFoundException cnfe) 
                     {
                        log.debug("could not load class: " + classname, cnfe);
                     }
                     catch (NoClassDefFoundError ncdfe) 
                     {
                        log.debug("could not load class (missing dependency): " + classname, ncdfe);
                     }
                  }
                  classes.get(annotationType.getName()).add( clazz );
               }
            }
         }
         catch (IOException ioe) 
         {
            log.debug("could not load classfile: " + classname, ioe);
         }
      }

   }

}
