package org.jboss.seam.util;

import javax.el.ValueExpression;
import javax.faces.context.FacesContext;
import javax.faces.el.EvaluationException;
import javax.faces.el.PropertyNotFoundException;
import javax.faces.el.ValueBinding;

@SuppressWarnings("deprecation")
@Deprecated
public class UnifiedELValueBinding extends ValueBinding
{
   private ValueExpression ve;

   public UnifiedELValueBinding() {}
   
   public UnifiedELValueBinding(ValueExpression ve)
   {
      this.ve = ve;
   }

   @Override
   public String getExpressionString()
   {
      return ve.getExpressionString();
   }

   @Override
   public Class getType(FacesContext ctx) throws EvaluationException, PropertyNotFoundException {
      return ve.getType( ctx.getELContext() );
   }

   @Override
   public Object getValue(FacesContext ctx) throws EvaluationException, PropertyNotFoundException {
   	return ve.getValue( ctx.getELContext() );
   }

   @Override
   public boolean isReadOnly(FacesContext ctx) throws EvaluationException, PropertyNotFoundException {
   	return ve.isReadOnly( ctx.getELContext() );
   }

   @Override
   public void setValue(FacesContext ctx, Object value) throws EvaluationException, PropertyNotFoundException {
      ve.setValue( ctx.getELContext(), value);
   }
   
   @Override
   public String toString()
   {
      return ve.getExpressionString();
   }
}