/*
 * JBoss, Home of Professional Open Source
 *
 * Distributable under LGPL license.
 * See terms of license at gnu.org.
 */
package org.jboss.seam.util;

import java.io.File;
import java.io.IOException;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

import org.jboss.seam.util.FileNameArchiveBrowser.Filter;

/**
 * @author <a href="mailto:theute@jboss.org">Thomas Heute</a>
 * @version $Revision$
 */
public class JarArchiveFileNameBrowser implements Iterator<String>
{

   private Filter filter;
   private Enumeration entries;
   private ZipFile zip;
   private ZipEntry next;

   public JarArchiveFileNameBrowser(File f, Filter filter)
   {
      this.filter = filter;
      try
      {
         this.zip = new ZipFile(f);
         this.entries = zip.entries();
      }
      catch (IOException e)
      {
         throw new RuntimeException(e);
      }
      setNext();
   }

   private void setNext()
   {
      next = null;
      while (entries.hasMoreElements() && next == null)
      {
         do
         {
            next = (ZipEntry) entries.nextElement();
         } while (entries.hasMoreElements() && next.isDirectory());
         if (next.isDirectory()) next = null;

         if (next != null && !filter.accept(next.getName()))
         {
            next = null;
         }
      }
   }

   public boolean hasNext()
   {
      return next != null;
   }

   public String next()
   {
      ZipEntry entry = next;
      setNext();
      return entry.getName();
   }

   public void remove()
   {
      throw new RuntimeException("Illegal operation on ArchiveBrowser");
   }

}


