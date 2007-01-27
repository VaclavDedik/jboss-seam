package org.jboss.seam.framework;

import static javax.faces.application.FacesMessage.SEVERITY_INFO;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.Create;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.annotations.Transactional;
import org.jboss.seam.core.Expressions.ValueBinding;

/**
 * Base class for components which provide persistence
 * operations to a managed entity instance. This class 
 * may be reused by either configuration or extension, 
 * and may be bound directly to a view, or accessed by 
 * some intermediate Seam component.
 * 
 * @author Gavin King
 *
 */
@Scope(ScopeType.CONVERSATION)
public abstract class Home<T, E> extends MutableController<T>
{
   private static final long serialVersionUID = -5462396456614090423L;
   
   private Object id;
   protected E instance;
   private Class<E> entityClass;
   protected ValueBinding newInstance;

   private String deletedMessage = "Successfully deleted";
   private String createdMessage = "Successfully created";
   private String updatedMessage = "Successfully updated";
   
   protected void updatedMessage()
   {
      getFacesMessages().addFromResourceBundle( SEVERITY_INFO, getUpdatedMessageKey(), getUpdatedMessage() );
   }
   
   protected void deletedMessage()
   {
      getFacesMessages().addFromResourceBundle( SEVERITY_INFO, getDeletedMessageKey(), getDeletedMessage() );
   }
   
   protected void createdMessage()
   {
      getFacesMessages().addFromResourceBundle( SEVERITY_INFO, getCreatedMessageKey(), getCreatedMessage() );
   }

   @Create
   public void create()
   {
      if ( getEntityClass()==null )
      {
         throw new IllegalStateException("entityClass is null");
      }
   }

   @Transactional
   public E getInstance()
   {
      if (instance==null)
      {
         initInstance();
      }
      return instance;
   }

   protected void initInstance()
   {
      if ( isIdDefined() )
      {
         //we cache the instance so that it does not "disappear"
         //after remove() is called on the instance
         //is this really a Good Idea??
         setInstance( find() );
      }
      else
      {
         setInstance( createInstance() );
      }
   }
   
   protected E find()
   {
      return null;
   }

   protected E handleNotFound()
   {
      throw new EntityNotFoundException();
   }

   protected E createInstance()
   {
      if (newInstance!=null)
      {
         return (E) newInstance.getValue();
      }
      else if (entityClass!=null)
      {
         try
         {
            return entityClass.newInstance();
         }
         catch (Exception e)
         {
            throw new RuntimeException(e);
         }
      }
      else
      {
         return null;
      }
   }

   public Class<E> getEntityClass()
   {
      if (entityClass==null)
      {
         Type type = getClass().getGenericSuperclass();
         if (type instanceof ParameterizedType)
         {
            ParameterizedType paramType = (ParameterizedType) type;
            entityClass = (Class<E>) paramType.getActualTypeArguments()[1];
         }
         else
         {
            throw new IllegalArgumentException("Could not guess entity class by reflection");
         }
      }
      return entityClass;
   }

   public void setEntityClass(Class<E> entityClass)
   {
      this.entityClass = entityClass;
   }
   
   public Object getId()
   {
      return id;
   }

   public void setId(Object id)
   {
      if ( setDirty(this.id, id) ) setInstance(null);
      this.id = id;
   }
   
   protected void assignId(Object id)
   {
      setDirty(this.id, id);
      this.id = id;
   }
   
   public boolean isIdDefined()
   {
      return getId()!=null && !"".equals( getId() );
   }

   public void setInstance(E instance)
   {
      setDirty(this.instance, instance);
      this.instance = instance;
   }

   public ValueBinding getNewInstance()
   {
      return newInstance;
   }

   public void setNewInstance(ValueBinding newInstance)
   {
      this.newInstance = newInstance;
   }

   public String getCreatedMessage()
   {
      return createdMessage;
   }

   public void setCreatedMessage(String createdMessage)
   {
      this.createdMessage = createdMessage;
   }

   public String getDeletedMessage()
   {
      return deletedMessage;
   }

   public void setDeletedMessage(String deletedMessage)
   {
      this.deletedMessage = deletedMessage;
   }

   public String getUpdatedMessage()
   {
      return updatedMessage;
   }

   public void setUpdatedMessage(String updatedMessage)
   {
      this.updatedMessage = updatedMessage;
   }
   
   protected String getMessageKeyPrefix()
   {
      String className = getEntityClass().getName();
      return className.substring( className.lastIndexOf('.') + 1 ) + '_';
   }
   
   protected String getCreatedMessageKey()
   {
      return getMessageKeyPrefix() + "created";
   }
   
   protected String getUpdatedMessageKey()
   {
      return getMessageKeyPrefix() + "updated";
   }
   
   protected String getDeletedMessageKey()
   {
      return getMessageKeyPrefix() + "deleted";
   }
   
}
