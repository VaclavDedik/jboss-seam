package org.jboss.seam.pages;

import org.jboss.seam.core.Expressions.MethodExpression;
import org.jboss.seam.core.Expressions.ValueExpression;

public class Action
{
   private MethodExpression methodExpression;
   private ValueExpression valueExpression;
   private String outcome;
   
   public boolean isExecutable()
   {
      return valueExpression==null || 
           Boolean.TRUE.equals( valueExpression.getValue() );
   }
   
   public MethodExpression getMethodExpression()
   {
      return methodExpression;
   }
   public void setMethodExpression(MethodExpression methodExpression)
   {
      this.methodExpression = methodExpression;
   }
   
   public ValueExpression getValueExpression()
   {
      return valueExpression;
   }
   public void setValueExpression(ValueExpression valueExpression)
   {
      this.valueExpression = valueExpression;
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
