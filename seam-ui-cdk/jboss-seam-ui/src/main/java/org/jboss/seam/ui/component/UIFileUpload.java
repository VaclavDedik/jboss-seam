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
import javax.faces.el.ValueBinding;

/**
 * JSF component class
 *
 */
public abstract class UIFileUpload extends UIComponentBase {
	
	private static final String COMPONENT_TYPE = "org.jboss.seam.ui.FileUpload";
	
	private static final String COMPONENT_FAMILY = "org.jboss.seam.ui.FileUpload";
   
   public abstract String getAccept();
   
   public abstract void setAccept(String accept);
   
   public abstract String getStyleClass();
   
   public abstract void setStyleClass(String styleClass);
   
   public abstract void setStyle(String style);
   
   public abstract String getStyle();
   
   public abstract ValueBinding getData();
   
   public abstract void setData(ValueBinding data);

   public abstract ValueBinding getContentType();
   
   public abstract void setContentType(ValueBinding contentType);
   
   public abstract ValueBinding getFileName();
   
   public abstract void setFileName(ValueBinding fileName);
   
   public abstract ValueBinding getFileSize();
   
   public abstract void setFileSize(ValueBinding fileSize); 
   
}
