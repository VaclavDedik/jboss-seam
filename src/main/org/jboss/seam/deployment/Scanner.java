//$Id$
package org.jboss.seam.deployment;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.Set;
import java.util.zip.ZipEntry;
import java.util.zip.ZipException;
import java.util.zip.ZipFile;

public class Scanner
{
   private String resourceName;
   private ClassLoader classLoader;
   
   public Scanner()
   {
      this("seam.properties", Thread.currentThread().getContextClassLoader());
   }
   
   public Scanner(String resourceName, ClassLoader classLoader)
   {
      this.resourceName = resourceName;
      this.classLoader = classLoader;      
   }
   
   public static String filenameToClassname(String filename)
   {
      return filename.substring(0, filename.lastIndexOf(".class"))
            .replace('/', '.').replace('\\', '.');
   }
   
   public Set<Class<?>> getClasses()
   {
      System.out.println("deploying resource: " + resourceName);
      Set<Class<?>> result = new HashSet<Class<?>>();
      Enumeration<URL> urls;
      try
      {
         urls = classLoader.getResources(resourceName);
      }
      catch (IOException ioe) {
         return result;
      }
      
      while (urls.hasMoreElements())
      {
         try
         {
            URL url = urls.nextElement();
            String urlPath = url.getFile();
            System.out.println("scanning: " + urlPath);
            if ( urlPath.startsWith("file:") )
            {
               urlPath = urlPath.substring(6);
            }
            if ( urlPath.indexOf('!')>0 )
            {
               urlPath = urlPath.substring(0, urlPath.indexOf('!'));
            }
            else
            {
               urlPath = new File(urlPath).getParent();
            }
            System.out.println("scanning: " + urlPath);
            File file = new File(urlPath);
            if ( file.isDirectory() )
            {
               handleDirectory(result, file, null);
            }
            else
            {
               handleArchive(result, file);
            }
         }
         catch (IOException ioe) {
            ioe.printStackTrace();
         }
      }
      return result;
   }

   private void handleArchive(Set<Class<?>> result, File file) throws ZipException, IOException
   {
      System.out.println("archive: " + file);
      ZipFile zip = new ZipFile(file);
      Enumeration<? extends ZipEntry> entries = zip.entries();
      while ( entries.hasMoreElements() )
      {
         ZipEntry entry = entries.nextElement();
         String name = entry.getName();
         System.out.println("found: " + name);
         handleItem(result, name);
      }
   }

   private void handleDirectory(Set<Class<?>> result, File file, String path)
   {
      System.out.println("directory: " + file);
      for ( File child: file.listFiles() )
      {
         String newPath = path==null ? 
                  child.getName() : path + '/' + child.getName();
         if ( child.isDirectory() )
         {
            handleDirectory( result, child, newPath );
         }
         else
         {
            handleItem( result, newPath );
         }
      }
   }

   private void handleItem(Set<Class<?>> result, String name)
   {
      if ( name.endsWith(".class") && !name.startsWith("org/jboss/seam/core") )
      {
         String classname = filenameToClassname( name );
         try
         {
            result.add( classLoader.loadClass( classname ) );
         }
         catch (ClassNotFoundException cnfe) {
            cnfe.printStackTrace();
         }
      }
   }
}
