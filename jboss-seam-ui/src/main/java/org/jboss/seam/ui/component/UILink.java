/**
 * License Agreement.
 *
 * Ajax4jsf 1.1 - Natural Ajax for Java Server Faces (JSF)
 *
 * Copyright (C) 2007 Exadel, Inc.
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License version 2.1 as published by the Free Software Foundation.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301  USA
 */

package org.jboss.seam.ui.component;

import org.richfaces.cdk.annotations.Attribute;
import org.richfaces.cdk.annotations.Description;
import org.richfaces.cdk.annotations.JsfComponent;
import org.richfaces.cdk.annotations.JsfRenderer;
import org.richfaces.cdk.annotations.Tag;


/**
 * JSF component class which renders a link that supports invocation of an action with control over conversation propagation. 
 * It does not submit the form.
 *
 */
@JsfComponent(description=@Description(displayName="org.jboss.seam.ui.Link",value="A link that supports invocation of an action with control over conversation propagation. Does not submit the form."),
family="org.jboss.seam.ui.Link", type="org.jboss.seam.ui.Link",generate="org.jboss.seam.ui.component.html.HtmlLink", 
tag = @Tag(baseClass="org.jboss.seam.ui.util.cdk.UIComponentTagBase", name="link"), 
renderer = @JsfRenderer(type="org.jboss.seam.ui.LinkRenderer",family="org.jboss.seam.ui.LinkRenderer"),
attributes = {"command-button-props.xml", "javax.faces.component.UICommand.xml", "javax.faces.component.ValueHolder.xml", "i18n-props.xml", "accesskey-props.xml"})
public abstract class UILink extends UISeamCommandBase {
   
   @Attribute
   public abstract String getStyleClass();
   
   public abstract void setStyleClass(String styleClass);
   
   @Attribute
   public abstract String getStyle();
   
   public abstract void setStyle(String style);
   
   @Attribute
   public abstract boolean isDisabled();

   public abstract void setDisabled(boolean disabled);

   @Attribute(description = @Description("the JSF view id to link to."))
   public abstract String getView();

   @Attribute(description = @Description("a pageflow definition to begin. (This is only useful when propagation=\"begin\" or propagation=\"join\".)"))
   public abstract String getPageflow();

   @Attribute(defaultValue = "default",
           description = @Description("determines the conversation propagation style: begin, join, nest, none, end or endRoot."))
   public abstract String getPropagation();

   @Attribute(description = @Description("the fragment identifier to link to."))
   public abstract String getFragment();

   @Attribute
   public abstract String getOutcome();

   @Attribute
   public abstract String getImage();

   @Attribute(description = @Description("Specify the task to operate on (e.g. for @StartTask)"))
   public abstract Object getTaskInstance();

   @Attribute(description = @Description("The name of the conversation for natural conversations"))
   public abstract String getConversationName();

   @Attribute(defaultValue = "true",
           description = @Description("Include page parameters defined in pages.xml when rendering the button"))
   public abstract boolean isIncludePageParams();
}
