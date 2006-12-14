package org.jboss.seam.ui.tag;

import javax.faces.component.UIComponent;

import org.jboss.seam.ui.EnumItem;

public class EnumItemTag
    extends UIComponentTagBase
{
    String label;
    String enumValue;

    @Override
    public String getComponentType()
    {
        return EnumItem.COMPONENT_TYPE;
    }

    @Override
    public String getRendererType()
    {
        return null;
    }

    
    public void setEnumValue(String enumValue) {
        this.enumValue = enumValue;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    @Override
    protected void setProperties(UIComponent component)
    {
        super.setProperties(component);
        setStringProperty(component, "label",     label);
        setStringProperty(component, "enumValue", enumValue);
        
        // check for missing values/
    }

}
