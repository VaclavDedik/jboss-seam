package org.jboss.seam.util;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Method;

/**
 * A convenience class for working with an annotated property (either a field or method) of
 * a JavaBean class.
 *  
 * @author Shane Bryzak
 */
public class AnnotatedBeanProperty<T extends Annotation>
{
   private Field propertyField;
   private Method propertyGetter;
   private Method propertySetter;
   private T annotation;
   private String name;
   private Class propertyClass;
   
   private boolean isFieldProperty;
   
   private AnnotatedBeanProperty(Field propertyField, T annotation)
   {
      this.propertyField = propertyField;
      isFieldProperty = true;
      this.annotation = annotation;
      this.name = propertyField.getName();
      this.propertyClass = propertyField.getDeclaringClass();
   }
   
   private AnnotatedBeanProperty(Method propertyMethod, T annotation)
   {
      if (!(propertyMethod.getName().startsWith("get") || (propertyMethod.getName().startsWith("is"))))
      {
         throw new IllegalArgumentException("Bean property method name " + propertyMethod.getClass().getName() +
               "." + propertyMethod.getName() + "() must start with \"get\" or \"is\".");
      }
      
      if (propertyMethod.getReturnType().equals(void.class) || propertyMethod.getParameterTypes().length > 0)
      {
         throw new IllegalArgumentException("Bean property method " + propertyMethod.getClass().getName() +
               "." + propertyMethod.getName() + "() must return a value and take no parameters");
      }
      
      this.propertyGetter = propertyMethod;
      this.propertyClass = propertyMethod.getReturnType();
      
      String methodName = propertyMethod.getName();
      
      this.name = methodName.startsWith("get") ?
            (methodName.substring(3,4).toLowerCase() + methodName.substring(4)) :
            (methodName.substring(2,3).toLowerCase() + methodName.substring(3));
      
      String setterName = propertyMethod.getName().startsWith("get") ?
            ("set" + methodName.substring(3)) : ("set" + methodName.substring(2));
            
      try
      {
         propertySetter = propertyMethod.getDeclaringClass().getMethod(setterName, new Class[] {propertyMethod.getReturnType()});
      }
      catch (NoSuchMethodException ex)
      {
         throw new IllegalArgumentException("Bean property method " + propertyMethod.getClass().getName() +
               "." + propertyMethod.getName() + "() must have a corresponding setter method.");                  
      }
      
      isFieldProperty = false;
      this.annotation = annotation;
   }
   
   public void setValue(Object bean, Object value)
   {
      if (isFieldProperty)
      {
         boolean accessible = propertyField.isAccessible();
         try
         {
            propertyField.setAccessible(true);
            propertyField.set(bean, value);   
         }
         catch (IllegalAccessException ex)
         {
            throw new RuntimeException("Exception setting bean property", ex);
         }
         finally
         {
            propertyField.setAccessible(accessible);
         }            
      }
      else
      {
         try
         {
            propertySetter.invoke(bean, value);
         }
         catch (Exception ex)
         {
            throw new RuntimeException("Exception setting bean property", ex);
         }
      }
   }
   
   public Object getValue(Object bean)
   {
      if (isFieldProperty)
      {
         boolean accessible = propertyField.isAccessible();
         try
         {
            propertyField.setAccessible(true);
            return propertyField.get(bean);
         }
         catch (IllegalAccessException ex)
         {
            throw new RuntimeException("Exception getting bean property", ex);
         }
         finally
         {
            propertyField.setAccessible(accessible);
         }
      }
      else
      {
         try
         {
            return propertyGetter.invoke(bean);
         }
         catch (Exception ex)
         {
            throw new RuntimeException("Exception getting bean property", ex);
         }
      }
   }
   
   public T getAnnotation()
   {
      return annotation;
   }
   
   public String getName()
   {
      return name;
   }
   
   public Class getPropertyClass()
   {
      return propertyClass;
   }
   
   
   public static AnnotatedBeanProperty scanForProperty(Class cls, Class<? extends Annotation> annotation)
   {
      for (Field f : cls.getFields())
      {
         if (f.isAnnotationPresent(annotation)) 
         {
            return new AnnotatedBeanProperty(f, f.getAnnotation(annotation));
         }
      }
      
      for (Method m : cls.getMethods())
      {
         if (m.isAnnotationPresent(annotation))
         {
            return new AnnotatedBeanProperty(m, m.getAnnotation(annotation));
         }
      }
      
      return null;
   }   
}