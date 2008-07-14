package org.jboss.seam.wicket.ioc;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;

import javassist.ClassPool;
import javassist.Loader;

public class WicketClassLoader extends Loader
{
   
   private List<String> classes;
   private File wicketComponentDirectory;

   public WicketClassLoader(List<String> classes, File wicketComponentDirectory)
   {
      super();
      this.classes = classes;
      this.wicketComponentDirectory = wicketComponentDirectory;
   }

   public WicketClassLoader(ClassLoader parent, ClassPool cp, List<String> classes, File wicketComponentDirectory)
   {
      super(parent, cp);
      this.classes = classes;
      this.wicketComponentDirectory = wicketComponentDirectory;
   }

   public WicketClassLoader(ClassPool cp, List<String> classes, File wicketComponentDirectory)
   {
      super(cp);
      this.classes = classes;
      this.wicketComponentDirectory = wicketComponentDirectory;
   }

   @Override
   protected Class loadClassByDelegation(String name) throws ClassNotFoundException
   {
      Class clazz = super.loadClassByDelegation(name);
      if (clazz == null)
      {
         if (!classes.contains(name))
         {
            clazz = delegateToParent(name);
         }
      }
      return clazz;
   }
   
   @Override
   public URL getResource(String name)
   {
      File file = new File(wicketComponentDirectory, name);
      if (file.exists())
      {
         try
         {
            return file.toURL();
         }
         catch (MalformedURLException e)
         {
            throw new RuntimeException(e);
         }
      }
      else
      {
         if (getParent() != null)
         {
            return getParent().getResource(name);
         }
         else
         {
            return null;
         }
      }
   }
   
}
