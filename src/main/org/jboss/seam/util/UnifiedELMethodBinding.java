package org.jboss.seam.util;

import javax.el.MethodExpression;
import javax.faces.context.FacesContext;
import javax.faces.el.EvaluationException;
import javax.faces.el.MethodBinding;
import javax.faces.el.MethodNotFoundException;

@SuppressWarnings("deprecation")
@Deprecated
public class UnifiedELMethodBinding extends MethodBinding
{
   private MethodExpression me;

   public UnifiedELMethodBinding() {}
   
   public UnifiedELMethodBinding(MethodExpression me)
   {
      this.me = me;
   }

   @Override
   public String getExpressionString()
   {
      return me.getExpressionString();
   }

   @Override
   public Class getType(FacesContext ctx) throws MethodNotFoundException
   {
      return me.getMethodInfo( ctx.getELContext() ).getReturnType();
   }

   @Override
   public Object invoke(FacesContext ctx, Object[] args) throws EvaluationException, MethodNotFoundException
   {
      return me.invoke( ctx.getELContext(), args);
   }

   @Override
   public String toString()
   {
      return me.getExpressionString();
   }
}