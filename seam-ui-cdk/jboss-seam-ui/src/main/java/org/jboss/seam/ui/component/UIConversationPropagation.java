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

import javax.faces.component.UIParameter;
import javax.faces.context.FacesContext;

/**
 * JSF component class
 *
 */
public class UIConversationPropagation extends UIParameter {
	
	private static final String COMPONENT_TYPE = "org.jboss.seam.ui.ConversationPropagation";
	
	private static final String COMPONENT_FAMILY = "org.jboss.seam.ui.ConversationPropagation";

   @Override
   public String getFamily()
   {
      return COMPONENT_FAMILY;
   }
   
   private String pageflow;
   private String type = "none";
   
   @Override
   public String getName()
   {
      return "conversationPropagation";
   }

   @Override
   public Object getValue()
   {
      return pageflow==null ? type : type + "." + pageflow;
   }

   public String getPageflow()
   {
      return pageflow;
   }

   public void setPageflow(String pageflow)
   {
      this.pageflow = pageflow;
   }

   public String getType()
   {
      return type;
   }

   public void setType(String type)
   {
      this.type = type;
   }

   @Override
   public void restoreState(FacesContext context, Object state) {
      Object[] values = (Object[]) state;
      super.restoreState(context, values[0]);
      type = (String) values[1];
      pageflow = (String) values[2];
   }

   @Override
   public Object saveState(FacesContext context) {
      Object[] values = new Object[3];
      values[0] = super.saveState(context);
      values[1] = type;
      values[2] = pageflow;
      return values;
   }
   
}
