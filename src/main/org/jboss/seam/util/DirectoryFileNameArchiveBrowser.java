/*
 * JBoss, Home of Professional Open Source
 *
 * Distributable under LGPL license.
 * See terms of license at gnu.org.
 */
package org.jboss.seam.util;

import java.io.File;
import java.util.ArrayList;
import java.util.Iterator;

import org.jboss.seam.util.FileNameArchiveBrowser.Filter;
import org.jboss.util.NotImplementedException;

/**
 * @author <a href="mailto:theute@jboss.org">Thomas Heute</a>
 * @version $Revision$
 */
public class DirectoryFileNameArchiveBrowser implements Iterator
{

   private Iterator files;

   public DirectoryFileNameArchiveBrowser(File file, Filter filter)
   {
      ArrayList list = new ArrayList();
      try
      {
         create(list, file, filter);
      }
      catch (Exception e)
      {
         throw new RuntimeException(e);
      }
      files = list.iterator();
   }

   private void create(ArrayList list, File dir, Filter filter)
   {
      File[] files = dir.listFiles();
      for (int i = 0; i < files.length; i++)
      {
         if (files[i].isDirectory())
         {
            create(list, files[i], filter);
         }
         else
         {
            if (filter.accept(files[i].getAbsolutePath()))
            {
               list.add(files[i].getPath());
            }
         }
      }
   }

   public boolean hasNext()
   {
      throw new NotImplementedException();
   }

   public Object next()
   {
      throw new NotImplementedException();
   }

   public void remove()
   {
      throw new NotImplementedException();
   }

}


