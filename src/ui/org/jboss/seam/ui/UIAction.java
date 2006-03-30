package org.jboss.seam.ui;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import javax.faces.component.UIParameter;
import javax.faces.context.FacesContext;

public class UIAction extends UIParameter
{
   
   public static final String COMPONENT_TYPE = "org.jboss.seam.ui.UIAction";
   
   private String outcome;
   
   public void setAction(String action)
   {
      this.outcome = action;
   }
   
   public String getAction()
   {
      return outcome;
   }
   
   private boolean isMethodBinding()
   {
      return outcome==null;
   }

   private String getMethodBindingExpression()
   {
      return getValueBinding("action").getExpressionString();
   }

   @Override
   public String getName()
   {
      return isMethodBinding() ? "actionMethod" : "actionOutcome";
   }
   
   @Override
   public Object getValue()
   {
      if ( isMethodBinding() )
      {
         String expression = getMethodBindingExpression();
         allowAction(expression);
         return expression.substring( 2, expression.length()-1 ); 
      }
      else
      {
         return outcome;
      }
   }

   private void allowAction(String expression)
   {
      Map applicationMap = getFacesContext().getExternalContext().getApplicationMap();
      Set actions;
      synchronized (UIAction.class)
      {
         actions = (Set) applicationMap.get("org.jboss.seam.actions");
         if (actions==null)
         {
            actions = new HashSet();
            applicationMap.put("org.jboss.seam.actions", actions);
         }
      }
      synchronized (actions)
      {
         actions.add(expression);
      }
   }
   
   @Override
   public void restoreState(FacesContext context, Object state) {
      Object[] values = (Object[]) state;
      super.restoreState(context, values[0]);
      outcome = (String) values[1];
   }

   @Override
   public Object saveState(FacesContext context) {
      Object[] values = new Object[2];
      values[0] = super.saveState(context);
      values[1] = outcome;
      return values;
   }

}
