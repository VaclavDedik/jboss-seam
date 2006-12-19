package org.jboss.seam.pages;

import org.jboss.seam.ScopeType;
import org.jboss.seam.core.Expressions.ValueBinding;

public class Put
{
   private String name;
   private ScopeType scope;
   private ValueBinding value;

   public String getName()
   {
      return name;
   }
   public void setName(String name)
   {
      this.name = name;
   }
   public ScopeType getScope()
   {
      return scope;
   }
   public void setScope(ScopeType scope)
   {
      this.scope = scope;
   }
   public ValueBinding getValue()
   {
      return value;
   }
   public void setValue(ValueBinding value)
   {
      this.value = value;
   }
   
}
