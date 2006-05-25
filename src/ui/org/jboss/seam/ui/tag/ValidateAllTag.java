package org.jboss.seam.ui.tag;

import javax.faces.webapp.UIComponentTag;

import org.jboss.seam.ui.UIValidateAll;

public class ValidateAllTag extends UIComponentTag
{

   @Override
   public String getComponentType()
   {
      return UIValidateAll.COMPONENT_TYPE;
   }

   @Override
   public String getRendererType()
   {
      return null;
   }

}
