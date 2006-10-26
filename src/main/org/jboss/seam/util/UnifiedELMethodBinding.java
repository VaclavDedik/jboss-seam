package org.jboss.seam.util;

import static org.jboss.seam.util.EL.EL_CONTEXT;
import static org.jboss.seam.util.EL.EXPRESSION_FACTORY;

import javax.el.MethodExpression;
import javax.faces.context.FacesContext;
import javax.faces.el.EvaluationException;
import javax.faces.el.MethodBinding;
import javax.faces.el.MethodNotFoundException;

public class UnifiedELMethodBinding extends MethodBinding
{
   private MethodExpression me;

   public UnifiedELMethodBinding(String expression, Class[] args)
   {
      me = EXPRESSION_FACTORY.createMethodExpression(EL_CONTEXT, expression, Object.class, args);
   }

   @Override
   public String getExpressionString()
   {
      return me.getExpressionString();
   }

   @Override
   public Class getType(FacesContext ctx) throws MethodNotFoundException
   {
      return me.getMethodInfo(EL_CONTEXT).getReturnType();
   }

   @Override
   public Object invoke(FacesContext ctx, Object[] args) throws EvaluationException, MethodNotFoundException
   {
      return me.invoke(EL_CONTEXT, args);
   }

   @Override
   public String toString()
   {
      return me.getExpressionString();
   }
}