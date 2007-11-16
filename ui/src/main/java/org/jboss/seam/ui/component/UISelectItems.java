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

import static org.jboss.seam.util.Strings.emptyIfNull;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.el.ValueExpression;
import javax.faces.FacesException;
import javax.faces.component.ValueHolder;
import javax.faces.convert.Converter;

import javax.faces.model.DataModel;

import org.jboss.seam.framework.Query;
import org.jboss.seam.ui.converter.ConverterChain;
import org.jboss.seam.ui.converter.NoSelectionConverter;


/**
 * JSF component class
 *
 */
public abstract class UISelectItems extends javax.faces.component.UISelectItems {
   
   private Object value;
   private Object originalValue;
   
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

   /* Kinder impl of get/setLabel */
   
   private String label;
   
   public String getLabel()
   {
      ValueExpression ve = getValueExpression("label");
      if (ve != null)
      {
         Object object = ve.getValue(getFacesContext().getELContext());
         if (object != null)
         {
            return object.toString();
         }
      }
      return label;
   }
   
   public void setLabel(String label)
   {
      this.label = label;
   }


   public abstract void setHideNoSelectionLabel(Boolean hideNoSelectionLabel);
   
   public abstract Boolean getHideNoSelectionLabel();
   
   public abstract String getNoSelectionLabel();
   
   public abstract void setNoSelectionLabel(String noSelectionLabel);
   
   public abstract String getVar();
   
   public abstract void setVar(String var);
      
   public abstract Boolean getDisabled();
   
   public abstract void setDisabled(Boolean disabled);
   
   public abstract Object getItemValue();
   
   public abstract void setItemValue(Object itemValue);

   @Override
   public Object getValue()
   {
      if (value == null || originalValue == null || !originalValue.equals(super.getValue()))
      {
         originalValue = super.getValue();
         
         if (originalValue instanceof Iterable)
         {
            value = asSelectItems((Iterable) originalValue);
         }
         else if (originalValue instanceof DataModel && ((DataModel) originalValue).getWrappedData() instanceof Iterable)
         {
            value = asSelectItems((Iterable) ((DataModel) originalValue).getWrappedData()); 
         }
         else if (originalValue instanceof Query)
         {
            value = asSelectItems(((Query) originalValue).getResultList());
         }
         else if (originalValue != null && originalValue.getClass().isArray())
         {
            if (originalValue.getClass().getComponentType().isPrimitive())
            {
               List list = new ArrayList();
               for (int i = 0; i < Array.getLength(originalValue); i++)
               {
                  list.add(Array.get(originalValue, i));
               }
               value = asSelectItems(list);
            }
            else
            {
               value = asSelectItems(Arrays.asList((Object[]) originalValue));
            }
         }
         else
         {
            javax.faces.model.SelectItem noSelectionLabel = noSelectionLabel();
            if (noSelectionLabel != null) 
            {
               List<javax.faces.model.SelectItem> selectItems = new ArrayList<javax.faces.model.SelectItem>();
               selectItems.add(noSelectionLabel);
               value = selectItems;
            }
            else 
            {
               value = originalValue;
            }
         }
      }
      return value;
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
         String itemLabel = emptyIfNull(getLabel());
         Object value = getItemValue();
         Object itemValue = value == null ? o : value;
         Boolean disabled = getDisabled();
         boolean itemDisabled = disabled == null ? false : disabled;
         selectItems.add( new javax.faces.model.SelectItem(itemValue, itemLabel, "", itemDisabled) );
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
      ValueExpression vb = getValueExpression("noSelectionLabel");
      String noSelectionLabel = getNoSelectionLabel();
      Object parentValue = getParentValue();
      Boolean hideNoSelectionLabel = getHideNoSelectionLabel();
      if (noSelectionLabel != null && vb == null && !(hideNoSelectionLabel  && parentValue != null))
      {
         /* 
          * Here, the user has specfied a noSelectionLabel (may be an empty string), and, if hideNoSelectionLabel
          * is set, then, if a value is selected, then the label is hidden
          */ 
         show = true;
      } 
      else if (noSelectionLabel != null && !"".equals(noSelectionLabel) && !(hideNoSelectionLabel && parentValue != null))
      {
         /*
          * Here, the user has used an EL expression as the noSelectionLabel.  In this case, an empty string is
          * indicates that the label should be hidden.
          */
         show = true;
      }
      
      if (show)
      {
         NullableSelectItem s = new NullableSelectItem(NO_SELECTION_VALUE, noSelectionLabel);
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
