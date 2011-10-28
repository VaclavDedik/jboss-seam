package org.jboss.seam.util;

import java.beans.Introspector;
import java.io.Serializable;
import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Type;

/**
 * A convenience class for working with an annotated property (either a field or method) of
 * a JavaBean class.
 *  
 * @author Shane Bryzak
 */
public class AnnotatedBeanProperty<T extends Annotation> implements Serializable
{
   private static final long serialVersionUID = 2508430507136805635L;
   private  transient Field propertyField;
   private transient  Method propertyGetter;
   private transient Method propertySetter;
   private String name;
   private Type propertyType;
   private T annotation;
   
   private boolean isFieldProperty;
   private boolean set = false;
   
   private Class<?> cls;
   private Class<? extends Annotation> annotationClass;
   
   public AnnotatedBeanProperty(Class<?> cls, Class<? extends Annotation> annotationClass)
   {      
      this.cls = cls;
      this.annotationClass = annotationClass;
       init();
   }    
   
   private void init()
   {
         // First check declared fields
         for (Field f : cls.getDeclaredFields())
         {
            if (f.isAnnotationPresent(annotationClass)) 
            {
               setupFieldProperty(f);
               this.annotation = (T) f.getAnnotation(annotationClass);            
               set = true;
               return;
            }
      }      
      
      // Then check public fields, in case it's inherited
      for (Field f : cls.getFields())
      {
         if (f.isAnnotationPresent(annotationClass)) 
         {
            this.annotation = (T) f.getAnnotation(annotationClass);
            setupFieldProperty(f);
            set = true;
            return;
         }
      }
      
      // Then check public methods (we ignore private methods)
      for (Method m : cls.getMethods())
      {
         if (m.isAnnotationPresent(annotationClass))
         {
            this.annotation = (T) m.getAnnotation(annotationClass);
            String methodName = m.getName();
            
            if ( m.getName().startsWith("get") )
            {
               this.name = Introspector.decapitalize( m.getName().substring(3) );
            }
            else if ( methodName.startsWith("is") )
            {
               this.name = Introspector.decapitalize( m.getName().substring(2) );
            }            
            
            if (this.name != null)
            {
               this.propertyGetter = Reflections.getGetterMethod(cls, this.name);
               this.propertySetter = Reflections.getSetterMethod(cls, this.name);
               this.propertyType = this.propertyGetter.getGenericReturnType();
               isFieldProperty = false;               
               set = true;
            }
            else
            {
               throw new IllegalStateException("Invalid accessor method, must start with 'get' or 'is'.  " +
                     "Method: " + m + " in class: " + cls);
            }
         }
      }      
   }

     public Field getPropertyField() 
     { 
        if (propertyField == null)
        {
           init();
        }
        return propertyField; 
     }

     public Method getPropertyGetter() 
     { 
        if (propertyGetter == null)
        {
           init();
        }
        return propertyGetter; 
     }

     public Method getPropertySetter() 
     { 
        if (propertySetter == null) 
        {
           init();
        }
        return propertySetter; 
     }

   private void setupFieldProperty(Field propertyField)
   {
      this.propertyField = propertyField;
      isFieldProperty = true;
      this.name = propertyField.getName();
      this.propertyType = propertyField.getGenericType();
   }

   public void setValue(Object bean, Object value)
   {
      if (isFieldProperty)
      {
         Reflections.setAndWrap(getPropertyField(), bean, value);         
      }
      else
      {
         Reflections.invokeAndWrap(getPropertySetter(), bean, value);
      }
   }
   
   public Object getValue(Object bean)
   {
      if (isFieldProperty)
      {
         return Reflections.getAndWrap(getPropertyField(), bean);  
      }
      else
      {
         return Reflections.invokeAndWrap(getPropertyGetter(), bean);
      }
   }
   
   public String getName()
   {
      return name;
   }
   
   public T getAnnotation()
   {
      return (T) annotation;
   }
   
   public Type getPropertyType()
   {
      return propertyType;
   }
   
   public boolean isSet()
   {
      return set;
   }
}