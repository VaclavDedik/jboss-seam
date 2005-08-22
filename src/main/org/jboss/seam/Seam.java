//$Id$
package org.jboss.seam;

import static org.jboss.seam.ComponentType.ENTITY_BEAN;
import static org.jboss.seam.ComponentType.JAVA_BEAN;
import static org.jboss.seam.ComponentType.STATEFUL_SESSION_BEAN;
import static org.jboss.seam.ComponentType.STATELESS_SESSION_BEAN;

import javax.ejb.Stateful;
import javax.ejb.Stateless;
import javax.persistence.Entity;

import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.finders.ComponentFinder;

/**
 * Convenience methods for accessing annotated information
 * about Seam component classes.
 * 
 * @author Gavin King
 */
public class Seam
{
   
   public static Component getComponent(String name)
   {
      return new ComponentFinder().getComponent(name);
   }
   
   /**
    * Get the default scope
    * @see Scope
    */
   public static ScopeType getComponentScope(Class<?> clazz)
   {
      return clazz.isAnnotationPresent(Scope.class) ?
            clazz.getAnnotation(Scope.class).value() :
            getComponentType(clazz).getDefaultScope();
   }
   
   /**
    * Get the component type
    */
   public static ComponentType getComponentType(Class<?> clazz)
   {
      if ( clazz.isAnnotationPresent(Stateful.class) )
      {
         return STATEFUL_SESSION_BEAN;
      }
      else if ( clazz.isAnnotationPresent(Stateless.class) )
      {
         return STATELESS_SESSION_BEAN;
      }
      else if ( clazz.isAnnotationPresent(Entity.class) )
      {
         return ENTITY_BEAN;
      }
      else {
         return JAVA_BEAN;
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
   
   static
   {
      //force init
      new Environment();
   }

}
