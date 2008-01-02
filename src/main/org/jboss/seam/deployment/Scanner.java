//$Id$
package org.jboss.seam.deployment;

import java.io.DataInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.net.URL;
import java.net.URLClassLoader;
import java.net.URLDecoder;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.Set;
import java.util.jar.JarInputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

import javassist.bytecode.AnnotationsAttribute;
import javassist.bytecode.ClassFile;
import javassist.bytecode.annotation.MemberValue;
import org.jboss.seam.log.LogProvider;
import org.jboss.seam.log.Logging;

/**
 * Abstract class for scanning archives in the
 * Seam classpath.
 * 
 * @author Thomas Heute
 * @author Gavin King
 * @author Norman Richards
 *
 */
public abstract class Scanner
{
   private static final LogProvider log = Logging.getLogProvider(Scanner.class);

   protected String resourceName;
   protected ClassLoader classLoader;
   protected static Boolean useVFS;

   public Scanner(String resourceName)
   {
      this( resourceName, Thread.currentThread().getContextClassLoader() );
   }
   
   public Scanner(String resourceName, ClassLoader classLoader)
   {
      this.resourceName = resourceName;
      this.classLoader = classLoader;
      ClassFile.class.getPackage(); //to force loading of javassist, throwing an exception if it is missing
   }

   protected static boolean useVFS()
   {
      if (useVFS == null)
      {
         try
         {
            Class.forName("org.jboss.virtual.VFS");
            // OK, we have VFS
            // but are we in JBoss5 and also not using Embedded
            useVFS = isJBoss5() && !isEmbedded();
            if (useVFS)
               log.info("Using JBoss VFS for scanning.");
         }
         catch(Throwable t)
         {
            useVFS = false;
            log.debug("Using default file utils for scanning.");
         }
      }
      return useVFS;
   }

   protected static boolean isJBoss5()
   {
      try
      {
         Class versionClass = Class.forName("org.jboss.Version");
         Method getVersionInstance = versionClass.getMethod("getInstance");
         Object versionInstance = getVersionInstance.invoke(null);
         Method getMajor = versionClass.getMethod("getMajor");
         Object major = getMajor.invoke(versionInstance);
         return major != null && major.equals(5);
      }
      catch (Exception e)
      {
         return false;
      }
   }

   protected static boolean isEmbedded()
   {
      try
      {
         Class.forName("org.jboss.embedded.Bootstrap");
         if (log.isTraceEnabled())
            log.trace("Using JBoss Embedded.");
         return true;
      }
      catch(Exception e)
      {
         return false;
      }
   }

   public static String filenameToClassname(String filename)
   {
      return filename.substring( 0, filename.lastIndexOf(".class") )
            .replace('/', '.').replace('\\', '.');
   }
   
   public static String filenameToPackage(String filename)
   {
      return filename.substring( 0, filename.lastIndexOf(".class") )
            .replace('/', '.').replace('\\', '.');
   }

    protected void scan() 
    {
      Set<String> paths = new HashSet<String>();
      if (resourceName==null)
      {
         for ( URL url: getURLsFromClassLoader() )
         {
            String urlPath = url.getFile();
            if ( urlPath.endsWith("/") )
            {
               urlPath = urlPath.substring( 0, urlPath.length()-1 );
            }
            paths.add( urlPath );
         }
      }
      else
      {
         try
         {
            Enumeration<URL> urlEnum = classLoader.getResources(resourceName);
            while ( urlEnum.hasMoreElements() )
            {
               String urlPath = urlEnum.nextElement().getFile();
               urlPath = URLDecoder.decode(urlPath, "UTF-8");
               if ( urlPath.startsWith("file:") )
               {
                  if (useVFS())
                     urlPath = "vfs" + urlPath;
                  else
                     urlPath = urlPath.substring(5);
               }
               if ( urlPath.indexOf('!')>0 )
               {
                  urlPath = urlPath.substring(0, urlPath.indexOf('!'));
               }
               else
               {
                  File dirOrArchive = new File(urlPath);
                  if ( resourceName!=null && resourceName.lastIndexOf('/')>0 )
                  {
                     //for META-INF/components.xml
                     dirOrArchive = dirOrArchive.getParentFile();
                  }
                  urlPath = dirOrArchive.getParent();
               }
               paths.add(urlPath);
            }
         }
         catch (IOException ioe) 
         {
            log.warn("could not read: " + resourceName, ioe);
            return;
         }
      }
      
      for ( String urlPath: paths )
      {
         try
         {
            log.info("scanning: " + urlPath);
            File file = new File(urlPath);
            if ( file.isDirectory() )
            {
               handleDirectory(file, null);
            }
            else if (useVFS())
            {
               URL url = new URL(urlPath);
               handleArchiveByURL(url);
            }
            else
            {
               handleArchiveByFile(file);
            }
         }
         catch (IOException ioe) 
         {
            log.warn("could not read entries", ioe);
         }
      }
   }

