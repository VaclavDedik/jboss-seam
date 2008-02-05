package org.jboss.seam.ui.converter;

import static org.jboss.seam.ScopeType.CONVERSATION;
import static org.jboss.seam.annotations.Install.BUILT_IN;

import java.io.Serializable;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.ConverterException;

import org.jboss.seam.annotations.Create;
import org.jboss.seam.annotations.Install;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.annotations.Transactional;
import org.jboss.seam.annotations.intercept.BypassInterceptors;
import org.jboss.seam.annotations.faces.Converter;
import org.jboss.seam.core.Expressions.ValueExpression;
import org.jboss.seam.ui.converter.entityConverter.EntityLoader;
import org.jboss.seam.ui.converter.entityConverter.HibernateEntityLoader;
import org.jboss.seam.ui.converter.entityConverter.AbstractEntityLoader;

/**
 * Allows conversion of an entity to/from a key which can be written to a page.
 * 
 * Support is provided for JPA (by default) and Hibernate (with the session 
 * specified in components.xml)
 */
@Name("org.jboss.seam.ui.EntityConverter")
@Scope(CONVERSATION)
@Install(precedence = BUILT_IN)
@Converter
@BypassInterceptors
public class EntityConverter implements
         javax.faces.convert.Converter, Serializable
{
   
   private ValueExpression entityManager;
   private ValueExpression session;
   private AbstractEntityLoader store;

   @Create
   public void create()
   {
      if (getEntityManager() == null && getSession() != null)
      {
         store = HibernateEntityLoader.instance();
      }
      else
      {
         store = EntityLoader.instance();
      }
   }
   
   private void init()
   {
      if (getPersistenceContext() != null)
      {
         store.setPersistenceContext(getPersistenceContext().getValue());
      }
      store.validate();
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
      return store.put(value);
   }
   

   @Transactional
   public Object getAsObject(FacesContext facesContext, UIComponent cmp, String value) throws ConverterException
   {
      init();
      if (value == null)
      {
         return null;
      }
      return store.get(value);
   }
   
   public ValueExpression getEntityManager()
   {
      return entityManager; 
   }
   
   public void setEntityManager(ValueExpression entityManager)
   {
      this.entityManager = entityManager;
   }
   
   public ValueExpression getSession()
   {
      return session;
   }
   
   public void setSession(ValueExpression session)
   {
      this.session = session;
   }
   
   private ValueExpression getPersistenceContext() 
   {
      if (getEntityManager() != null)
      {
         return getEntityManager();
      }
      else
      {
         return getSession();
      }
   }
   
}