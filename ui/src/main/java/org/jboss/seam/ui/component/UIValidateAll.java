package org.jboss.seam.ui.component;

import javax.faces.component.UIComponentBase;

/**
 * JSF component class
 * 
 */
public abstract class UIValidateAll extends UIComponentBase
{

   private static final String COMPONENT_TYPE = "org.jboss.seam.ui.ValidateAll";

   private static final String COMPONENT_FAMILY = "org.jboss.seam.ui.ValidateAll";

   private boolean validatorsAdded = false;

   public boolean isValidatorsAdded()
   {
      return validatorsAdded;
   }

   public void setValidatorsAdded(boolean validatorsAdded)
   {
      this.validatorsAdded = validatorsAdded;
   }
}
