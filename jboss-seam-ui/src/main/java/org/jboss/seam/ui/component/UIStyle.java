package org.jboss.seam.ui.component;

import javax.faces.component.UIComponentBase;

import org.richfaces.cdk.annotations.Attribute;
import org.richfaces.cdk.annotations.Description;
import org.richfaces.cdk.annotations.JsfComponent;
import org.richfaces.cdk.annotations.JsfRenderer;
import org.richfaces.cdk.annotations.Tag;

/**
 * JSF Component which renders a HTML &lt;div&gt;
 * @author mnovotny
 *
 */
@JsfComponent(description=@Description(displayName="org.jboss.seam.ui.Div",value="Render a HTML &lt;div&gt;"),
family="org.jboss.seam.ui.Div", type="org.jboss.seam.ui.Div",generate="org.jboss.seam.ui.component.html.HtmlDiv", 
tag = @Tag(baseClass="org.jboss.seam.ui.util.cdk.UIComponentTagBase", name="div"), 
renderer = @JsfRenderer(type="org.jboss.seam.ui.DivRenderer", family="org.jboss.seam.ui.DivRenderer"))
public abstract class UIStyle extends UIComponentBase
{
   @Attribute
   public abstract String getStyleClass();

   @Attribute
   public abstract String getStyle();
   
   public abstract void setStyleClass(String styleClass);
   
   public abstract void setStyle(String style);

}
