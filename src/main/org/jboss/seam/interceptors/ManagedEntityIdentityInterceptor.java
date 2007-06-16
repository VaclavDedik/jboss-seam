package org.jboss.seam.interceptors;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.faces.model.DataModel;

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
 * @see org.jboss.seam.contexts.PassivatedEntity
 * 
 * @author Gavin King
 *
 */
@Interceptor(around=BijectionInterceptor.class)
public class ManagedEntityIdentityInterceptor extends AbstractInterceptor
{
   private static final long serialVersionUID = 3105217046803964083L;
   
   //TODO: cache much more - the list of fields, PassivatedEntity obects, etc
   //TODO: optimize serialization of these maps...
   private Map<String, PassivatedEntity> passivatedEntities = new HashMap<String, PassivatedEntity>();
   //TODO: keep the actual concrete class of the collection around, so that we can recreate it after nullifying
   private Map<String, List<PassivatedEntity>> passivatedEntityLists = new HashMap<String, List<PassivatedEntity>>();
   private Map<String, List<PassivatedEntity>> passivatedEntitySets = new HashMap<String, List<PassivatedEntity>>();
   private Map<String, Map<Object, PassivatedEntity>> passivatedEntityMaps = new HashMap<String, Map<Object, PassivatedEntity>>();
   
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
         if ( !PassivatedEntity.isTransactionRolledBackOrMarkedRollback() )
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
                     DataModel dataModel = null;
                     if (value instanceof DataModel)
                     {
                        dataModel = (DataModel) value;
                        value = dataModel.getWrappedData();
                        if ( !(value instanceof List) && !(value instanceof Map) )
                        {
                           //we can't handle DataModels that wrap a single entity directly!
                           continue;
                        }
                     }
                     entityRefsToIds(bean, field, value, dataModel);
                  }
               }
            }
         }
      }
   }

   private void entityRefsToIds(Object bean, Field field, Object value, DataModel dataModel) throws Exception
   {
      if (value instanceof List)
      {
         List values = (List) value;
         entityListToIdList(bean, field, values, dataModel);
      }
      else if (value instanceof Set)
      {
         Set values = (Set) value;
         entitySetToIdSet(bean, field, values, dataModel);
      }
      else if (value instanceof Map)
      {
         Map values = (Map) value;
         entityMapToIdMap(bean, field, values, dataModel);
      }
      else
      {
         entityRefToId(bean, field, value);
      }
   }

   private void entityListToIdList(Object bean, Field field, List values, DataModel dataModel) throws Exception
   {
      int count = 0;
      List<PassivatedEntity> list = new ArrayList<PassivatedEntity>();
      for ( int i=0; i<values.size(); i++ )
      {
         PassivatedEntity pi = PassivatedEntity.createPassivatedEntity( values.get(i) );
         if (pi==null)
         {
            list.add(null);
         }
         else
         {
            list.add(pi);
            //values.set(i, null);
            count++;
         }
      }
      if (count>0)
      {
         passivatedEntityLists.put( field.getName(), list );
         if ( count==values.size() )
         {
            nullify(bean, field, dataModel);
         }
      }
   }

   private void entityMapToIdMap(Object bean, Field field, Map values, DataModel dataModel) throws Exception
   {
      int count = 0;
      Map<Object, PassivatedEntity> map = new HashMap<Object, PassivatedEntity>();
      for ( Map.Entry me: (Set<Map.Entry>) values.entrySet() )
      {
         PassivatedEntity pe = PassivatedEntity.createPassivatedEntity( me.getValue() );
         if (pe!=null)
         {
            map.put( me.getKey(), pe );
            //values.remove( me.getKey() );
            count++;
         }
      }
      if (count>0)
      {
         passivatedEntityMaps.put( field.getName(), map );
         if ( count==values.size() )
         {
            nullify(bean, field, dataModel);
         }
      }
   }

   private void entitySetToIdSet(Object bean, Field field, Set values, DataModel dataModel) throws Exception
   {
      int count = 0;
      List<PassivatedEntity> list = new ArrayList<PassivatedEntity>();
      Iterator iter = values.iterator();
      while ( iter.hasNext() )
      {
         PassivatedEntity pe = PassivatedEntity.createPassivatedEntity( iter.next() );
         if (pe!=null)
         {
            list.add(pe);
            //iter.remove();
            count++;
         }
      }
      if (count>0)
      {
         passivatedEntitySets.put( field.getName(), list );
         if ( count==values.size() )
         {
            nullify(bean, field, dataModel);
         }
      }
   }

   private void entityRefToId(Object bean, Field field, Object value) throws Exception
   {
      PassivatedEntity pe = PassivatedEntity.createPassivatedEntity(value);
      if (pe!=null)
      {
         passivatedEntities.put( field.getName(), pe );
         Reflections.set(field, bean, null);
      }
   }
   
   private void nullify(Object bean, Field field, DataModel dataModel) throws Exception
   {
      if (dataModel==null)
      {
         Reflections.set(field, bean, null);
      }
      /*else
      {
         //TODO: put back in, once we figure
         //      out how to reconstruct it
         dataModel.setWrappedData(null);
      }*/
   }

   public void entityIdsToRefs(InvocationContext ctx) throws Exception
   {
      if ( passivatedEntities.size()>0 || passivatedEntityLists.size()>0 )
      {
         Object bean = ctx.getTarget();
         Class beanClass = bean.getClass();
         
         for ( Map.Entry<String, PassivatedEntity> entry: passivatedEntities.entrySet() )
         {
            entityIdToRef(bean, beanClass, entry);
         }
         passivatedEntities.clear();
         
         for ( Map.Entry<String, List<PassivatedEntity>> entry: passivatedEntityLists.entrySet() )
         {
            entityIdListToList(bean, beanClass, entry);
         }
         passivatedEntityLists.clear();

         for ( Map.Entry<String, List<PassivatedEntity>> entry: passivatedEntitySets.entrySet() )
         {
            entityIdSetToSet(bean, beanClass, entry);
         }
         passivatedEntitySets.clear();

         for ( Map.Entry<String, Map<Object, PassivatedEntity>> entry: passivatedEntityMaps.entrySet() )
         {
            entityIdMapToMap(bean, beanClass, entry);
         }
         passivatedEntityMaps.clear();
      }
   }

   private void entityIdListToList(Object bean, Class beanClass, Map.Entry<String, List<PassivatedEntity>> entry) throws Exception
   {
      Field field = getField( beanClass, entry.getKey() );
      List<PassivatedEntity> list = entry.getValue();
      List values = (List) getFieldValues(bean, field);
      boolean recreate = false;
      if (values==null)
      {
         recreate = true;
         values = new ArrayList( list.size() );
         //TODO: reconstruct a DataModel
         Reflections.set(field, bean, values);
      }
      for ( int i=0; i<list.size(); i++ )
      {
         if (recreate) values.add(null);
         PassivatedEntity pe = list.get(i);
         if ( pe!=null )
         {
            values.set( i, pe.toEntityReference() );
         }
      }
   }

   private void entityIdSetToSet(Object bean, Class beanClass, Map.Entry<String, List<PassivatedEntity>> entry) throws Exception
   {
      Field field = getField( beanClass, entry.getKey() );
      List<PassivatedEntity> list = entry.getValue();
      Set values = (Set) getFieldValues(bean, field);
      if (values==null)
      {
         values = new HashSet( list.size() );
         //TODO: reconstruct a DataModel
         Reflections.set(field, bean, values);
      }
      for ( PassivatedEntity pe: list )
      {
         Object reference = pe.toEntityReference();
         if (reference!=null)
         {
            values.add(reference);
         }
      }
   }

   private void entityIdMapToMap(Object bean, Class beanClass, Map.Entry<String, Map<Object, PassivatedEntity>> entry) throws Exception
   {
      Field field = getField( beanClass, entry.getKey() );
      Map<Object, PassivatedEntity> map = entry.getValue();
      Map values = (Map) getFieldValues(bean, field);
      if (values==null)
      {
         values = new HashMap( map.size() );
         //TODO: reconstruct a DataModel
         Reflections.set(field, bean, values);
      }
      for ( Map.Entry<Object, PassivatedEntity> me: map.entrySet() )
      {
         Object reference = me.getValue().toEntityReference();
         if (reference!=null)
         {
            values.put( me.getKey(), reference );
         }
      }
   }

   private void entityIdToRef(Object bean, Class beanClass, Map.Entry<String, PassivatedEntity> entry) throws IllegalAccessException
   {
      Object reference = entry.getValue().toEntityReference();
      if (reference!=null)
      {
         getField( beanClass, entry.getKey() ).set(bean, reference);
      }
   }
   
   private Field getField(Class beanClass, String fieldName)
   {
      Field field = Reflections.getField(beanClass, fieldName);
      if ( !field.isAccessible() ) field.setAccessible(true);
      return field;
   }

   private Object getFieldValues(Object bean, Field field) throws IllegalAccessException
   {
      Object value = field.get(bean);
      return value!=null && value instanceof DataModel ? 
            ( (DataModel) value ).getWrappedData() : value;
   }

}
