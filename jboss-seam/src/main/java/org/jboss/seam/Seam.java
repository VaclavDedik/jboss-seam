//$Id: Seam.java 10262 2009-04-01 07:03:21Z dan.j.allen $
package org.jboss.seam;

import static org.jboss.seam.ComponentType.ENTITY_BEAN;
import static org.jboss.seam.ComponentType.JAVA_BEAN;
import static org.jboss.seam.ComponentType.MESSAGE_DRIVEN_BEAN;
import static org.jboss.seam.ComponentType.STATEFUL_SESSION_BEAN;
import static org.jboss.seam.ComponentType.STATELESS_SESSION_BEAN;
import static org.jboss.seam.ComponentType.SINGLETON_SESSION_BEAN;
import static org.jboss.seam.util.EJB.MESSAGE_DRIVEN;
import static org.jboss.seam.util.EJB.STATEFUL;
import static org.jboss.seam.util.EJB.STATELESS;
import static org.jboss.seam.util.EJB.SINGLETON;
import static org.jboss.seam.util.EJB.name;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.jar.Attributes;
import java.util.jar.Manifest;

import javax.persistence.Entity;

import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Role;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.annotations.intercept.BypassInterceptors;
import org.jboss.seam.contexts.Contexts;
import org.jboss.seam.contexts.Lifecycle;
import org.jboss.seam.init.DeploymentDescriptor;
import org.jboss.seam.init.EjbDescriptor;
import org.jboss.seam.util.Strings;
import org.jboss.seam.web.Session;

/**
 * Convenience methods for accessing annotated information
 * about Seam component classes.
 * 
 * @author Gavin King
 */
public class Seam
{
   private static final Map<Class, String> COMPONENT_NAME_CACHE = new ConcurrentHashMap<Class, String>();
   private static final Map<Class, EjbDescriptor> EJB_DESCRIPTOR_CACHE = new ConcurrentHashMap<Class, EjbDescriptor>();
   private static final Set<ClassLoader> CLASSLOADERS_LOADED = new HashSet<ClassLoader>(); 

   // application-scoped property in which the Seam version is stored
   public static final String VERSION = "org.jboss.seam.version";
   
   private static String jarName;
   private static String versionString;

   public static EjbDescriptor getEjbDescriptor(Class clazz)
   {
      EjbDescriptor info = EJB_DESCRIPTOR_CACHE.get(clazz);
      if (info != null) 
      {
          return info;
      }
      else if (!CLASSLOADERS_LOADED.contains(clazz.getClassLoader()))
      {
         cacheEjbDescriptors(clazz);
         return EJB_DESCRIPTOR_CACHE.get(clazz);
      }
      
      return null;
   }
   
   private synchronized static void cacheEjbDescriptors(Class clazz)
   {
      if (!CLASSLOADERS_LOADED.contains(clazz.getClassLoader()))
      {         
         Map<Class, EjbDescriptor> ejbDescriptors = new DeploymentDescriptor(clazz).getEjbDescriptors();
         EJB_DESCRIPTOR_CACHE.putAll(ejbDescriptors);
         CLASSLOADERS_LOADED.add(clazz.getClassLoader());         
      }
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
      if ( clazz.isAnnotationPresent(STATEFUL) ) 
      {
          return STATEFUL_SESSION_BEAN;
      } 
      else if ( clazz.isAnnotationPresent(STATELESS) ) 
      {
          return STATELESS_SESSION_BEAN;
      } 
      else if ( clazz.isAnnotationPresent(SINGLETON) ) 
      {
          return SINGLETON_SESSION_BEAN;
      } 
      else if ( clazz.isAnnotationPresent(MESSAGE_DRIVEN) ) 
      {
          return MESSAGE_DRIVEN_BEAN;
      } 
      else if ( clazz.isAnnotationPresent(Entity.class) ) 
      {
          return ENTITY_BEAN;
      } 
      else 
      {          
         EjbDescriptor ejbDescriptor = getEjbDescriptor(clazz);
         if (ejbDescriptor == null) 
         {
            return JAVA_BEAN;
         }
         else
         {
            return ejbDescriptor.getBeanType();
         }
      }      
   }
      
   /**
    * Get the component name
    * @see Name
    */
   public static String getComponentName(Class<?> clazz)
   {
      String result = COMPONENT_NAME_CACHE.get(clazz);
      if (result==null)
      {
         result = searchComponentName(clazz);
         if (result!=null)
         {
            COMPONENT_NAME_CACHE.put(clazz, result);
         }
      }
      return result;
   }
   
   public static String searchComponentName(Class<?> clazz)
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
    * class
    * 
    */
   public static Class getEntityClass(Class clazz)
   {
      while (clazz != null && !Object.class.equals(clazz))
      {
         if (clazz.isAnnotationPresent(Entity.class))
         {
            return clazz;
         }
         else
         {
            EjbDescriptor ejbDescriptor = Seam.getEjbDescriptor(clazz);
            if (ejbDescriptor != null)
            {
               return ejbDescriptor.getBeanType() == ComponentType.ENTITY_BEAN ? clazz : null;
            }
            else
            {
               clazz = clazz.getSuperclass();
            }
         }
      }
      return null;
   }
   
