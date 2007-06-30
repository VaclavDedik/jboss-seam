package org.jboss.seam;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

import javax.persistence.EmbeddedId;
import javax.persistence.Id;
import javax.persistence.PostLoad;
import javax.persistence.PrePersist;
import javax.persistence.PreRemove;
import javax.persistence.PreUpdate;
import javax.persistence.Version;

import org.jboss.seam.contexts.Contexts;
import org.jboss.seam.util.Reflections;

/**
 * Metamodel class for entity classes.
 * 
 * A class will be identified as an entity class
 * if it has an @Entity annotation.
 * 
 * @author Gavin King
 *
 */
public class Entity extends Model
{
   
   private Method preRemoveMethod;
   private Method prePersistMethod;
   private Method preUpdateMethod;
   private Method postLoadMethod;
   private Method identifierGetter;
   private Field identifierField;
   private Method versionGetter;
   private Field versionField;

   public Entity(Class<?> beanClass)
   {
      super(beanClass);
      
      for ( Class<?> clazz=beanClass; clazz!=Object.class; clazz = clazz.getSuperclass() )
      {

         for ( Method method: clazz.getDeclaredMethods() )
         {
            //TODO: does the spec allow multiple lifecycle method
            //      in the entity class heirarchy?
            if ( method.isAnnotationPresent(PreRemove.class) )
            {
               preRemoveMethod = method;
            }
            if ( method.isAnnotationPresent(PrePersist.class) )
            {
               prePersistMethod = method;
            }
            if ( method.isAnnotationPresent(PreUpdate.class) )
            {
               preUpdateMethod = method;
            }
            if ( method.isAnnotationPresent(PostLoad.class) )
            {
               postLoadMethod = method;
            }
            if ( method.isAnnotationPresent(Id.class) || method.isAnnotationPresent(EmbeddedId.class))
            {
               identifierGetter = method;
            }
            if ( method.isAnnotationPresent(Version.class) )
            {
               versionGetter = method;
            }
            
            if ( !method.isAccessible() )
            {
               method.setAccessible(true);
            }
         }
         
         if (identifierGetter==null)
         {
            for ( Field field: clazz.getDeclaredFields() )
            {
               if ( field.isAnnotationPresent(Id.class) || field.isAnnotationPresent(EmbeddedId.class))
               {
                  identifierField = field;
                  if ( !field.isAccessible() )
                  {
                     field.setAccessible(true);
                  }
               }
               if ( field.isAnnotationPresent(Version.class) )
               {
                  versionField = field;
                  if ( !field.isAccessible() )
                  {
                     field.setAccessible(true);
                  }
               }
            }
         }
         
      }
      
   }

   public Method getPostLoadMethod()
   {
      return postLoadMethod;
   }

   public Method getPrePersistMethod()
   {
      return prePersistMethod;
   }

   public Method getPreRemoveMethod()
   {
      return preRemoveMethod;
   }

   public Method getPreUpdateMethod()
   {
      return preUpdateMethod;
   }

   public Field getIdentifierField()
   {
      return identifierField;
   }

   public Method getIdentifierGetter()
   {
      return identifierGetter;
   }
   
   public Field getVersionField()
   {
      return versionField;
   }

   public Method getVersionGetter()
   {
      return versionGetter;
   }
   
   public Object getIdentifier(Object entity)
   {
      if (identifierGetter!=null)
      {
         return Reflections.invokeAndWrap(identifierGetter, entity);
      }
      else if (identifierField!=null)
      {
         return Reflections.getAndWrap(identifierField, entity);
      }
      else
      {
         throw new IllegalStateException("@Id attribute not found for entity class: " + getBeanClass().getName());
      }
   }

   public Object getVersion(Object entity)
   {
      if (versionGetter!=null)
      {
         return Reflections.invokeAndWrap(versionGetter, entity);
      }
      else if (versionField!=null)
      {
         return Reflections.getAndWrap(versionField, entity);
      }
      else
      {
         return null;
      }
   }

   public static Entity forClass(Class clazz)
   {
      if ( !Contexts.isApplicationContextActive() )
      {
         throw new IllegalStateException("No application context active");
      }
      
      Class entityClass = Seam.getEntityClass(clazz);
      
      if (entityClass==null)
      {
         throw new IllegalArgumentException("Not an entity class: " + clazz.getName());
      }
      
      String name = getModelName(entityClass);
      Model model = (Model) Contexts.getApplicationContext().get(name);
      if ( model==null || !(model instanceof Entity) )
      {
         Entity entity = new Entity(entityClass);
         Contexts.getApplicationContext().set(name, entity);
         return entity;
      }
      else
      {
         return (Entity) model;
      }
   }

}
