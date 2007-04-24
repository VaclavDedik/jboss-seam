package org.jboss.seam.ui;

import java.io.IOException;
import java.util.List;

import javax.faces.component.EditableValueHolder;
import javax.faces.component.UIComponent;
import javax.faces.component.UIComponentBase;
import javax.faces.context.FacesContext;

public class UIValidateAll extends UIComponentBase
{
   public static final String COMPONENT_FAMILY = "org.jboss.seam.ui.ValidateAll";
   public static final String COMPONENT_TYPE = "org.jboss.seam.ui.UIValidateAll";
   
   private boolean validatorsAdded = false;
   
   @Override
   public String getFamily()
   {
      return COMPONENT_FAMILY;
   }
   
   @Override
   public boolean getRendersChildren()
   {
      return true;
   }   

   @Override
   public void encodeChildren(FacesContext context) 
       throws IOException
   {
      if (!validatorsAdded)
      {
         addValidators( super.getChildren() );
         validatorsAdded = true;
      }
      JSF.renderChildren(context, this);
   }   

   private void addValidators(List children)
   {
      for (Object child: children)
      {
         if (child instanceof EditableValueHolder)
         {
            EditableValueHolder evh =  (EditableValueHolder) child;
            
            if ( evh.getValidators().length==0 && evh.getValidator()==null) 
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
   
   private Object[] values;

   @Override
   public Object saveState(FacesContext context)
   {
      if (values == null)
      {
         values = new Object[2];
      }

      values[0] = super.saveState(context);
      values[1] = validatorsAdded ? Boolean.TRUE : Boolean.FALSE;

      return (values);
   }

   @Override
   public void restoreState(FacesContext context, Object state)
   {
      values = (Object[]) state;
      super.restoreState(context, values[0]);
      
      validatorsAdded = ((Boolean) values[1]).booleanValue();      
   }      
}
