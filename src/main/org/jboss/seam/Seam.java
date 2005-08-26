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
import org.jboss.seam.contexts.Contexts;

/**
 * Convenience methods for accessing annotated information
 * about Seam component classes.
 * 
 * @author Gavin King
 */
public class Seam
{
      
   private static final String SESSION_INVALID = "org.jboss.seam.sessionInvalid";

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
   
   /**
    * Mark the session for invalidation at the end of the request cycle
    */
   public static void invalidateSession()
   {
      if ( !Contexts.isSessionContextActive() )
      {
         throw new IllegalStateException("No active session context");
      }
      Contexts.getSessionContext().set(SESSION_INVALID, true);
   }
   
   /**
    * Is the session marked for invalidation?
    */
   public static boolean isSessionInvalid()
   {
      if ( !Contexts.isSessionContextActive() )
      {
         throw new IllegalStateException("No active session context");
      }
      Boolean isSessionInvalid = (Boolean) Contexts.getSessionContext().get(SESSION_INVALID);
      return isSessionInvalid!=null && isSessionInvalid;
   }

}
