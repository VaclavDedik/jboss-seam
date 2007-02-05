package org.jboss.seam.ui;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.faces.FacesException;
import javax.faces.component.ValueHolder;
import javax.faces.context.FacesContext;
import javax.faces.el.ValueBinding;
import javax.faces.model.DataModel;

public class UISelectItems extends javax.faces.component.UISelectItems {

   private class NullableSelectItem extends javax.faces.model.SelectItem {
      
      private Object value;
      
      private NullableSelectItem(Object value, String label) {
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
	
	public String getNoSelectionLabel() {
		if (noSelectionLabel != null) {
			return noSelectionLabel;
		} else {
			return getString("noSelectionLabel");
		}
	}

	public void setNoSelectionLabel(String noSelectionLabel) {
		this.noSelectionLabel = noSelectionLabel;
	}

	public boolean isHideNoSelectionLabel() {
		if (hideNoSelectionLabel != null) {
			return hideNoSelectionLabel;
		} else {
         Boolean value = getBoolean("hideNoSelectionLabel");
			return value == null ? false : value;
		}
	}

	public void setHideNoSelectionLabel(boolean hideNoSelection) {
		this.hideNoSelectionLabel = hideNoSelection;
	}
	
	public String getVar() {
		return var;
	}

	public void setVar(String var) {
		this.var = var;
	}

	public String getLabel() {
		if (label != null) {
			return label;
		} else {
			return getString("label");
		}
	}

	public void setLabel(String label) {
		this.label = label;
	}
	
	public boolean isDisabled() {
		if (disabled != null) {
			return disabled;
		} else {
			Boolean value = getBoolean("disabled");
			return value != null ? value : false;
		}
	}

	public void setDisabled(boolean disabled) {
		this.disabled = disabled;
	}

	@Override
	public void restoreState(FacesContext context, Object state) {
		Object[] values = (Object[]) state;
		super.restoreState(context, values[0]);
		noSelectionLabel = (String) values[1];
		hideNoSelectionLabel = (Boolean) values[2];
		var = (String) values[3];
		label = (String) values[4];
		disabled = (Boolean) values[5];
	}
	
	@Override
	public Object saveState(FacesContext context) {
		
		Object[] values = new Object[6];
		values[0] = super.saveState(context);
		values[1] = noSelectionLabel;
		values[2] = hideNoSelectionLabel;
		values[3] = var;
		values[4] = label;
		values[5] = disabled;
		return values;
	}
	
	private String getString(String localName) {
		ValueBinding vb = getValueBinding(localName);
      if (vb == null) {
			return null;
		} else {
			return vb.getValue(getFacesContext()).toString();
		}
	}
	
	private Boolean getBoolean(String localName) {
		String string = getString(localName);
      if (string != null) {
			return Boolean.valueOf(string);
		} else {
			return null;
		}
	}
	
   @Override
	public Object getValue() {
			return createSelectItems(super.getValue());
	}
	
	private Object createSelectItems(Object value) 
   {
		Iterable<?> iterable = null;
		if (value instanceof DataModel) 
      {
			value = ((DataModel) value).getWrappedData();
		}
		if (value.getClass().isArray()) 
      {
         if (value.getClass().getComponentType().isPrimitive()) 
         {
            List list = new ArrayList();
            for (int i = 0; i < Array.getLength(value); i++)
            {
               list.add(Array.get(value, i));
            }
            iterable = list;
         } 
         else 
         {
            iterable = Arrays.asList((Object[]) value);
         }
		}
      else if (value instanceof Iterable) 
      {
			iterable = (Iterable) value;
		}
		if (iterable != null) 
      {
			List<javax.faces.model.SelectItem> selectItems = new ArrayList<javax.faces.model.SelectItem>();
			addNoSelectionLabel(selectItems, iterable);
			for (Object o : iterable) {
				initVar(o);
				selectItems.add(new javax.faces.model.SelectItem(o, getLabel(), "", isDisabled()));
				destroyVar();
			}
			return selectItems;
		} else {
			ValueBinding vb = this.getValueBinding("value");
			if (vb != null) {
				throw new IllegalArgumentException("selectItems' value=\"" + vb.getExpressionString() + "\" must implement java.lang.Iteratable, be an array or a JSF DataModel but it is " + iterable + " (" + vb.getType(getFacesContext()) + ")");	
			} else {
				throw new IllegalArgumentException("selectItems' value must implement java.lang.Iteratable, be an array or a JSF DataModel but is " + iterable);
			}
			
		}
	}
	
	private boolean addNoSelectionLabel(List<javax.faces.model.SelectItem> selectItems, Object originalValue) {
		boolean add = true;
		if (isHideNoSelectionLabel() &&  getParentValue() != null){
			add = false;
		}
		if (add && getNoSelectionLabel() != null) {
			NullableSelectItem s = new NullableSelectItem(NO_SELECTION_VALUE, getNoSelectionLabel());
			selectItems.add(s);
			ConverterChain converterChain = new ConverterChain(this.getParent());
			converterChain.addConverterToChain(new NoSelectionConverter(), ConverterChain.CHAIN_START);
			return true;
		} else {
			return false;
		}
	}
	
	@SuppressWarnings("unchecked")
	private void initVar(Object varValue) {
		if (getVar() == null) {
			throw new FacesException("var attribute must be set");
		}
		getFacesContext().getExternalContext().getRequestMap().put(
				getVar(), varValue);
	}
	
	private void destroyVar() {
		getFacesContext().getExternalContext().getRequestMap().remove(
				getVar());
	}
	
	private Object getParentValue() {
		if (getParent() instanceof ValueHolder) {
			ValueHolder parent = (ValueHolder) getParent();
			return parent.getValue();
		} else {
			return null;
		}
	}
}