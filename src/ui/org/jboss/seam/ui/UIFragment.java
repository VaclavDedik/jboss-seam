package org.jboss.seam.ui;

import javax.faces.component.UIComponentBase;

public class UIFragment extends UIComponentBase
{
   
   public static final String COMPONENT_TYPE = "org.jboss.seam.ui.UIFragment";
   public static final String COMPONENT_FAMILY = "org.jboss.seam.ui.Fragment";

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
   

}
