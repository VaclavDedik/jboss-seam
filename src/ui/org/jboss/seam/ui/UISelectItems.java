package org.jboss.seam.ui;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.faces.FacesException;
import javax.faces.component.ValueHolder;
import javax.faces.context.FacesContext;
import javax.faces.el.ValueBinding;
import javax.faces.model.DataModel;
import javax.faces.model.SelectItem;

public class UISelectItems extends javax.faces.component.UISelectItems {
	
	public static final String COMPONENT_TYPE = "org.jboss.seam.ui.UISelectItems";
   
   private static final String NO_SELECTION_VALUE = "";
	
	private String noSelectionLabel;
	private Boolean hideNoSelection;
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

	public boolean isHideNoSelection() {
		if (hideNoSelection != null) {
			return hideNoSelection;
		} else {
			return getBoolean("hideNoSelection");
		}
	}

	public void setHideNoSelection(boolean hideNoSelection) {
		this.hideNoSelection = hideNoSelection;
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
		hideNoSelection = (Boolean) values[2];
		var = (String) values[3];
		label = (String) values[4];
		disabled = (Boolean) values[5];
	}
	
	@Override
	public Object saveState(FacesContext context) {
		
		Object[] values = new Object[6];
		values[0] = super.saveState(context);
		values[1] = noSelectionLabel;
		values[2] = hideNoSelection;
		values[3] = var;
		values[4] = label;
		values[5] = disabled;
		return values;
	}
	
	private String getString(String localName) {
		if (getValueBinding(localName) == null) {
			return null;
		} else {
			return (String) getValueBinding(localName).getValue(getFacesContext());
		}
	}
	
	private Object getObject(String localName) {
		if (getValueBinding(localName) == null) {
			return null;
		} else {
			return getValueBinding(localName).getValue(getFacesContext());
		}
	}
	
	private Boolean getBoolean(String localName) {
		if (getString(localName) != null) {
			return Boolean.valueOf(getString(localName));
		} else {
			return null;
		}
	}
	
   @Override
	public Object getValue() {
			return createSelectItems(super.getValue());
	}
	
	private Object createSelectItems(Object value) {
		Iterable<?> iterable = null;
		if (value instanceof DataModel) {
			value = ((DataModel) value).getWrappedData();
		}
		if (value.getClass().isArray()) {
			iterable = Arrays.asList((Object[]) value);
		} else if (value instanceof Iterable) {
			iterable = (Iterable) value;
		}
		if (iterable != null) {
			List<SelectItem> selectItems = new ArrayList<SelectItem>();
			addNoSelectionLabel(selectItems, iterable);
			for (Object o : iterable) {
				initVar(o);
				selectItems.add(new SelectItem(o, getLabel(), "", isDisabled()));
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
	
	private boolean addNoSelectionLabel(List<SelectItem> selectItems, Object originalValue) {
		boolean add = true;
		if (isHideNoSelection() &&  getParentValue() != null){
			add = false;
		}
		if (add && getNoSelectionLabel() != null) {
			SelectItem s = new SelectItem(NO_SELECTION_VALUE, getNoSelectionLabel());
			selectItems.add(s);
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