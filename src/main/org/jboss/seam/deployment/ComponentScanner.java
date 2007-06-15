package org.jboss.seam.deployment;

import org.jboss.seam.annotations.Install;
import org.jboss.seam.annotations.Name;

import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

import javassist.bytecode.ClassFile;

import org.jboss.seam.log.LogProvider;
import org.jboss.seam.log.Logging;

public class ComponentScanner extends Scanner
{
   private static final LogProvider log = Logging.getLogProvider(ComponentScanner.class);

   protected Set<Class<Object>> classes;

   private Set<String> resources;

   public ComponentScanner(String resourceName)
   {
      super(resourceName);
   }

   public ComponentScanner(String resourceName, ClassLoader classLoader)
   {
      super(resourceName, classLoader);
   }

   /**
    * Returns only Seam components (ie: classes annotated with @Name)
    */
   public Set<Class<Object>> getClasses()
   {
      if (classes == null) {
         classes = new HashSet<Class<Object>>();
         resources = new HashSet<String>();
         scan();
      }
      return classes;
   }
   
   public Set<String> getResources() {
       return resources;
   }

   @Override
   @SuppressWarnings("unchecked")
   protected void handleItem(String name)
   {
      if (name.endsWith(".class")) {
         String classname = filenameToClassname(name);
         String filename = Scanner.componentFilename(name);
         try {
            ClassFile classFile = getClassFile(name);
            boolean installable = ( hasAnnotation(classFile, Name.class) || classLoader.getResources(filename).hasMoreElements() )
                     && !"false".equals( getAnnotationValue(classFile, Install.class, "value") );
            if (installable) {
               log.debug("found component class: " + name);
               classes.add( (Class<Object>) classLoader.loadClass(classname) );
            }
            
         } catch (ClassNotFoundException cnfe) {
            log.debug("could not load class: " + classname, cnfe);
         } catch (NoClassDefFoundError ncdfe) {
            log.debug("could not load class (missing dependency): " + classname, ncdfe);
         } catch (IOException ioe) {
            log.debug("could not load classfile: " + classname, ioe);
         }
      } else if (name.endsWith(".component.xml") || name.endsWith("/components.xml")) {
          // we want to skip over known meta-directories since Seam will auto-load these without a scan
          if (!name.startsWith("WEB-INF/") && !name.startsWith("META-INF/")) {
              resources.add(name);
          }           
      }
           
   }
   
   public ClassLoader getClassLoader() {
       return classLoader;
   }
}
