package org.jboss.seam.framework;

import static org.jboss.seam.InterceptionType.NEVER;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Map;

import org.jboss.seam.annotations.Intercept;
import org.jboss.seam.annotations.Transactional;
import org.jboss.seam.annotations.Unwrap;
import org.jboss.seam.core.Expressions;
import org.jboss.seam.core.Expressions.ValueBinding;
import org.jboss.seam.util.Reflections;

/**
 * Manager component for an instance of any class.
 * 
 * @author Gavin King
 *
 */
@Intercept(NEVER)
public class ManagedObject<E>
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

   @Unwrap @Transactional
   public final E getInstance() throws Exception
   {
      if (instance==null)
      {
         initInstance();
      }
      return instance;
   }

   protected void initInstance() throws Exception
   {
      instance = createInstance();
      initialize(instance);
   }

   public void setInstance(E instance)
   {
      this.instance = instance;
   }

   protected E createInstance() throws Exception
   {
      return getObjectClass().newInstance();
   }

   protected void initialize(E instance) throws Exception
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
               Reflections.set(field, instance, value);
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
               Method method = Reflections.getSetterMethod( objectClass, initializer.getKey() );
               if ( !method.isAccessible() ) method.setAccessible(true);
               Reflections.invoke(method, instance, value);
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
