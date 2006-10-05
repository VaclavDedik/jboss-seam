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
public class ManagedObject
{
   private Class<?> objectClass;
   protected Object instance;
   
   private Map<String, String> initialFieldValues;
   private Map<String, String> initialPropertyValues;
   
   public Class getObjectClass()
   {
      return objectClass;
   }

   public void setObjectClass(Class<?> objectClass)
   {
      this.objectClass = objectClass;
   }

   @Unwrap @Transactional
   public final Object getInstance() throws Exception
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

   public void setInstance(Object instance)
   {
      this.instance = instance;
   }

   protected Object createInstance() throws Exception
   {
      return objectClass.newInstance();
   }

   protected void initialize(Object instance) throws Exception
   {
      if (initialFieldValues!=null)
      {
         for ( Map.Entry<String, String> initializer: initialFieldValues.entrySet() )
         {
            Object value = Expressions.instance().createValueBinding( initializer.getValue() ).getValue();
            Field field = Reflections.getField( objectClass, initializer.getKey() );
            if ( !field.isAccessible() ) field.setAccessible(true);
            Reflections.set(field, instance, value);
         }
      }
      if (initialPropertyValues!=null)
      {
         for ( Map.Entry<String, String> initializer: initialPropertyValues.entrySet() )
         {
            ValueBinding valueBinding = Expressions.instance().createValueBinding( initializer.getValue() );
            Object value = valueBinding.getValue();
            Method method = Reflections.getSetterMethod( objectClass, initializer.getKey() );
            if ( !method.isAccessible() ) method.setAccessible(true);
            Reflections.invoke(method, instance, value);
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
