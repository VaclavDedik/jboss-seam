package org.jboss.seam.wicket;

import java.io.Serializable;
import java.lang.annotation.Annotation;

public interface BijectedAttribute<T extends Annotation>
{
   public String getName();
   public T getAnnotation();
   public Class getType();
   public void set(Object bean, Object value);
   public Object get(Object bean);
   public MetaModel getMetaModel();
}
