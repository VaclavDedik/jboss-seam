package org.jboss.seam.framework;

import org.jboss.seam.annotations.Transactional;
import org.jboss.seam.core.Expressions.ValueBinding;

/**
 * Manager component for an instance of any class.
 * 
 * @author Gavin King
 *
 */
public class Home<E>
{
   private Object id;
   protected E instance;
   private Class<E> entityClass;
   protected ValueBinding newInstance;   

   private String deletedMessage = "Successfully deleted";
   private String createdMessage = "Successfully created";
   private String updatedMessage = "Successfully updated";

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
      this.id = id;
   }
   
   public boolean isIdDefined()
   {
      return getId()!=null && !"".equals( getId() );
   }

   public void setInstance(E instance)
   {
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
   
}
