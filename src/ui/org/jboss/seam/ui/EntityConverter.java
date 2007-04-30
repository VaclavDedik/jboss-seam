package org.jboss.seam.ui;

import static org.jboss.seam.InterceptionType.NEVER;
import static org.jboss.seam.annotations.Install.BUILT_IN;
import static org.jboss.seam.ScopeType.CONVERSATION;

import java.io.Serializable;

import javax.faces.component.StateHolder;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.ConverterException;
import javax.persistence.EntityManager;

import org.jboss.seam.annotations.Create;
import org.jboss.seam.annotations.Install;
import org.jboss.seam.annotations.Intercept;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.annotations.Transactional;
import org.jboss.seam.annotations.jsf.Converter;
import org.jboss.seam.core.Expressions.ValueBinding;

/**
 * Allows conversion of an entity to/from a key which can be written to a page.
 * 
 * Any annotated Entity will work, or any entity if a PersistenceProvider for your ORM exists
 */
@Name("org.jboss.seam.ui.entityConverter")
@Scope(CONVERSATION)
@Install(precedence = BUILT_IN)
@Converter
@Intercept(NEVER)
public class EntityConverter implements
         javax.faces.convert.Converter, Serializable
{
   
   private ValueBinding<EntityManager> entityManager;
   private EntityConverterStore entityIdentifierStore;

   @Create
   public void create()
   {
      entityIdentifierStore = EntityConverterStore.instance();
      
   }
   
   private void init()
   {
      if (getEntityManager() != null)
      {
         entityIdentifierStore.setEntityManager(getEntityManager());
      }
   }
   
   @SuppressWarnings("unchecked")
   @Transactional
   public String getAsString(FacesContext facesContext, UIComponent cmp, Object value) throws ConverterException
   {
      init();
      if (value == null)
      {
         return null;
      }
      if (value instanceof String) 
      {
         return (String) value;
      }
      return entityIdentifierStore.put(value).toString();
   }
   

   @Transactional
   public Object getAsObject(FacesContext facesContext, UIComponent cmp, String value) throws ConverterException
   {
      init();
      if (value == null)
      {
         return null;
      }
      return entityIdentifierStore.get(new Integer(value));
   }
   
   public void setEntityManager(ValueBinding<EntityManager> entityManager)
   {
      this.entityManager = entityManager;
   }
   
   private EntityManager getEntityManager() {
      return entityManager == null ? null : entityManager.getValue();
   }
   
   /*private boolean _transient;

   public boolean isTransient()
   {
      return _transient;
   }

   public void restoreState(FacesContext context, Object object)
   {
      Object[] state = (Object[]) object;
      entityManager = (ValueBinding<EntityManager>) state[0];
      
   }

   public Object saveState(FacesContext context)
   {
      Object[] state = new Object[1];
      state[0] = entityManager;
      return state;
   }

   public void setTransient(boolean newTransientValue)
   {
     this._transient = newTransientValue;
   }*/
}