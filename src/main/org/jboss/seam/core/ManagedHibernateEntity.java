package org.jboss.seam.core;

import java.io.Serializable;

import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;

import org.hibernate.Session;
import org.jboss.seam.annotations.Transactional;
import org.jboss.seam.annotations.Unwrap;
import org.jboss.seam.util.Reflections;

/**
 * Manager component for a Hibernate entity instance. Allows
 * auto-fetching of contextual entities. The identifier
 * is determined by evaluating an EL expression and then
 * using JSF type conversion if necessary.
 * 
 * @author Gavin King
 *
 */
public class ManagedHibernateEntity
{
   private Session session;
   private Serializable id;
   private String entityClass;
   private String idClass;
   private Object newInstance;
   private String idConverterId;
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
      Class clazz = Reflections.classForName(entityClass);
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

   private Serializable getConvertedId() throws Exception
   {
      FacesContext facesContext = FacesContext.getCurrentInstance();
      if (idConverter==null)
      {
         if (idConverterId==null)
         {
            //TODO: guess the id class using @Id
            idConverter = facesContext.getApplication().createConverter( Reflections.classForName(idClass) );
         }
         else
         {
            idConverter = facesContext.getApplication().createConverter(idConverterId); //cache the lookup
         }
      }
      
      if (idConverter==null)
      {
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
