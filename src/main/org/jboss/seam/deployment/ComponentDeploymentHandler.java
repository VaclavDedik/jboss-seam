package org.jboss.seam.deployment;

import java.io.IOException;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import javassist.bytecode.ClassFile;

import org.jboss.seam.annotations.Install;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.log.LogProvider;
import org.jboss.seam.log.Logging;

/**
 * @author Pete Muir
 *
 */
public class ComponentDeploymentHandler extends AbstractDeploymentHandler
{
   
   public static final String NAME = "org.jboss.seam.deployment.ComponentDeploymentHandler";
   
   private static final LogProvider log = Logging.getLogProvider(ComponentDeploymentHandler.class);

   protected Set<Class<Object>> classes;

   private Set<String> resources;
   
   public ComponentDeploymentHandler()
   {
      classes = new HashSet<Class<Object>>();
      resources = new HashSet<String>();
   }

   /**
    * Returns only Seam components (ie: classes annotated with @Name)
    */
   public Set<Class<Object>> getClasses()
   {
      return Collections.unmodifiableSet(classes);
   }
   
   public Set<String> getResources() 
   {
       return Collections.unmodifiableSet(resources);
   }

   public void handle(String name, ClassLoader classLoader)
   {
      if (name.endsWith(".class")) 
      {
         String classname = filenameToClassname(name);
         String filename = componentFilename(name);
         try 
         {
            ClassFile classFile = getClassFile(name, classLoader);
            boolean installable = ( hasAnnotation(classFile, Name.class) || classLoader.getResources(filename).hasMoreElements() )
                     && !"false".equals( getAnnotationValue(classFile, Install.class, "value") );
            if (installable) 
            {
               log.trace("found component class: " + name);
               classes.add( (Class<Object>) classLoader.loadClass(classname) );
            }
            
         }
         catch (ClassNotFoundException cnfe) 
         {
            log.debug("could not load class: " + classname, cnfe);
         }
         catch (NoClassDefFoundError ncdfe) 
         {
            log.debug("could not load class (missing dependency): " + classname, ncdfe);
         }
         catch (IOException ioe) 
         {
            log.debug("could not load classfile: " + classname, ioe);
         }
      }
      else if (name.endsWith(".component.xml") || name.endsWith("/components.xml")) 
      {
          // we want to skip over known meta-directories since Seam will auto-load these without a scan
          if (!name.startsWith("WEB-INF/") && !name.startsWith("META-INF/")) 
          {
              resources.add(name);
          }           
      }
           
   }
  
   private static String componentFilename(String name)
   {
      return name.substring( 0, name.lastIndexOf(".class") ) + ".component.xml";
   }
   
}
