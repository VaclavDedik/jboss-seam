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

import java.util.List;

import javax.faces.component.EditableValueHolder;
import javax.faces.component.UIComponent;
import javax.faces.component.UIComponentBase;

import org.jboss.seam.ui.validator.ModelValidator;



/**
 * JSF component class
 *
 */
public abstract class UIValidateAll extends UIComponentBase {
	
	private static final String COMPONENT_TYPE = "org.jboss.seam.ui.ValidateAll";
	
	private static final String COMPONENT_FAMILY = "org.jboss.seam.ui.ValidateAll";
	
   @Override
   public String getFamily()
   {
      return COMPONENT_FAMILY;
   }
   
   @Override
   public List getChildren()
   {
      addValidators( super.getChildren() );
      return super.getChildren();
   }

   private void addValidators(List children)
   {
      for (Object child: children)
      {
         if (child instanceof EditableValueHolder)
         {
            EditableValueHolder evh =  (EditableValueHolder) child;
            if ( evh.getValidators().length==0 && evh.getValidator()==null )
            {
               evh.addValidator( new ModelValidator() );
            }
         }
         else if (child instanceof UIComponent)
         {
            addValidators( ( (UIComponent) child ).getChildren() );
         }
      }
   }
}
