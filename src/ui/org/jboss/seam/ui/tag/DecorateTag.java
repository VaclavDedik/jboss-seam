package org.jboss.seam.ui.tag;

import javax.faces.component.UIComponent;

import org.jboss.seam.ui.UIDecorate;

public class DecorateTag extends UIComponentTagBase
{

   @Override
   public String getComponentType()
   {
      return UIDecorate.COMPONENT_TYPE;
   }

   @Override
   public String getRendererType()
   {
      return null;
   }
   
   private String forId;

   public void setFor(String forId)
   {
      this.forId = forId;
   }

   @Override
   protected void setProperties(UIComponent component)
   {
       super.setProperties(component);
       setStringProperty(component, "for", forId);
   }

}