   protected URL[] getURLsFromClassLoader()
   {
      return ( (URLClassLoader) classLoader ).getURLs();
   }


   private void handleArchiveByFile(File file) throws IOException
   {
      log.debug("archive: " + file);
      ZipFile zip = new ZipFile(file);
      Enumeration<? extends ZipEntry> entries = zip.entries();
      while ( entries.hasMoreElements() )
      {
         ZipEntry entry = entries.nextElement();
         String name = entry.getName();
         log.debug("found: " + name);
         handleItem(name);
      }
   }

   private void handleArchiveByURL(URL url) throws IOException
   {
      log.debug("archive: " + url);
      JarInputStream inputStream = new JarInputStream(url.openStream());
      try
      {
         ZipEntry entry = inputStream.getNextEntry();
         while ( entry != null )
         {
            String name = entry.getName();
            log.debug("found: " + name);
            handleItem(name);
            entry = inputStream.getNextEntry();
         }
      }
      finally
      {
         inputStream.close();
      }
   }

   private void handleDirectory(File file, String path)
   {
      log.debug("directory: " + file);
      for ( File child: file.listFiles() )
      {
         String newPath = path==null ? child.getName() : path + '/' + child.getName();
         if ( child.isDirectory() )
         {
            handleDirectory(child, newPath);
         }
         else
         {
            handleItem(newPath);
         }
      }
   }

   abstract void handleItem(String name);

   protected ClassFile getClassFile(String name) throws IOException 
   {
      InputStream stream = classLoader.getResourceAsStream(name);
      DataInputStream dstream = new DataInputStream(stream); 

      try 
      { 
         return new ClassFile(dstream); 
      } 
      finally 
      { 
         dstream.close(); 
         stream.close(); 
      }
   }
   
   protected boolean hasAnnotation(ClassFile cf, Class<? extends Annotation> annotationType)
   { 
      AnnotationsAttribute visible = (AnnotationsAttribute) cf.getAttribute( AnnotationsAttribute.visibleTag ); 
      if ( visible != null ) 
      {
         return visible.getAnnotation( annotationType.getName() ) != null; 
      } 
      return false; 
   }

   protected String getAnnotationValue(ClassFile cf, Class<? extends Annotation> annotationType, String memberName)
   { 
      AnnotationsAttribute visible = (AnnotationsAttribute) cf.getAttribute( AnnotationsAttribute.visibleTag ); 
      if ( visible != null ) 
      {
         javassist.bytecode.annotation.Annotation annotation = visible.getAnnotation( annotationType.getName() );
         if (annotation==null)
         {
            return null;
         }
         else
         {
            MemberValue memberValue = annotation.getMemberValue(memberName);
            return memberValue==null ? null : memberValue.toString(); //TODO: toString() here is probably Bad ;-)
         }
      }
      else
      {
         return null;
      }
   }

   public static String componentFilename(String name)
   {
      return name.substring( 0, name.lastIndexOf(".class") ) + ".component.xml";
   }

}
