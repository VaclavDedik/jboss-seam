package org.jboss.seam.pages;

import org.jboss.seam.core.Expressions.MethodBinding;
import org.jboss.seam.core.Expressions.ValueBinding;

public class Action
{
   private MethodBinding methodBinding;
   private ValueBinding valueBinding;
   private String outcome;
   
   public boolean isExecutable()
   {
      return valueBinding==null || 
           Boolean.TRUE.equals( valueBinding.getValue() );
   }
   
   public MethodBinding getMethodBinding()
   {
      return methodBinding;
   }
   public void setMethodBinding(MethodBinding methodBinding)
   {
      this.methodBinding = methodBinding;
   }
   
   public ValueBinding getValueBinding()
   {
      return valueBinding;
   }
   public void setValueBinding(ValueBinding valueBinding)
   {
      this.valueBinding = valueBinding;
   }

   public String getOutcome()
   {
      return outcome;
   }
   public void setOutcome(String outcome)
   {
      this.outcome = outcome;
   }
}
