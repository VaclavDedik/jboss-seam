package org.jboss.seam.wicket.ioc;

import java.lang.annotation.Annotation;

/**
 * A bijected attribute (field or get/set pair)
 * @author Pete Muir
 *
 *
 * TODO Move into Seam core 
 */
public interface BijectedAttribute<T extends Annotation>
{
   public String getName();
   public T getAnnotation();
   public Class getType();
   public void set(Object bean, Object value);
   public Object get(Object bean);
   public MetaModel getMetaModel();
}
