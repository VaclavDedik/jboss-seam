package org.jboss.seam.interceptors;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.List;

import org.jboss.seam.annotations.AroundInvoke;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Interceptor;
import org.jboss.seam.contexts.PassivatedEntity;
import org.jboss.seam.core.PersistenceContexts;
import org.jboss.seam.intercept.InvocationContext;
import org.jboss.seam.util.Reflections;

/**
 * Swizzles entity references around each invocation, maintaining
 * referential integrity even across passivation of the stateful 
 * bean or Seam-managed extended persistence context, and allowing 
 * for more efficient replication.
 * 
 * @author Gavin King
 *
 */
@Interceptor(around=BijectionInterceptor.class)
public class ManagedEntityIdentityInterceptor extends AbstractInterceptor
{
   
   //TODO: cache much more - the list of fields, PassivatedEntity obects, etc
   
   private List<PassivatedEntity> passivatedEntities = new ArrayList<PassivatedEntity>();
   
   @AroundInvoke
   public Object aroundInvoke(InvocationContext ctx) throws Exception
   {
      entityIdsToRefs(ctx);
      try
      {
         return ctx.proceed();
      }
      finally
      {
         if ( !PassivatedEntity.isTransactionMarkedRollback() )
         {
            entityRefsToIds(ctx);
         }
      }
   }
   
   public void entityRefsToIds(InvocationContext ctx) throws Exception
   {      
      PersistenceContexts touchedContexts = PersistenceContexts.instance();
      if ( touchedContexts!=null && touchedContexts.getTouchedContexts().size()>0 )
      {
         Object bean = ctx.getTarget();
         Class beanClass = bean.getClass();
         for (; beanClass!=Object.class; beanClass=beanClass.getSuperclass())
         {
            Field[] fields = beanClass.getDeclaredFields();
            for ( Field field: fields )
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
                     PassivatedEntity pi = PassivatedEntity.createPassivatedEntity( value, field.getName() );
                     if (pi!=null)
                     {
                        passivatedEntities.add(pi);
                        Reflections.set(field, bean, null);
                     }
                  }
               }
            }
         }
      }
   }

   public void entityIdsToRefs(InvocationContext ctx) throws Exception
   {
      if ( passivatedEntities.size()>0 )
      {
         Object bean = ctx.getTarget();
         Class beanClass = bean.getClass();
         for (PassivatedEntity pe: passivatedEntities)
         {
            Object reference = pe.toEntityReference();
            if (reference!=null)
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
         }
         passivatedEntities.clear();
      }
   
}
