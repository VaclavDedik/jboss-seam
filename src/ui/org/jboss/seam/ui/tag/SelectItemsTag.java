package org.jboss.seam.ui.tag;

import org.jboss.seam.ui.UISelectItems;
import org.jboss.seam.ui.tag.UIComponentTagBase;

import javax.faces.component.UIComponent;

public class SelectItemsTag extends UIComponentTagBase {
	
	private String noSelectionLabel;
	private String hideNoSelection;
	private String var;
	private String label;
	private String disabled;
	
	
	@Override
	protected void setProperties(UIComponent component) {
		super.setProperties(component);
		setStringProperty(component, "noSelectionLabel", noSelectionLabel);
		setBooleanProperty(component, "hideNoSelection", hideNoSelection);
		setStringProperty(component, "var", var);
		setStringProperty(component, "label", label);
		setBooleanProperty(component, "disabled", disabled);
	}

	@Override
	public String getComponentType() {
		return UISelectItems.COMPONENT_TYPE;
	}

	@Override
	public String getRendererType() {
		return null;
	}

	public void setDisabled(String disabled) {
		this.disabled = disabled;
	}

	public void setHideNoSelection(String hideNoSelection) {
		this.hideNoSelection = hideNoSelection;
	}

	public void setLabel(String label) {
		this.label = label;
	}

	public void setNoSelectionLabel(String noSelectionLabel) {
		this.noSelectionLabel = noSelectionLabel;
	}

	public void setVar(String var) {
		this.var = var;
	}

}
