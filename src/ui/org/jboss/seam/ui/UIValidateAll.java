package org.jboss.seam.ui;

import java.util.List;

import javax.faces.component.UIComponentBase;
import javax.faces.component.UIInput;

public class UIValidateAll extends UIComponentBase
{

   @Override
   public String getFamily()
   {
      return "org.jboss.seam.ui.ValidateAll";
   }

   @Override
   public List getChildren()
   {
      for ( Object child: super.getChildren() )
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
            //TODO: recurse
         }
      }
      return super.getChildren();
   }

   

}
