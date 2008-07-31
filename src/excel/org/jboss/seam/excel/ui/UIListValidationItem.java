package org.jboss.seam.excel.ui;


public class UIListValidationItem extends ExcelComponent
{
   public static final String COMPONENT_TYPE = "org.jboss.seam.excel.ui.UIListValidationItem";

   private String value;
   
   public String getValue()
   {
      return (String) valueOf("value", value);
   }

   public void setValue(String value)
   {
      this.value = value;
   }

   public String getFamily()
   {
      return COMPONENT_TYPE;
   }

}
