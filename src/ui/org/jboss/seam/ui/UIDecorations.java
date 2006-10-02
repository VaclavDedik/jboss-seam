package org.jboss.seam.ui;

import javax.faces.component.UIComponentBase;

public class UIDecorations extends UIComponentBase
{

   public static final String COMPONENT_TYPE = "org.jboss.seam.ui.UIDecorations";
   public static final String COMPONENT_FAMILY = "org.jboss.seam.ui.Decorations";

   @Override
   public String getFamily()
   {
      return COMPONENT_FAMILY;
   }

}
