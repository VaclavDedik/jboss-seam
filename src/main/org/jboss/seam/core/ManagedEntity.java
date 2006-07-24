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
   private String idConverterId;
   private String idClass;
   private Converter idConverter;
   
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
   
   private Object getConvertedId() throws Exception
   {
      FacesContext facesContext = FacesContext.getCurrentInstance();
      if (idConverterId!=null)
      {
         idConverter = facesContext.getApplication().createConverter(idConverterId); //cache the lookup
      }
      else if (idClass!=null)
      {
         idConverter = facesContext.getApplication().createConverter( Class.forName(idClass) );
      }
      
      if (idConverter==null)
      {
         //TODO: look for an @Id annotation and guess the id type!
         //converter = facesContext.getApplication().createConverter(idClass)
         return id;
      }
      else
      {
         return idConverter.getAsObject( 
               facesContext, 
               facesContext.getViewRoot(), 
               (String) id 
            );
      }
   }

   public String getIdConverterId()
   {
      return idConverterId;
   }

   public void setIdConverterId(String converterId)
   {
      this.idConverterId = converterId;
   }

   public Converter getIdConverter()
   {
      return idConverter;
   }

   public void setIdConverter(Converter converter)
   {
      this.idConverter = converter;
   }

   public String getIdClass()
   {
      return idClass;
   }

   public void setIdClass(String idClass)
   {
      this.idClass = idClass;
   }

}
