package org.jboss.seam.framework;

import static org.jboss.seam.InterceptionType.NEVER;

import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.persistence.EntityManager;

import org.jboss.seam.annotations.Create;
import org.jboss.seam.annotations.Intercept;
import org.jboss.seam.annotations.Transactional;
import org.jboss.seam.annotations.Unwrap;
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
@Intercept(NEVER)
public class ManagedEntity
{
   private EntityManager entityManager;
   private Object id;
   private String entityClassName;
   private Class<?> entityClass;
   private String idClass;
   private Object instance;
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
   
   public String getEntityClass()
   {
      return entityClassName;
   }

   public void setEntityClass(String entityClass)
   {
      this.entityClassName = entityClass;
   }

   @Create
   public void initEntityClass() throws Exception
   {
      entityClass = Reflections.classForName(entityClassName);
   }
   
   @Unwrap @Transactional
   public Object getInstance() throws Exception
   {
      if ( id==null || "".equals(id) )
      {
         if (instance==null)
         {
            instance = createInstance();
         }
      }
      else
      {
         if (instance==null)
         {
            //we cache the instance so that it does not "disappear"
            //after remove() is called on the instance
            //is this really a Good Idea??
            getEntityManager().joinTransaction();
            instance =loadInstance( getConvertedId() );
         }
      }
      return instance;
   }

   protected Object createInstance() throws Exception
   {
      return entityClass.newInstance();
   }

   protected Object loadInstance(Object id)
   {
      return getEntityManager().find(entityClass, id);
   }
   
   //////////// TODO: copy/paste from ManagedHibernateEntity ///////////////////
   
   protected Object getConvertedId() throws Exception
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
