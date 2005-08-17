//$Id$
package org.jboss.seam;

import static org.jboss.seam.ComponentType.ENTITY_BEAN;
import static org.jboss.seam.ComponentType.JAVA_BEAN;
import static org.jboss.seam.ComponentType.STATEFUL_SESSION_BEAN;
import static org.jboss.seam.ComponentType.STATELESS_SESSION_BEAN;
import static org.jboss.seam.ScopeType.CONVERSATION;
import static org.jboss.seam.ScopeType.EVENT;
import static org.jboss.seam.ScopeType.STATELESS;

import javax.ejb.Stateful;
import javax.ejb.Stateless;
import javax.persistence.Entity;

import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;

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
      if ( clazz.isAnnotationPresent(Scope.class) )
      {
         return clazz.getAnnotation(Scope.class).value();
      }
      else
      {
         switch ( getComponentType(clazz) )
         {
            case STATEFUL_SESSION_BEAN:
            case ENTITY_BEAN:
               return CONVERSATION;
            case STATELESS_SESSION_BEAN:
               return STATELESS;
            case JAVA_BEAN:
               return EVENT;
            default:
               throw new IllegalStateException();
         }
      }
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

}
