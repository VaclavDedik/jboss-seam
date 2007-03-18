package org.jboss.seam.ui;

import javax.faces.component.UIComponent;
import javax.faces.component.html.HtmlMessage;


public class HtmlMessageDecoration extends HtmlMessage
{
   public static final String COMPONENT_TYPE = "org.jboss.seam.ui.HtmlMessageDecoration";

   private String getFor(UIComponent component)
   {
      if (component instanceof UIDecorate) 
      {
         return UIDecorate.getInputId(component);
      }
      else if ( component.getParent() instanceof UIDecorateAll )
      {
         return UIDecorate.getInputId(component);
      }
      else if ( component.getParent()==null )
      {
         return null;
      }
      else
      {
         return getFor( component.getParent() );
      }
   }

   @Override
   public String getFor()
   {
      return getFor(this);
   }

}
