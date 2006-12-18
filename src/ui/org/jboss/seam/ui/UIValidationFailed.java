package org.jboss.seam.ui;

import javax.faces.context.FacesContext;

import org.jboss.seam.core.Manager;


public class UIValidationFailed extends javax.faces.component.UICommand
{
   
   @Override
   public String getRendererType()
   {
      return null;
   }
   
   @Override
   public void processValidators(FacesContext context)
   {
      super.processValidators(context);
      Manager.instance().setValidationFailedAction( getAction() );
   }
   
}
