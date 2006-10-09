package org.jboss.seam.ui;

import javax.faces.component.UIParameter;
import javax.faces.context.FacesContext;

import org.jboss.seam.core.SafeActions;

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
      String viewId = getFacesContext().getViewRoot().getViewId();
      if ( isMethodBinding() )
      {
         String actionId = SafeActions.toActionId( viewId, getMethodBindingExpression() );
         SafeActions.instance().addSafeAction(actionId);
         return actionId;
      }
      else
      {
         return outcome;
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
