package org.jboss.seam.wicket;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;

public class BijectedMethod<T extends Annotation> implements BijectedAttribute<T>
   {
      private String name;
      private Method method;
      private T annotation;
      private MetaModel metaModel;
      
      public BijectedMethod(String name, Method method, T annotation, MetaModel metaModel)
      {
         this.name = name;
         this.method = method;
         this.annotation = annotation;
      }
      public String getName()
      {
         return name;
      }
      public Method getMethod()
      {
         return method;
      }
      public T getAnnotation()
      {
         return annotation;
      }
      public void set(Object bean, Object value)
      {
         metaModel.setPropertyValue(bean, method, name, value);
      }
      public Object get(Object bean)
      {
         return metaModel.getPropertyValue(bean, method, name);
      }
      public Class getType()
      {
         return method.getParameterTypes()[0];
      }
      @Override
      public String toString()
      {
         return "BijectedMethod(" + name + ')';
      }
      
      public MetaModel getMetaModel()
      {
         return metaModel;
      }
   }