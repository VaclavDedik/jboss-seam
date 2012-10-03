package org.jboss.seam.ui.component;

import javax.faces.component.UIComponentBase;

import org.richfaces.cdk.annotations.Description;
import org.richfaces.cdk.annotations.JsfComponent;
import org.richfaces.cdk.annotations.JsfRenderer;
import org.richfaces.cdk.annotations.Tag;

/**
 * JSF component class which validates all child JSF input fields against the bound properties using Bean validation 
 * 
 */
@JsfComponent(description=@Description(displayName="org.jboss.seam.ui.ValidateAll",value="Validate all child JSF input fields against the bound propertys using Hibernate Validator."),
family="org.jboss.seam.ui.ValidateAll", type="org.jboss.seam.ui.ValidateAll",generate="org.jboss.seam.ui.component.html.HtmlValidateAll", 
tag = @Tag(baseClass="org.jboss.seam.ui.util.cdk.UIComponentTagBase", name="validateAll"), 
renderer = @JsfRenderer(type="org.jboss.seam.ui.ValidateAllRenderer", family="org.jboss.seam.ui.ValidateAllRenderer"))
public abstract class UIValidateAll extends UIComponentBase
{

   // TODO Make this a hidden=true, el=false property in validateAll.xml
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
