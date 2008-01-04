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
 * @auth Pete Muir
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
   
   private abstract class ContextualSelectItem {
      
      private Object varValue;
      
      public ContextualSelectItem(Object varValue)
      {
         if (varValue == null)
         {
            throw new FacesException("var attribute must be set");
         }
         this.varValue = varValue;
      }
      
      /**
       * @return the varValue
       */
      protected Object getVarValue()
      {
         return this.varValue;
      }
      
      private void setup()
      {
         getFacesContext().getExternalContext().getRequestMap().put(getVar(), varValue);
      }
      
      private void cleanup()
      {
         getFacesContext().getExternalContext().getRequestMap().remove(getVar());
      }
      
      protected abstract Object getSelectItemValue();
      protected abstract String getSelectItemLabel();
      protected abstract Boolean getSelectItemDisabled();
      
      protected javax.faces.model.SelectItem create()
      {
         try
         {
            setup();
            return new javax.faces.model.SelectItem(this.getSelectItemValue(), this.getSelectItemLabel(), "", this.getSelectItemDisabled());
         }
         finally
         {
            cleanup();
         }
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
      final List<javax.faces.model.SelectItem> selectItems =  new ArrayList<javax.faces.model.SelectItem>();
      javax.faces.model.SelectItem noSelectionLabel = noSelectionLabel();
      if (noSelectionLabel != null) 
      {
         selectItems.add(noSelectionLabel);
      }
      for (final Object o : iterable)
      {
         selectItems.add(new ContextualSelectItem(o)
         {

            @Override
            protected Boolean getSelectItemDisabled()
            {
               Boolean disabled = getDisabled();
               return disabled == null ? false : disabled;
            }

            @Override
            protected String getSelectItemLabel()
            {
               return emptyIfNull(getLabel());
            }

            @Override
            protected Object getSelectItemValue()
            {
               Object value = getItemValue();
               return value == null ? getVarValue() : value;
            }
            
         }.create());
      }
      return selectItems;
   }

   private javax.faces.model.SelectItem noSelectionLabel()
   {
      if (isShowNoSelectionLabel())
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
   
   private boolean isShowNoSelectionLabel()
   {  
      ValueExpression vb = getValueExpression("noSelectionLabel");
      String noSelectionLabel = getNoSelectionLabel();
      Boolean hideNoSelectionLabel = getHideNoSelectionLabel();
      Object parentValue = getParentValue();
      
      /*
       * This is a slight hack. If you do an EL expresison like this (to hide the label)
       * 
       * noSelectionLabel="#{x eq y ? 'Please Select' : null}"
       * 
       * then, if x != y, EL will return an empty String, not null, so we work around that, with the side effect
       * that if the result of the EL expression is an empty String, then the label will be hidden.
       */
      if (noSelectionLabel != null && vb == null && !(hideNoSelectionLabel  && parentValue != null))
      {
         /* 
          * Here, the user has specfied a noSelectionLabel (may be an empty string), and, if hideNoSelectionLabel
          * is set, then, if a value is selected, then the label is hidden
          */ 
         return true;
      } 
      else if (noSelectionLabel != null && !"".equals(noSelectionLabel) && !(hideNoSelectionLabel && parentValue != null))
      {
         /*
          * Here, the user has used an EL expression as the noSelectionLabel.  In this case, an empty string is
          * indicates that the label should be hidden.
          */
         return true;
      }
      else
      {
         return false;
      }
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
