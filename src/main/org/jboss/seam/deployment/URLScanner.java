package org.jboss.seam.deployment;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.net.URLClassLoader;
import java.net.URLDecoder;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.Set;
import java.util.zip.ZipEntry;
import java.util.zip.ZipException;
import java.util.zip.ZipFile;

import org.jboss.seam.log.LogProvider;
import org.jboss.seam.log.Logging;

/**
 * Implementation of {@link Scanner} which can scan a {@link URLClassLoader}
 * 
 * @author Thomas Heute
 * @author Gavin King
 * @author Norman Richards
 * @author Pete Muir
 *
 */
public class URLScanner extends AbstractScanner
{
   private static final LogProvider log = Logging.getLogProvider(URLScanner.class);
   
   private long timestamp;
   
   public URLScanner(DeploymentStrategy deploymentStrategy)
   {
      super(deploymentStrategy);
   }
   
   public void scanDirectories(File[] directories)
   {
      for (File directory : directories)
      {
         handleDirectory(directory, null);
      }
   }
   
   public void scanResources(String[] resources)
   {
      Set<String> paths = new HashSet<String>();
      for (String resourceName : resources)
      {
         try
         {
            Enumeration<URL> urlEnum = getDeploymentStrategy().getClassLoader().getResources(resourceName);
            while ( urlEnum.hasMoreElements() )
            {
               String urlPath = urlEnum.nextElement().getFile();
               urlPath = URLDecoder.decode(urlPath, "UTF-8");
               if ( urlPath.startsWith("file:") )
               {
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
         }
      }
      handle(paths);
   }
   
   protected void handle(Set<String> paths)
   {
      for ( String urlPath: paths )
      {
         try
         {
            log.trace("scanning: " + urlPath);
            File file = new File(urlPath);
            if ( file.isDirectory() )
            {
               handleDirectory(file, null);
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

   private void handleArchiveByFile(File file) throws IOException
   {
      try
      {
         log.trace("archive: " + file);
         touchTimestamp(file);
         ZipFile zip = new ZipFile(file);
         Enumeration<? extends ZipEntry> entries = zip.entries();
         while ( entries.hasMoreElements() )
         {
            ZipEntry entry = entries.nextElement();
            String name = entry.getName();
            handleItem(name);
         }
      }
      catch (ZipException e)
      {
         throw new RuntimeException("Error handling file " + file, e);
      }
   }

   private void handleDirectory(File file, String path)
   {
      log.trace("directory: " + file);
      for ( File child: file.listFiles() )
      {
         String newPath = path==null ? child.getName() : path + '/' + child.getName();
         if ( child.isDirectory() )
         {
            handleDirectory(child, newPath);
         }
         else
         {
            touchTimestamp(child);
            handleItem(newPath);
         }
      }
   }

   private void touchTimestamp(File file)
   {
      if (file.lastModified() > timestamp)
      {
         timestamp = file.lastModified();
      }
   }
   
   @Override
   public long getTimestamp()
   {
      return timestamp;
   }
   
}
