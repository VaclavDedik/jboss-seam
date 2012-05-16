
package org.jboss.seam.ui.component;

import javax.faces.component.UISelectItem;
import javax.faces.model.SelectItem;

import org.richfaces.cdk.annotations.Attribute;
import org.richfaces.cdk.annotations.Description;
import org.richfaces.cdk.annotations.JsfComponent;
import org.richfaces.cdk.annotations.Tag;

/**
 * JSF component class
 * 
 */
@JsfComponent(description=@Description(displayName="org.jboss.seam.ui.EnumItem",value="Creates a SelectItem from an enum value."),
family="org.jboss.seam.ui.EnumItem", type="org.jboss.seam.ui.EnumItem",generate="org.jboss.seam.ui.component.html.HtmlEnumItem", 
tag = @Tag(baseClass="org.jboss.seam.ui.util.cdk.UIComponentTagBase", name="enumItem"),
attributes = {"enumItem.xml" })
public abstract class UIEnumItem extends UISelectItem
{

   @Attribute
   public abstract String getEnumValue();

   public abstract void setEnumValue(String enumValue);
   
   public abstract void setLabel(String label);
   
   @Attribute
   public abstract String getLabel();
   
   @Override
   public Object getItemValue()
   {
      return getEnumValue();
   }
   
   @Override
   public void setItemValue(Object itemValue)
   {
      setEnumValue(itemValue == null ? null : itemValue.toString());
   }
   
   @Override
   public String getItemLabel()
   {
      return getLabel();
   }

   @Override
   public void setItemLabel(String itemLabel)
   {
      setLabel(itemLabel);
   }
   
   @Override
   public Object getValue()
   {
      Class c = getParent().getValueExpression("value").getType(getFacesContext().getELContext());
      String enumValue = getEnumValue();
      String label = getLabel();
      Object value = Enum.valueOf(c, enumValue);
      return new SelectItem(value, label == null ? enumValue : label);
   }
}
