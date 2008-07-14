package org.jboss.seam.wicket.ioc;

import java.util.List;

import javassist.ClassPool;
import javassist.Loader;

public class WicketClassLoader extends Loader
{
   
   private List<String> classes;

   public WicketClassLoader(List<String> classes)
   {
      super();
      this.classes = classes;
   }

   public WicketClassLoader(ClassLoader parent, ClassPool cp, List<String> classes)
   {
      super(parent, cp);
      this.classes = classes;
   }

   public WicketClassLoader(ClassPool cp, List<String> classes)
   {
      super(cp);
      this.classes = classes;
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
   
}
