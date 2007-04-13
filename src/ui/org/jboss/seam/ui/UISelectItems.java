package org.jboss.seam.ui;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.faces.FacesException;
import javax.faces.component.ValueHolder;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.faces.el.ValueBinding;
import javax.faces.model.DataModel;
import javax.faces.model.SelectItem;

import org.jboss.seam.framework.EntityQuery;

public class UISelectItems extends javax.faces.component.UISelectItems
{

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

   public static final String COMPONENT_TYPE = "org.jboss.seam.ui.UISelectItems";

   private static final String NO_SELECTION_VALUE = null;

   private String noSelectionLabel;

   private Boolean hideNoSelectionLabel;

   private String var;

   private String label;

   private Boolean disabled;
   
   private Object value;

   public String getNoSelectionLabel()
   {
      if (noSelectionLabel != null)
      {
         return noSelectionLabel;
      }
      else
      {
         ValueBinding vb = getValueBinding("noSelectionLabel");
         return vb == null ? null :  JSF.getStringValue( getFacesContext(), vb);
      }
   }

   public void setNoSelectionLabel(String noSelectionLabel)
   {
      this.noSelectionLabel = noSelectionLabel;
   }

   public boolean isHideNoSelectionLabel()
   {
      if (hideNoSelectionLabel != null)
      {
         return hideNoSelectionLabel;
      }
      else
      {
         ValueBinding vb = getValueBinding("hideNoSelectionLabel");
         Boolean b = vb == null ? false : JSF.getBooleanValue(getFacesContext(), vb);
         return b == null ? false : b;
      }
   }

   public void setHideNoSelectionLabel(boolean hideNoSelection)
   {
      this.hideNoSelectionLabel = hideNoSelection;
   }

   public String getVar()
   {
      return var;
   }

   public void setVar(String var)
   {
      this.var = var;
   }

   public String getLabel()
   {
      if (label != null)
      {
         return label;
      }
      else
      {
         ValueBinding vb = getValueBinding("label");
         return vb == null ? null :  JSF.getStringValue( getFacesContext(), vb);
      }
   }

   public void setLabel(String label)
   {
      this.label = label;
   }

   public boolean isDisabled()
   {
      if (disabled != null)
      {
         return disabled;
      }
      else
      {
         ValueBinding vb = getValueBinding("disabled");
         Boolean b = vb == null ? false : JSF.getBooleanValue(getFacesContext(), vb);
         return b == null ? false : b;
      }
   }

   public void setDisabled(boolean disabled)
   {
      this.disabled = disabled;
   }

   @Override
   public void restoreState(FacesContext context, Object state)
   {
      Object[] values = (Object[]) state;
      super.restoreState(context, values[0]);
      noSelectionLabel = (String) values[1];
      hideNoSelectionLabel = (Boolean) values[2];
      var = (String) values[3];
      label = (String) values[4];
      disabled = (Boolean) values[5];
   }

   @Override
   public Object saveState(FacesContext context)
   {

      Object[] values = new Object[6];
      values[0] = super.saveState(context);
      values[1] = noSelectionLabel;
      values[2] = hideNoSelectionLabel;
      values[3] = var;
      values[4] = label;
      values[5] = disabled;
      return values;
   }

   @Override
   public Object getValue()
   {
      if (value == null)
      {
         Object originalValue = super.getValue();
         
         if (originalValue instanceof Iterable)
         {
            value = asSelectItems((Iterable) originalValue);
         }
         else if (originalValue instanceof DataModel && ((DataModel) originalValue).getWrappedData() instanceof Iterable)
         {
            value = asSelectItems((Iterable) ((DataModel) originalValue).getWrappedData()); 
         }
         else if (originalValue instanceof EntityQuery)
         {
            value = asSelectItems(((EntityQuery) originalValue).getResultList());
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
         selectItems.add(new javax.faces.model.SelectItem(o, getLabel(), "", isDisabled()));
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
      if (noSelectionLabel != null && !(isHideNoSelectionLabel() && getParentValue() != null))
      {
         /* 
          * Here, the user has specfied a noSelectionLabel (may be an empty string), and, if hideNoSelectionLabel
          * is set, then, if a value is selected, then the label is hidden
          */ 
         show = true;
      } 
      else if (getNoSelectionLabel() != null && !"".equals(getNoSelectionLabel()) && !(isHideNoSelectionLabel() && getParentValue() != null))
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