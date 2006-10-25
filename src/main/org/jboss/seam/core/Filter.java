package org.jboss.seam.core;

import java.util.Map;

import org.jboss.seam.InterceptionType;
import org.jboss.seam.ScopeType;
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

   @Override
   public String toString()
   {
      return "Filter(" + name + ")";
   }
}
