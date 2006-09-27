//$Id$
package org.jboss.seam;

import static org.jboss.seam.ComponentType.ENTITY_BEAN;
import static org.jboss.seam.ComponentType.JAVA_BEAN;
import static org.jboss.seam.ComponentType.STATEFUL_SESSION_BEAN;
import static org.jboss.seam.ComponentType.STATELESS_SESSION_BEAN;
import static org.jboss.seam.ComponentType.MESSAGE_DRIVEN_BEAN;

import javax.ejb.MessageDriven;
import javax.ejb.Stateful;
import javax.ejb.Stateless;
import javax.persistence.Entity;

import org.jboss.seam.annotations.Intercept;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Role;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.contexts.Contexts;
import org.jboss.seam.contexts.Lifecycle;
import org.jboss.seam.util.Strings;

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
    * Get the scope for a role
    * @see Scope
    */
   public static ScopeType getComponentRoleScope(Class clazz, Role role)
   {
      return role.scope()==ScopeType.UNSPECIFIED ?
            getComponentType(clazz).getDefaultScope() :
            role.scope();
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
      else if ( clazz.isAnnotationPresent(MessageDriven.class) )
      {
         return MESSAGE_DRIVEN_BEAN;
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
      while ( clazz!=null && !Object.class.equals(clazz) )
      {
         Name name = clazz.getAnnotation(Name.class);
         if ( name!=null ) return name.value();
         clazz = clazz.getSuperclass();
      }
      return null;
   }
   
   /**
    * Get the bean class from a container-generated proxy
    * class BROKEN!!!!!
    */
   /*public static Class getBeanClass(Class<?> clazz)
   {
      while ( clazz!=null && !Object.class.equals(clazz) )
      {
         Name name = clazz.getAnnotation(Name.class);
         if ( name!=null ) return clazz;
         clazz = clazz.getSuperclass();
      }
      return null;
   }*/
   
   /**
    * Get the bean class from a container-generated proxy
    * class
    */
   public static Class getEntityClass(Class<?> clazz)
   {
      while ( clazz!=null && !Object.class.equals(clazz) )
      {
         Entity name = clazz.getAnnotation(Entity.class);
         if ( name!=null ) return clazz;
         clazz = clazz.getSuperclass();
      }
      return null;
   }
   
   public static String getEjbName(Class<?> clazz)
   {
      switch ( getComponentType(clazz) )
      {
         case ENTITY_BEAN:
         case JAVA_BEAN:
            return null;
         case STATEFUL_SESSION_BEAN:
            Stateful stateful = clazz.getAnnotation(Stateful.class);
            return stateful.name().equals("") ? unqualifyClassName(clazz) : stateful.name();
         case STATELESS_SESSION_BEAN:
            Stateless stateless = clazz.getAnnotation(Stateless.class);
            return stateless.name().equals("") ? unqualifyClassName(clazz) : stateless.name();
         case MESSAGE_DRIVEN_BEAN:
            MessageDriven md = clazz.getAnnotation(MessageDriven.class);
            return md.name().equals("") ? unqualifyClassName(clazz) : md.name();
         default:
            throw new IllegalArgumentException();
      }
   }

   private static String unqualifyClassName(Class<?> clazz) {
      return Strings.unqualify( Strings.unqualify( clazz.getName() ), '$' );
   }
   
   public static InterceptionType getInterceptionType(Class<?> clazz)
   {
      ComponentType componentType = getComponentType(clazz);
      if ( componentType==ENTITY_BEAN )
      {
         return InterceptionType.NEVER;
      }
      else if ( getComponentType(clazz)==MESSAGE_DRIVEN_BEAN )
      {
         return InterceptionType.ALWAYS;
      }
      else if ( clazz.isAnnotationPresent(Intercept.class) )
      {
         return clazz.getAnnotation(Intercept.class).value();
      }
      else 
      {
         return InterceptionType.ALWAYS;
      }
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
   
   /**
    * Get the Seam component, even if no application context
    * is associated with the current thread.
    */
   public static Component componentForName(String name)
   {
      if ( Contexts.isApplicationContextActive() )
      {
         return Component.forName(name);
      }
      else
      {
         Lifecycle.beginApplication();
         try
         {
            return Component.forName(name);
         }
         finally
         {
            Lifecycle.endApplication();
         }
      }
   }

}
