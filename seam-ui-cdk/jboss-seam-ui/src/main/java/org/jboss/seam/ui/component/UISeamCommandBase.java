package org.jboss.seam.ui.component;

import javax.faces.component.ActionSource;
import javax.faces.component.UIComponent;
import javax.faces.component.UIData;
import javax.faces.component.UIOutput;
import javax.faces.el.MethodBinding;
import javax.faces.event.ActionListener;
import javax.faces.model.DataModel;

public abstract class UISeamCommandBase extends UIOutput implements ActionSource
{
   
public abstract String getDisabled();
   
   public abstract void setDisabled(String disabled);
   
   public boolean disabled() {
     return new Boolean(getDisabled());
   }
   
   public abstract String getView();
   
   public abstract void setView(String view);
   
   public abstract MethodBinding getAction();
   
   public abstract void setAction(MethodBinding action);
   
   public abstract String getOutcome();
   
   public abstract void setOutcome(String outcome);
   
   public abstract String getPropagation();
   
   public abstract void setPropagation(String propagtion);
   
   public abstract String getPageflow();
   
   public abstract void setPageflow(String pageflow);
   
   public abstract String getFragment();
   
   public abstract void setFragment(String fragment);
   
   public abstract String getOnclick();
   
   public abstract void setOnclick(String onclick);

   public UISelection getSelection()
   {
      UIData parentUIData = getParentUIData();
      if (parentUIData!=null)
      {
         if ( parentUIData.getValue() instanceof DataModel )
         {
            String dataModelExpression = parentUIData.getValueBinding("value").getExpressionString();
            String dataModelName = dataModelExpression.substring(2, dataModelExpression.length()-1).replace('$','.');
            UISelection uiSelection = new UISelection();
            uiSelection.setDataModel(dataModelName);
            uiSelection.setVar( parentUIData.getVar() );
            return uiSelection;
         }
         else
         {
            return null;
         }
      }
      else
      {
         return null;
      }
   }
   
   public UIData getParentUIData()
   {
      UIComponent parent = this.getParent();
      while (parent!=null)
      {
         if (parent instanceof UIData)
         {
            return (UIData) parent;
         }
         else 
         {
            parent = parent.getParent();
         }
      }
      return null;
   }
   
   public void addActionListener(ActionListener listener)
   {
      // TODO Auto-generated method stub  
   }
   
   public void removeActionListener(ActionListener listener)
   {
      // TODO Auto-generated method stub
   }
   
   public ActionListener[] getActionListeners()
   {
      // TODO Auto-generated method stub
      return null;
   }

}
