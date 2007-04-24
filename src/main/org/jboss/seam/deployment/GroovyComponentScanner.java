//$Id$
package org.jboss.seam.deployment;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLClassLoader;

import groovy.lang.GroovyClassLoader;
import groovy.lang.GroovyRuntimeException;
import org.codehaus.groovy.control.CompilationFailedException;
import org.jboss.seam.annotations.Install;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.log.LogProvider;
import org.jboss.seam.log.Logging;

/**
 * Scan Groovy files as well as Java files
 * <p/>
 * TODO: make it so that you can plug any Scripting scanner
 *
 * @author Emmanuel Bernard
 */
public class GroovyComponentScanner extends ComponentScanner
{
   private static final LogProvider log = Logging.getLogProvider(GroovyComponentScanner.class);
   private final String groovyFileExtension;

   public GroovyComponentScanner(String resourceName, String groovyFileExtension)
   {
      super(resourceName);
      this.groovyFileExtension = groovyFileExtension;
   }

   //has to be a GroovyClassLoader see #getURLsFromClassLoader
   public GroovyComponentScanner(String resourceName, GroovyClassLoader classLoader, String groovyFileExtension)
   {
      super(resourceName, classLoader);
      this.groovyFileExtension = groovyFileExtension;
   }

   @Override
   protected URL[] getURLsFromClassLoader()
   {
      /*
       * a GroovyClassLoader accept no URL,
       * use the parent URL
       */
      return ( (URLClassLoader) classLoader.getParent() ).getURLs();
   }

   @Override
   protected void handleItem(String name)
   {
      //handle java classes
      super.handleItem(name);

      if (name.endsWith(groovyFileExtension))
      {
         log.debug("Found a groovy file: " + name);
         String classname = filenameToGroovyname(name);
         String filename = groovyComponentFilename(name);
         BufferedReader buffReader = null;
         try
         {
            InputStream stream = classLoader.getResourceAsStream(name);
            //TODO is BufferedInputStream necessary?
            buffReader = new BufferedReader(new InputStreamReader(stream));
            String line = buffReader.readLine();
            while (line != null)
            {
               if (line.indexOf("@Name") != -1 || line.indexOf("@" + Name.class.getName()) != -1)
               {
                  //possibly a Seam component
                  log.debug("Groovy file possibly a Seam component: " + name);
                  Class<Object> groovyClass = (Class<Object>) classLoader.loadClass(classname);
                  Install install = groovyClass.getAnnotation(Install.class);
                  boolean installable = ( install == null || install.value() )
                        && ( groovyClass.isAnnotationPresent(Name.class)
                           || classLoader.getResources(filename).hasMoreElements() );
                  if (installable)
                  {
                     log.debug("found groovy component class: " + name);
                     classes.add(groovyClass);
                  }
                  break;
               }
               line = buffReader.readLine();
            }
         }
         catch (ClassNotFoundException cnfe)
         {
            log.debug("could not load groovy class: " + classname, cnfe);

         }
         catch (NoClassDefFoundError ncdfe)
         {
            log.debug("could not load groovy class (missing dependency): " + classname, ncdfe);

         }
         catch (IOException ioe)
         {
            log.debug("could not load groovy file: " + classname, ioe);
         }
         catch( CompilationFailedException e) {
            log.info("Compilation error in Groovy file:" + classname, e);
         }
         catch(GroovyRuntimeException e) {
            log.info("Unknown error reading Groovy file:" + classname, e);
         }
         finally
         {
            if (buffReader != null) {
               try
               {
                  buffReader.close();
               }
               catch (IOException e)
               {
                  log.trace("Could not close stream");
               }
            }
         }
      }
   }

   public String filenameToGroovyname(String filename)
   {
      return filename.substring(0, filename.lastIndexOf(groovyFileExtension))
            .replace('/', '.').replace('\\', '.');
   }

   public String groovyComponentFilename(String name)
   {
      return name.substring(0, name.lastIndexOf(groovyFileExtension)) + ".component.xml";
   }
}
