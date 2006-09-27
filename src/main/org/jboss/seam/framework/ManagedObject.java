package org.jboss.seam.framework;

import static org.jboss.seam.InterceptionType.NEVER;

import org.jboss.seam.annotations.Intercept;
import org.jboss.seam.annotations.Unwrap;
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
   private String objectClass;
   private Object instance;
   
   public String getObjectClass()
   {
      return objectClass;
   }

   public void setObjectClass(String entityClass)
   {
      this.objectClass = entityClass;
   }

   @Unwrap
   public Object getInstance() throws Exception
   {
      if (instance==null)
      {
         Class<?> clazz = Reflections.classForName(objectClass);
         instance = clazz.newInstance();
      }
      return instance;
   }

   public void setInstance(Object instance)
   {
      this.instance = instance;
   }
   
}
