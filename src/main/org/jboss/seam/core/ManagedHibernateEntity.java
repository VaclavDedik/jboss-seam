package org.jboss.seam.core;

import java.io.Serializable;

import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;

import org.hibernate.Session;
import org.jboss.seam.annotations.Transactional;
import org.jboss.seam.annotations.Unwrap;

public class ManagedHibernateEntity
{
   private Session session;
   private Serializable id;
   private String entityClass;
   private Object newInstance;
   private String idConverterId;
   private String idClass;
   private Converter idConverter;
   
   public Session getSession()
   {
      return session;
   }

   public void setSession(Session session)
   {
      this.session = session;
   }

   public Serializable getId()
   {
      return id;
   }

   public void setId(Serializable id)
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

   @Unwrap @Transactional
   public Object getInstance() throws Exception
   {
      Class clazz = Class.forName(entityClass);
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
         return session.get( clazz, getConvertedId() );
      }
   }
   
   ////////////TODO: copy/paste from ManagedEntity ///////////////////

   private Serializable getConvertedId() throws ClassNotFoundException
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
         return (Serializable) idConverter.getAsObject( 
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
