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
import org.richfaces.cdk.annotations.Attribute;
import org.richfaces.cdk.annotations.Description;
import org.richfaces.cdk.annotations.JsfComponent;
import org.richfaces.cdk.annotations.Tag;

/**
 * JSF component class which inserts a parameter that can be bound to a data model
 *
 */
@JsfComponent(description=@Description(displayName="org.jboss.seam.ui.Selection", value="It inserts a parameter that can be bound to a data model"),
family="org.jboss.seam.ui.Selection", type="org.jboss.seam.ui.Selection",generate="org.jboss.seam.ui.component.html.HtmlSelection", 
tag = @Tag(baseClass="org.jboss.seam.ui.util.cdk.UIComponentTagBase", name="selection"),
attributes = {"javax.faces.component.UIComponent.xml" })
public abstract class UISelection extends UIParameter {
	
	private static final String COMPONENT_TYPE = "org.jboss.seam.ui.Selection";
   
   @Override
   public String getName()
   {
      return "dataModelSelection";
   }
   
   @Override
   public Object getValue()
   {
      Object value = Contexts.lookupInStatefulContexts(getDataModel());
      if (value==null)
      {
         return null;
      }
      else
      {
         int rowIndex = ( (DataModel) value ).getRowIndex();
         return rowIndex<0 ? null : getVar() + ':' + getDataModel() + '[' + rowIndex + ']';
      }
   }
   @Attribute
   public abstract String getDataModel();

   public abstract void setDataModel(String dataModel);
   
   @Attribute
   public abstract String getVar();

   public abstract void setVar(String var);
   
   public static UISelection newInstance() {
      return (UISelection) FacesContext.getCurrentInstance().getApplication().createComponent(COMPONENT_TYPE);
   }
	
}
