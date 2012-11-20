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
import org.richfaces.cdk.annotations.Tag;
import org.richfaces.cdk.annotations.JsfComponent;
import org.richfaces.cdk.annotations.JsfRenderer;


/**
 * JSF component class for Seam UIButton
 *
 */
@JsfComponent(description=@Description(displayName="org.jboss.seam.ui.Button",value="A link that supports invocation of an action with control over conversation propagation"),
 family="org.jboss.seam.ui.Button", type="org.jboss.seam.ui.Button",generate="org.jboss.seam.ui.component.html.HtmlButton", 
 tag = @Tag(baseClass="org.jboss.seam.ui.util.cdk.UIComponentTagBase", name="button", handler="org.jboss.seam.ui.handler.CommandButtonParameterComponentHandler"),
 renderer = @JsfRenderer(type="org.jboss.seam.ui.ButtonRenderer", family="org.jboss.seam.ui.ButtonRenderer"),
 attributes = {"command-button-props.xml", "javax.faces.component.UICommand.xml", "javax.faces.component.ValueHolder.xml", "i18n-props.xml", "accesskey-props.xml" })
public abstract class UIButton extends UISeamCommandBase  {

   @Attribute
	public abstract String getStyleClass();
   
   @Attribute   
   public abstract String getStyle();

   @Attribute(defaultValue="false")
   public abstract boolean isDisabled();

   @Attribute
   public abstract String getOnclick();

   @Attribute
   public abstract String getImage();

   @Attribute(defaultValue = "true", description = @Description("Include page parameters defined in pages.xml when rendering the button"))
   public abstract boolean isIncludePageParams();

   @Attribute(description = @Description("The name of the conversation for natural conversations"))
   public abstract String getConversationName();

   @Attribute(description = @Description("Specify the task to operate on (e.g. for @StartTask)"))
   public abstract Object getTaskInstance();

   @Attribute
   public abstract String getOutcome();

   @Attribute(description = @Description("the fragment identifier to link to."))
   public abstract String getFragment();

   @Attribute(defaultValue = "default", description = @Description("determines the conversation propagation style: begin, join, nest, none, end or endRoot."))
   public abstract String getPropagation();

   @Attribute(description = @Description("a pageflow definition to begin. (This is only useful when propagation=\"begin\" or propagation=\"join\".)"))
   public abstract String getPageflow();

   @Attribute(description = @Description("the JSF view id to link to."))
   public abstract String getView();
   
}
