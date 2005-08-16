//$Id$
package org.jboss.seam;

import javax.ejb.Stateless;

import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.annotations.ScopeType;

/**
 * Convenience methods for accessing annotated information
 * about Seam component classes.
 * @author Gavin King
 */
public class Seam
{
   
   /**
    * Get the default scope
    * @see Scope
    */
   public static ScopeType getComponentScope(Class<?> clazz)
   {
      Scope scope = clazz.getAnnotation(Scope.class);
      if (scope!=null) return scope.value();
      if (clazz.isAnnotationPresent(Stateless.class))
      {
         return ScopeType.STATELESS;
      }
      else 
      {
         return ScopeType.CONVERSATION;
      }
   }
   
   /**
    * Get the component name
    * @see Name
    */
   public static String getComponentName(Class<?> clazz)
   {
      return clazz.getAnnotation(Name.class).value();
   }

}
