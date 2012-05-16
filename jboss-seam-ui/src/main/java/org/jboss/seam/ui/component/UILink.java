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
 * JSF component class
 *
 */
@JsfComponent(description=@Description(displayName="org.jboss.seam.ui.Link",value="A link that supports invocation of an action with control over conversation propagation. Does not submit the form."),
family="org.jboss.seam.ui.Link", type="org.jboss.seam.ui.Link",generate="org.jboss.seam.ui.component.html.HtmlLink", 
tag = @Tag(baseClass="org.jboss.seam.ui.util.cdk.UIComponentTagBase", name="link"), 
renderer = @JsfRenderer(type="org.jboss.seam.ui.LinkRenderer",family="org.jboss.seam.ui.LinkRenderer"),
attributes = {"command-button-props.xml", "javax.faces.component.UICommand.xml", "javax.faces.component.ValueHolder.xml", "i18n-props.xml", "accesskey-props.xml", "button.xml" })
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
	
}
