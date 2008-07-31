package org.jboss.seam.excel.ui;

import org.jboss.seam.excel.Template;

public class UICellTemplate extends UICellFormat implements Template
{
   public static final String COMPONENT_TYPE = "org.jboss.seam.excel.ui.UICellTemplate";

   private String name;
   
   public String getName()
   {
      return (String) valueOf("name", name);
   }

   public void setName(String name)
   {
      this.name = name;
   }

   public String getFamily()
   {
      return COMPONENT_TYPE; 
   }

   public TemplateType getType()
   {
      return TemplateType.cell;
   }

}
