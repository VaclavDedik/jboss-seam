
package org.jboss.seam.ui.component;

import javax.faces.component.UISelectItem;
import javax.faces.model.SelectItem;

/**
 * JSF component class
 * 
 */
public abstract class UIEnumItem extends UISelectItem
{

   private static final String COMPONENT_TYPE = "org.jboss.seam.ui.EnumItem";

   private static final String COMPONENT_FAMILY = "org.jboss.seam.ui.EnumItem";

   public abstract String getEnumValue();

   public abstract void setEnumValue(String enumValue);
   
   public abstract void setLabel(String label);
   
   public abstract String getLabel();

   @Override
   public Object getValue()
   {
      Class c = getParent().getValueBinding("value").getType(getFacesContext());
      String enumValue = getEnumValue();
      String label = getLabel();
      Object value = Enum.valueOf(c, enumValue);
      return new SelectItem(value, label == null ? enumValue : label);
   }
}
