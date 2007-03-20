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

import javax.faces.component.UIComponent;
import javax.faces.component.UIData;
import javax.faces.el.MethodBinding;
import javax.faces.event.ActionListener;
import javax.faces.model.DataModel;

/**
 * JSF component class
 *
 */
public abstract class UIButton extends UISeamCommandBase  {
	
	private static final String COMPONENT_TYPE = "org.jboss.seam.ui.Button";
	
	private static final String COMPONENT_FAMILY = "org.jboss.seam.ui.Button";
   
   public abstract String getImage();
   
   public abstract void setImage(String image);
   
   
}
