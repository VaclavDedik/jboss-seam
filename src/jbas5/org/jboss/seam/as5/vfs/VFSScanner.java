/*
* JBoss, Home of Professional Open Source
* Copyright 2006, JBoss Inc., and individual contributors as indicated
* by the @authors tag. See the copyright.txt in the distribution for a
* full listing of individual contributors.
*
* This is free software; you can redistribute it and/or modify it
* under the terms of the GNU Lesser General Public License as
* published by the Free Software Foundation; either version 2.1 of
* the License, or (at your option) any later version.
*
* This software is distributed in the hope that it will be useful,
* but WITHOUT ANY WARRANTY; without even the implied warranty of
* MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
* Lesser General Public License for more details.
*
* You should have received a copy of the GNU Lesser General Public
* License along with this software; if not, write to the Free
* Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA
* 02110-1301 USA, or see the FSF site: http://www.fsf.org.
*/
package org.jboss.seam.as5.vfs;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.Enumeration;
import java.util.List;

import org.jboss.seam.deployment.AbstractScanner;
import org.jboss.seam.deployment.DeploymentStrategy;
import org.jboss.seam.log.LogProvider;
import org.jboss.seam.log.Logging;
import org.jboss.virtual.VFS;
import org.jboss.virtual.VirtualFile;

/**
 * JBoss VSF aware scanner.
 *
 * @author <a href="mailto:ales.justin@jboss.com">Ales Justin</a>
 */
public class VFSScanner extends AbstractScanner
{
   private static final LogProvider log = Logging.getLogProvider(VFSScanner.class);

   public VFSScanner(DeploymentStrategy deploymentStrategy)
   {
      super(deploymentStrategy);
   }

   /**
    * Get the virtual file root.
    *
    * @param url the root URL
    * @param parentDepth level of parent depth
    * @return actual virtual file from url param
    * @throws IOException for any error
    */
   protected static VirtualFile getRoot(URL url, int parentDepth) throws IOException
   {
      boolean trace = log.isTraceEnabled();

      if (trace)
         log.trace("Root url: " + url);

      String urlString = url.toString();
      // TODO - this should go away once we figure out why -exp.war is part of CL resources
      if (urlString.startsWith("vfs") == false)
         return null;

      int p = urlString.indexOf(":");
      String file = urlString.substring(p + 1);
      URL vfsurl = null;
      String relative;
      File fp = new File(file);
      if (trace)
         log.trace("File: " + fp);

      if (fp.exists())
      {
         vfsurl = fp.getParentFile().toURL();
         relative = fp.getName();
      }
      else
      {
         File curr = fp;
         relative = fp.getName();
         while ((curr = curr.getParentFile()) != null)
         {
            if (curr.exists())
            {
               vfsurl = curr.toURL();
               break;
            }
            else
            {
               relative = curr.getName() + "/" + relative;
            }
         }
      }

      if (trace)
         log.trace("URL: " + vfsurl + ", relative: " + relative);

      VirtualFile top = VFS.getRoot(vfsurl);
      top = top.getChild(relative);
      while(parentDepth > 0)
      {
         if (top == null)
            throw new IllegalArgumentException("Null parent: " + vfsurl);
         top = top.getParent();
         parentDepth--;
      }

      if (trace)
         log.trace("Top: " + top);

      return top;
   }

   public void scanDirectories(File[] directories)
   {
      for (File dir : directories)
      {
         try
         {
            VirtualFile root = getRoot(dir.toURL(), 0);
            if (root != null)
               handleRoot(root);
            else if (log.isTraceEnabled())
               log.trace("Null root: " + dir);
         }
         catch (IOException e)
         {
            log.warn("Cannot scan directory " + dir, e);
         }
      }
   }

   public void scanResources(String[] resources)
   {
      for (String resourceName : resources)
      {
         try
         {
            Enumeration<URL> urlEnum = getDeploymentStrategy().getClassLoader().getResources(resourceName);
            while (urlEnum.hasMoreElements())
            {
               URL url = urlEnum.nextElement();
               VirtualFile root = getRoot(url, resourceName.lastIndexOf('/') > 0 ? 2 : 1);
               if (root != null)
                  handleRoot(root);
               else if (log.isTraceEnabled())
                  log.trace("Null root: " + url);
            }
         }
         catch (IOException ioe)
         {
            log.warn("Cannot read resource: " + resourceName, ioe);
         }
      }
   }

   /**
    * Handle virtual file root.
    *
    * @param file the virtual file root
    * @throws IOException for any error
    */
   protected void handleRoot(VirtualFile file) throws IOException
   {
      if (file.isLeaf())
      {
         getDeploymentStrategy().handle(file.getPathName());
      }
      else
      {
         List<VirtualFile> children = file.getChildrenRecursively();
         for (VirtualFile child : children)
         {
            if (child.isLeaf())
            {
               getDeploymentStrategy().handle(child.getPathName());
            }
         }
      }
   }
}
