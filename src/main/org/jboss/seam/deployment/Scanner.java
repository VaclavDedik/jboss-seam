//$Id$
package org.jboss.seam.deployment;

import java.io.DataInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.lang.annotation.Annotation;
import java.net.URL;
import java.net.URLClassLoader;
import java.net.URLDecoder;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.Set;
import java.util.zip.ZipEntry;
import java.util.zip.ZipException;
import java.util.zip.ZipFile;

import javassist.bytecode.AnnotationsAttribute;
import javassist.bytecode.ClassFile;
import javassist.bytecode.annotation.MemberValue;

import org.jboss.seam.log.LogProvider;
import org.jboss.seam.log.Logging;

public abstract class Scanner
{
    private static final LogProvider log = Logging.getLogProvider(Scanner.class);

    protected String resourceName;
    protected ClassLoader classLoader;
   
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
   
   public static String filenameToClassname(String filename)
   {
      return filename.substring( 0, filename.lastIndexOf(".class") )
            .replace('/', '.').replace('\\', '.');
   }
   

    protected void scan() 
    {
      Set<String> paths = new HashSet<String>();
      if (resourceName==null)
      {
         for ( URL url: ( (URLClassLoader) classLoader ).getURLs() )
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
                  // On windows urlpath looks like file:/C: on Linux file:/home
                  // substring(5) works for both
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
            else
            {
               handleArchive(file);
            }
         }
         catch (IOException ioe) 
         {
            log.warn("could not read entries", ioe);
         }
      }
   }


   private void handleArchive(File file) throws ZipException, IOException
   {
      log.info("archive: " + file);
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

   private void handleDirectory(File file, String path)
   {
      log.debug("directory: " + file);
      for ( File child: file.listFiles() )
      {
         String newPath = path==null ? 
                  child.getName() : path + '/' + child.getName();
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
