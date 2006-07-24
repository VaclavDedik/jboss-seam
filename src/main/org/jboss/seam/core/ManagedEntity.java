package org.jboss.seam.core;

import static org.jboss.seam.InterceptionType.NEVER;

import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.persistence.EntityManager;

import org.jboss.seam.annotations.Intercept;
import org.jboss.seam.annotations.Unwrap;

@Intercept(NEVER)
public class ManagedEntity
{
   private EntityManager entityManager;
   private Object id;
   private String entityClass;
   private Object newInstance;
   private String converterId;
   private Converter converter;
   
   public EntityManager getEntityManager()
   {
      return entityManager;
   }

   public void setEntityManager(EntityManager entityManager)
   {
      this.entityManager = entityManager;
   }

   public Object getId()
   {
      return id;
   }

   public void setId(Object id)
   {
      this.id = id;
   }
   
   public String getEntityClass()
   {
      return entityClass;
   }

   public void setEntityClass(String entityClass)
   {
      this.entityClass = entityClass;
   }

   @Unwrap
   public Object getInstance() throws Exception
   {
      Class<?> clazz = Class.forName(entityClass);
      if (id==null)
      {
         if (newInstance==null)
         {
            newInstance = clazz.newInstance();
         }
         return newInstance;
      }
      else
      {
         return entityManager.find( clazz, getConvertedId() );
      }
   }
   
   //////////// TODO: copy/paste from ManagedHibernateEntity ///////////////////
   
   private Object getConvertedId()
   {
      FacesContext facesContext = FacesContext.getCurrentInstance();
      if (converterId!=null)
      {
         converter = facesContext.getApplication().createConverter(converterId); //cache the lookup
      }
      
      if (converter==null)
      {
         //TODO: look for an @Id annotation and guess the id type!
         //converter = facesContext.getApplication().createConverter(idClass)
         return id;
      }
      else
      {
         return converter.getAsObject( 
               facesContext, 
               facesContext.getViewRoot(), 
               (String) id 
            );
      }
   }

   public String getConverterId()
   {
      return converterId;
   }

   public void setConverterId(String converterId)
   {
      this.converterId = converterId;
   }

   public Converter getConverter()
   {
      return converter;
   }

   public void setConverter(Converter converter)
   {
      this.converter = converter;
   }


}
