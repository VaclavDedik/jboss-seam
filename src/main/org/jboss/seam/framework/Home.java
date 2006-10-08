package org.jboss.seam.framework;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Map;

import org.jboss.seam.annotations.Transactional;
import org.jboss.seam.core.Expressions;
import org.jboss.seam.core.Expressions.ValueBinding;
import org.jboss.seam.util.Reflections;

/**
 * Manager component for an instance of any class.
 * 
 * @author Gavin King
 *
 */
public class Home<E>
{
   private Class<E> objectClass;
   protected E instance;
   
   private Map<String, String> initialFieldValues;
   private Map<String, String> initialPropertyValues;
   
   public Class<E> getObjectClass()
   {
      return objectClass;
   }

   public void setObjectClass(Class<E> objectClass)
   {
      this.objectClass = objectClass;
   }

   @Transactional
   public E getInstance()
   {
      if (instance==null)
      {
         initInstance();
      }
      return instance;
   }

   protected void initInstance()
   {
      setInstance( createInstance() );
      initialize(instance);
   }

   public void setInstance(E instance)
   {
      this.instance = instance;
   }

   protected E createInstance()
   {
      try
      {
         return getObjectClass().newInstance();
      }
      catch (Exception e)
      {
         throw new RuntimeException(e);
      }
   }

   protected void initialize(E instance)
   {
      if (initialFieldValues!=null)
      {
         for ( Map.Entry<String, String> initializer: initialFieldValues.entrySet() )
         {
            Object value = Expressions.instance().createValueBinding( initializer.getValue() ).getValue();
            if ( value!=null )
            {
               Field field = Reflections.getField( getObjectClass(), initializer.getKey() );
               if ( !field.isAccessible() ) field.setAccessible(true);
               Reflections.setAndWrap(field, instance, value);
            }
         }
      }
      if (initialPropertyValues!=null)
      {
         for ( Map.Entry<String, String> initializer: initialPropertyValues.entrySet() )
         {
            ValueBinding valueBinding = Expressions.instance().createValueBinding( initializer.getValue() );
            Object value = valueBinding.getValue();
            if ( value!=null )
            {
               Method method = Reflections.getSetterMethod( getObjectClass(), initializer.getKey() );
               if ( !method.isAccessible() ) method.setAccessible(true);
               Reflections.invokeAndWrap(method, instance, value);
            }
         }
      }
   }
   
   public Map<String, String> getInitialFieldValues()
   {
      return initialFieldValues;
   }

   public void setInitialFieldValues(Map<String, String> initializers)
   {
      this.initialFieldValues = initializers;
   }

   public Map<String, String> getInitialPropertyValues()
   {
      return initialPropertyValues;
   }

   public void setInitialPropertyValues(Map<String, String> initialPropertyValues)
   {
      this.initialPropertyValues = initialPropertyValues;
   }

}
