package org.jboss.seam.ui.component;

import org.richfaces.cdk.annotations.Attribute;
import org.richfaces.cdk.annotations.Description;
import org.richfaces.cdk.annotations.JsfComponent;
import org.richfaces.cdk.annotations.JsfRenderer;
import org.richfaces.cdk.annotations.Tag;

@JsfComponent(description=@Description(displayName="org.jboss.seam.ui.Span",value="Render a HTML <span>"),
family="org.jboss.seam.ui.Span", type="org.jboss.seam.ui.Span",generate="org.jboss.seam.ui.component.html.HtmlSpan", 
tag = @Tag(baseClass="org.jboss.seam.ui.util.cdk.UIComponentTagBase", name="span"), 
renderer = @JsfRenderer(generate="false",type="org.jboss.seam.ui.renderkit.SpanRendererBase", family="org.jboss.seam.ui.SpanRenderer"),
attributes = {"core-props.xml", "span.xml"})
public abstract class UISpan extends UIStyle
{

   @Attribute
   public abstract String getTitle();

   public abstract void setTitle(String title);

}
