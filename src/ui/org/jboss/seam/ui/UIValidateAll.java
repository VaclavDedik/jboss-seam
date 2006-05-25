package org.jboss.seam.ui;

import java.util.List;

import javax.faces.component.UIComponentBase;
import javax.faces.component.UIInput;

public class UIValidateAll extends UIComponentBase
{
   public static final String COMPONENT_TYPE = "org.jboss.seam.ui.UIValidateAll";
      
   @Override
   public String getFamily()
   {
      return "org.jboss.seam.ui.ValidateAll";
   }

   @Override
   public List getChildren()
   {
      addValidators( super.getChildren() );
      return super.getChildren();
   }

   private void addValidators(List children)
   {
      for (Object child: children)
      {
         if (child instanceof UIInput)
         {
            UIInput uiInput =  (UIInput) child;
            if ( uiInput.getValidators().length==0 && uiInput.getValidator()==null )
            {
               uiInput.addValidator( new ModelValidator() );
            }
         }
         else if (child instanceof UIComponentBase)
         {
            addValidators( ( (UIComponentBase) child ).getChildren() );
         }
      }
   }

}
