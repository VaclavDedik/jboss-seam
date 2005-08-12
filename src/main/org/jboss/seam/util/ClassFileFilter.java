/*
 * JBoss, Home of Professional Open Source
 *
 * Distributable under LGPL license.
 * See terms of license at gnu.org.
 */
package org.jboss.seam.util;

/**
 * ArchiveBrowser filter to find .class files
 *
 * @author <a href="mailto:bill@jboss.org">Bill Burke</a>
 * @version $Revision$
 */
public class ClassFileFilter implements FileNameArchiveBrowser.Filter
{
   public boolean accept(String filename)
   {
      return filename.endsWith(".class");
   }
}