package org.jboss.seam.ui;

import javax.faces.component.UIComponentBase;
import javax.faces.context.FacesContext;

import org.jboss.seam.core.Manager;


public class UIForceModelUpdate extends UIComponentBase
{

   public static final String COMPONENT_FAMILY = "org.jboss.seam.ui.ForceModelUpdate";

   @Override
   public String getFamily()
   {
      return COMPONENT_FAMILY;
   }
   
   @Override
   public void processValidators(FacesContext ctx)
   {
      super.processValidators(ctx);
      Manager.instance().setForceModelUpdate();
   }

}
