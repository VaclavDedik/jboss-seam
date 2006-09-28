package org.jboss.seam.interceptors;

import java.io.Serializable;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import javax.interceptor.AroundInvoke;
import javax.interceptor.InvocationContext;
import javax.persistence.EntityManager;

import org.hibernate.Session;
import org.jboss.seam.Component;
import org.jboss.seam.Seam;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Interceptor;
import org.jboss.seam.core.TouchedContexts;
import org.jboss.seam.util.Persistence;
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
@Interceptor(around=BijectionInterceptor.class)
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
      entityIdsToRefs(ctx);
      Object result = ctx.proceed();
      entityRefsToIds(ctx);
      return result;
   }
   
   public void entityRefsToIds(InvocationContext ctx) throws Exception
   {      
      Set<String> pcs = TouchedContexts.instance();
      if ( pcs!=null && pcs.size()>0 )
      {
         Object bean = ctx.getTarget();
         Class beanClass = bean.getClass();
         for (; beanClass!=Object.class; beanClass=beanClass.getSuperclass())
         {
            Field[] fields = beanClass.getDeclaredFields();
            for (Field field: fields)
            {
               boolean ignoreField = Modifier.isTransient( field.getModifiers() ) || 
                  Modifier.isStatic( field.getModifiers() )
                  || field.isAnnotationPresent(In.class);
               if ( !ignoreField )
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
                           boolean managed;
                           Object id;
                           if (persistenceContext instanceof EntityManager)
                           {
                              EntityManager em = (EntityManager) persistenceContext;
                              managed = em.contains(value);
                              id = managed ? Persistence.getId(value, em) : null;
                           }
                           else
                           {
                              Session session = (Session) persistenceContext;
                              managed = session.contains(value);
                              id = managed ? session.getIdentifier(value) : null;
                           }
                           if (managed)
                           {
                              if (id==null)
                              {
                                 throw new IllegalStateException("could not get id of: " + beanClass.getName() + '.' + field.getName());
                              }
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
         Class beanClass = bean.getClass();
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
                  if ( !field.isAccessible() ) field.setAccessible(true);
                  field.set(bean, reference);
                  break;
               }
               catch (NoSuchFieldException nsfe) {}
            }
         }
         list.clear();
      }
   }
   
}
