package org.jboss.seam.wicket.ioc;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

import org.jboss.seam.Model;
import org.jboss.seam.contexts.Contexts;
import org.jboss.seam.util.Reflections;

/**
 * MetaModel for a component
 * @author pmuir
 *
 */
public abstract class MetaModel extends Model 
{

   private Injector injectionSupport;
   private Outjector outjectionSupport;
   private Loggable loggerSupport;
   
   public MetaModel(Class<?> beanClass)
   {
      super(beanClass);
      injectionSupport = new Injector(this);
      outjectionSupport = new Outjector(this);
      loggerSupport = new Loggable(this);
      scan();
   }
   
   public void initialize()
   {
      scan();
   }
   
   public void inject(Object instance) throws Exception
   {
      injectionSupport.inject(instance);
      loggerSupport.inject(instance);
   }
   
   public void outject(Object instance)
   {
      outjectionSupport.outject(instance);
   }
   
   private void scan()
   {
      Class clazz = getBeanClass();
      for ( ; clazz!=Object.class; clazz = clazz.getSuperclass() )
      {
         for ( Method method: clazz.getDeclaredMethods() )
         {
            scanMethod(method);
         }

         for ( Field field: clazz.getDeclaredFields() )
         {
            scanField(field);
         }
      }
   }
   
   private void scanField(Field field)
   {
      if ( !field.isAccessible() )
      {
         field.setAccessible(true);
      }
      injectionSupport.add(field);
      loggerSupport.add(field);
   }

   private void scanMethod(Method method)
   {
      injectionSupport.add(method);
   }

   protected void setFieldValue(Object bean, Field field, String name, Object value)
   {
      try
      {
         Reflections.set(field, bean, value);
      }
      catch (Exception e)
      {
         throw new IllegalArgumentException("could not set field value: " + getAttributeMessage(name), e);
      }
   }

   protected Object getFieldValue(Object bean, Field field, String name)
   {
      try {
         return Reflections.get(field, bean);
      }
      catch (Exception e)
      {
         throw new IllegalArgumentException("could not get field value: " + getAttributeMessage(name), e);
      }
   }
   
   protected String getAttributeMessage(String attributeName)
   {
      return getName() + '.' + attributeName;
   }
   
   protected String getName()
   {
      return getBeanClass().getName();
   }

   protected abstract String getMetaModelName();
   
   protected void setPropertyValue(Object bean, Method method, String name, Object value)
   {
      try
      {
         Reflections.invoke(method, bean, value );
      }
      catch (Exception e)
      {
         throw new IllegalArgumentException("could not set property value: " + getAttributeMessage(name), e);
      }
   }

   public Object getPropertyValue(Object bean, Method method, String name)
   {
      try {
         return Reflections.invoke(method, bean);
      }
      catch (Exception e)
      {
         throw new IllegalArgumentException("could not get property value: " + getAttributeMessage(name), e);
      }
   }
   
   public static MetaModel forName(String name)
   {
      if (Contexts.isApplicationContextActive())
      {
         return (MetaModel) Contexts.getApplicationContext().get(name);
      }
      else
      {
         throw new IllegalStateException("Application context is not active");
      }
   }
   
}
