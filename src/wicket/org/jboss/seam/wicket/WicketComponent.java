package org.jboss.seam.wicket;

import org.jboss.seam.contexts.Contexts;

public class WicketComponent extends MetaModel
{

   public WicketComponent(Class<?> beanClass)
   {
      super(beanClass);
   }
   
   @Override
   protected String getMetaModelName()
   {
      return getComponentName(getBeanClass());
   }

   protected static String getComponentName(Class clazz)
   {
      return clazz.getName() + ".wicketComponent";
   }

   public static WicketComponent forClass(Class clazz)
   {
      if (Contexts.isApplicationContextActive())
      {
         String metaModelName = getComponentName(clazz);
         instantiate(metaModelName, clazz);
         return (WicketComponent) forName(metaModelName);
      }
      else
      {
         throw new IllegalStateException("Application context is not active");
      }
   }
   
   private static void instantiate(String componentName, Class clazz)
   {
      if (!Contexts.getApplicationContext().isSet(componentName))
      {
         WicketComponent component = new WicketComponent(clazz);
         Contexts.getApplicationContext().set(componentName, component);
      }
   }
   
}
