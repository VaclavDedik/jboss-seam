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
import javax.faces.model.DataModel;

import org.jboss.seam.contexts.Contexts;

/**
 * JSF component class
 *
 */
public class UISelection extends UIParameter {
	
	private static final String COMPONENT_TYPE = "org.jboss.seam.ui.Selection";
	
	private static final String COMPONENT_FAMILY = "org.jboss.seam.ui.Selection";
   
   @Override
   public String getFamily()
   {
      return COMPONENT_FAMILY;
   }
   
   @Override
   public String getName()
   {
      return "dataModelSelection";
   }
   
   @Override
   public Object getValue()
   {
      Object value = Contexts.lookupInStatefulContexts(dataModel);
      if (value==null)
      {
         return null;
      }
      else
      {
         int rowIndex = ( (DataModel) value ).getRowIndex();
         return rowIndex<0 ? null : var + ':' + dataModel + '[' + rowIndex + ']';
      }
   }
   
   /* Variables */
   
   private String dataModel;
   private String var;
   
   @Override
   public void restoreState(FacesContext context, Object state) {
      Object[] values = (Object[]) state;
      super.restoreState(context, values[0]);
      dataModel = (String) values[1];
   }

   @Override
   public Object saveState(FacesContext context) {
      Object[] values = new Object[2];
      values[0] = super.saveState(context);
      values[1] = dataModel;
      return values;
   }

   public String getDataModel()
   {
      return dataModel;
   }

   public void setDataModel(String dataModel)
   {
      this.dataModel = dataModel;
   }

   public String getVar()
   {
      return var;
   }

   public void setVar(String var)
   {
      this.var = var;
   }
	
}
