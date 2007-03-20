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
import javax.faces.component.html.HtmlMessage;

/**
 * JSF component class
 *
 */
public abstract class UIMessage extends HtmlMessage {
	
	private static final String COMPONENT_TYPE = "org.jboss.seam.ui.Message";
	
	private static final String COMPONENT_FAMILY = "org.jboss.seam.ui.Message";

    private String getFor(UIComponent component)
      {
         /*if (component instanceof UIDecorate) 
         {
            return Decorate.getInputId(component);
         }
         else if ( component.getParent() instanceof UIDecorateAll )
         {
            return Decorate.getInputId(component);
         }
         else if ( component.getParent()==null )
         {
            return null;
         }
         else
         {*/
            return getFor( component.getParent() );
         /*}*/
      }

      @Override
      public String getFor()
      {
         return getFor(this);
      }
   
}