   /**
    * Is the class a container-generated proxy class for an 
    * entity bean?
    */
   public static boolean isEntityClass(Class<?> clazz)
   {
      return getEntityClass(clazz)!=null;
   }
   
   public static String getEjbName(Class<?> clazz)
   {
       switch ( getComponentType(clazz) ) 
       {
           case ENTITY_BEAN:
           case JAVA_BEAN:
               return null;
           case STATEFUL_SESSION_BEAN:
               return clazz.isAnnotationPresent(STATEFUL) ? 
                     getStatefulEjbName(clazz) : getEjbNameFromDescriptor(clazz);
           case STATELESS_SESSION_BEAN:
               return clazz.isAnnotationPresent(STATELESS) ?
                     getStatelessEjbName(clazz) : getEjbNameFromDescriptor(clazz);
           case SINGLETON_SESSION_BEAN:
              return clazz.isAnnotationPresent(SINGLETON) ?
                     getSingletonEjbName(clazz) : getEjbNameFromDescriptor(clazz);
           case MESSAGE_DRIVEN_BEAN:
               return clazz.isAnnotationPresent(MESSAGE_DRIVEN) ?
                     getMessageDrivenEjbName(clazz) : getEjbNameFromDescriptor(clazz);
           default:
               throw new IllegalArgumentException();
       }
   }

   private static String getMessageDrivenEjbName(Class<?> clazz)
   {
      String mdName = name( clazz.getAnnotation(MESSAGE_DRIVEN) );
      return mdName.equals("") ? unqualifyClassName(clazz) : mdName;
   }

   private static String getStatelessEjbName(Class<?> clazz)
   {
      String statelessName = name( clazz.getAnnotation(STATELESS) );
      return statelessName.equals("") ? unqualifyClassName(clazz) : statelessName;
   }

   private static String getStatefulEjbName(Class<?> clazz)
   {
      String statefulName = name( clazz.getAnnotation(STATEFUL) );
      return statefulName.equals("") ? unqualifyClassName(clazz) : statefulName;
   }
   
   private static String getSingletonEjbName(Class<?> clazz)
   {
      String singletonName = name( clazz.getAnnotation(SINGLETON) );
      return singletonName.equals("") ? unqualifyClassName(clazz) : singletonName;
   }

   private static String getEjbNameFromDescriptor(Class<?> clazz)
   {
      EjbDescriptor ejbDescriptor = getEjbDescriptor(clazz);
      return ejbDescriptor==null ? null : ejbDescriptor.getEjbName();
   }
   
   private static String unqualifyClassName(Class<?> clazz) 
   {
      return Strings.unqualify( Strings.unqualify( clazz.getName() ), '$' );
   }
   
   public static boolean isInterceptionEnabled(Class<?> clazz)
   {
      ComponentType componentType = getComponentType(clazz);
      if ( componentType==ENTITY_BEAN )
      {
         return false;
      }
      else if ( getComponentType(clazz)==MESSAGE_DRIVEN_BEAN )
      {
         return true;
      }
      else if ( clazz.isAnnotationPresent(BypassInterceptors.class) )
      {
         return false;
      }
      else 
      {
         return true;
      }
   }
   
   /**
    * Mark the session for invalidation at the end of the 
    * request cycle
    * 
    * @deprecated use Session.instance().invalidate()
    */
   public static void invalidateSession()
   {
      Session.instance().invalidate();
   }
   
   /**
    * Is the session marked for invalidation?
    * 
    * @deprecated use Session.instance().isInvalidated()
    */
   public static boolean isSessionInvalid()
   {
      return Session.instance().isInvalid();
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
         Lifecycle.setupApplication();
         try
         {
            return Component.forName(name);
         }
         finally
         {
            Lifecycle.cleanupApplication();
         }
      }
   }
   
   public static String getVersion()
   {
      Package pkg = Seam.class.getPackage();
      if (pkg.getImplementationVersion() != null)
      {
         return pkg.getImplementationVersion();
      }

      // new way of getting Manifest version string
      // in case of module classloading for instance in JBoss AS7
      if (versionString == null)
      {
         final Enumeration<URL> resources;
         try
         {
            resources = Seam.class.getClassLoader().getResources("META-INF/MANIFEST.MF");
            while (resources.hasMoreElements() && versionString == null)
            {
               final URL url = resources.nextElement();
               final InputStream stream = url.openStream();
               if (stream != null)
                  try
                  {
                     final Manifest manifest = new Manifest(stream);
                     final Attributes mainAttributes = manifest.getMainAttributes();
                     if (mainAttributes != null && "Seam Core".equals(mainAttributes.getValue("Specification-Title")))
                     {
                        jarName = mainAttributes.getValue("Specification-Title");
                        versionString = mainAttributes.getValue("Specification-Version");
                     }
                  }
                  finally
                  {
                     try
                     {
                        stream.close();
                     }
                     catch (Throwable ignored)
                     {
                     }
                  }
            }
         }
         catch (IOException ignored)
         {
         }
      }
      return versionString;
   }
   
   public static void clearComponentNameCache()
   {
      COMPONENT_NAME_CACHE.clear();
      EJB_DESCRIPTOR_CACHE.clear();
   }
   
}
