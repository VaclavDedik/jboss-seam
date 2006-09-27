package org.jboss.seam.util;

import javax.persistence.EntityManager;

import org.hibernate.FlushMode;
import org.hibernate.Session;

public class Persistence
{

   public static void setFlushModeManual(EntityManager entityManager)
   {
      if (entityManager.getDelegate() instanceof Session)
      {
         ( (Session) entityManager.getDelegate() ).setFlushMode(FlushMode.NEVER);
      }
      else
      {
         throw new IllegalArgumentException("FlushMode.MANUAL only supported for Hibernate EntityManager");
      }
   }

   public static boolean isDirty(EntityManager entityManager)
   {
      if (entityManager.getDelegate() instanceof Session)
      {
         return ( (Session) entityManager.getDelegate() ).isDirty();
      }
      else
      {
         return true; //best we can do!
      }
   }

   public static Object getId(Object bean, EntityManager entityManager) throws Exception
   {
      if (entityManager.getDelegate() instanceof Session)
      {
         return ( (Session) entityManager.getDelegate() ).getIdentifier(bean);
      }
      else
      {
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
