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

import javax.faces.component.UIComponentBase;
import javax.faces.context.FacesContext;

import org.richfaces.cdk.annotations.Description;
import org.richfaces.cdk.annotations.JsfComponent;
import org.richfaces.cdk.annotations.JsfRenderer;
import org.richfaces.cdk.annotations.Tag;

/**
 * JSF component class which surrounds a page fragment and allows you to apply render/not render without any html output
 *
 */
@JsfComponent(description=@Description(displayName="org.jboss.seam.ui.Fragment",value="Surround a page fragment, allows you to apply render/not render without any html output."),
family="org.jboss.seam.ui.Fragment", type="org.jboss.seam.ui.Fragment",generate="org.jboss.seam.ui.component.html.HtmlFragment", 
tag = @Tag(baseClass="org.jboss.seam.ui.util.cdk.UIComponentTagBase", name="fragment"), 
renderer = @JsfRenderer(type="org.jboss.seam.ui.renderkit.FragmentRendererBase", family="org.jboss.seam.ui.FragmentRenderer"),
attributes = {"base-props.xml" })
public abstract class UIFragment extends UIComponentBase {
	
   private static final String COMPONENT_TYPE = "org.jboss.seam.ui.Fragment";
	
	@SuppressWarnings("unused")
   private static final String COMPONENT_FAMILY = "org.jboss.seam.ui.Fragment";
	
	public static UIFragment newInstance() {
      return (UIFragment) FacesContext.getCurrentInstance().getApplication().createComponent(COMPONENT_TYPE);
   }
	
}
