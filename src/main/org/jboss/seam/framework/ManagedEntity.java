package org.jboss.seam.framework;

import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.persistence.EntityManager;

import org.jboss.seam.util.Reflections;

/**
 * Manager component for an EJB 3.0 entity instance. Allows
 * auto-fetching of contextual entities. The identifier
 * is determined by evaluating an EL expression and then
 * using JSF type conversion if necessary.
 * 
 * @author Gavin King
 *
 */
public class ManagedEntity<E> extends ManagedObject<E>
{
   private EntityManager entityManager;
   private Object id;
   private String idClass;
   private String idConverterId;
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
         getEntityManager().joinTransaction();
         instance = loadInstance();
      }            
   }

   protected E loadInstance() throws Exception
   {
      return loadInstance( getConvertedId() );
   }

   protected E loadInstance(Object id) throws Exception
   {
      E result = getEntityManager().find( getObjectClass(), id );
      if (result==null) result = handleNotFound();
      return result;
   }

   protected E handleNotFound()
   {
      throw new EntityNotFoundException();
   }
   
   //////////// TODO: copy/paste from ManagedHibernateEntity ///////////////////
   
   protected Object getConvertedId() throws Exception
   {
      
      if ( !(getId() instanceof String) ) return getId();
      
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
         return idConverter.getAsObject( 
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

   public Class<E> getEntityClass()
   {
      return getObjectClass();
   }

   public void setEntityClass(Class<E> entityClass)
   {
      setObjectClass(entityClass);
   }

}
