package org.jboss.seam.interceptors;

import java.io.Serializable;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import javax.ejb.PostActivate;
import javax.ejb.PrePassivate;
import javax.interceptor.InvocationContext;
import javax.persistence.EntityManager;
import javax.persistence.Id;

import org.hibernate.Session;
import org.jboss.seam.Component;
import org.jboss.seam.Seam;
import org.jboss.seam.core.TouchedContexts;
import org.jboss.seam.util.Reflections;

public class PassivationInterceptor extends AbstractInterceptor
{
   private List<PassivatedEntity> list = new ArrayList<PassivatedEntity>();
   
   static class PassivatedEntity implements Serializable
   {
      private Object id;
      private String persistenceContext;
      private String fieldName;
      private Class<?> entityClass;
      
      PassivatedEntity(Object id, Class<?> entityClass, String fieldName, String persistenceContext)
      {
         super();
         this.id = id;
         this.persistenceContext = persistenceContext;
         this.fieldName = fieldName;
         this.entityClass = entityClass;
      }
      String getPersistenceContext()
      {
         return persistenceContext;
      }
      Object getId()
      {
         return id;
      }
      String getFieldName()
      {
         return fieldName;
      }
      Class<?> getEntityClass()
      {
         return entityClass;
      }
   }
   
   @PrePassivate
   public void passivate(InvocationContext ctx) throws Exception
   {      
      proceed(ctx);

      Set<String> pcs = TouchedContexts.instance();
      if ( pcs.size()>0 )
      {
         Object bean = ctx.getTarget();
         Class beanClass = Seam.getBeanClass( bean.getClass() );
         Field[] fields = beanClass.getFields(); //TODO: what about inherited fields!
         for (Field field: fields)
         {
            if ( !Modifier.isTransient( field.getModifiers() ) && !Modifier.isStatic( field.getModifiers() ) )
            {
               Object value = Reflections.get(field, bean);
               if (value!=null)
               {
                  Class entityClass = Seam.getEntityClass( value.getClass() );
                  if (entityClass!=null)
                  {
                     for (String persistenceContextName: pcs)
                     {
                        Object persistenceContext = Component.getInstance(persistenceContextName);
                        boolean managed = false;
                        if (persistenceContext instanceof EntityManager)
                        {
                           EntityManager em = (EntityManager) persistenceContext;
                           managed = em.contains(value);
                        }
                        else
                        {
                           Session session = (Session) persistenceContext;
                           managed = session.contains(value);
                        }
                        if (managed)
                        {
                           Object id = getId(value, entityClass);
                           list.add( new PassivatedEntity( id, entityClass, field.getName(), persistenceContextName ) );
                           Reflections.set(field, bean, null);
                           break;
                        }
                     }
                  }
               }
            }
         }
      }
   }
   
   @PostActivate
   public void activate(InvocationContext ctx) throws Exception
   {
      if ( list.size()>0 )
      {
         Object bean = ctx.getTarget();
         Class beanClass = Seam.getBeanClass( bean.getClass() );
         for (PassivatedEntity pe: list)
         {
            Object persistenceContext = Component.getInstance( pe.getPersistenceContext() );
            Object reference;
            if (persistenceContext instanceof EntityManager)
            {
               EntityManager em = (EntityManager) persistenceContext;
               reference = em.getReference( pe.getEntityClass(), pe.getId() );
            }
            else
            {
               Session session = (Session) persistenceContext;
               reference = session.load( pe.getEntityClass(), (Serializable) pe.getId() );
            }
            beanClass.getField( pe.getFieldName() ).set( bean, reference );
         }
         list.clear();
      }
      
      proceed(ctx);
   }

   private static Object getId(Object bean, Class beanClass) throws Exception
   {
      for (Field field: beanClass.getFields()) //TODO: superclasses
      {
         if ( field.isAnnotationPresent(Id.class) )
         {
            return Reflections.get(field, bean);
         }
      }
      for (Method method: beanClass.getMethods())
      {
         if ( method.isAnnotationPresent(Id.class) )
         {
            return Reflections.invoke(method, bean);
         }
      }
      throw new IllegalArgumentException("no id property found for entity class: " + beanClass.getName());
   }

   private static void proceed(InvocationContext ctx)
   {
      try
      {
         ctx.proceed();
      }
      catch (RuntimeException e)
      {
         throw e;
      }
      catch (Exception e)
      {
         throw new RuntimeException("exception in EJB lifecycle callback", e);
      }
   }
   
}
