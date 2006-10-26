package org.jboss.seam.util;

import static org.jboss.seam.util.EL.EL_CONTEXT;
import static org.jboss.seam.util.EL.EXPRESSION_FACTORY;

import javax.el.ValueExpression;
import javax.faces.context.FacesContext;
import javax.faces.el.EvaluationException;
import javax.faces.el.PropertyNotFoundException;
import javax.faces.el.ValueBinding;

public class UnifiedELValueBinding extends ValueBinding
{
   private ValueExpression ve;

   public UnifiedELValueBinding(String expression)
   {
      ve = EXPRESSION_FACTORY.createValueExpression(EL_CONTEXT, expression, Object.class);
   }

   @Override
   public String getExpressionString()
   {
      return ve.getExpressionString();
   }

   @Override
   public Class getType(FacesContext ctx) throws EvaluationException, PropertyNotFoundException {
      return ve.getType(EL_CONTEXT);
   }

   @Override
   public Object getValue(FacesContext ctx) throws EvaluationException, PropertyNotFoundException {
   	return ve.getValue(EL_CONTEXT);
   }

   @Override
   public boolean isReadOnly(FacesContext ctx) throws EvaluationException, PropertyNotFoundException {
   	return ve.isReadOnly(EL_CONTEXT);
   }

   @Override
   public void setValue(FacesContext ctx, Object value) throws EvaluationException, PropertyNotFoundException {
      ve.setValue(EL_CONTEXT, value);
   }
   
   @Override
   public String toString()
   {
      return ve.getExpressionString();
   }
}