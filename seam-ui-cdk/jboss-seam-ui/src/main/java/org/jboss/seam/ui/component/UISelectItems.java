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

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.faces.FacesException;
import javax.faces.component.ValueHolder;
import javax.faces.convert.Converter;
import javax.faces.el.ValueBinding;
import javax.faces.model.DataModel;

import org.jboss.seam.framework.EntityQuery;
import org.jboss.seam.ui.converter.ConverterChain;
import org.jboss.seam.ui.converter.NoSelectionConverter;


/**
 * JSF component class
 *
 */
public abstract class UISelectItems extends javax.faces.component.UISelectItems {
	
	private static final String COMPONENT_TYPE = "org.jboss.seam.ui.SelectItems";
	
	private static final String COMPONENT_FAMILY = "org.jboss.seam.ui.SelectItems";
   
   private class NullableSelectItem extends javax.faces.model.SelectItem
   {

      private Object value;

      private NullableSelectItem(Object value, String label)
      {
         super.setLabel(label);
         this.value = value;
      }

      @Override
      public Object getValue()
      {
         return value;
      }

   }

   private static final String NO_SELECTION_VALUE = null;

   public abstract void setHideNoSelectionLabel(Boolean hideNoSelectionLabel);
   
   public abstract Boolean getHideNoSelectionLabel();
   
   public abstract String getNoSelectionLabel();
   
   public abstract void setNoSelectionLabel(String noSelectionLabel);
   
   public abstract String getVar();
   
   public abstract void setVar(String var);
   
   public abstract String getLabel();
   
   public abstract void setLabel(String label);
   
   public abstract Boolean getDisabled();
   
   public abstract void setDisabled(Boolean disabled);

   @Override
   public Object getValue()
   {
      Object value = super.getValue();
      
      if (value instanceof Iterable)
      {
         return asSelectItems((Iterable) value);
      }
      else if (value instanceof DataModel && ((DataModel) value).getWrappedData() instanceof Iterable)
      {
         return asSelectItems((Iterable) ((DataModel) value).getWrappedData()); 
      }
      else if (value instanceof EntityQuery)
      {
         return asSelectItems(((EntityQuery) value).getResultList());
      }
      else if (value != null && value.getClass().isArray())
      {
         if (value.getClass().getComponentType().isPrimitive())
         {
            List list = new ArrayList();
            for (int i = 0; i < Array.getLength(value); i++)
            {
               list.add(Array.get(value, i));
            }
            return asSelectItems(list);
         }
         else
         {
            return asSelectItems(Arrays.asList((Object[]) value));
         }
      }
      else
      {
         javax.faces.model.SelectItem noSelectionLabel = noSelectionLabel();
         if (noSelectionLabel != null) 
         {
            List<javax.faces.model.SelectItem> selectItems = new ArrayList<javax.faces.model.SelectItem>();
            selectItems.add(noSelectionLabel);
            return selectItems;
         }
         else 
         {
            return value;
         }
      }
   }
   
   private List<javax.faces.model.SelectItem> asSelectItems(Iterable iterable) 
   {
      List<javax.faces.model.SelectItem> selectItems =  new ArrayList<javax.faces.model.SelectItem>();
      javax.faces.model.SelectItem noSelectionLabel = noSelectionLabel();
      if (noSelectionLabel != null) 
      {
         selectItems.add(noSelectionLabel);
      }
      for (Object o : iterable)
      {
         initVar(o);
         selectItems.add(new javax.faces.model.SelectItem(o, getLabel(), "", getDisabled() == null ? false : getDisabled()));
         destroyVar();
      }
      return selectItems;
   }

   private javax.faces.model.SelectItem noSelectionLabel()
   {
      boolean show = false;
      /*
       * This is a slight hack. If you do an EL expresison like this (to hide the label)
       * 
       * noSelectionLabel="#{x eq y ? 'Please Select' : null}"
       * 
       * then, if x != y, EL will return an empty String, not null, so we work around that, with the side effect
       * that if the result of the EL expression is an empty String, then the label will be hidden.
       */
      ValueBinding vb = getValueBinding("noSelectionLabel");
      if (vb == null && !(getHideNoSelectionLabel() && getParentValue() != null))
      {
         /* 
          * Here, the user has specfied a noSelectionLabel (may be an empty string), and, if hideNoSelectionLabel
          * is set, then, if a value is selected, then the label is hidden
          */ 
         show = true;
      } 
      else if (getNoSelectionLabel() != null && !"".equals(getNoSelectionLabel()) && !(getHideNoSelectionLabel() && getParentValue() != null))
      {
         /*
          * Here, the user has used an EL expression as the noSelectionLabel.  In this case, an empty string is
          * indicates that the label should be hidden.
          */
         show = true;
      }
      
      if (show)
      {
         NullableSelectItem s = new NullableSelectItem(NO_SELECTION_VALUE, getNoSelectionLabel());
         ConverterChain converterChain = new ConverterChain(this.getParent());
         Converter noSelectionConverter = new NoSelectionConverter();
         // Make sure that the converter is only added once
         if (!converterChain.containsConverterType(noSelectionConverter)) {
            converterChain.addConverterToChain(noSelectionConverter, ConverterChain.CHAIN_START);
         }
         return s;
      }
      else
      {
         return null;
      }
   }

   @SuppressWarnings("unchecked")
   private void initVar(Object varValue)
   {
      if (getVar() == null)
      {
         throw new FacesException("var attribute must be set");
      }
      getFacesContext().getExternalContext().getRequestMap().put(getVar(), varValue);
   }

   private void destroyVar()
   {
      getFacesContext().getExternalContext().getRequestMap().remove(getVar());
   }

   private Object getParentValue()
   {
      if (getParent() instanceof ValueHolder)
      {
         ValueHolder parent = (ValueHolder) getParent();
         return parent.getValue();
      }
      else
      {
         return null;
      }
   }
	
}
