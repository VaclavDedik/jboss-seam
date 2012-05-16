package org.jboss.seam.ui.component;

import javax.faces.component.UIComponentBase;

import org.richfaces.cdk.annotations.Attribute;
import org.richfaces.cdk.annotations.Description;
import org.richfaces.cdk.annotations.JsfComponent;
import org.richfaces.cdk.annotations.JsfRenderer;
import org.richfaces.cdk.annotations.Tag;

/**
 * UIComponent for validator 
 * 
 * @author Daniel Roth
 */
@JsfComponent(description=@Description(displayName="org.jboss.seam.ui.EqualityValidator",value="Validate all child JSF input fields against the bound propertys using Hibernate Validator."),
family="org.jboss.seam.ui.EqualityValidator", type="org.jboss.seam.ui.EqualityValidator",generate="org.jboss.seam.ui.component.html.HtmlEqualityValidator", 
tag = @Tag(baseClass="org.jboss.seam.ui.util.cdk.UIComponentTagBase", name="equalityValidator"), 
renderer = @JsfRenderer(type="org.jboss.seam.ui.EqualityValidatorRenderer", family="org.jboss.seam.ui.EqualityValidatorRenderer"),
attributes = {"equalityValidator.xml" })
public abstract class UIEqualityValidator extends UIComponentBase
{
   @Attribute
   public abstract String getFor();

   public abstract void setFor(String forId);
   
   @Attribute
   public abstract String getMessage();

   public abstract void setMessage(String message);

   @Attribute
   public abstract String getMessageId();

   public abstract void setMessageId(String messageId);
   
   public abstract void setOperator(String operator);
   
   @Attribute
   public abstract String getOperator();

   public abstract void setRequired(boolean required);
   
   @Attribute
   public abstract boolean isRequired();
   
}
