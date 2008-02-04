package org.jboss.seam.wicket;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;

public class BijectedField<T extends Annotation> implements BijectedAttribute<T>
   {
      private String name;
      private Field field;
      private T annotation;
      private MetaModel metaModel;
      
      public BijectedField(String name, Field field, T annotation, MetaModel metaModel)
      {
         this.name = name;
         this.field = field;
         this.annotation = annotation;
         this.metaModel = metaModel;
      }
      public String getName()
      {
         return name;
      }
      public Field getField()
      {
         return field;
      }
      public T getAnnotation()
      {
         return annotation;
      }
      public Class getType()
      {
         return field.getType();
      }
      public void set(Object bean, Object value)
      {
         metaModel.setFieldValue(bean, field, name, value);
      }
      public Object get(Object bean)
      {
         return metaModel.getFieldValue(bean, field, name);
      }
      @Override
      public String toString()
      {
         return "BijectedField(" + name + ')';
      }
      
      public MetaModel getMetaModel()
      {
         return metaModel;
      }
   }