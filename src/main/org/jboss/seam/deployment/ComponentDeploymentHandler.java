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
 * The {@link ComponentDeploymentHandler} process Seam's component annotated 
 * with {@link org.jboss.seam.annotations.Name} 
 *  
 * @author Pete Muir
 *
 */
public class ComponentDeploymentHandler extends AbstractDeploymentHandler
{
   /**
    * Name under which this {@link DeploymentHandler} is registered
    */
   public static final String NAME = "org.jboss.seam.deployment.ComponentDeploymentHandler";
   
   private static final LogProvider log = Logging.getLogProvider(ComponentDeploymentHandler.class);

   protected Set<Class<Object>> classes;
   
   public ComponentDeploymentHandler()
   {
      classes = new HashSet<Class<Object>>();
   }

   /**
    * Get annotated Seam components
    */
   public Set<Class<Object>> getClasses()
   {
      return Collections.unmodifiableSet(classes);
   }

   /**
    * @see DeploymentHandler#handle(String, ClassLoader)
    */
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
   }
  
   private static String componentFilename(String name)
   {
      return name.substring( 0, name.lastIndexOf(".class") ) + ".component.xml";
   }
   
   public String getName()
   {
      return NAME;
   }
   
}
