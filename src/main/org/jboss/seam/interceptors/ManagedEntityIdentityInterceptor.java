package org.jboss.seam.interceptors;

import java.io.Serializable;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import javax.interceptor.AroundInvoke;
import javax.interceptor.InvocationContext;
import javax.persistence.EntityManager;
import javax.persistence.Id;

import org.hibernate.Session;
import org.jboss.seam.Component;
import org.jboss.seam.Seam;
import org.jboss.seam.core.TouchedContexts;
import org.jboss.seam.util.Reflections;

/**
 * Swizzles entity references around each invocation, maintaining
 * referential even across passivation of the stateful bean or
 * Seam-managed extended persistence context, and allowing for
 * more efficient replication.
 * 
 * @author Gavin King
 *
 */
public class ManagedEntityIdentityInterceptor extends AbstractInterceptor
{
   
   //TODO: cache much more - the list of fields, PassivatedEntity obects, etc
   
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
   
   @AroundInvoke
   public Object aroundInvoke(InvocationContext ctx) throws Exception
   {
      entityRefsToIds(ctx);
      Object result = ctx.proceed();
      entityIdsToRefs(ctx);
      return result;
   }
   
   public void entityRefsToIds(InvocationContext ctx) throws Exception
   {      
      Set<String> pcs = TouchedContexts.instance();
      if ( pcs.size()>0 )
      {
         Object bean = ctx.getTarget();
         Class beanClass = Seam.getBeanClass( bean.getClass() );
         for (; beanClass!=Object.class; beanClass=beanClass.getSuperclass())
         {
            Field[] fields = beanClass.getDeclaredFields();
            for (Field field: fields)
            {
               if ( !Modifier.isTransient( field.getModifiers() ) && !Modifier.isStatic( field.getModifiers() ) )
               {
                  if ( !field.isAccessible() ) field.setAccessible(true);
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
   }
   
   public void entityIdsToRefs(InvocationContext ctx) throws Exception
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
            for (; beanClass!=Object.class; beanClass=beanClass.getSuperclass())
            {
               try
               {
                  Field field = beanClass.getDeclaredField( pe.getFieldName() );
                  field.set(bean, reference);
                  break;
               }
               catch (NoSuchFieldException nsfe) {}
            }
         }
         list.clear();
      }
   }

   private static Object getId(Object bean, Class beanClass) throws Exception
   {
      for (; beanClass!=Object.class; beanClass=beanClass.getSuperclass() )
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
      }
      throw new IllegalArgumentException("no id property found for entity class: " + beanClass.getName());
   }
   
}
