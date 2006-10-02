package org.jboss.seam.ui;

import javax.faces.component.UIComponent;
import javax.faces.component.UIMessage;


public class UIMessageDecoration extends UIMessage
{
   public static final String COMPONENT_TYPE = "org.jboss.seam.ui.UIMessageDecoration";

   private UIDecorate getParentDecorate(UIComponent component)
   {
      if (component instanceof UIDecorate) 
      {
         return (UIDecorate) component;
      }
      else if ( component.getParent()==null )
      {
         return null;
      }
      else
      {
         return getParentDecorate( component.getParent() );
      }
   }

   @Override
   public String getFor()
   {
      UIDecorate decorate = getParentDecorate(this);
      return decorate==null ? null : decorate.getInputId();
   }

}
