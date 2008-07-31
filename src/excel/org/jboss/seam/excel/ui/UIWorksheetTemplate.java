package org.jboss.seam.excel.ui;

import org.jboss.seam.excel.Template;

public class UIWorksheetTemplate extends UIWorksheetSettings implements Template
{
   public static final String COMPONENT_TYPE = "org.jboss.seam.excel.ui.UIWorksheetTemplate";

   private String name;
   
   @Override
   public String getFamily()
   {
      return COMPONENT_TYPE;
   }

   public String getName()
   {
      return (String) valueOf("name", name);
   }
   
   public void setName(String name) {
      this.name = name;
   }

   public TemplateType getType()
   {
      return TemplateType.worksheet;
   }

}
