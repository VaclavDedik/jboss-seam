package org.jboss.seam.util;

import javax.persistence.EntityManager;

import java.lang.reflect.Method;
import java.lang.reflect.Field;

public class Persistence
{
   private static Object hibernateSession(EntityManager entityManager) {
      try {
          Object session = entityManager.getDelegate();

          if (Reflections.classForName("org.hibernate.Session").isAssignableFrom(session.getClass())) {
              return session;
          }
      } catch (Exception e) {
          // eat it
      }

      return null;
   }

   public static void setFlushModeManual(EntityManager entityManager)
   {
       Object session = hibernateSession(entityManager);
       if (session != null) {           
           try {
               Class flushMode = Reflections.classForName("org.hibernate.FlushMode");
               Method setFlushMode = Reflections.getSetterMethod(session.getClass(), "flushMode");
               Object never = Reflections.getField(flushMode,"NEVER").get(null);
               Reflections.invokeAndWrap(setFlushMode, session, never);
           } catch (Exception e) {
               throw new IllegalArgumentException("FlushMode.MANUAL only supported for Hibernate EntityManager", e);
           }

       }  else {
           throw new IllegalArgumentException("FlushMode.MANUAL only supported for Hibernate EntityManager");
       }

   }

   public static boolean isDirty(EntityManager entityManager)
   {
       Object session = hibernateSession(entityManager);
       if (session != null) {
           try {
               Method isDirty = session.getClass().getMethod("isDirty", new Class[] {});
               return (Boolean) Reflections.invokeAndWrap(isDirty, session);
           } catch (NoSuchMethodException e) {
               return true; // same asssumption as below
           }
       } else {
         return true; //best we can do!
      }
   }

   public static Object getId(Object bean, EntityManager entityManager) 
   {
      Object session = hibernateSession(entityManager);
      if (session != null) {
          try {
              Method getIdentifier = session.getClass().getMethod("getIdentifier", 
                                                                  new Class[] {Object.class});
              return Reflections.invokeAndWrap(getIdentifier, session, bean);
          } catch (NoSuchMethodException e) {
              throw new IllegalArgumentException("Please use Hibernate as the persistence provider");
          }
      } else {
         throw new IllegalArgumentException("Please use Hibernate as the persistence provider");
      }
      /*for (Class beanClass=entityClass; beanClass!=Object.class; beanClass=beanClass.getSuperclass() )
      {
         for (Field field: beanClass.getDeclaredFields()) //TODO: superclasses
         {
            if ( field.isAnnotationPresent(Id.class) )
            {
               if ( !field.isAccessible() ) field.setAccessible(true);
               return Reflections.get(field, bean);
            }
         }
         for (Method method: beanClass.getDeclaredMethods())
         {
            if ( method.isAnnotationPresent(Id.class) )
            {
               if ( !method.isAccessible() ) method.setAccessible(true);
               return Reflections.invoke(method, bean);
            }
         }
      }
      throw new IllegalArgumentException("no id property found for entity class: " + entityClass.getName());*/
   }

}
