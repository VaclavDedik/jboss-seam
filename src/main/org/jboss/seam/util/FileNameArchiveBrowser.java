/*
 * JBoss, Home of Professional Open Source
 *
 * Distributable under LGPL license.
 * See terms of license at gnu.org.
 */
package org.jboss.seam.util;

import java.io.File;
import java.net.URL;
import java.util.Iterator;

import org.jboss.util.NotImplementedException;

/**
 * @author <a href="mailto:theute@jboss.org">Thomas Heute</a>
 * @author <a href="mailto:bill@jboss.org">Bill Burke</a>
 * @version $Revision$
 */
public class FileNameArchiveBrowser
{
   public interface Filter
   {
      boolean accept(String filename);
   }
   
   public static Iterator getBrowser(URL url, Filter filter)
   {
      if (url.getProtocol().equals("file"))
      {
         File f = new File(url.getFile());
         if (f.isDirectory())
         {
            return new DirectoryFileNameArchiveBrowser(f, filter);
         }
         else
         {
            return new JarArchiveFileNameBrowser(f, filter);
         }
      }
      else
      {
         throw new NotImplementedException();
      }
   }
}


