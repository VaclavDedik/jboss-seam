package org.jboss.seam.core;

import static org.jboss.seam.InterceptionType.NEVER;

import org.jboss.seam.Component;
import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.Intercept;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.annotations.Unwrap;

/**
 * Allows creation of an alias to some existing context
 * variable via components.xml.
 * 
 * @author Gavin King
 */
@Scope(ScopeType.STATELESS)
@Intercept(NEVER)
public class Alias
{
   
   private String name;
   private String expression;
   
   @Unwrap
   public Object getValue()
   {
      //Note that by making this component stateless, we ensure
      //that this method never gets called if create=false
      if (expression==null)
      {
         return Component.getInstance(name);
      }
      else
      {
         return Expressions.instance().createValueBinding(expression).getValue();
      }
   }

   public String getName()
   {
      return name;
   }

   public void setName(String name)
   {
      this.name = name;
   }

   public String getExpression()
   {
      return expression;
   }

   public void setExpression(String expression)
   {
      this.expression = expression;
   }
   
}
