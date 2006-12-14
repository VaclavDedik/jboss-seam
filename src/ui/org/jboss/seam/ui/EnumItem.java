package org.jboss.seam.ui;

import javax.faces.component.UISelectItem;
import javax.faces.context.FacesContext;
import javax.faces.el.ValueBinding;
import javax.faces.model.SelectItem;

public class EnumItem
    extends UISelectItem
{
    public static final String COMPONENT_TYPE = "org.jboss.seam.ui.EnumItem";

    String enumValue = null;
    String label = null;

    public void setEnumValue(String enumValue) {
        this.enumValue = enumValue;
    }

    public String getEnumValue() {
        return enumValue;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public String getLabel() {
        return label;
    }


    private String labelValue() {
        ValueBinding valueBinding = getValueBinding("label");
        if (valueBinding!=null) {
            Object labelValue = valueBinding.getValue(getFacesContext());
            if (labelValue != null) {
                return labelValue.toString();
            }
        }
        return label;
    }

    @Override
    public Object getValue()
    {
        Class c = getParent().getValueBinding("value").getType(getFacesContext());

        SelectItem item = new SelectItem();
        Object value = Enum.valueOf(c, enumValue);

        item.setValue(value);

        String labelValue = labelValue();
        item.setLabel(labelValue==null?enumValue:labelValue);
        
        return item;
    }
    

    @Override
    public void restoreState(FacesContext context, Object state) {
        Object[] values = (Object[]) state;
        super.restoreState(context, values[0]);
        enumValue  = (String) values[1];
        label      = (String) values[2];
    }
    
    @Override
    public Object saveState(FacesContext context) {
        Object[] values = new Object[3];
        values[0] = super.saveState(context);
        values[1] = enumValue;
        values[2] = label;
        return values;
    }


}
