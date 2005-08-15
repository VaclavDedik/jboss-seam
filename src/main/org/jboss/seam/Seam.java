//$Id$
package org.jboss.seam;

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
      return clazz.getAnnotation(Scope.class).value();
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
