package org.jboss.seam.persistence;

import static org.jboss.seam.util.JSF.DATA_MODEL;
import static org.jboss.seam.util.JSF.getWrappedData;
import static org.jboss.seam.util.JSF.setWrappedData;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.jboss.seam.Component;
import org.jboss.seam.Seam;
import org.jboss.seam.annotations.In;
import org.jboss.seam.contexts.Contexts;
import org.jboss.seam.util.Reflections;

/**
 * @author Gavin King
 * @author Pete Muir
 * @author Norman Richards
 *
 */
public class ManagedEntityStateManager
{

   public void entityRefsToIds(Object controllerBean, Component component) throws Exception
   {
      if ( touchedContextsExist() )
      {
         Class beanClass = controllerBean.getClass();
         for (; beanClass!=Object.class; beanClass=beanClass.getSuperclass())
         {
            for ( Field field: beanClass.getDeclaredFields() )
            {
               if ( !ignore(field) )
               {
                  Object value = getFieldValue(controllerBean, field);
                  if (value!=null)
                  {
                     Object dataModel = null;
                     if ( DATA_MODEL.isInstance(value) )
                     {
                        dataModel = value;
                        value = getWrappedData(dataModel);
                     }
                     if ( isRef(value) )
                     {
                        saveWrapper(controllerBean, component, field, dataModel, value);
                     }
                     else
                     {
                        clearWrapper(component, field);
                     }
                  }
                  else
                  {
                     clearWrapper(component, field);
                  }
               }
            }
         }
      }
   }

   public void entityIdsToRefs(Object controllerBean, Component component) throws Exception
   {      
      if ( touchedContextsExist() )
      {
         Class beanClass = controllerBean.getClass();
         for (; beanClass!=Object.class; beanClass=beanClass.getSuperclass())
         {
            for ( Field field: beanClass.getDeclaredFields() )
            {
               if ( !ignore(field) )
               {
                  Object value = getFieldValue(controllerBean, field);
                  Object dataModel = null;
                  if (value!=null && DATA_MODEL.isInstance(value) )
                  {
                     dataModel = value;
                  }
                  //TODO: be more selective
                  getFromWrapper(controllerBean, component, field, dataModel);
               }
            }
         }
      }
   }

   private boolean isRef(Object value)
   {
      //TODO: could do better by checking if the
      //      collection really contains an entity
      return value instanceof List || 
            value instanceof Map || 
            value instanceof Set || 
            Seam.getEntityClass(value.getClass()) != null;
   }

   private Object getFieldValue(Object bean, Field field) throws Exception
   {
      if ( !field.isAccessible() ) field.setAccessible(true);
      Object value = Reflections.get(field, bean);
      return value;
   }

   private boolean ignore(Field field)
   {
      return Modifier.isTransient( field.getModifiers() ) || 
            Modifier.isStatic( field.getModifiers() )
            || field.isAnnotationPresent(In.class);
   }

   private boolean touchedContextsExist()
   {
       PersistenceContexts touchedContexts = PersistenceContexts.instance();
       return touchedContexts!=null && touchedContexts.getTouchedContexts().size()>0;
   }

   private String getFieldId(Component component, Field field)
   {
      return component.getName() + '.' + field.getName();
   }

   private void saveWrapper(Object bean, Component component, Field field, Object dataModel, Object value) throws Exception
   {
      Contexts.getConversationContext().set( getFieldId(component, field), value );
      if (dataModel==null)
      {
         Reflections.set(field, bean, null);
      }
      else
      {
         setWrappedData(dataModel, null);
      }
   }

   private void clearWrapper(Component component, Field field) throws Exception
   {
      Contexts.getConversationContext().remove( getFieldId(component, field) );
   }

   private void getFromWrapper(Object bean, Component component, Field field, Object dataModel) throws Exception
   {
      Object value =Contexts.getConversationContext().get( getFieldId(component, field) );
      if (value!=null)
      {
         if (dataModel==null)
         {
            Reflections.set(field, bean, value);
         }
         else
         {
            setWrappedData(dataModel, value);
         }
      }
   }

   
}
