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
   private String converterId;
   private Converter converter;
   
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

   private Serializable getConvertedId()
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
         return (Serializable) converter.getAsObject( 
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
