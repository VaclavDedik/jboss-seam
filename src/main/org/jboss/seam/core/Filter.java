package org.jboss.seam.core;

import java.util.Map;

import org.jboss.seam.Component;
import org.jboss.seam.InterceptionType;
import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.Create;
import org.jboss.seam.annotations.Intercept;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.core.Expressions.ValueBinding;

/**
 * Support for declarative application of
 * Hibernate filters to persistence contexts.
 * 
 * @see org.hibernate.Filter
 * @see ManagedHibernateSession
 * @see ManagedPersistenceContext
 * @author Gavin King
 */
@Intercept(InterceptionType.NEVER)
@Scope(ScopeType.APPLICATION)
public class Filter
{
   private String name;
   private Map<String, ValueBinding> parameters;
   private ValueBinding enabled;
   
   @Create
   public void create(Component component)
   {
      //default the filter name to the component name
      if (name==null)
      {
         name = component.getName();
      }
   }
   
   /**
    * The filter parameters.
    * 
    * @see org.hibernate.Filter#setParameter(String, Object)
    */
   public Map<String, ValueBinding> getParameters()
   {
      return parameters;
   }
   public void setParameters(Map<String, ValueBinding> parameters)
   {
      this.parameters = parameters;
   }
   
   /**
    * The Hibernate filter name.
    * 
    * @see org.hibernate.Session#enableFilter(String)
    */
   public String getName()
   {
      return name;
   }
   
   public void setName(String name)
   {
      this.name = name;
   }
   
   public boolean isFilterEnabled()
   {
      ValueBinding enabledValueBinding = getEnabled();
      if (enabledValueBinding==null)
      {
         return true;
      }
      else
      {
         Boolean enabled = (Boolean) enabledValueBinding.getValue();
         return enabled!=null && enabled;
      }
   }

   @Override
   public String toString()
   {
      return "Filter(" + name + ")";
   }

   public ValueBinding getEnabled()
   {
      return enabled;
   }

   public void setEnabled(ValueBinding enabled)
   {
      this.enabled = enabled;
   }
}
