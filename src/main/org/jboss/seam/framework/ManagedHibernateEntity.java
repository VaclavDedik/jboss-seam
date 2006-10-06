package org.jboss.seam.framework;

import java.io.Serializable;

import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;

import org.hibernate.Session;
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
public class ManagedHibernateEntity<E> extends ManagedObject<E>
{
   private Session session;
   private Serializable id;
   private String idClass;
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
   
   public Class<E> getEntityClass()
   {
      return getObjectClass();
   }

   public void setEntityClass(Class<E> entityClass)
   {
      setObjectClass(entityClass);
   }

   
   @Override
   protected void initInstance() throws Exception
   {
      if ( getId()==null || "".equals( getId() ) )
      {
         super.initInstance();
      }
      else
      {
         //we cache the instance so that it does not "disappear"
         //after remove() is called on the instance
         //is this really a Good Idea??
         instance = loadInstance();
      }
   }

   protected E loadInstance() throws Exception
   {
      return loadInstance( getConvertedId() );
   }
   
   protected E loadInstance(Serializable id)
   {
      E result = (E) getSession().get( getObjectClass(), id );
      if (result==null) result = handleNotFound();
      return result;
   }

   protected E handleNotFound()
   {
      throw new EntityNotFoundException();
   }
   
   ////////////TODO: copy/paste from ManagedEntity ///////////////////

   protected Serializable getConvertedId() throws Exception
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
         return getId();
      }
      else
      {
         return (Serializable) idConverter.getAsObject( 
               facesContext, 
               facesContext.getViewRoot(), 
               (String) getId() 
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
