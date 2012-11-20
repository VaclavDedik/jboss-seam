package org.jboss.seam.ui.component;

import javax.faces.component.UIComponentBase;

import org.richfaces.cdk.annotations.Attribute;
import org.richfaces.cdk.annotations.Description;
import org.richfaces.cdk.annotations.JsfComponent;
import org.richfaces.cdk.annotations.JsfRenderer;
import org.richfaces.cdk.annotations.Tag;

/**
 * JSF Component for validation of all child JSF input fields against the bound properties using Bean Validation 
 * 
 * @author Daniel Roth
 */
@JsfComponent(description=@Description(displayName="org.jboss.seam.ui.EqualityValidator",value="Validate all child JSF input fields against the bound propertys using Hibernate Validator."),
family="org.jboss.seam.ui.EqualityValidator", type="org.jboss.seam.ui.EqualityValidator", generate="org.jboss.seam.ui.component.html.HtmlEqualityValidator", 
tag = @Tag(baseClass="org.jboss.seam.ui.util.cdk.UIComponentTagBase", name="validateEquality"), 
renderer = @JsfRenderer(type="org.jboss.seam.ui.EqualityValidatorRenderer", family="org.jboss.seam.ui.EqualityValidatorRenderer"))
public abstract class UIEqualityValidator extends UIComponentBase
{
   @Attribute(description = @Description("Id of component to validate against"))
   public abstract String getFor();

   public abstract void setFor(String forId);
   
   @Attribute(description = @Description("Error message to show"))
   public abstract String getMessage();

   public abstract void setMessage(String message);

   @Attribute(description = @Description("Message id to use on failure"))
   public abstract String getMessageId();

   public abstract void setMessageId(String messageId);
   
   public abstract void setOperator(String operator);
   
   @Attribute(description = @Description("Operation to use."))
   public abstract String getOperator();

   public abstract void setRequired(boolean required);
   
   @Attribute(defaultValue = "true",
           description = @Description("True if a value is required for the filed to validate (default:true)"))
   public abstract boolean isRequired();
   
}
