package org.jboss.seam.ui;

import java.util.List;

import javax.faces.component.EditableValueHolder;
import javax.faces.component.UIComponent;
import javax.faces.component.UIComponentBase;

public class UIValidateAll extends UIComponentBase
{
   public static final String COMPONENT_FAMILY = "org.jboss.seam.ui.ValidateAll";
   public static final String COMPONENT_TYPE = "org.jboss.seam.ui.UIValidateAll";
      
   @Override
   public String getFamily()
   {
      return COMPONENT_FAMILY;
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
         if (child instanceof EditableValueHolder)
         {
            EditableValueHolder evh =  (EditableValueHolder) child;
            if ( evh.getValidators().length==0 && evh.getValidator()==null )
            {
               evh.addValidator( new ModelValidator() );
            }
         }
         else if (child instanceof UIComponent)
         {
            addValidators( ( (UIComponent) child ).getChildren() );
         }
      }
   }

}
