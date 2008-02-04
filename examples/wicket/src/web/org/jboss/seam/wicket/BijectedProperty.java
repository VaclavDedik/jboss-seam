package org.jboss.seam.wicket;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;

import org.jboss.seam.util.Reflections;

public class BijectedProperty<T extends Annotation> implements BijectedAttribute<T>
   {
      
      private BijectedMethod<T> getter;
      private BijectedMethod<T> setter;
      private MetaModel metaModel;
      
      public BijectedProperty(String name, Method getter, Method setter, T annotation, MetaModel metaModel)
      {
         this.getter = new BijectedMethod(name, getter, annotation, metaModel);
         this.setter = new BijectedMethod(name, setter, annotation, metaModel);
      }
      
      public BijectedProperty(String name, Method getter, T annotation, MetaModel metaModel)
      {
         this.getter = new BijectedMethod(name, getter, annotation, metaModel);
         try
         {
            Method setterMethod = Reflections.getSetterMethod(getter.getDeclaringClass(), name);
            this.setter = new BijectedMethod(name, setterMethod, annotation, metaModel);
         }
         catch (IllegalArgumentException e) {}        
      }

      public Object get(Object bean)
      {
         return getter.get(bean);
      }

      public T getAnnotation()
      {
         return getter.getAnnotation();
      }

      public String getName()
      {
         return getter.getName();
      }

      public Class getType()
      {
         return getter.getType();
      }

      public void set(Object bean, Object value)
      {
         if (setter == null)
         {
            throw new IllegalArgumentException("Component must have a setter for " + metaModel.getName());
         }
         setter.set(bean, value); 
      }
      
      public MetaModel getMetaModel()
      {
         return metaModel;
      }
      
   }