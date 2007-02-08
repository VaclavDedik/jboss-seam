package org.jboss.seam.ui;

import java.io.IOException;

import javax.faces.component.UIComponentBase;
import javax.faces.context.FacesContext;

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
   
   @Override
   public void encodeChildren(FacesContext context) throws IOException
   {
      JSF.renderChildren(context, this);
   }
   

}
