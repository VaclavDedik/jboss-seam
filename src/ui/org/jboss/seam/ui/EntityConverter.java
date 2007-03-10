package org.jboss.seam.ui;

import static javax.faces.application.FacesMessage.SEVERITY_ERROR;
import static org.jboss.seam.annotations.Install.BUILT_IN;
import static org.jboss.seam.InterceptionType.NEVER;

import java.io.Serializable;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.List;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.ConverterException;
import javax.persistence.EntityManager;
import javax.persistence.Id;

import org.jboss.seam.Component;
import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.Install;
import org.jboss.seam.annotations.Intercept;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.annotations.Transactional;
import org.jboss.seam.annotations.jsf.Converter;
import org.jboss.seam.core.FacesMessages;
import org.jboss.seam.core.Expressions.ValueBinding;
import org.jboss.seam.log.Log;
import org.jboss.seam.log.Logging;
import org.jboss.seam.util.Reflections;

/**
 * This implementation of the EntityConverter is suitable for any Entity which
 * uses annotations
 * 
 * 
 */
@Name("org.jboss.seam.ui.entityConverter")
@Scope(ScopeType.CONVERSATION)
@Install(precedence = BUILT_IN)
@Converter
@Intercept(NEVER)
public class EntityConverter implements
         javax.faces.convert.Converter, Serializable
{
   
   private ValueBinding<EntityManager> entityManager;
   
   private Log log = Logging.getLog(EntityConverter.class);

   private String errorMessage = "Error selecting object";
   
   public void setEntityManager(ValueBinding<EntityManager> entityManager)
   {
      this.entityManager = entityManager;
   }
   
   private EntityManager getEntityManager() {
      if (entityManager==null)
      {
        return (EntityManager) Component.getInstance( "entityManager" );
      }
      else
      {
         return entityManager.getValue();
      }
   }

   protected void errorGettingIdMessage(UIComponent cmp, FacesContext facesContext, Object entity)
   {
      log.error("@Id annotation not on #0", entity.getClass());
      throw new ConverterException(FacesMessages.createFacesMessage(SEVERITY_ERROR, getErrorMessageKey(), getErrorMessage()));
   }

   protected String getErrorMessage()
   {
      return errorMessage;
   }

   protected String getErrorMessageKey()
   {
      return getEntityConverterKeyPrefix() + "idNotFound";
   }

   protected void invalidSelectionMessage(Class clazz, Object id)
   {
      log.error("Cannot load entity (#0 with id #1) from persistence context", clazz.getName(), id);
      throw new ConverterException(FacesMessages.createFacesMessage(SEVERITY_ERROR, getErrorMessageKey(), getErrorMessage()));
   }
   
   protected void entityManagerNotFoundMessage()
   {
      log.error("Entity Manager not found");
      throw new ConverterException(FacesMessages.createFacesMessage(SEVERITY_ERROR, getErrorMessageKey(), getErrorMessage()));
   }

   protected String getEntityConverterKeyPrefix()
   {
      return "org.jboss.seam.ui.entityConverter.";
   }

   /**
    * @param entity
    *           The entity to use
    * @param cmp
    *           The UIComponent this converter is attached to
    * @param facesContext
    *           The current facesContext
    * @return The ID of the entity as a string or null if unable to determine it
    */
   protected Object getIdFromEntity(UIComponent cmp, FacesContext facesContext,
            Object entity)
   {
      Object id = null;
      List<Field> fields = Reflections.getFields(entity.getClass(), Id.class);
      if (fields.size() == 1)
      {
         Field field = fields.get(0);
         boolean accessible = field.isAccessible();
         field.setAccessible(true);
         
         try {
            id = Reflections.get(field, entity);
         }
         catch (Exception e)
         {
            errorGettingIdMessage(cmp, facesContext, entity);
         }
         finally
         {
            field.setAccessible(accessible);
         }
      }
      else
      {
         List<Method> methods = Reflections.getGetterMethods(entity.getClass(), Id.class);
         if (methods.size() == 1)
         {
            try
            {
               id = Reflections.invoke(methods.get(0), entity, new Object[0]);
            }
            catch (Exception e)
            {
              errorGettingIdMessage(cmp, facesContext, entity);
            }
         }
      }
      if (id == null)
      {
         return NoSelectionConverter.NO_SELECTION_VALUE;
      } 
      else
      {
         return id;
      }
   }

   @SuppressWarnings("unchecked")
   @Transactional
   public String getAsString(FacesContext facesContext, UIComponent cmp, Object value) throws ConverterException
   {
      if (value == null)
      {
         return null;
      }
      if (value instanceof String) 
      {
         return (String) value;
      }
      return EntityConverterStore.instance().put(value.getClass(), getIdFromEntity(cmp, facesContext, value)).toString();
   }

   @Transactional
   public Object getAsObject(FacesContext facesContext, UIComponent cmp, String value) throws ConverterException
   {
      if (value == null)
      {
         return null;
      }
      Integer key = new Integer(value);
      Class clazz = EntityConverterStore.instance().getClass(key);
      Object id = EntityConverterStore.instance().getId(key);
      return loadEntityFromPersistenceContext(clazz, id);
   }

   /**
    * Retrieve the Entity from the PersistenceContext
    * 
    * @param clazz
    *           The class of the entity to load
    * @param id
    *           The id of the entity to load
    * @return The entity, null if not found
    */
   @SuppressWarnings("unchecked")
   protected Object loadEntityFromPersistenceContext(Class clazz, Object id)
   {
      if (id == null || clazz == null)
      {
         return null;
      }
      Object entity = null;
      if (getEntityManager() == null)
      {
         entityManagerNotFoundMessage();
      }
      entity = getEntityManager().find(clazz, id);
      if (entity == null)
      {
         invalidSelectionMessage(clazz, id);
         return null;
      }
      else
      {
         return entity;
      }
   }
}